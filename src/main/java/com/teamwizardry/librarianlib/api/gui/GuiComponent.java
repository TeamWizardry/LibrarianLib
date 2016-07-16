package com.teamwizardry.librarianlib.api.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.client.renderer.GlStateManager;

import org.lwjgl.input.Keyboard;

import com.teamwizardry.librarianlib.api.util.misc.DefaultedMap;
import com.teamwizardry.librarianlib.math.BoundingBox2D;
import com.teamwizardry.librarianlib.math.Vec2;

/**
 * A component of a capability, such as a button, image, piece of text, list, etc.
 * 
 * @param <T> The class of this component. Used for setup()
 */
public abstract class GuiComponent<T extends GuiComponent<?>> implements IGuiDrawable {
	
	public final HandlerList<IComponentDrawEventHandler<T>> preDraw = new HandlerList<>();
	public final HandlerList<IComponentDrawEventHandler<T>> postDraw = new HandlerList<>();
	
	public final HandlerList<IComponentMouseEventHandler<T>> mouseDown = new HandlerList<>();
	public final HandlerList<IComponentMouseEventHandler<T>> mouseUp = new HandlerList<>();
	public final HandlerList<IComponentMouseEventHandler<T>> mouseDrag = new HandlerList<>();
	public final HandlerList<IComponentMouseEventHandler<T>> mouseClick = new HandlerList<>();
	
	public final HandlerList<IComponentKeyEventHandler<T>> keyDown = new HandlerList<>();
	public final HandlerList<IComponentKeyEventHandler<T>> keyUp = new HandlerList<>();
	
	public final HandlerList<IComponentCoordEventHandler<T>> mouseIn = new HandlerList<>();
	public final HandlerList<IComponentCoordEventHandler<T>> mouseOut = new HandlerList<>();
	public final HandlerList<IComponentMouseWheelEventHandler<T>> mouseWheel = new HandlerList<>();
	
	public final HandlerList<IComponentVoidEventHandler<T>> focus = new HandlerList<>();
	public final HandlerList<IComponentVoidEventHandler<T>> blur = new HandlerList<>();
	
	public final HandlerList<IComponentVoidEventHandler<T>> enable = new HandlerList<>();
	public final HandlerList<IComponentVoidEventHandler<T>> disable = new HandlerList<>();
	
	public final HandlerList<IComponentChildEventHandler<T>> addChild = new HandlerList<>();
	public final HandlerList<IComponentChildEventHandler<T>> removeChild = new HandlerList<>();
	public final HandlerList<IComponentTagEventHandler<T>> matchesTag = new HandlerList<>();
	public final HandlerList<IComponentTagEventHandler<T>> addTag = new HandlerList<>();
	public final HandlerList<IComponentTagEventHandler<T>> removeTag = new HandlerList<>();

	public final HandlerList<IComponentLogicalSizeEventHandler<T>> logicalSize = new HandlerList<>();
	
	public int zIndex = 0;
	protected Vec2 pos, size;
	
	public boolean mouseOverThisFrame = false;
	protected Set<String> tags = new HashSet<>();
	
	protected boolean enabled = true, visible = true, focused = false, invalid = false;
	
	protected boolean[] mouseButtonsDown = new boolean[EnumMouseButton.values().length];
	protected Map<Key, Boolean> keysDown = new DefaultedMap<>(false);
	
	/* subcomponent stuff */
	protected boolean calculateOwnHover = true;
	protected List<GuiComponent<?>> components = new ArrayList<>();
	protected GuiComponent<?> parent;
	
	public GuiComponent(int posX, int posY) {
		this(posX, posY, 0, 0);
	}
	
	public GuiComponent(int posX, int posY, int width, int height) {
		this.pos = new Vec2(posX, posY);
		this.size = new Vec2(width, height);
	}
	
	/**
	 * Draws the component, this is called between pre and post draw events
	 */
	public abstract void drawComponent(Vec2 mousePos, float partialTicks);
	
	/**
	 * Setup the component without making temporary variables
	 */
	public T setup(IComponentSetup<T> setup) {
		setup.setup(thiz());
		return thiz();
	}
	
	/**
	 * Transforms the position passed to be relative to this component's position.
	 */
	public Vec2 relativePos(Vec2 pos) {
		return pos.sub(this.pos);
	}
	
	/**
	 * Transforms the position passed to be relative to the root component's position.
	 */
	public Vec2 rootPos(Vec2 pos) {
		if(parent == null)
			return pos.add(this.pos);
		else
			return parent.rootPos(pos.add(this.pos));
	}
	
	//=============================================================================
	{/* Children */}
	//=============================================================================
	
	/**
	 * Adds a child to this component.
	 * 
	 * @throws IllegalArgumentException if the component had a parent already
	 */
	public void add(GuiComponent<?> component) {
		if(component.getParent() != null)
			throw new IllegalArgumentException("Component already had a parent!");
		components.add(component);
		component.setParent(this);
		Collections.sort(components, (a, b) -> Integer.compare(a.zIndex, b.zIndex));
		addChild.fireAll((h) -> h.handle(thiz(), component));
	}
	
	/**
	 * Removes the supplied component
	 * @param component
	 */
	public void remove(GuiComponent<?> component) {
		component.setParent(null);
		removeChild.fireAll((h) -> h.handle(thiz(), component));
		components.remove(component);
	}
	
	/**
	 * Removes all components that have the supplied tag
	 */
	public void remove(String tag) {
		components.removeIf((e) -> {
			boolean b = e.hasTag(tag);
			if(b) {
				e.setParent(null);
				removeChild.fireAll((h) -> h.handle(thiz(), e));
			}
			return b;
		});
	}
	
	/**
	 * Calls the consumer for each component that has the supplied tag
	 */
	public void getByTag(String tag, Consumer<GuiComponent<?>> consumer) {
		for (GuiComponent<?> component : components) {
			if(component.hasTag(tag))
				consumer.accept(component);
		}
	}
	
	//=============================================================================
	{/* Events/checks */}
	//=============================================================================
	
	/**
	 * Test if the mouse is over this component. mousePos is relative to the position of the element.
	 */
	public boolean isMouseOver(Vec2 mousePos) {
		boolean hover = false;
		if(shouldCalculateOwnHover())
			hover = hover || mousePos.x >= 0 && mousePos.x <= size.x && mousePos.y >= 0 && mousePos.y <= size.y;
			
		for (GuiComponent<?> child : components) {
			hover = child.isMouseOver(child.relativePos(mousePos)) || hover;
		}
		return hover;
	}
	
	/**
	 * Draw this component, don't override in subclasses unless you know what you're doing.
	 * @param mousePos Mouse position relative to the position of this component
	 * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
	 */
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		if(!isVisible()) return;
		boolean wasMouseOverLastFrame = mouseOverThisFrame;
		mouseOverThisFrame = isMouseOver(mousePos);
		
		if(wasMouseOverLastFrame && !mouseOverThisFrame)
			mouseOut.fireAll((e) -> e.handle(thiz(), mousePos));
		if(!wasMouseOverLastFrame && mouseOverThisFrame)
			mouseIn.fireAll((e) -> e.handle(thiz(), mousePos));
		
		preDraw.fireAll((e) -> e.handle(thiz(), mousePos, partialTicks));
		
		drawComponent(mousePos, partialTicks);
		
		GlStateManager.pushMatrix();
		GlStateManager.pushAttrib();
		GlStateManager.translate(pos.x, pos.y, 0);
		
		List<GuiComponent<?>> remove = new ArrayList<>();
		for (GuiComponent<?> component : components) {
			if(component.isInvalid()) {
				remove.add(component);
				continue;
			}
			component.draw(component.relativePos(mousePos), partialTicks);
		}
		
		for (GuiComponent<?> rem : remove) {
			rem.setParent(null);
			removeChild.fireAll((h) -> h.handle(thiz(), rem));
		}
		components.removeAll(remove);
		
		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
		
		postDraw.fireAll((e) -> e.handle(thiz(), mousePos, partialTicks));
	}

	/**
	 * Called when the mouse is pressed. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseDown(Vec2 mousePos, EnumMouseButton button) {
		if(!isVisible()) return;
		if(mouseDown.fire((e) -> e.handle(thiz(), mousePos, button)))
			return;
		
		if(isMouseOver(mousePos))
			mouseButtonsDown[button.ordinal()] = true;
		
		for (GuiComponent<?> child : components) {
			child.mouseDown(child.relativePos(mousePos), button);
		}
	}
	
	/**
	 * Called when the mouse is released. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseUp(Vec2 mousePos, EnumMouseButton button) {
		if(!isVisible()) return;
		boolean wasDown = mouseButtonsDown[button.ordinal()];
		mouseButtonsDown[button.ordinal()] = false;
		
		if( mouseUp.fire((e) -> e.handle(thiz(), mousePos, button)) )
			return;
		
		if(isMouseOver(mousePos) && wasDown) {
			mouseClick.fire((e) -> e.handle(thiz(), mousePos, button)); // don't return here, if a click was handled we should still handle the mouseUp
		}
		
		for (GuiComponent<?> child : components) {
			child.mouseUp(child.relativePos(mousePos), button);
		}
	}
	
	/**
	 * Called when the mouse is moved while pressed. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseDrag(Vec2 mousePos, EnumMouseButton button) {
		if(!isVisible()) return;
		if(mouseDrag.fire((e) -> e.handle(thiz(), mousePos, button)))
			return;
		
		for (GuiComponent<?> child : components) {
			child.mouseDrag(child.relativePos(mousePos), button);
		}
	}
	
	/**
	 * Called when the mouse wheel is moved.
	 * @param mousePos
	 * @param button
	 */
	public void mouseWheel(Vec2 mousePos, int direction) {
		if(!isVisible()) return;
		if(mouseWheel.fire((e) -> e.handle(thiz(), mousePos, direction)))
			return;
		
		for (GuiComponent<?> child : components) {
			child.mouseWheel(child.relativePos(mousePos), direction);
		}
	}
	
	/**
	 * Called when a key is pressed in the parent component.
	 * @param key The actual character that was pressed
	 * @param keyCode The key code, codes listed in {@link Keyboard}
	 */
	public void keyPressed(char key, int keyCode) {
		if(!isVisible()) return;
		if( keyDown.fire((e) -> e.handle(thiz(), key, keyCode)) )
			return;
		
		keysDown.put(Key.get(key, keyCode), true);
		
		for (GuiComponent<?> child : components) {
			child.keyPressed(key, keyCode);
		}
	}
	
	/**
	 * Called when a key is released in the parent component.
	 * @param key The actual key that was pressed
	 * @param keyCode The key code, codes listed in {@link Keyboard}
	 */
	public void keyReleased(char key, int keyCode) {
		if(!isVisible()) return;
		keysDown.put(Key.get(key, keyCode), false); // do this before so we don't have lingering keyDown entries

		if( keyUp.fire((e) -> e.handle(thiz(), key, keyCode)) )
			return;
		
		for (GuiComponent<?> child : components) {
			child.keyReleased(key, keyCode);
		}
	}

	//=============================================================================
	{/* Utils, getters/setters */}
	//=============================================================================
	
	/**
	 * Returns {@code this} casted to {@code T}. Used to avoid unchecked cast warnings everywhere.
	 */
	@SuppressWarnings("unchecked")
	public T thiz() {
		return (T)this;
	}

	/**
	 * Get the position of the component relative to it's parent
	 */
	public Vec2 getPos() {
		return pos;
	}

	/**
	 * Set the position of the component relative to it's parent
	 */
	public void setPos(Vec2 pos) {
		this.pos = pos;
	}

	/**
	 * Get the size of the component
	 */
	public Vec2 getSize() {
		return size;
	}

	/**
	 * Set the size of the component
	 */
	public void setSize(Vec2 size) {
		this.size = size;
	}
	
	/**
	 * The size of the component for layout. Often dynamically calculated
	 */
	public BoundingBox2D getLogicalSize() {
		BoundingBox2D aabb = getContentSize();
		
		for (GuiComponent<?> child : components) {
			BoundingBox2D childAABB = child.getLogicalSize();
			aabb = aabb.union(childAABB);
		}
		
		return logicalSize.fire((v, t) -> t.handle(v, thiz()), aabb);
	}
	
	/**
	 * Gets the size of the content of this component.
	 */
	protected BoundingBox2D getContentSize() {
		return new BoundingBox2D(Vec2.ZERO, size);
	}
	
	/**
	 * Get the parent component
	 */
	public GuiComponent<?> getParent() {
		return this.parent;
	}

	/**
	 * Set the parent component
	 */
	public void setParent(GuiComponent<?> parent) {
		this.parent = parent;
	}

	/**
	 * Set whether the element should calculate hovering based on it's bounds as
	 * well as it's children or if it should only calculate based on it's children.
	 */
	public void setCalculateOwnHover(boolean calculateOwnHover) {
		this.calculateOwnHover = calculateOwnHover;
	}
	
	/**
	 * Whether to calculate hovering based on this component's bounds and its
	 * children or only this element's children
	 */
	public boolean shouldCalculateOwnHover() {
		return calculateOwnHover;
	}
	
	//=============================================================================
	{/* Assorted info */}
	//=============================================================================
	
	/**
	 * Adds the passed tag to this component if it doesn't already have it. Tags are not case sensitive
	 */
	public void addTag(String tag) {
		final String lowerTag = tag.toLowerCase();
		if(tags.add(lowerTag))
			addTag.fireAll((h) -> h.handle(thiz(), lowerTag));
	}
	
	/**
	 * Removes the passed tag to this component if it doesn't already have it. Tags are not case sensitive
	 */
	public void removeTag(String tag) {
		final String lowerTag = tag.toLowerCase();
		tags.remove(lowerTag);
	}
	
	/**
	 * Checks if the component has the tag specified. Tags are not case sensitive
	 */
	public boolean hasTag(String tag) {
		final String lowerTag = tag.toLowerCase();
		return tags.contains(lowerTag);
	}
	
	/**
	 * Returns an unmodifiable wrapper of the tag set. Tags are all stored in lowercase
	 */
	public Set<String> getTags() {
		return Collections.unmodifiableSet(tags);
	}
	
	public void setFocused(boolean focused) {
		if(this.focused != focused) {
			if(focused)
				focus.fireAll((h) -> h.handle(thiz()));
			else
				blur.fireAll((h) -> h.handle(thiz()));
		}
		this.focused = focused;
	}
	
	public boolean isFocused() {
		return focused;
	}
	
	public void setEnabled(boolean enabled) {
		if(this.enabled != enabled) {
			if(enabled)
				enable.fireAll((h) -> h.handle(thiz()));
			else
				disable.fireAll((h) -> h.handle(thiz()));
		}
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Returns true if this component is invalid and it should be removed from it's parent
	 * @return
	 */
	public boolean isInvalid() {
		return invalid;
	}

	/**
	 * Set this component invalid so it will be removed from it's parent element
	 */
	public void invalidate() {
		this.invalid = true;
	}
	
	/**
	 * Whether this component should be drawn or have events fire
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Set whether this component should be drawn or have events fire
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	//=============================================================================
	{/* Event interfaces */}
	//=============================================================================
	
	@FunctionalInterface
	public interface IComponentSetup<T extends GuiComponent<?>> {
		void setup(T component);
	}

	@FunctionalInterface
	public interface IComponentDrawEventHandler<T extends GuiComponent<?>> {
		void handle(T component, Vec2 mousePos, float partialTicks);
	}

	@FunctionalInterface
	public interface IComponentMouseEventHandler<T extends GuiComponent<?>> {
		boolean handle(T component, Vec2 pos, EnumMouseButton button);
	}

	@FunctionalInterface
	public interface IComponentMouseWheelEventHandler<T extends GuiComponent<?>> {
		boolean handle(T component, Vec2 pos, int direction);
	}

	@FunctionalInterface
	public interface IComponentKeyEventHandler<T extends GuiComponent<?>> {
		boolean handle(T component, char character, int code);
	}
	
	@FunctionalInterface
	public interface IComponentCoordEventHandler<T extends GuiComponent<?>> {
		boolean handle(T component, Vec2 mousePos);
	}
	
	@FunctionalInterface
	public interface IComponentChildEventHandler<T extends GuiComponent<?>> {
		boolean handle(T component, GuiComponent<?> other);
	}
	
	@FunctionalInterface
	public interface IComponentTagEventHandler<T extends GuiComponent<?>> {
		boolean handle(T component, String tag);
	}
	
	@FunctionalInterface
	public interface IComponentVoidEventHandler<T extends GuiComponent<?>> {
		void handle(T component);
	}
	
	@FunctionalInterface
	public interface IComponentLogicalSizeEventHandler<T extends GuiComponent<?>> {
		BoundingBox2D handle(BoundingBox2D size, T component);
	}
}
