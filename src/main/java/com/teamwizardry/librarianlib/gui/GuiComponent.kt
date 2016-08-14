package com.teamwizardry.librarianlib.gui

import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet

import com.teamwizardry.librarianlib.LibrarianLog
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager

import org.lwjgl.input.Keyboard

import com.teamwizardry.librarianlib.util.DefaultedMap
import com.teamwizardry.librarianlib.math.BoundingBox2D
import com.teamwizardry.librarianlib.math.Vec2d

/**
   * A component of a capability, such as a button, image, piece of text, list, etc.
  
   * @param  The class of this component. Used for setup()
 */
abstract class GuiComponent<T : GuiComponent<*>> @JvmOverloads  constructor(posX:Int, posY:Int, width:Int = 0, height:Int = 0):IGuiDrawable {

 val preDraw = HandlerList<IComponentDrawEventHandler<T>>()
 val postDraw = HandlerList<IComponentDrawEventHandler<T>>()
 val preChildrenDraw = HandlerList<IComponentDrawEventHandler<T>>()

 val mouseDown = HandlerList<IComponentMouseEventHandler<T>>()
 val mouseUp = HandlerList<IComponentMouseEventHandler<T>>()
 val mouseDrag = HandlerList<IComponentMouseEventHandler<T>>()
 val mouseClick = HandlerList<IComponentMouseEventHandler<T>>()

 val keyDown = HandlerList<IComponentKeyEventHandler<T>>()
 val keyUp = HandlerList<IComponentKeyEventHandler<T>>()

 val mouseIn = HandlerList<IComponentCoordEventHandler<T>>()
 val mouseOut = HandlerList<IComponentCoordEventHandler<T>>()
 val mouseWheel = HandlerList<IComponentMouseWheelEventHandler<T>>()

 val focus = HandlerList<IComponentVoidEventHandler<T>>()
 val blur = HandlerList<IComponentVoidEventHandler<T>>()

 val enable = HandlerList<IComponentVoidEventHandler<T>>()
 val disable = HandlerList<IComponentVoidEventHandler<T>>()

 val addChild = HandlerList<IComponentChildEventHandler<T>>()
 val removeChild = HandlerList<IComponentChildEventHandler<T>>()
 val matchesTag = HandlerList<IComponentTagEventHandler<T>>()
 val addTag = HandlerList<IComponentTagEventHandler<T>>()
 val removeTag = HandlerList<IComponentTagEventHandler<T>>()

 val logicalSize = HandlerList<IComponentLogicalSizeEventHandler<T>>()
 val transformChildPos = HandlerList<IComponentChildCoordTransformEventHandler<T>>()

 var zIndex = 0
/**
	   * Get the position of the component relative to it's parent
	  */
	/**
	   * Set the position of the component relative to it's parent
	  */
	 var pos:Vec2d
/**
	   * Get the size of the component
	  */
	/**
	   * Set the size of the component
	  */
	 var size:Vec2d

 var mouseOverThisFrame = false
 var mousePosThisFrame = Vec2d.ZERO
protected var tags:MutableSet<Any> = HashSet<Any>()

 var animationTicks = 0
private var guiTicksLastFrame = TickCounter.ticks

protected var enabled = true
/**
	   * Whether this component should be drawn or have events fire
	  */
	/**
	   * Set whether this component should be drawn or have events fire
	  */
	 var isVisible = true
protected var focused = false
/**
	   * Returns true if this component is invalid and it should be removed from it's parent
	   * @return
 */
	 var isInvalid = false
protected set
 var isAnimating = true

protected var mouseButtonsDown = BooleanArray(EnumMouseButton.values().size)
protected var keysDown:MutableMap<Key, Boolean> = DefaultedMap<Key, Boolean>(false)
private val data = HashMap<Class<*>, Map<String, Any>>()

 var tooltipText:List<String>
 var tooltipFont:FontRenderer? = null

 /* subcomponent stuff */
	protected var calculateOwnHover = true
protected var components:MutableList<GuiComponent<*>> = ArrayList<GuiComponent<*>>()
/**
	   * Get the parent component
	  */
	/**
	   * Set the parent component
	  */
	 var parent:GuiComponent<*>? = null

init{
this.pos = Vec2d(posX.toDouble(), posY.toDouble())
this.size = Vec2d(width.toDouble(), height.toDouble())
}

/**
	   * Draws the component, this is called between pre and post draw events
	  */
	abstract fun drawComponent(mousePos:Vec2d, partialTicks:Float) 

/**
	   * Setup the component without making temporary variables
	  */
	 fun setup(setup:IComponentSetup<T>):T {
setup.setup(thiz())
return thiz()
}

/**
	   * Transforms the position passed to be relative to this component's position.
	  */
	open fun relativePos(pos:Vec2d):Vec2d {
return pos.sub(this.pos)
}

/**
	   * Transforms the position passed to be relative to the root component's position.
	  */
	 fun rootPos(pos:Vec2d):Vec2d {
if (parent == null)
return pos.add(this.pos)
else
return parent!!.rootPos(pos.add(this.pos))
}

 //=============================================================================
	init{/* Children */}
 //=============================================================================
	
	/**
	   * Adds a child to this component.
	  
	   * @throws IllegalArgumentException if the component had a parent already
 */
	 fun add(component:GuiComponent<*>?) {
if (component == null)
{
LibrarianLog.I.warn("You shouldn't be addinng null components!")
return 
}
if (component === this)
throw IllegalArgumentException("Can't add components recursivly!")
if (component!!.parent != null)
throw IllegalArgumentException("Component already had a parent!")
components.add(component)
component!!.parent = this
Collections.sort<GuiComponent<*>>(components, { a, b-> Integer.compare(a.zIndex, b.zIndex) })
addChild.fireAll({ h-> h.handle(thiz(), component) })
}

/**
	   * Removes the supplied component
	   * @param component
 */
	 fun remove(component:GuiComponent<*>) {
component.parent = null
removeChild.fireAll({ h-> h.handle(thiz(), component) })
components.remove(component)
}

/**
	   * Removes all components that have the supplied tag
	  */
	 fun removeByTag(tag:Any) {
components.removeIf({ e->
val b = e.hasTag(tag)
if (b)
{
e.parent = null
removeChild.fireAll({ h-> h.handle(thiz(), e) })
}
b })
}

/**
	   * Returns all the elements with a given tag
	  */
	 fun getByTag(tag:Any):List<GuiComponent<*>> {
val list = ArrayList<GuiComponent<*>>()
for (component in components)
{
if (component.hasTag(tag))
list.add(component)
}
return list
}

/**
	   * Returns all the elements that can be cast to the specified class
	  */
	 fun <C> getByClass(klass:Class<C>):List<C> {
val list = ArrayList<C>()
for (component in components)
{
if (klass.isAssignableFrom(component.javaClass))
list.add(component as C)
}
return list
}

 //=============================================================================
	init{/* Events/checks */}
 //=============================================================================
	
	/**
	   * Allows the component to modify the position before it is passed to a child element.
	  */
	open fun transformChildPos(child:GuiComponent<*>, pos:Vec2d):Vec2d {
var pos = pos
pos = child.relativePos(pos)
return transformChildPos.fireModifier<Vec2d>(pos, { h, v-> h.handle(thiz(), child, v) })
}

/**
	   * Test if the mouse is over this component. mousePos is relative to the position of the element.
	  */
	 fun isMouseOver(mousePos:Vec2d):Boolean {
if (!isVisible) return false
var hover = false
if (shouldCalculateOwnHover())
hover = hover || mousePos.x >= 0 && mousePos.x < size.x && mousePos.y >= 0 && mousePos.y < size.y

for (child in components)
{
hover = child.isMouseOver(transformChildPos(child, mousePos)) || hover
}
return hover
}

/**
	   * Draw this component, don't override in subclasses unless you know what you're doing.
	   * @param mousePos Mouse position relative to the position of this component
	  * * 
 * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
 */
	public override fun draw(mousePos:Vec2d, partialTicks:Float) {
if (!isVisible) return 

if (isAnimating)
{
animationTicks += TickCounter.ticks - guiTicksLastFrame
guiTicksLastFrame = TickCounter.ticks
}

val wasMouseOverLastFrame = mouseOverThisFrame
mouseOverThisFrame = isMouseOver(mousePos)
mousePosThisFrame = mousePos

if (wasMouseOverLastFrame && !mouseOverThisFrame)
mouseOut.fireAll({ e-> e.handle(thiz(), mousePos) })
if (!wasMouseOverLastFrame && mouseOverThisFrame)
mouseIn.fireAll({ e-> e.handle(thiz(), mousePos) })

preDraw.fireAll({ e-> e.handle(thiz(), mousePos, partialTicks) })

drawComponent(mousePos, partialTicks)

GlStateManager.pushMatrix()
GlStateManager.pushAttrib()
GlStateManager.translate(pos.x, pos.y, 0.0)

val remove = ArrayList<GuiComponent<*>>()
for (component in components)
{
if (component.isInvalid)
{
remove.add(component)
continue
}
component.draw(transformChildPos(component, mousePos), partialTicks)
}

preChildrenDraw.fireAll({ e-> e.handle(thiz(), mousePos, partialTicks) })

for (rem in remove)
{
rem.parent = null
removeChild.fireAll({ h-> h.handle(thiz(), rem) })
}
components.removeAll(remove)

GlStateManager.popAttrib()
GlStateManager.popMatrix()

postDraw.fireAll({ e-> e.handle(thiz(), mousePos, partialTicks) })
}

/**
	   * Called when the mouse is pressed. mousePos is relative to the position of this component.
	   * @param mousePos
	  * * 
 * @param button
 */
	open fun mouseDown(mousePos:Vec2d, button:EnumMouseButton) {
if (!isVisible) return 
if (mouseDown.fireCancel({ e-> e.handle(thiz(), mousePos, button) }))
return 

if (isMouseOver(mousePos))
mouseButtonsDown[button.ordinal] = true

for (child in components)
{
child.mouseDown(transformChildPos(child, mousePos), button)
}
}

/**
	   * Called when the mouse is released. mousePos is relative to the position of this component.
	   * @param mousePos
	  * * 
 * @param button
 */
	open fun mouseUp(mousePos:Vec2d, button:EnumMouseButton) {
if (!isVisible) return 
val wasDown = mouseButtonsDown[button.ordinal]
mouseButtonsDown[button.ordinal] = false

if (mouseUp.fireCancel({ e-> e.handle(thiz(), mousePos, button) }))
return 

if (isMouseOver(mousePos) && wasDown)
{
mouseClick.fireCancel({ e-> e.handle(thiz(), mousePos, button) }) // don't return here, if a click was handled we should still handle the mouseUp
}

for (child in components)
{
child.mouseUp(transformChildPos(child, mousePos), button)
}
}

/**
	   * Called when the mouse is moved while pressed. mousePos is relative to the position of this component.
	   * @param mousePos
	  * * 
 * @param button
 */
	 fun mouseDrag(mousePos:Vec2d, button:EnumMouseButton) {
if (!isVisible) return 
if (mouseDrag.fireCancel({ e-> e.handle(thiz(), mousePos, button) }))
return 

for (child in components)
{
child.mouseDrag(transformChildPos(child, mousePos), button)
}
}

/**
	   * Called when the mouse wheel is moved.
	   * @param mousePos
 */
	open fun mouseWheel(mousePos:Vec2d, direction:Int) {
if (!isVisible) return 
if (mouseWheel.fireCancel({ e-> e.handle(thiz(), mousePos, direction) }))
return 

for (child in components)
{
child.mouseWheel(transformChildPos(child, mousePos), direction)
}
}

/**
	   * Called when a key is pressed in the parent component.
	   * @param key The actual character that was pressed
	  * * 
 * @param keyCode The key code, codes listed in [Keyboard]
 */
	 fun keyPressed(key:Char, keyCode:Int) {
if (!isVisible) return 
if (keyDown.fireCancel({ e-> e.handle(thiz(), key, keyCode) }))
return 

keysDown.put(Key.get(key, keyCode), true)

for (child in components)
{
child.keyPressed(key, keyCode)
}
}

/**
	   * Called when a key is released in the parent component.
	   * @param key The actual key that was pressed
	  * * 
 * @param keyCode The key code, codes listed in [Keyboard]
 */
	 fun keyReleased(key:Char, keyCode:Int) {
if (!isVisible) return 
keysDown.put(Key.get(key, keyCode), false) // do this before so we don't have lingering keyDown entries

if (keyUp.fireCancel({ e-> e.handle(thiz(), key, keyCode) }))
return 

for (child in components)
{
child.keyReleased(key, keyCode)
}
}

 //=============================================================================
	init{/* Utils, getters/setters */}
 //=============================================================================
	
	/**
	   * Returns `this` casted to `T`. Used to avoid unchecked cast warnings everywhere.
	  */
	@SuppressWarnings("unchecked")
 fun thiz():T {
return this as T
}

/**
	   * The size of the component for layout. Often dynamically calculated
	  */
	open fun getLogicalSize():BoundingBox2D {
var aabb = contentSize

for (child in components)
{
if (!child.isVisible) continue
val childAABB = child.getLogicalSize()
aabb = aabb.union(childAABB)
}

aabb = BoundingBox2D(aabb.min.add(pos), aabb.max.add(pos))

return logicalSize.fireModifier<BoundingBox2D>(aabb, { t, v-> t.handle(v, thiz()) })
}

/**
	   * Gets the size of the content of this component.
	  */
	protected val contentSize:BoundingBox2D
get() {
return BoundingBox2D(Vec2d.ZERO, size)
}

/**
	   * Gets the root component
	  */
	 val root:GuiComponent<*>
get() {
if (parent != null)
return parent!!.root
return this
}

/**
	   * Sets the tooltip to be drawn, overriding the existing value. Pass null for the font to use the default font renderer.
	  */
	 fun setTooltip(text:List<String>, font:FontRenderer) {
val component = root
component.tooltipText = text
component.tooltipFont = font
}

/**
	   * Sets the tooltip to be drawn, overriding the existing value and using the default font renderer.
	  */
	 fun setTooltip(text:List<String>) {
val component = root
component.tooltipText = text
component.tooltipFont = null
}

/**
	   * Set whether the element should calculate hovering based on it's bounds as
	   * well as it's children or if it should only calculate based on it's children.
	  */
	 fun setCalculateOwnHover(calculateOwnHover:Boolean) {
this.calculateOwnHover = calculateOwnHover
}

/**
	   * Whether to calculate hovering based on this component's bounds and its
	   * children or only this element's children
	  */
	 fun shouldCalculateOwnHover():Boolean {
return calculateOwnHover
}

 //=============================================================================
	init{/* Assorted info */}
 //=============================================================================
	
	 fun <D> setData(klass:Class<D>, key:String, value:D) {
if (!data.containsKey(klass))
data.put(klass, HashMap<String, Any>())
data.get(klass).put(key, value)
}

@SuppressWarnings("unchecked")
 fun <D> getData(klass:Class<D>, key:String):D {
if (!data.containsKey(klass))
data.put(klass, HashMap<String, Any>())
return data.get(klass).get(key) as D
}

/**
	   * Adds the passed tag to this component if it doesn't already have it. Tags are not case sensitive
	  
	   * @return true if the tag didn't exist and was added
 */
	 fun addTag(tag:Any):Boolean {
if (tags.add(tag))
{
addTag.fireAll({ h-> h.handle(thiz(), tag) })
return true
}
return false
}

/**
	   * Removes the passed tag to this component if it doesn't already have it. Tags are not case sensitive
	  
	   * @return true if the tag existed and was removed
 */
	 fun removeTag(tag:Any):Boolean {
if (tags.remove(tag))
{
removeTag.fireAll({ h-> h.handle(thiz(), tag) })
return true
}
return false
}

/**
	   * Checks if the component has the tag specified. Tags are not case sensitive
	  */
	 fun hasTag(tag:Any):Boolean {
return tags.contains(tag)
}

/**
	   * Returns an unmodifiable wrapper of the tag set. Tags are all stored in lowercase
	  */
	 fun getTags():Set<Any> {
return Collections.unmodifiableSet<Any>(tags)
}

 var isFocused:Boolean
get() {
return focused
}
set(focused) {
if (this.focused != focused)
{
if (focused)
focus.fireAll({ h-> h.handle(thiz()) })
else
blur.fireAll({ h-> h.handle(thiz()) })
}
this.focused = focused
}

 var isEnabled:Boolean
get() {
return enabled
}
set(enabled) {
if (this.enabled != enabled)
{
if (enabled)
enable.fireAll({ h-> h.handle(thiz()) })
else
disable.fireAll({ h-> h.handle(thiz()) })
}
this.enabled = enabled
}

/**
	   * Set this component invalid so it will be removed from it's parent element
	  */
	 fun invalidate() {
this.isInvalid = true
}

 fun resetAnimation() {
animationTicks = 0
}

 //=============================================================================
	init{/* Event interfaces */}
 //=============================================================================
	
	@FunctionalInterface
 interface IComponentSetup<T : GuiComponent<*>> {
 fun setup(component:T) 
}

@FunctionalInterface
 interface IComponentDrawEventHandler<T : GuiComponent<*>> {
 fun handle(component:T, mousePos:Vec2d, partialTicks:Float) 
}

@FunctionalInterface
 interface IComponentMouseEventHandler<T : GuiComponent<*>> {
 fun handle(component:T, pos:Vec2d, button:EnumMouseButton):Boolean 
}

@FunctionalInterface
 interface IComponentMouseWheelEventHandler<T : GuiComponent<*>> {
 fun handle(component:T, pos:Vec2d, direction:Int):Boolean 
}

@FunctionalInterface
 interface IComponentKeyEventHandler<T : GuiComponent<*>> {
 fun handle(component:T, character:Char, code:Int):Boolean 
}

@FunctionalInterface
 interface IComponentCoordEventHandler<T : GuiComponent<*>> {
 fun handle(component:T, mousePos:Vec2d):Boolean 
}

@FunctionalInterface
 interface IComponentChildCoordTransformEventHandler<T : GuiComponent<*>> {
 fun handle(component:T, child:GuiComponent<*>, mousePos:Vec2d):Vec2d 
}

@FunctionalInterface
 interface IComponentChildEventHandler<T : GuiComponent<*>> {
 fun handle(component:T, other:GuiComponent<*>):Boolean 
}

@FunctionalInterface
 interface IComponentTagEventHandler<T : GuiComponent<*>> {
 fun handle(component:T, tag:Any):Boolean 
}

@FunctionalInterface
 interface IComponentVoidEventHandler<T : GuiComponent<*>> {
 fun handle(component:T) 
}

@FunctionalInterface
 interface IComponentLogicalSizeEventHandler<T : GuiComponent<*>> {
 fun handle(size:BoundingBox2D, component:T):BoundingBox2D 
}
}
