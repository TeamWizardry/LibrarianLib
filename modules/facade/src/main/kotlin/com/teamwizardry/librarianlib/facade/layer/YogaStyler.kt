package com.teamwizardry.librarianlib.facade.layer

import org.lwjgl.util.yoga.Yoga.*

/**
 * A set of convenient builder-like methods for setting up layer styles. Distinct from [YogaStyle] in that that is
 * primarily focused on read/write symmetry and data access, while YogaStyler is focused on write-only access and ease
 * of configurability.
 */
@Suppress("PublicApiImplicitType")
class YogaStyler(private val layer: GuiLayer) {

    /**
     * Sets the layout direction (used for RTL localization)
     */
    @get:JvmName("direction")
    val direction: DirectionStyler = DirectionStyler()
    inner class DirectionStyler internal constructor() {
        /**
         * Left-to-right
         */
        fun ltr() = build { direction = YGDirectionLTR }
        /**
         * Right-to-left
         */
        fun rtl() = build { direction = YGDirectionRTL }
        /**
         * Right-to-left
         */
        fun inherit() = build { direction = YGDirectionInherit }
    }

    /** [`flex-direction`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction) */
    @get:JvmName("flexDirection")
    val flexDirection: FlexDirectionStyler = FlexDirectionStyler()
    inner class FlexDirectionStyler internal constructor() {
        /** `flex-direction: row;` */
        fun row() = build { flexDirection = YGFlexDirectionRow }
        /** `flex-direction: row-reverse;` */
        fun rowReverse() = build { flexDirection = YGFlexDirectionRowReverse }
        /** `flex-direction: column;` */
        fun column() = build { flexDirection = YGFlexDirectionColumn }
        /** `flex-direction: column-reverse;` */
        fun columnReverse() = build { flexDirection = YGFlexDirectionColumnReverse }
    }

    /** [`justify-content`](https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content) */
    @get:JvmName("justifyContent")
    val justifyContent: JustifyContentStyler = JustifyContentStyler()
    inner class JustifyContentStyler internal constructor() {
        /** `justify-content: start;` */
        fun start() = build { justifyContent = YGJustifyFlexStart }
        /** `justify-content: center;` */
        fun center() = build { justifyContent = YGJustifyCenter }
        /** `justify-content: end;` */
        fun end() = build { justifyContent = YGJustifyFlexEnd }
        /** `justify-content: space-between;` */
        fun spaceBetween() = build { justifyContent = YGJustifySpaceBetween }
        /** `justify-content: space-around;` */
        fun spaceAround() = build { justifyContent = YGJustifySpaceAround }
        /** `justify-content: space-evenly;` */
        fun spaceEvenly() = build { justifyContent = YGJustifySpaceEvenly }
    }

    /** [`align-content`](https://developer.mozilla.org/en-US/docs/Web/CSS/align-content) */
    @get:JvmName("alignContent")
    val alignContent: AlignContentStyler = AlignContentStyler()
    inner class AlignContentStyler internal constructor() {
        /** `align-content: auto;` */
        fun auto() = build { alignContent = YGAlignAuto }
        /** `align-content: start;` */
        fun start() = build { alignContent = YGAlignFlexStart }
        /** `align-content: center;` */
        fun center() = build { alignContent = YGAlignCenter }
        /** `align-content: end;` */
        fun end() = build { alignContent = YGAlignFlexEnd }
        /** `align-content: stretch;` */
        fun stretch() = build { alignContent = YGAlignStretch }
        /** `align-content: baseline;` */
        fun baseline() = build { alignContent = YGAlignBaseline }
        /** `align-content: space-between;` */
        fun spaceBetween() = build { alignContent = YGAlignSpaceBetween }
        /** `align-content: space-around;` */
        fun spaceAround() = build { alignContent = YGAlignSpaceAround }
    }

    /** [`align-items`](https://developer.mozilla.org/en-US/docs/Web/CSS/align-items) */
    @get:JvmName("alignItems")
    val alignItems: AlignItemsStyler = AlignItemsStyler()
    inner class AlignItemsStyler internal constructor() {
        /** `align-items: auto;` */
        fun auto() = build { alignItems = YGAlignAuto }
        /** `align-items: start;` */
        fun start() = build { alignItems = YGAlignFlexStart }
        /** `align-items: center;` */
        fun center() = build { alignItems = YGAlignCenter }
        /** `align-items: end;` */
        fun end() = build { alignItems = YGAlignFlexEnd }
        /** `align-items: stretch;` */
        fun stretch() = build { alignItems = YGAlignStretch }
        /** `align-items: baseline;` */
        fun baseline() = build { alignItems = YGAlignBaseline }
        /** `align-items: space-between;` */
        fun spaceBetween() = build { alignItems = YGAlignSpaceBetween }
        /** `align-items: space-around;` */
        fun spaceAround() = build { alignItems = YGAlignSpaceAround }
    }

    /** [`align-self`](https://developer.mozilla.org/en-US/docs/Web/CSS/align-self) */
    @get:JvmName("alignSelf")
    val alignSelf: AlignSelfStyler = AlignSelfStyler()
    inner class AlignSelfStyler internal constructor() {
        /** `align-self: auto;` */
        fun auto() = build { alignSelf = YGAlignAuto }
        /** `align-self: start;` */
        fun start() = build { alignSelf = YGAlignFlexStart }
        /** `align-self: center;` */
        fun center() = build { alignSelf = YGAlignCenter }
        /** `align-self: end;` */
        fun end() = build { alignSelf = YGAlignFlexEnd }
        /** `align-self: stretch;` */
        fun stretch() = build { alignSelf = YGAlignStretch }
        /** `align-self: baseline;` */
        fun baseline() = build { alignSelf = YGAlignBaseline }
        /** `align-self: space-between;` */
        fun spaceBetween() = build { alignSelf = YGAlignSpaceBetween }
        /** `align-self: space-around;` */
        fun spaceAround() = build { alignSelf = YGAlignSpaceAround }
    }

    /** [`position`](https://developer.mozilla.org/en-US/docs/Web/CSS/position) */
    @get:JvmName("positionType")
    val positionType: PositionTypeStyler = PositionTypeStyler()
    inner class PositionTypeStyler internal constructor() {
        /** `position: absolute;` */
        fun absolute() = build { positionType = YGPositionTypeAbsolute }
        /** `position: relative;` */
        fun relative() = build { positionType = YGPositionTypeRelative }
    }

    /** [`flex-wrap`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-wrap) */
    @get:JvmName("flexWrap")
    val flexWrap: FlexWrapStyler = FlexWrapStyler()
    inner class FlexWrapStyler internal constructor() {
        /** `flex-wrap: nowrap;` */
        fun nowrap() = build { flexWrap = YGWrapNoWrap }
        /** `flex-wrap: wrap;` */
        fun wrap() = build { flexWrap = YGWrapWrap }
        /** `flex-wrap: wrap-reverse;` */
        fun wrapReverse() = build { flexWrap = YGWrapReverse }
    }

    /** [`overflow`](https://developer.mozilla.org/en-US/docs/Web/CSS/overflow) */
    @get:JvmName("overflow")
    val overflow: OverflowStyler = OverflowStyler()
    inner class OverflowStyler internal constructor() {
        /** `overflow: visible;` */
        fun visible() = build { overflow = YGOverflowVisible }
        /** `overflow: hidden;` */
        fun hidden() = build { overflow = YGOverflowHidden }
        /** `overflow: scroll;` */
        fun scroll() = build { overflow = YGOverflowScroll }
    }

    /** [`display`](https://developer.mozilla.org/en-US/docs/Web/CSS/display) */
    @get:JvmName("display")
    val display: DisplayStyler = DisplayStyler()
    inner class DisplayStyler internal constructor() {
        /** `display: flex;` */
        fun flex() = build { display = YGDisplayFlex }
        /** `display: none;` */
        fun none() = build { display = YGDisplayNone }
    }

    /** [`flex`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex) */
    fun flex(grow: Float) = build {
        flexGrow = grow
        flexShrink = 1f
        flexBasis.px = 0f
    }

    /** [`flex`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex) */
    fun flex(grow: Float, shrink: Float) = build {
        flexGrow = grow
        flexShrink = shrink
        flexBasis.px = 0f
    }

    /** [`flex-grow`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow) */
    fun flexGrow(grow: Float) = build {
        flexGrow = grow
    }

    /** [`flex-shrink`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink) */
    fun flexShrink(shrink: Float) = build {
        flexShrink = shrink
    }

    /** [`flex-basis`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis) */
    @get:JvmName("flexBasis")
    val flexBasis: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.flexBasis)

    /** [`left`](https://developer.mozilla.org/en-US/docs/Web/CSS/left) */
    @get:JvmName("left")
    val left: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.left)
    /** [`top`](https://developer.mozilla.org/en-US/docs/Web/CSS/top) */
    @get:JvmName("top")
    val top: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.top)
    /** [`right`](https://developer.mozilla.org/en-US/docs/Web/CSS/right) */
    @get:JvmName("right")
    val right: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.right)
    /** [`bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/bottom) */
    @get:JvmName("bottom")
    val bottom: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.bottom)

    /** [`margin-left`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin-left) */
    @get:JvmName("marginLeft")
    val marginLeft: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginLeft)
    /** [`margin-top`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin-top) */
    @get:JvmName("marginTop")
    val marginTop: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginTop)
    /** [`margin-right`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin-right) */
    @get:JvmName("marginRight")
    val marginRight: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginRight)
    /** [`margin-bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin-bottom) */
    @get:JvmName("marginBottom")
    val marginBottom: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginBottom)

    /** [`margin-left`/`margin-right`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin) */
    @get:JvmName("marginHorizontal")
    val marginHorizontal: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginLeft, layer.yogaStyle.marginRight)
    /** [`margin-top`/`margin-bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin) */
    @get:JvmName("marginVertical")
    val marginVertical: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginTop, layer.yogaStyle.marginBottom)
    /** [`margin`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin) */
    @get:JvmName("margin")
    val margin: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginLeft, layer.yogaStyle.marginTop,
        layer.yogaStyle.marginRight, layer.yogaStyle.marginBottom)

    /** [`padding-left`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding-left) */
    @get:JvmName("paddingLeft")
    val paddingLeft: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingLeft)
    /** [`padding-top`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding-top) */
    @get:JvmName("paddingTop")
    val paddingTop: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingTop)
    /** [`padding-right`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding-right) */
    @get:JvmName("paddingRight")
    val paddingRight: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingRight)
    /** [`padding-bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding-bottom) */
    @get:JvmName("paddingBottom")
    val paddingBottom: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingBottom)

    /** [`padding-left`/`padding-right`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding) */
    @get:JvmName("paddingHorizontal")
    val paddingHorizontal: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingLeft, layer.yogaStyle.paddingRight)
    /** [`padding-top`/`padding-bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding) */
    @get:JvmName("paddingVertical")
    val paddingVertical: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingTop, layer.yogaStyle.paddingBottom)
    /** [`padding`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding) */
    @get:JvmName("padding")
    val padding: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingLeft, layer.yogaStyle.paddingTop,
        layer.yogaStyle.paddingRight, layer.yogaStyle.paddingBottom)

    /** [`border-left`](https://yogalayout.com/docs/margins-paddings-borders) */
    fun borderLeft(border: Float) = build { borderLeft = border }
    /** [`border-top`](https://yogalayout.com/docs/margins-paddings-borders) */
    fun borderTop(border: Float) = build { borderTop = border }
    /** [`border-right`](https://yogalayout.com/docs/margins-paddings-borders) */
    fun borderRight(border: Float) = build { borderRight = border }
    /** [`border-bottom`](https://yogalayout.com/docs/margins-paddings-borders) */
    fun borderBottom(border: Float) = build { borderBottom = border }
    /** [`border-left`/`border-right`](https://yogalayout.com/docs/margins-paddings-borders) */
    fun borderHorizontal(border: Float) = build {
        borderLeft = border
        borderRight = border
    }
    /** [`border-top`/`border-bottom`](https://yogalayout.com/docs/margins-paddings-borders) */
    fun borderVertical(border: Float) = build {
        borderTop = border
        borderBottom = border
    }
    /** [`border`](https://yogalayout.com/docs/margins-paddings-borders) */
    fun border(border: Float) = build {
        borderLeft = border
        borderTop = border
        borderRight = border
        borderBottom = border
    }

    /** [`width`](https://developer.mozilla.org/en-US/docs/Web/CSS/width) */
    @get:JvmName("width")
    val width: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.width)
    /** [`height`](https://developer.mozilla.org/en-US/docs/Web/CSS/height) */
    @get:JvmName("height")
    val height: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.height)
    /** [`min-width`](https://developer.mozilla.org/en-US/docs/Web/CSS/min-width) */
    @get:JvmName("minWidth")
    val minWidth: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.minWidth)
    /** [`min-height`](https://developer.mozilla.org/en-US/docs/Web/CSS/min-height) */
    @get:JvmName("minHeight")
    val minHeight: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.minHeight)
    /** [`max-width`](https://developer.mozilla.org/en-US/docs/Web/CSS/max-width) */
    @get:JvmName("maxWidth")
    val maxWidth: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.maxWidth)
    /** [`max-height`](https://developer.mozilla.org/en-US/docs/Web/CSS/max-height) */
    @get:JvmName("maxHeight")
    val maxHeight: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.maxHeight)

    fun widthFromCurrent() = build { width.px = layer.widthf }
    fun heightFromCurrent() = build { height.px = layer.heightf }
    fun minWidthFromCurrent() = build { minWidth.px = layer.widthf }
    fun minHeightFromCurrent() = build { minHeight.px = layer.heightf }
    fun maxWidthFromCurrent() = build { maxWidth.px = layer.widthf }
    fun maxHeightFromCurrent() = build { maxHeight.px = layer.heightf }

    fun sizeFromCurrent() = build {
        width.px = layer.widthf
        height.px = layer.heightf
    }
    fun minSizeFromCurrent() = build {
        minWidth.px = layer.widthf
        minHeight.px = layer.heightf
    }
    fun maxSizeFromCurrent() = build {
        maxWidth.px = layer.widthf
        maxHeight.px = layer.heightf
    }

    fun lockWidth() = build {
        lockWidth = true
    }
    fun lockHeight() = build {
        lockHeight = true
    }
    fun lockSize() = build {
        lockWidth = true
        lockHeight = true
    }

    /** [Aspect Ratio](https://yogalayout.com/docs/aspect-ratio) */
    fun aspectRatio(ratio: Float) = build {
        aspectRatio = ratio
    }

    inner class AutoPropertyStyler internal constructor(private vararg val properties: YogaAutoProperty) {
        fun px(pixels: Float) = build {
            properties.forEach {
                it.px = pixels
            }
        }
        fun percent(percent: Float) = build {
            properties.forEach {
                it.percent = percent
            }
        }
        fun auto() = build {
            properties.forEach {
                it.auto()
            }
        }
    }

    inner class PercentagePropertyStyler internal constructor(private vararg val properties: YogaPercentageProperty) {
        fun px(pixels: Float) = build {
            properties.forEach {
                it.px = pixels
            }
        }
        fun percent(percent: Float) = build {
            properties.forEach {
                it.percent = percent
            }
        }
    }

    private inline fun build(block: YogaStyle.() -> Unit): YogaStyler {
        layer.yogaStyle.block()
        return this
    }
}

