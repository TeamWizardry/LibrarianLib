package com.teamwizardry.librarianlib.facade.layer

import org.lwjgl.util.yoga.YGValue
import org.lwjgl.util.yoga.Yoga.*

// much boilerplate and reference was yoinked from this project:
// https://github.com/Wieku/danser/blob/d60ef6f/framework/src/main/kotlin/me/wieku/framework/graphics/drawables/containers/YogaContainer.kt
public class YogaStyle(private val yogaNode: Long) {

    public var direction: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetDirection, ::YGNodeStyleSetDirection)
    public var flexDirection: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetFlexDirection, ::YGNodeStyleSetFlexDirection)
    public var justifyContent: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetJustifyContent, ::YGNodeStyleSetJustifyContent)
    public var alignContent: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetAlignContent, ::YGNodeStyleSetAlignContent)
    public var alignItems: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetAlignItems, ::YGNodeStyleSetAlignItems)
    public var alignSelf: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetAlignSelf, ::YGNodeStyleSetAlignSelf)
    public var positionType: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetPositionType, ::YGNodeStyleSetPositionType)
    public var flexWrap: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetFlexWrap, ::YGNodeStyleSetFlexWrap)
    public var overflow: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetOverflow, ::YGNodeStyleSetOverflow)
    public var display: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetDisplay, ::YGNodeStyleSetDisplay)

    public var flex: Float by YogaFloatProperty(yogaNode, ::YGNodeStyleGetFlex, ::YGNodeStyleSetFlex)
    public var flexGrow: Float by YogaFloatProperty(yogaNode, ::YGNodeStyleGetFlexGrow, ::YGNodeStyleSetFlexGrow)
    public var flexShrink: Float by YogaFloatProperty(yogaNode, ::YGNodeStyleGetFlexShrink, ::YGNodeStyleSetFlexShrink)

    public val flexBasis: YogaAutoProperty = YogaAutoProperty(yogaNode, ::YGNodeStyleGetFlexBasis,
        ::YGNodeStyleSetFlexBasis, ::YGNodeStyleSetFlexBasisPercent, ::YGNodeStyleSetFlexBasisAuto)

    private fun setWithEdge(edge: Int, f: (Long, Int, Float) -> Unit): (Long, Float) -> Unit = { node, value -> f(node, edge, value) }
    private fun getWithEdge(edge: Int, f: (Long, Int, YGValue) -> YGValue): (Long, YGValue) -> YGValue = { node, value -> f(node, edge, value) }
    private fun autoWithEdge(edge: Int, f: (Long, Int) -> Unit): (Long) -> Unit = { node -> f(node, edge) }

    public val left: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeLeft, ::YGNodeStyleGetPosition),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetPosition),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetPositionPercent)
    )
    public val top: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeTop, ::YGNodeStyleGetPosition),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetPosition),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetPositionPercent)
    )
    public val right: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeRight, ::YGNodeStyleGetPosition),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetPosition),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetPositionPercent)
    )
    public val bottom: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeBottom, ::YGNodeStyleGetPosition),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetPosition),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetPositionPercent)
    )

    public val marginLeft: YogaAutoProperty = YogaAutoProperty(yogaNode,
        getWithEdge(YGEdgeLeft, ::YGNodeStyleGetMargin),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetMargin),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetMarginPercent),
        autoWithEdge(YGEdgeLeft, ::YGNodeStyleSetMarginAuto)
    )
    public val marginTop: YogaAutoProperty = YogaAutoProperty(yogaNode,
        getWithEdge(YGEdgeTop, ::YGNodeStyleGetMargin),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetMargin),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetMarginPercent),
        autoWithEdge(YGEdgeTop, ::YGNodeStyleSetMarginAuto)
    )
    public val marginRight: YogaAutoProperty = YogaAutoProperty(yogaNode,
        getWithEdge(YGEdgeRight, ::YGNodeStyleGetMargin),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetMargin),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetMarginPercent),
        autoWithEdge(YGEdgeRight, ::YGNodeStyleSetMarginAuto)
    )
    public val marginBottom: YogaAutoProperty = YogaAutoProperty(yogaNode,
        getWithEdge(YGEdgeBottom, ::YGNodeStyleGetMargin),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetMargin),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetMarginPercent),
        autoWithEdge(YGEdgeBottom, ::YGNodeStyleSetMarginAuto)
    )

    public val paddingLeft: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeLeft, ::YGNodeStyleGetPadding),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetPadding),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetPaddingPercent)
    )
    public val paddingTop: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeTop, ::YGNodeStyleGetPadding),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetPadding),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetPaddingPercent)
    )
    public val paddingRight: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeRight, ::YGNodeStyleGetPadding),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetPadding),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetPaddingPercent)
    )
    public val paddingBottom: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeBottom, ::YGNodeStyleGetPadding),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetPadding),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetPaddingPercent)
    )

    public var borderLeft: Float by YogaFloatProperty(yogaNode,
        { node -> YGNodeStyleGetBorder(node, YGEdgeLeft) },
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetBorder)
    )
    public var borderTop: Float by YogaFloatProperty(yogaNode,
        { node -> YGNodeStyleGetBorder(node, YGEdgeTop) },
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetBorder)
    )
    public var borderRight: Float by YogaFloatProperty(yogaNode,
        { node -> YGNodeStyleGetBorder(node, YGEdgeRight) },
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetBorder)
    )
    public var borderBottom: Float by YogaFloatProperty(yogaNode,
        { node -> YGNodeStyleGetBorder(node, YGEdgeBottom) },
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetBorder)
    )

    /**
     * When true, this layer's width will be locked by setting `minWidth = width = maxWidth = layer.width` before each
     * frame
     */
    public var lockWidth: Boolean = false

    /**
     * When true, this layer's height will be locked by setting `minHeight = height = maxHeight = layer.height` before
     * each frame
     */
    public var lockHeight: Boolean = false

    public val width: YogaAutoProperty = YogaAutoProperty(yogaNode, ::YGNodeStyleGetWidth,
        ::YGNodeStyleSetWidth, ::YGNodeStyleSetWidthPercent, ::YGNodeStyleSetWidthAuto)
    public val height: YogaAutoProperty = YogaAutoProperty(yogaNode, ::YGNodeStyleGetHeight,
        ::YGNodeStyleSetHeight, ::YGNodeStyleSetHeightPercent, ::YGNodeStyleSetHeightAuto)

    public val minWidth: YogaPercentageProperty = YogaPercentageProperty(yogaNode, ::YGNodeStyleGetMinWidth,
        ::YGNodeStyleSetMinWidth, ::YGNodeStyleSetMinWidthPercent)
    public val minHeight: YogaPercentageProperty = YogaPercentageProperty(yogaNode, ::YGNodeStyleGetMinHeight,
        ::YGNodeStyleSetMinHeight, ::YGNodeStyleSetMinHeightPercent)
    public val maxWidth: YogaPercentageProperty = YogaPercentageProperty(yogaNode, ::YGNodeStyleGetMaxWidth,
        ::YGNodeStyleSetMaxWidth, ::YGNodeStyleSetMaxWidthPercent)
    public val maxHeight: YogaPercentageProperty = YogaPercentageProperty(yogaNode, ::YGNodeStyleGetMaxHeight,
        ::YGNodeStyleSetMaxHeight, ::YGNodeStyleSetMaxHeightPercent)

    public var aspectRatio: Float by YogaFloatProperty(yogaNode, ::YGNodeStyleGetAspectRatio, ::YGNodeStyleSetAspectRatio)
    //endregion
}