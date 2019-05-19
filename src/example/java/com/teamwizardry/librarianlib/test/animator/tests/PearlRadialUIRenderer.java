package com.teamwizardry.librarianlib.test.animator.tests;

import com.teamwizardry.librarianlib.features.animator.Animation;
import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.test.animator.AnimatorItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class PearlRadialUIRenderer {

    public static PearlRadialUIRenderer INSTANCE = new PearlRadialUIRenderer();

	private final int SELECTOR_RADIUS = 40;
	private final int SELECTOR_WIDTH = 25;
	private final int SELECTOR_SHIFT = 5;
	private final float SELECTOR_ALPHA = 0.7F;
	public float[] slotRadii = new float[20];
	public Animation[] slotAnimations = new Animation[20];
	public Animator ANIMATOR = new Animator();
	private boolean lastSneakTick = false;

	public PearlRadialUIRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static int getScrollSlot(MouseEvent event, int count, int scrollSlot) {
		if (event.getDwheel() < 0) {
			if (scrollSlot == count) scrollSlot = 0;
			else scrollSlot = MathHelper.clamp(scrollSlot + 1, 0, count);
		} else {
			if (scrollSlot == 0) scrollSlot = count;
			else scrollSlot = MathHelper.clamp(scrollSlot - 1, 0, count);
		}
		return scrollSlot;
	}

	@SubscribeEvent
	public void onScroll(MouseEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (Keyboard.isCreated() && event.getDwheel() != 0 && player.isSneaking()) {

			ItemStack stack = player.getHeldItemMainhand();

			if (stack.getItem() == AnimatorItems.INSTANCE.getAnimatoritem()) {

				int count = 5;

				int scrollSlot = ItemNBTHelper.getInt(stack, "scroll_slot", -1);
				int lastSlot = scrollSlot;

				scrollSlot = getScrollSlot(event, count, scrollSlot);

				if (lastSlot != scrollSlot && scrollSlot >= 0) {
					ItemNBTHelper.setInt(stack, "scroll_slot", scrollSlot);

					for (int i = 0; i < INSTANCE.slotAnimations.length; i++) {
						Animation animation = INSTANCE.slotAnimations[i];
						if (animation != null)
							INSTANCE.ANIMATOR.removeAnimations(animation);

						if (i == scrollSlot) continue;
						INSTANCE.slotAnimations[i] = new BasicAnimation<>(INSTANCE, "slotRadii[" + i + "]")
								.to(0).ease(Easing.easeInQuint)
								.duration(50).addTo(INSTANCE.ANIMATOR);
					}

					INSTANCE.slotAnimations[scrollSlot] = new BasicAnimation<>(INSTANCE, "slotRadii[" + scrollSlot + "]")
							.to(SELECTOR_SHIFT * 2).ease(Easing.easeOutQuint)
							.duration(50f).addTo(INSTANCE.ANIMATOR);

					event.setCanceled(true);
				}

			}
		}
	}

	@SubscribeEvent
	public void renderHud(RenderGameOverlayEvent.Post event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack stack = player.getHeldItemMainhand();

			if (stack.getItem() == AnimatorItems.INSTANCE.getAnimatoritem()) {

				ScaledResolution resolution = event.getResolution();
				int width = resolution.getScaledWidth();
				int height = resolution.getScaledHeight();

				int numSegmentsPerArc = (int) Math.ceil(360d / 5);
				float anglePerColor = (float) (2 * Math.PI / 5);
				float anglePerSegment = anglePerColor / (numSegmentsPerArc);
				float angle = 0;

				int scrollSlot = ItemNBTHelper.getInt(stack, "scroll_slot", -1);

				Tessellator tess = Tessellator.getInstance();
				BufferBuilder bb = tess.getBuffer();
				for (int j = 0; j < 5; j++) {

					Random random = new Random(j);

					Color color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f);

					double innerRadius = SELECTOR_RADIUS - SELECTOR_WIDTH / 2.0;
					double outerRadius = SELECTOR_RADIUS + SELECTOR_WIDTH / 2.0 + (INSTANCE.slotRadii[j]);// + (scrollSlot == j ? SELECTOR_SHIFT : 0);

					GlStateManager.pushMatrix();
					GlStateManager.translate(width / 2.0, height / 2.0, 0);

					GlStateManager.enableBlend();
					GlStateManager.disableTexture2D();
					GlStateManager.shadeModel(GL11.GL_SMOOTH);

					bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					for (int i = 0; i < numSegmentsPerArc; i++) {
						float currentAngle = i * anglePerSegment + angle;
						bb.pos(innerRadius * MathHelper.cos(currentAngle), innerRadius * MathHelper.sin(currentAngle), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
						bb.pos(innerRadius * MathHelper.cos(currentAngle + anglePerSegment), innerRadius * MathHelper.sin(currentAngle + anglePerSegment), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
						bb.pos(outerRadius * MathHelper.cos(currentAngle + anglePerSegment), outerRadius * MathHelper.sin(currentAngle + anglePerSegment), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
						bb.pos(outerRadius * MathHelper.cos(currentAngle), outerRadius * MathHelper.sin(currentAngle), 0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
					}
					tess.draw();

					GlStateManager.disableRescaleNormal();
					RenderHelper.disableStandardItemLighting();
					GlStateManager.disableBlend();
					GlStateManager.disableAlpha();

					GlStateManager.translate(-width / 2.0, -height / 2.0, 0);
					GlStateManager.popMatrix();

					angle += anglePerColor;
				}
			}
		}
	}
}
