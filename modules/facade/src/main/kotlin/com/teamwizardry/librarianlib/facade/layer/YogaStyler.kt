package com.teamwizardry.librarianlib.facade.layer

import org.lwjgl.util.yoga.Yoga.*

/**
 * A set of convenient builder-like methods for setting up layer styles. Distinct from [YogaStyle] in that that is
 * primarily focused on read/write symmetry and data access, while YogaStyler is focused on write-only access and ease
 * of configurability.
 */
public class YogaStyler(private val layer: GuiLayer) {

    /**
     * Sets the layout direction (used for RTL localization)
     */
    @get:JvmName("direction")
    public val direction: DirectionStyler = DirectionStyler()

    public inner class DirectionStyler internal constructor() {
        /**
         * Left-to-right
         */
        public fun ltr(): YogaStyler = build { direction = YGDirectionLTR }

        /**
         * Right-to-left
         */
        public fun rtl(): YogaStyler = build { direction = YGDirectionRTL }

        /**
         * Right-to-left
         */
        public fun inherit(): YogaStyler = build { direction = YGDirectionInherit }
    }

    /** [`flex-direction`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction) */
    @get:JvmName("flexDirection")
    public val flexDirection: FlexDirectionStyler = FlexDirectionStyler()

    public inner class FlexDirectionStyler internal constructor() {
        /** `flex-direction: row;` */
        public fun row(): YogaStyler = build { flexDirection = YGFlexDirectionRow }

        /** `flex-direction: row-reverse;` */
        public fun rowReverse(): YogaStyler = build { flexDirection = YGFlexDirectionRowReverse }

        /** `flex-direction: column;` */
        public fun column(): YogaStyler = build { flexDirection = YGFlexDirectionColumn }

        /** `flex-direction: column-reverse;` */
        public fun columnReverse(): YogaStyler = build { flexDirection = YGFlexDirectionColumnReverse }
    }

    /** [`justify-content`](https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content) */
    @get:JvmName("justifyContent")
    public val justifyContent: JustifyContentStyler = JustifyContentStyler()

    public inner class JustifyContentStyler internal constructor() {
        /** `justify-content: start;` */
        public fun start(): YogaStyler = build { justifyContent = YGJustifyFlexStart }

        /** `justify-content: center;` */
        public fun center(): YogaStyler = build { justifyContent = YGJustifyCenter }

        /** `justify-content: end;` */
        public fun end(): YogaStyler = build { justifyContent = YGJustifyFlexEnd }

        /** `justify-content: space-between;` */
        public fun spaceBetween(): YogaStyler = build { justifyContent = YGJustifySpaceBetween }

        /** `justify-content: space-around;` */
        public fun spaceAround(): YogaStyler = build { justifyContent = YGJustifySpaceAround }

        /** `justify-content: space-evenly;` */
        public fun spaceEvenly(): YogaStyler = build { justifyContent = YGJustifySpaceEvenly }
    }

    /** [`align-content`](https://developer.mozilla.org/en-US/docs/Web/CSS/align-content) */
    @get:JvmName("alignContent")
    public val alignContent: AlignContentStyler = AlignContentStyler()

    public inner class AlignContentStyler internal constructor() {
        /** `align-content: auto;` */
        public fun auto(): YogaStyler = build { alignContent = YGAlignAuto }

        /** `align-content: start;` */
        public fun start(): YogaStyler = build { alignContent = YGAlignFlexStart }

        /** `align-content: center;` */
        public fun center(): YogaStyler = build { alignContent = YGAlignCenter }

        /** `align-content: end;` */
        public fun end(): YogaStyler = build { alignContent = YGAlignFlexEnd }

        /** `align-content: stretch;` */
        public fun stretch(): YogaStyler = build { alignContent = YGAlignStretch }

        /** `align-content: baseline;` */
        public fun baseline(): YogaStyler = build { alignContent = YGAlignBaseline }

        /** `align-content: space-between;` */
        public fun spaceBetween(): YogaStyler = build { alignContent = YGAlignSpaceBetween }

        /** `align-content: space-around;` */
        public fun spaceAround(): YogaStyler = build { alignContent = YGAlignSpaceAround }
    }

    /** [`align-items`](https://developer.mozilla.org/en-US/docs/Web/CSS/align-items) */
    @get:JvmName("alignItems")
    public val alignItems: AlignItemsStyler = AlignItemsStyler()

    public inner class AlignItemsStyler internal constructor() {
        /** `align-items: auto;` */
        public fun auto(): YogaStyler = build { alignItems = YGAlignAuto }

        /** `align-items: start;` */
        public fun start(): YogaStyler = build { alignItems = YGAlignFlexStart }

        /** `align-items: center;` */
        public fun center(): YogaStyler = build { alignItems = YGAlignCenter }

        /** `align-items: end;` */
        public fun end(): YogaStyler = build { alignItems = YGAlignFlexEnd }

        /** `align-items: stretch;` */
        public fun stretch(): YogaStyler = build { alignItems = YGAlignStretch }

        /** `align-items: baseline;` */
        public fun baseline(): YogaStyler = build { alignItems = YGAlignBaseline }

        /** `align-items: space-between;` */
        public fun spaceBetween(): YogaStyler = build { alignItems = YGAlignSpaceBetween }

        /** `align-items: space-around;` */
        public fun spaceAround(): YogaStyler = build { alignItems = YGAlignSpaceAround }
    }

    /** [`align-self`](https://developer.mozilla.org/en-US/docs/Web/CSS/align-self) */
    @get:JvmName("alignSelf")
    public val alignSelf: AlignSelfStyler = AlignSelfStyler()

    public inner class AlignSelfStyler internal constructor() {
        /** `align-self: auto;` */
        public fun auto(): YogaStyler = build { alignSelf = YGAlignAuto }

        /** `align-self: start;` */
        public fun start(): YogaStyler = build { alignSelf = YGAlignFlexStart }

        /** `align-self: center;` */
        public fun center(): YogaStyler = build { alignSelf = YGAlignCenter }

        /** `align-self: end;` */
        public fun end(): YogaStyler = build { alignSelf = YGAlignFlexEnd }

        /** `align-self: stretch;` */
        public fun stretch(): YogaStyler = build { alignSelf = YGAlignStretch }

        /** `align-self: baseline;` */
        public fun baseline(): YogaStyler = build { alignSelf = YGAlignBaseline }

        /** `align-self: space-between;` */
        public fun spaceBetween(): YogaStyler = build { alignSelf = YGAlignSpaceBetween }

        /** `align-self: space-around;` */
        public fun spaceAround(): YogaStyler = build { alignSelf = YGAlignSpaceAround }
    }

    /** [`position`](https://developer.mozilla.org/en-US/docs/Web/CSS/position) */
    @get:JvmName("positionType")
    public val positionType: PositionTypeStyler = PositionTypeStyler()

    public inner class PositionTypeStyler internal constructor() {
        /** `position: absolute;` */
        public fun absolute(): YogaStyler = build { positionType = YGPositionTypeAbsolute }

        /** `position: relative;` */
        public fun relative(): YogaStyler = build { positionType = YGPositionTypeRelative }
    }

    /** [`flex-wrap`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-wrap) */
    @get:JvmName("flexWrap")
    public val flexWrap: FlexWrapStyler = FlexWrapStyler()

    public inner class FlexWrapStyler internal constructor() {
        /** `flex-wrap: nowrap;` */
        public fun nowrap(): YogaStyler = build { flexWrap = YGWrapNoWrap }

        /** `flex-wrap: wrap;` */
        public fun wrap(): YogaStyler = build { flexWrap = YGWrapWrap }

        /** `flex-wrap: wrap-reverse;` */
        public fun wrapReverse(): YogaStyler = build { flexWrap = YGWrapReverse }
    }

    /** [`overflow`](https://developer.mozilla.org/en-US/docs/Web/CSS/overflow) */
    @get:JvmName("overflow")
    public val overflow: OverflowStyler = OverflowStyler()

    public inner class OverflowStyler internal constructor() {
        /** `overflow: visible;` */
        public fun visible(): YogaStyler = build { overflow = YGOverflowVisible }

        /** `overflow: hidden;` */
        public fun hidden(): YogaStyler = build { overflow = YGOverflowHidden }

        /** `overflow: scroll;` */
        public fun scroll(): YogaStyler = build { overflow = YGOverflowScroll }
    }

    /** [`display`](https://developer.mozilla.org/en-US/docs/Web/CSS/display) */
    @get:JvmName("display")
    public val display: DisplayStyler = DisplayStyler()

    public inner class DisplayStyler internal constructor() {
        /** `display: flex;` */
        public fun flex(): YogaStyler = build { display = YGDisplayFlex }

        /** `display: none;` */
        public fun none(): YogaStyler = build { display = YGDisplayNone }
    }

    /** [`flex`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex) */
    public fun flex(grow: Float): YogaStyler = build {
        flexGrow = grow
        flexShrink = 1f
        flexBasis.px = 0f
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun flex(grow: Number): YogaStyler = flex(grow.toFloat())

    /** [`flex`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex) */
    public fun flex(grow: Float, shrink: Float): YogaStyler = build {
        flexGrow = grow
        flexShrink = shrink
        flexBasis.px = 0f
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun flex(grow: Number, shrink: Number): YogaStyler = flex(grow.toFloat(), shrink.toFloat())

    /** [`flex-grow`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow) */
    public fun flexGrow(grow: Float): YogaStyler = build {
        flexGrow = grow
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun flexGrow(grow: Number): YogaStyler = flexGrow(grow.toFloat())

    /** [`flex-shrink`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink) */
    public fun flexShrink(shrink: Float): YogaStyler = build {
        flexShrink = shrink
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun flexShrink(shrink: Number): YogaStyler = flexShrink(shrink.toFloat())

    /** [`flex-basis`](https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis) */
    @get:JvmName("flexBasis")
    public val flexBasis: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.flexBasis)

    /** [`left`](https://developer.mozilla.org/en-US/docs/Web/CSS/left) */
    @get:JvmName("left")
    public val left: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.left)

    /** [`top`](https://developer.mozilla.org/en-US/docs/Web/CSS/top) */
    @get:JvmName("top")
    public val top: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.top)

    /** [`right`](https://developer.mozilla.org/en-US/docs/Web/CSS/right) */
    @get:JvmName("right")
    public val right: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.right)

    /** [`bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/bottom) */
    @get:JvmName("bottom")
    public val bottom: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.bottom)

    /** [`margin-left`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin-left) */
    @get:JvmName("marginLeft")
    public val marginLeft: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginLeft)

    /** [`margin-top`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin-top) */
    @get:JvmName("marginTop")
    public val marginTop: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginTop)

    /** [`margin-right`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin-right) */
    @get:JvmName("marginRight")
    public val marginRight: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginRight)

    /** [`margin-bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin-bottom) */
    @get:JvmName("marginBottom")
    public val marginBottom: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginBottom)

    /** [`margin-left`/`margin-right`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin) */
    @get:JvmName("marginHorizontal")
    public val marginHorizontal: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginLeft, layer.yogaStyle.marginRight)

    /** [`margin-top`/`margin-bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin) */
    @get:JvmName("marginVertical")
    public val marginVertical: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginTop, layer.yogaStyle.marginBottom)

    /** [`margin`](https://developer.mozilla.org/en-US/docs/Web/CSS/margin) */
    @get:JvmName("margin")
    public val margin: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.marginLeft, layer.yogaStyle.marginTop,
        layer.yogaStyle.marginRight, layer.yogaStyle.marginBottom)

    /** [`padding-left`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding-left) */
    @get:JvmName("paddingLeft")
    public val paddingLeft: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingLeft)

    /** [`padding-top`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding-top) */
    @get:JvmName("paddingTop")
    public val paddingTop: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingTop)

    /** [`padding-right`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding-right) */
    @get:JvmName("paddingRight")
    public val paddingRight: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingRight)

    /** [`padding-bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding-bottom) */
    @get:JvmName("paddingBottom")
    public val paddingBottom: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingBottom)

    /** [`padding-left`/`padding-right`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding) */
    @get:JvmName("paddingHorizontal")
    public val paddingHorizontal: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingLeft, layer.yogaStyle.paddingRight)

    /** [`padding-top`/`padding-bottom`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding) */
    @get:JvmName("paddingVertical")
    public val paddingVertical: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingTop, layer.yogaStyle.paddingBottom)

    /** [`padding`](https://developer.mozilla.org/en-US/docs/Web/CSS/padding) */
    @get:JvmName("padding")
    public val padding: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.paddingLeft, layer.yogaStyle.paddingTop,
        layer.yogaStyle.paddingRight, layer.yogaStyle.paddingBottom)

    /** [`border-left`](https://yogalayout.com/docs/margins-paddings-borders) */
    public fun borderLeft(border: Float): YogaStyler = build { borderLeft = border }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun borderLeft(border: Number): YogaStyler = borderLeft(border.toFloat())

    /** [`border-top`](https://yogalayout.com/docs/margins-paddings-borders) */
    public fun borderTop(border: Float): YogaStyler = build { borderTop = border }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun borderTop(border: Number): YogaStyler = borderTop(border.toFloat())

    /** [`border-right`](https://yogalayout.com/docs/margins-paddings-borders) */
    public fun borderRight(border: Float): YogaStyler = build { borderRight = border }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun borderRight(border: Number): YogaStyler = borderRight(border.toFloat())

    /** [`border-bottom`](https://yogalayout.com/docs/margins-paddings-borders) */
    public fun borderBottom(border: Float): YogaStyler = build { borderBottom = border }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun borderBottom(border: Number): YogaStyler = borderBottom(border.toFloat())

    /** [`border-left`/`border-right`](https://yogalayout.com/docs/margins-paddings-borders) */
    public fun borderHorizontal(border: Float): YogaStyler = build {
        borderLeft = border
        borderRight = border
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun borderHorizontal(border: Number): YogaStyler = borderHorizontal(border.toFloat())

    /** [`border-top`/`border-bottom`](https://yogalayout.com/docs/margins-paddings-borders) */
    public fun borderVertical(border: Float): YogaStyler = build {
        borderTop = border
        borderBottom = border
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun borderVertical(border: Number): YogaStyler = borderVertical(border.toFloat())

    /** [`border`](https://yogalayout.com/docs/margins-paddings-borders) */
    public fun border(border: Float): YogaStyler = build {
        borderLeft = border
        borderTop = border
        borderRight = border
        borderBottom = border
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun border(border: Number): YogaStyler = border(border.toFloat())

    /** [`width`](https://developer.mozilla.org/en-US/docs/Web/CSS/width) */
    @get:JvmName("width")
    public val width: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.width)

    /** [`height`](https://developer.mozilla.org/en-US/docs/Web/CSS/height) */
    @get:JvmName("height")
    public val height: AutoPropertyStyler = AutoPropertyStyler(layer.yogaStyle.height)

    /** [`min-width`](https://developer.mozilla.org/en-US/docs/Web/CSS/min-width) */
    @get:JvmName("minWidth")
    public val minWidth: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.minWidth)

    /** [`min-height`](https://developer.mozilla.org/en-US/docs/Web/CSS/min-height) */
    @get:JvmName("minHeight")
    public val minHeight: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.minHeight)

    /** [`max-width`](https://developer.mozilla.org/en-US/docs/Web/CSS/max-width) */
    @get:JvmName("maxWidth")
    public val maxWidth: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.maxWidth)

    /** [`max-height`](https://developer.mozilla.org/en-US/docs/Web/CSS/max-height) */
    @get:JvmName("maxHeight")
    public val maxHeight: PercentagePropertyStyler = PercentagePropertyStyler(layer.yogaStyle.maxHeight)

    public fun widthFromCurrent(): YogaStyler = build { width.px = layer.widthf }
    public fun heightFromCurrent(): YogaStyler = build { height.px = layer.heightf }
    public fun minWidthFromCurrent(): YogaStyler = build { minWidth.px = layer.widthf }
    public fun minHeightFromCurrent(): YogaStyler = build { minHeight.px = layer.heightf }
    public fun maxWidthFromCurrent(): YogaStyler = build { maxWidth.px = layer.widthf }
    public fun maxHeightFromCurrent(): YogaStyler = build { maxHeight.px = layer.heightf }

    public fun sizeFromCurrent(): YogaStyler = build {
        width.px = layer.widthf
        height.px = layer.heightf
    }

    public fun minSizeFromCurrent(): YogaStyler = build {
        minWidth.px = layer.widthf
        minHeight.px = layer.heightf
    }

    public fun maxSizeFromCurrent(): YogaStyler = build {
        maxWidth.px = layer.widthf
        maxHeight.px = layer.heightf
    }

    public fun lockWidth(): YogaStyler = build {
        lockWidth = true
    }

    public fun lockHeight(): YogaStyler = build {
        lockHeight = true
    }

    public fun lockSize(): YogaStyler = build {
        lockWidth = true
        lockHeight = true
    }

    /** [Aspect Ratio](https://yogalayout.com/docs/aspect-ratio) */
    public fun aspectRatio(ratio: Float): YogaStyler = build {
        aspectRatio = ratio
    }

    @Suppress("NOTHING_TO_INLINE")
    @JvmSynthetic
    public inline fun aspectRatio(ratio: Number): YogaStyler = aspectRatio(ratio.toFloat())

    public inner class AutoPropertyStyler internal constructor(private vararg val properties: YogaAutoProperty) {
        public fun px(pixels: Float): YogaStyler = build {
            properties.forEach {
                it.px = pixels
            }
        }

        public fun percent(percent: Float): YogaStyler = build {
            properties.forEach {
                it.percent = percent
            }
        }

        public fun auto(): YogaStyler = build {
            properties.forEach {
                it.auto()
            }
        }

        @Suppress("NOTHING_TO_INLINE")
        @JvmSynthetic
        public inline fun px(pixels: Number): YogaStyler = px(pixels.toFloat())

        @Suppress("NOTHING_TO_INLINE")
        @JvmSynthetic
        public inline fun percent(percent: Number): YogaStyler = percent(percent.toFloat())
    }

    public inner class PercentagePropertyStyler internal constructor(private vararg val properties: YogaPercentageProperty) {
        public fun px(pixels: Float): YogaStyler = build {
            properties.forEach {
                it.px = pixels
            }
        }

        public fun percent(percent: Float): YogaStyler = build {
            properties.forEach {
                it.percent = percent
            }
        }

        @Suppress("NOTHING_TO_INLINE")
        @JvmSynthetic
        public inline fun px(pixels: Number): YogaStyler = px(pixels.toFloat())

        @Suppress("NOTHING_TO_INLINE")
        @JvmSynthetic
        public inline fun percent(percent: Number): YogaStyler = percent(percent.toFloat())
    }

    private inline fun build(block: YogaStyle.() -> Unit): YogaStyler {
        layer.yogaStyle.block()
        return this
    }
}

