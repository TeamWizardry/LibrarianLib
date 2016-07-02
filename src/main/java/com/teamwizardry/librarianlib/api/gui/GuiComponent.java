package com.teamwizardry.librarianlib.api.gui;

import com.teamwizardry.librarianlib.math.Vec2;
import org.lwjgl.input.Keyboard;

/**
 * A component of a gui, such as a button, image, piece of text, list, etc.
 * 
 * @param <T> The class of this component. Used for setup()
 */
public abstract class GuiComponent<T extends GuiComponent<?>> implements IGuiDrawable {
	
	public final HandlerList<IComponentDrawEventHandler<T>> preDraw = new HandlerList<>();
	public final HandlerList<IComponentDrawEventHandler<T>> postDraw = new HandlerList<>();
	public final HandlerList<IComponentMouseEventHandler<T>> mouseDown = new HandlerList<>();
	public final HandlerList<IComponentMouseEventHandler<T>> mouseUp = new HandlerList<>();
	public final HandlerList<IComponentMouseEventHandler<T>> mouseDrag = new HandlerList<>();
	public final HandlerList<IComponentMouseWheelEventHandler<T>> mouseWheel = new HandlerList<>();
	public final HandlerList<IComponentKeyEventHandler<T>> keyDown = new HandlerList<>();
	public final HandlerList<IComponentKeyEventHandler<T>> keyUp = new HandlerList<>();
	public int zIndex = 0;
	public boolean mouseOverThisFrame = false;
	protected Vec2 pos, size;
	protected GuiComponent<?> parent;

	{/* getters and setters */}
	
	public GuiComponent(int posX, int posY) {
		this(posX, posY, 0, 0);
	}
	
	public GuiComponent(int posX, int posY, int width, int height) {
		this.pos = new Vec2(posX, posY);
		this.size = new Vec2(width, height);
	}
	
	/**
	 * Draw this component, don't override.
	 * @param mousePos Mouse position relative to the position of this component
	 * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
	 */
	@Override
	public void draw(Vec2 mousePos, float partialTicks) {
		mouseOverThisFrame = isMouseOver(mousePos);
		preDraw.fire((e) -> e.handle(thiz(), mousePos, partialTicks));
		drawComponent(mousePos, partialTicks);
		postDraw.fire((e) -> e.handle(thiz(), mousePos, partialTicks));
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
	
	/**
	 * Test if the mouse is over this component. mousePos is relative to the position of the element.
	 */
	public boolean isMouseOver(Vec2 mousePos) {
		return mousePos.x >= 0 && mousePos.x <= size.x && mousePos.y >= 0 && mousePos.y <= size.y;
	}
	
	/**
	 * Called when the mouse is pressed. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseDown(Vec2 mousePos, EnumMouseButton button) {
		mouseDown.fire((e) -> e.handle(thiz(), mousePos, button));
	}
	
	/**
	 * Called when the mouse is released. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseUp(Vec2 mousePos, EnumMouseButton button) {
		mouseUp.fire((e) -> e.handle(thiz(), mousePos, button));
	}
	
	/**
	 * Called when the mouse is moved while pressed. mousePos is relative to the position of this component.
	 * @param mousePos
	 * @param button
	 */
	public void mouseDrag(Vec2 mousePos, EnumMouseButton button) {
		mouseDrag.fire((e) -> e.handle(thiz(), mousePos, button));
	}
	
	/**
	 * Called when the mouse wheel is moved.
	 * @param mousePos
	 * @param button
	 */
	public void mouseWheel(Vec2 mousePos, int direction) {
		mouseWheel.fire((e) -> e.handle(thiz(), mousePos, direction));
	}
	
	/**
	 * Called when a key is pressed in the parent component.
	 * @param key The actual character that was pressed
	 * @param keyCode The key code, codes listed in {@link Keyboard}
	 */
	public void keyPressed(char key, int keyCode) {
		keyDown.fire((e) -> e.handle(thiz(), key, keyCode));
	}
	
	/**
	 * Called when a key is released in the parent component.
	 * @param key The actual key that was pressed
	 * @param keyCode The key code, codes listed in {@link Keyboard}
	 */
	public void keyReleased(char key, int keyCode) {
		keyUp.fire((e) -> e.handle(thiz(), key, keyCode));
	}

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

	@FunctionalInterface
	public interface IComponentSetup<T extends GuiComponent> {
		void setup(T component);
	}

	@FunctionalInterface
	public interface IComponentDrawEventHandler<T extends GuiComponent> {
		void handle(T component, Vec2 mousePos, float partialTicks);
	}

	@FunctionalInterface
	public interface IComponentMouseEventHandler<T extends GuiComponent> {
		void handle(T component, Vec2 pos, EnumMouseButton button);
	}

	@FunctionalInterface
	public interface IComponentMouseWheelEventHandler<T extends GuiComponent> {
		void handle(T component, Vec2 pos, int direction);
	}

	@FunctionalInterface
	public interface IComponentKeyEventHandler<T extends GuiComponent> {
		void handle(T component, char character, int code);
	}
}
