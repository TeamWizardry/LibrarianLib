package com.teamwizardry.librarianlib.features.facade.layout

import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.supporting.getData
import com.teamwizardry.librarianlib.features.facade.component.supporting.setData
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.clamp
import com.teamwizardry.librarianlib.features.math.Axis2d
import com.teamwizardry.librarianlib.features.math.Cardinal2d
import com.teamwizardry.librarianlib.features.math.Vec2d
import kotlin.math.max
import kotlin.math.roundToInt

open class Flexbox(x: Int, y: Int, width: Int, height: Int, flexDirection: Cardinal2d = Cardinal2d.GUI.RIGHT): GuiComponent(x, y, width, height) {
    /**
     * The direction children should flow
     */
    var flexDirection: Cardinal2d = flexDirection

    /**
     * The alignment along the cross axis (perpendicular to [flexDirection])
     */
    var alignItems: Align = Align.START

    /**
     * Whether to stretch/squash children along the cross axis. If this is true then [alignItems] will only be used when
     * children reach their maximum size
     */
    var stretch: Boolean = true

    /**
     * The justification algorithm to apply when there is space left over
     * (e.g. all elements have already reached their maximum size)
     */
    var justifyContent: Justify = Justify.SPACE_BETWEEN

    /**
     * Spacing to apply between each child.
     *
     * Unless [justifyContent] is [Justify.SPACE_AROUND] or [Justify.SPACE_EVENLY] this spacing is only applied between
     * children, not before/after the first/last ones.
     */
    var spacing: Int = 0

    val minSize: Vec2d
        get() {
            var major = 0
            var cross = 0
            val items = subComponents.map {
                FlexItem(
                    it,
                    if(flexDirection.axis == Axis2d.X) it.frame.heighti else it.frame.widthi,
                    it.getData() ?: inertData(it)
                )
            }
            items.forEach {
                major += it.flexBasis + it.data.marginBefore + it.data.marginAfter
                cross = max(cross,
                    if(it.data.stretchSelf ?: stretch)
                        it.data.minCrossSize
                    else
                        it.crossSize
                )
            }
            return if(flexDirection.axis == Axis2d.X) vec(major, cross) else vec(cross, major)
        }

    override fun layoutChildren() {
        super.layoutChildren()
        val items = subComponents.map {
            FlexItem(
                it,
                if(flexDirection.axis == Axis2d.X) it.frame.heighti else it.frame.widthi,
                it.getData() ?: inertData(it)
            )
        }.sortedBy { it.data.order }

        val majorLength = if(flexDirection.axis == Axis2d.X) this.widthi else this.heighti
        val crossLength = if(flexDirection.axis == Axis2d.X) this.heighti else this.widthi

        layoutMajorAxis(majorLength, items)
        layoutAlignment(majorLength, crossLength, items)

        items.forEach {
            val majorPos = if(flexDirection.sign < 1)
                majorLength - it.pos - it.size
            else
                it.pos
            if(flexDirection.axis == Axis2d.X) {
                it.component.frame = rect(
                    majorPos, it.crossPos,
                    it.size, it.crossSize
                )
            } else {
                it.component.frame = rect(
                    it.crossPos, majorPos,
                    it.crossSize, it.size
                )
            }
        }
    }

    private fun inertData(component: GuiComponent) = Data(
        order = 0,
        flexBasis = if(flexDirection.axis == Axis2d.X) component.widthi else component.heighti,
        flexGrow = 0,
        flexShrink = 0,
        marginBefore = 0,
        marginAfter = 0,
        minSize = 0,
        maxSize = Int.MAX_VALUE,
        marginCrossBefore = 0,
        marginCrossAfter = 0,
        minCrossSize = 0,
        maxCrossSize = Int.MAX_VALUE,
        stretchSelf = false,
        alignSelf = null
    )

    private fun layoutMajorAxis(space: Int, list: List<FlexItem>) {
        var remaining = space

        list.forEach {
            remaining -= it.flexBasis + it.data.marginBefore + it.data.marginAfter
        }

        val growSum = list.sumBy { it.data.flexGrow }
        if(remaining > 0 && growSum != 0) {
            var lastRemaining = 0
            while(remaining != lastRemaining) {
                lastRemaining = remaining
                var leftover = 0
                var unitsLeft = growSum.toDouble()

                list.forEach {
                    if(unitsLeft == 0.0) return@forEach
                    val portion = (remaining * it.data.flexGrow / unitsLeft).roundToInt()
                    it.size += portion
                    remaining -= portion
                    unitsLeft -= it.data.flexGrow

                    if(it.size > it.data.maxSize) {
                        leftover += it.size - it.data.maxSize
                        it.size = it.data.maxSize
                    }
                }
                remaining += leftover
            }
        }

        val basisSum = list.sumBy { max(1, it.flexBasis) }
        if(remaining < 0 && basisSum != 0) {
            var lastRemaining = 0
            while(remaining != lastRemaining) {
                lastRemaining = remaining
                var leftover = 0
                var unitsLeft = basisSum.toDouble()

                list.forEach {
                    if(unitsLeft == 0.0) return@forEach
                    val portion = (remaining * it.data.flexShrink * max(1, it.flexBasis) / unitsLeft).roundToInt()
                    it.size += portion
                    remaining -= portion
                    unitsLeft -= max(1, it.flexBasis)

                    if(it.size < it.data.minSize) {
                        leftover -= it.data.minSize - it.size
                        it.size = it.data.minSize
                    }
                }
                remaining = leftover
            }
        }
    }

    private fun layoutAlignment(majorSpace: Int, crossSpace: Int, list: List<FlexItem>) {
        list.forEach {
            if(it.data.stretchSelf ?: stretch)
                it.crossSize = crossSpace.clamp(it.data.minCrossSize, it.data.maxCrossSize)
            when(it.data.alignSelf ?: alignItems) {
                Align.START -> it.crossPos =
                    it.data.marginCrossBefore
                Align.CENTER -> it.crossPos =
                    (crossSpace - (it.crossSize + it.data.marginCrossBefore + it.data.marginCrossAfter)) / 2
                Align.END -> it.crossPos =
                    crossSpace - (it.crossSize + it.data.marginCrossAfter)
            }
        }

        var width =  0
        list.forEach {
            it.pos = width + it.data.marginBefore
            width += it.data.marginBefore + it.size + it.data.marginAfter
        }

        val emptySpace = majorSpace - width

        if(emptySpace > 0) {
            val justify = justifyContent
            when (justify) {
                Justify.START -> {
                } // nop
                Justify.END -> {
                    list.forEach {
                        it.pos += emptySpace
                    }
                }
                Justify.CENTER -> {
                    val offset = emptySpace / 2
                    list.forEach {
                        it.pos += offset
                    }
                }
                Justify.SPACE_AROUND -> {
                    if(list.isNotEmpty()) {
                        val gap = emptySpace / list.size.toDouble()
                        list.forEachIndexed { i, it ->
                            it.pos += (gap * (i + 0.5)).roundToInt()
                        }
                    }
                }
                Justify.SPACE_BETWEEN -> {
                    if(list.size > 1) {
                        val gap = emptySpace / (list.size-1).toDouble()
                        list.forEachIndexed { i, it ->
                            it.pos += (gap * i).roundToInt()
                        }
                    }
                }
                Justify.SPACE_EVENLY -> {
                    val gap = emptySpace / (list.size + 1).toDouble()
                    list.forEachIndexed { i, it ->
                        it.pos += (gap * (i + 1)).roundToInt()
                    }
                }
            }
        }
    }

    private class FlexItem(val component: GuiComponent, var crossSize: Int, val data: Data) {
        val flexBasis = (data.flexBasis ?: component.frame.widthi).clamp(data.minSize, data.maxSize)
        var size: Int = flexBasis
        var crossPos: Int = 0
        var pos: Int = 0
    }

    data class Data(
        var order: Int,
        var flexBasis: Int?,
        var flexGrow: Int,
        var flexShrink: Int,
        var marginBefore: Int,
        var marginAfter: Int,
        var minSize: Int,
        var maxSize: Int,
        var marginCrossBefore: Int,
        var marginCrossAfter: Int,
        var minCrossSize: Int,
        var maxCrossSize: Int,
        var stretchSelf: Boolean?,
        var alignSelf: Align?
    ) {
        fun config(
            order: Int = this.order,
            flexGrow: Int = this.flexGrow,
            flexShrink: Int = this.flexShrink,
            flexBasis: Int? = if(flexGrow == 0 && flexShrink == 0) null else this.flexBasis,
            marginBefore: Int = this.marginBefore,
            marginAfter: Int = this.marginAfter,
            minSize: Int = this.minSize,
            maxSize: Int = this.maxSize,
            marginCrossBefore: Int = this.marginCrossBefore,
            marginCrossAfter: Int = this.marginCrossAfter,
            minCrossSize: Int = this.minCrossSize,
            maxCrossSize: Int = this.maxCrossSize,
            stretchSelf: Boolean? = this.stretchSelf,
            alignSelf: Align? = this.alignSelf
        ) {
            this.order = order
            this.flexBasis = flexBasis
            this.flexGrow = flexGrow
            this.flexShrink = flexShrink
            this.marginBefore = marginBefore
            this.marginAfter = marginAfter
            this.minSize = minSize
            this.maxSize = maxSize

            this.marginCrossBefore = marginCrossBefore
            this.marginCrossAfter = marginCrossAfter
            this.minCrossSize = minCrossSize
            this.maxCrossSize = maxCrossSize
            this.stretchSelf = stretchSelf
            this.alignSelf = alignSelf
        }
    }

    enum class Align {
        /**
         * Align children along the max edge along the cross axis
         */
        START,
        /**
         * Center children along the cross axis
         */
        CENTER,
        /**
         * Align children along the max edge along the cross axis
         */
        END
    }

    enum class Justify {
        /**
         * Justify children toward the start of the flexbox. (e.g. minX for a flexbox with a +X [flexDirection])
         */
        START,
        /**
         * Justify children toward the end of the flexbox. (e.g. maxX for a flexbox with a +X [flexDirection])
         */
        END,
        /**
         * Pack children into the center of the flexbox, leaving equal space on both sides of the group
         */
        CENTER,
        /**
         * Distribute children evenly, with equal space on either side of each.
         */
        SPACE_AROUND,
        /**
         * Distribute children evenly, the first child is flush with the start and the last with the end
         */
        SPACE_BETWEEN,
        /**
         * Distribute children evenly, with each gap being equal in size.
         *
         * Distinct from [SPACE_AROUND] in that it will have a full-sized gap at the ends of the list, as opposed to
         * the half-sized gaps of [SPACE_AROUND]
         */
        SPACE_EVENLY
    }
}

val GuiComponent.flex: Flexbox.Data
    get() {
        var data = this.getData<Flexbox.Data>()
        if(data == null)
            data = Flexbox.Data(
                order = 0,
                flexBasis = this.widthi,
                flexGrow = 1,
                flexShrink = 1,
                marginBefore = 0,
                marginAfter = 0,
                minSize = 0,
                maxSize = Int.MAX_VALUE,
                marginCrossBefore = 0,
                marginCrossAfter = 0,
                minCrossSize = 0,
                maxCrossSize = Int.MAX_VALUE,
                stretchSelf = null,
                alignSelf = null
            ).also { this.setData(it) }
        return data
    }