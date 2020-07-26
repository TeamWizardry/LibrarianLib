package com.teamwizardry.librarianlib.facade.layer

import org.lwjgl.util.yoga.YGValue
import org.lwjgl.util.yoga.Yoga.*

// much boilerplate and reference was yoinked from this project:
// https://github.com/Wieku/danser/blob/d60ef6f/framework/src/main/kotlin/me/wieku/framework/graphics/drawables/containers/YogaContainer.kt
class YogaStyle(private val yogaNode: Long) {

    var direction: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetDirection, ::YGNodeStyleSetDirection)
    var flexDirection: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetFlexDirection, ::YGNodeStyleSetFlexDirection)
    var justifyContent: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetJustifyContent, ::YGNodeStyleSetJustifyContent)
    var alignContent: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetAlignContent, ::YGNodeStyleSetAlignContent)
    var alignItems: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetAlignItems, ::YGNodeStyleSetAlignItems)
    var alignSelf: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetAlignSelf, ::YGNodeStyleSetAlignSelf)
    var positionType: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetPositionType, ::YGNodeStyleSetPositionType)
    var flexWrap: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetFlexWrap, ::YGNodeStyleSetFlexWrap)
    var overflow: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetOverflow, ::YGNodeStyleSetOverflow)
    var display: Int by YogaEnumProperty(yogaNode, ::YGNodeStyleGetDisplay, ::YGNodeStyleSetDisplay)

    var flex: Float by YogaFloatProperty(yogaNode, ::YGNodeStyleGetFlex, ::YGNodeStyleSetFlex)
    var flexGrow: Float by YogaFloatProperty(yogaNode, ::YGNodeStyleGetFlexGrow, ::YGNodeStyleSetFlexGrow)
    var flexShrink: Float by YogaFloatProperty(yogaNode, ::YGNodeStyleGetFlexShrink, ::YGNodeStyleSetFlexShrink)

    val flexBasis: YogaAutoProperty = YogaAutoProperty(yogaNode, ::YGNodeStyleGetFlexBasis,
        ::YGNodeStyleSetFlexBasis, ::YGNodeStyleSetFlexBasisPercent, ::YGNodeStyleSetFlexBasisAuto)

    private fun setWithEdge(edge: Int, f: (Long, Int, Float) -> Unit): (Long, Float) -> Unit = { node, value -> f(node, edge, value) }
    private fun getWithEdge(edge: Int, f: (Long, Int, YGValue) -> YGValue): (Long, YGValue) -> YGValue = { node, value -> f(node, edge, value) }
    private fun autoWithEdge(edge: Int, f: (Long, Int) -> Unit): (Long) -> Unit = { node -> f(node, edge) }

    val left: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeLeft, ::YGNodeStyleGetPosition),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetPosition),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetPositionPercent)
    )
    val top: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeTop, ::YGNodeStyleGetPosition),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetPosition),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetPositionPercent)
    )
    val right: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeRight, ::YGNodeStyleGetPosition),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetPosition),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetPositionPercent)
    )
    val bottom: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeBottom, ::YGNodeStyleGetPosition),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetPosition),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetPositionPercent)
    )


    val marginLeft: YogaAutoProperty = YogaAutoProperty(yogaNode,
        getWithEdge(YGEdgeLeft, ::YGNodeStyleGetMargin),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetMargin),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetMarginPercent),
        autoWithEdge(YGEdgeLeft, ::YGNodeStyleSetMarginAuto)
    )
    val marginTop: YogaAutoProperty = YogaAutoProperty(yogaNode,
        getWithEdge(YGEdgeTop, ::YGNodeStyleGetMargin),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetMargin),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetMarginPercent),
        autoWithEdge(YGEdgeTop, ::YGNodeStyleSetMarginAuto)
    )
    val marginRight: YogaAutoProperty = YogaAutoProperty(yogaNode,
        getWithEdge(YGEdgeRight, ::YGNodeStyleGetMargin),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetMargin),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetMarginPercent),
        autoWithEdge(YGEdgeRight, ::YGNodeStyleSetMarginAuto)
    )
    val marginBottom: YogaAutoProperty = YogaAutoProperty(yogaNode,
        getWithEdge(YGEdgeBottom, ::YGNodeStyleGetMargin),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetMargin),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetMarginPercent),
        autoWithEdge(YGEdgeBottom, ::YGNodeStyleSetMarginAuto)
    )

    val paddingLeft: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeLeft, ::YGNodeStyleGetPadding),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetPadding),
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetPaddingPercent)
    )
    val paddingTop: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeTop, ::YGNodeStyleGetPadding),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetPadding),
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetPaddingPercent)
    )
    val paddingRight: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeRight, ::YGNodeStyleGetPadding),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetPadding),
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetPaddingPercent)
    )
    val paddingBottom: YogaPercentageProperty = YogaPercentageProperty(yogaNode,
        getWithEdge(YGEdgeBottom, ::YGNodeStyleGetPadding),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetPadding),
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetPaddingPercent)
    )

    var borderLeft by YogaFloatProperty(yogaNode,
        { node -> YGNodeStyleGetBorder(node, YGEdgeLeft) },
        setWithEdge(YGEdgeLeft, ::YGNodeStyleSetBorder)
    )
    var borderTop by YogaFloatProperty(yogaNode,
        { node -> YGNodeStyleGetBorder(node, YGEdgeTop) },
        setWithEdge(YGEdgeTop, ::YGNodeStyleSetBorder)
    )
    var borderRight by YogaFloatProperty(yogaNode,
        { node -> YGNodeStyleGetBorder(node, YGEdgeRight) },
        setWithEdge(YGEdgeRight, ::YGNodeStyleSetBorder)
    )
    var borderBottom by YogaFloatProperty(yogaNode,
        { node -> YGNodeStyleGetBorder(node, YGEdgeBottom) },
        setWithEdge(YGEdgeBottom, ::YGNodeStyleSetBorder)
    )

    /**
     * When true, this layer's width will be locked by setting `minWidth = width = maxWidth = layer.width` before each
     * frame
     */
    var lockWidth: Boolean = false
    /**
     * When true, this layer's height will be locked by setting `minHeight = height = maxHeight = layer.height` before
     * each frame
     */
    var lockHeight: Boolean = false

    val width: YogaAutoProperty = YogaAutoProperty(yogaNode, ::YGNodeStyleGetWidth,
        ::YGNodeStyleSetWidth, ::YGNodeStyleSetWidthPercent, ::YGNodeStyleSetWidthAuto)
    val height: YogaAutoProperty = YogaAutoProperty(yogaNode, ::YGNodeStyleGetHeight,
        ::YGNodeStyleSetHeight, ::YGNodeStyleSetHeightPercent, ::YGNodeStyleSetHeightAuto)

    val minWidth: YogaPercentageProperty = YogaPercentageProperty(yogaNode, ::YGNodeStyleGetMinWidth,
        ::YGNodeStyleSetMinWidth, ::YGNodeStyleSetMinWidthPercent)
    val minHeight: YogaPercentageProperty = YogaPercentageProperty(yogaNode, ::YGNodeStyleGetMinHeight,
        ::YGNodeStyleSetMinHeight, ::YGNodeStyleSetMinHeightPercent)
    val maxWidth: YogaPercentageProperty = YogaPercentageProperty(yogaNode, ::YGNodeStyleGetMaxWidth,
        ::YGNodeStyleSetMaxWidth, ::YGNodeStyleSetMaxWidthPercent)
    val maxHeight: YogaPercentageProperty = YogaPercentageProperty(yogaNode, ::YGNodeStyleGetMaxHeight,
        ::YGNodeStyleSetMaxHeight, ::YGNodeStyleSetMaxHeightPercent)

    var aspectRatio: Float by YogaFloatProperty(yogaNode, ::YGNodeStyleGetAspectRatio, ::YGNodeStyleSetAspectRatio)
    //endregion
}