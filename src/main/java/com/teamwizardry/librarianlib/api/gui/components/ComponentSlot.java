package com.teamwizardry.librarianlib.api.gui.components;

import java.util.List;

import net.minecraftforge.fml.client.config.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.HandlerList;
import com.teamwizardry.librarianlib.api.gui.Option;
import com.teamwizardry.librarianlib.api.util.gui.ScissorUtil;
import com.teamwizardry.librarianlib.math.Vec2;

public class ComponentSlot extends GuiComponent<ComponentSlot> {

	public final Option<ComponentSlot, ItemStack> stack = new Option<>(null);
	public final HandlerList<ISlotTextEventHandler<ComponentSlot>> quantityText = new HandlerList<>();
	public final HandlerList<ISlotInfoEventHandler<ComponentSlot>> itemInfo = new HandlerList<>();
	
	public ComponentSlot(int posX, int posY) {
		super(posX, posY, 16, 16);
	}

	@Override
	public void drawComponent(Vec2 mousePos, float partialTicks) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
		
		ItemStack stack = this.stack.getValue(this);
		String str = "" + stack.stackSize;
		str = quantityText.fire((v, h) -> h.handle(this, v), str);
		
		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
        itemRender.zLevel = 200.0F;
        
        FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = Minecraft.getMinecraft().fontRendererObj;
        
        itemRender.renderItemAndEffectIntoGUI(stack, pos.xi, pos.yi);
        itemRender.renderItemOverlayIntoGUI(font, stack, pos.xi, pos.yi, str);
        
        itemRender.zLevel = 0.0F;
        
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();

        if(mouseOverThisFrame)
        	drawTooltip(stack, mousePos);
	}
	
	public void drawTooltip(ItemStack stack, Vec2 mousePos) {
		ScissorUtil.push();
		ScissorUtil.disable();
		List<String> list = stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
            }
            else
            {
                list.set(i, TextFormatting.GRAY + (String)list.get(i));
            }
        }

        itemInfo.fireAll((h) -> h.handle(this, list));
        
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        
        GuiUtils.drawHoveringText(list, pos.xi + mousePos.xi, pos.yi + mousePos.yi, 1000, 1000, -1, font == null ? Minecraft.getMinecraft().fontRendererObj : font);
        ScissorUtil.pop();
	}
	
	@FunctionalInterface
	public static interface ISlotTextEventHandler<T> {
		String handle(T component, String text);
	}
	
	@FunctionalInterface
	public static interface ISlotInfoEventHandler<T> {
		void handle(T component, List<String> info);
	}

}
