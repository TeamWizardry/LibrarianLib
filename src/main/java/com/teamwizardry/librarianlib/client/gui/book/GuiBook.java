package com.teamwizardry.librarianlib.client.gui.book;

import net.minecraft.util.ResourceLocation;

import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.api.gui.GuiBase;
import com.teamwizardry.librarianlib.api.gui.GuiComponentContainer;
import com.teamwizardry.librarianlib.api.gui.components.ComponentButton;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.gui.components.ComponentText;
import com.teamwizardry.librarianlib.api.gui.components.ComponentText.TextAlignH;
import com.teamwizardry.librarianlib.api.gui.components.ComponentText.TextAlignV;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;

public class GuiBook extends GuiBase {
	
	public static Texture BACKGROUND = new Texture(new ResourceLocation(LibrarianLib.MODID, "textures/bookcomponents/book/book.png"), 512, 512);
	public static Sprite BOOK_BACKGROUND_BORDER = BACKGROUND.getSprite(0, 0, 146, 180);
	public static Sprite BACKGROUND_PAGE = BACKGROUND.getSprite(146, 0, 146, 180);
	
	public static Sprite TITLE_BAR = BACKGROUND.getSprite(0, 180, 133, 13);
	public static Sprite BOOKMARK = BACKGROUND.getSprite(133, 180, 100, 13);
	
	public static Sprite BACK_PAGE = BACKGROUND.getSprite(0, 193, 18, 10);
	public static Sprite NEXT_PAGE = BACKGROUND.getSprite(18, 193, 18, 10);
	
	public static Sprite BACK_ARROW = BACKGROUND.getSprite(0, 203, 18, 9);
	public static Sprite UP_ARROW = BACKGROUND.getSprite(0, 212, 9, 18);
	public static Sprite DOWN_ARROW = BACKGROUND.getSprite(9, 212, 9, 18);
	
	public static Sprite CHECKBOX = BACKGROUND.getSprite(18, 203, 9, 9);
	public static Sprite CHECKBOX_ON = BACKGROUND.getSprite(27, 203, 9, 9);
	public static Sprite CHECKMARK = BACKGROUND.getSprite(18, 212, 16, 16);
	
	
	public GuiBook() {
		super(146, 180);
		
		GuiComponentContainer titleBar = new GuiComponentContainer(0, -20, TITLE_BAR.getWidth(), TITLE_BAR.getHeight());
		titleBar.add(new ComponentSprite(TITLE_BAR, 0, 0));
		titleBar.add(new ComponentText(66, 7, TextAlignH.CENTER, TextAlignV.MIDDLE).val("TITLE"));
		
		GuiComponentContainer navBar = new GuiComponentContainer(0, 189, TITLE_BAR.getWidth(), TITLE_BAR.getHeight());
		
		navBar.add(new ComponentSprite(TITLE_BAR, 0, 0));
		navBar.add(new ComponentButton(15, 2, BACK_PAGE).setup((b) -> {
			
		}));
		navBar.add(new ComponentButton(15, 2, NEXT_PAGE).setup((b) -> {
			
		}));
		navBar.add(new ComponentButton((width / 2) - (BACK_ARROW.getWidth() / 2), 2, BACK_ARROW).setup((b) -> {
			
		}));
		
		components.add(titleBar);
		components.add(navBar);
	}
	
}
