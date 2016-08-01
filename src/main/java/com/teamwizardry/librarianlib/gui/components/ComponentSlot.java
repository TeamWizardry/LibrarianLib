package com.teamwizardry.librarianlib.gui.components;

import com.teamwizardry.librarianlib.gui.GuiComponent;
import com.teamwizardry.librarianlib.gui.HandlerList;
import com.teamwizardry.librarianlib.gui.Option;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class ComponentSlot extends GuiComponent<ComponentSlot> {
	
	public final Option<ComponentSlot, ItemStack> stack = new Option<>(null);
	public final Option<ComponentSlot, Boolean> tooltip = new Option<>(null);
	public final HandlerList<ISlotTextEventHandler<ComponentSlot>> quantityText = new HandlerList<>();
	public final HandlerList<ISlotInfoEventHandler<ComponentSlot>> itemInfo = new HandlerList<>();
	
	public ComponentSlot(int posX, int posY) {
		super(posX, posY, 16, 16);
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
		
		ItemStack stack = this.stack.getValue(this);
		String str = "" + stack.stackSize;
		str = quantityText.fireModifier(str, (h, v) -> h.handle(this, v));
		
		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
        itemRender.zLevel = 200.0F;
        
        FontRenderer font = null;
        font = stack.getItem().getFontRenderer(stack);

        itemRender.renderItemAndEffectIntoGUI(stack, pos.xi, pos.yi);
        itemRender.renderItemOverlayIntoGUI(font == null ? Minecraft.getMinecraft().fontRendererObj : font, stack, pos.xi, pos.yi, str);
        
        itemRender.zLevel = 0.0F;
        
        
        if(mouseOverThisFrame && tooltip.getValue(this))
        	drawTooltip(stack, mousePos);
        
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
	}
	
	public void drawTooltip(ItemStack stack, Vec2d mousePos) {
		List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().rarityColor + list.get(i));
            }
            else
            {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }

        itemInfo.fireAll((h) -> h.handle(this, list));
        
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        setTooltip(list, font == null ? Minecraft.getMinecraft().fontRendererObj : font);
	}
	
	@FunctionalInterface
	public interface ISlotTextEventHandler<T> {
		String handle(T component, String text);
	}
	
	@FunctionalInterface
	public interface ISlotInfoEventHandler<T> {
		void handle(T component, List<String> info);
	}

}
