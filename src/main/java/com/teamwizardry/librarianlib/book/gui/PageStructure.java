package com.teamwizardry.librarianlib.book.gui;

import com.teamwizardry.librarianlib.gui.components.*;
import com.teamwizardry.librarianlib.gui.mixin.gl.GlMixin;
import com.teamwizardry.librarianlib.math.Vec2d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.structure.Structure;
import com.teamwizardry.librarianlib.data.DataNode;
import com.teamwizardry.librarianlib.data.DataNodeParsers;
import net.minecraft.world.gen.structure.template.Template;
import org.lwjgl.opengl.GL11;

public class PageStructure extends GuiBook {

	protected IBlockState originState;
	
	public PageStructure(Book book, DataNode rootData, DataNode pageData, Page page) {
		super(book, rootData, pageData, page);
		
		originState = DataNodeParsers.parseBlockState(pageData.get("block"));
		
		Structure structure = new Structure(new ResourceLocation( pageData.get("structure").asStringOr("minecraft:missingno") ));
		Structure structureTransparent = new Structure(new ResourceLocation( pageData.get("structure").asStringOr("minecraft:missingno") ));
		
		Component3DView view = new Component3DView(0, 0, PAGE_WIDTH, PAGE_HEIGHT);
		view.rotX = 22;
		view.rotY = 45;
		view.zoom = 10;
		
		view.offset = view.offset.add( new Vec3d(-0.5, 0.5, -0.5) );
		
		ComponentStructure structureComp = new ComponentStructure(0, 0, structure);
		ComponentStructure structureCompTransparent = new ComponentStructure(0, 0, structureTransparent);
		structureCompTransparent.color.setValue(new Color(1, 1, 1, 0.5f));
		
		view.add(structureComp);
		view.add(structureCompTransparent);
		
		contents.add(view);
		
		int hudScale = 3;
		
		ComponentSpriteTiled tiled = new ComponentSpriteTiled(GuiBook.SLIDER_NORMAL, 6, 0, 0);
		contents.add(tiled);
		
		tiled.preDraw.add((c, pos, ticks) -> GlStateManager.depthFunc(GL11.GL_ALWAYS));
		tiled.preChildrenDraw.add((c, pos, ticks) -> GlStateManager.depthFunc(GL11.GL_LEQUAL));
		
		tiled.setSize(new Vec2d(
			(structure.getMax().getX()-structure.getMin().getX()+1)*hudScale + 12,
			(structure.getMax().getY()-structure.getMin().getY()+1)*hudScale + 12
		));
		
		Structure structureHud = new Structure(new ResourceLocation( pageData.get("structure").asStringOr("minecraft:missingno") ));
		
		ComponentStructure structureCompHud = new ComponentStructure(0, 0, structureHud);
		
		GlMixin.scale(structureCompHud).setValue(new Vec3d(hudScale, hudScale, hudScale));
		GlMixin.transform(structureCompHud).setValue(new Vec3d(structureHud.getOrigin().getX()+1+2, structureHud.getMax().getY()-structureHud.getOrigin().getY()+1+2, 50));
		GlMixin.rotate(structureCompHud).setValue(new Vec3d(0, 0, 180));
		tiled.add(structureCompHud);
		
		Structure structureTransparentHud = new Structure(new ResourceLocation( pageData.get("structure").asStringOr("minecraft:missingno") ));
		
		ComponentStructure structureCompHudTransparent = new ComponentStructure(0, 0, structureTransparentHud);
		structureCompHudTransparent.color.setValue(new Color(1, 1, 1, 0.5f));
		
		GlMixin.scale(structureCompHudTransparent).setValue(new Vec3d(hudScale, hudScale, hudScale));
		GlMixin.transform(structureCompHudTransparent).setValue(new Vec3d(structureTransparentHud.getOrigin().getX()+1+2, structureTransparentHud.getMax().getY()-structureTransparentHud.getOrigin().getY()+1+2, 50));
		GlMixin.rotate(structureCompHudTransparent).setValue(new Vec3d(0, 0, 180));
		tiled.add(structureCompHudTransparent);
		
		ComponentVoid clickArea = new ComponentVoid(6, 6);
		
		clickArea.setSize(tiled.getSize().sub(12, 12));
		
		clickArea.mouseClick.add((c, pos, button) -> {
			
			if(c.mouseOverThisFrame) {
				int y = ( c.getSize().yi - pos.yi + 1 ) / 3;
				
				setClipY(y, structureTransparent);
				setClipY(y, structureTransparentHud);
				
				setOnlyY(y, structure);
				setOnlyY(y, structureHud);
				
				structureComp.initStructure();
				structureCompHud.initStructure();
				
				IBlockState opaqueState = structure.getBlockAccess().getBlockState(new BlockPos(6, y, 6));
				IBlockState transpState = structureTransparent.getBlockAccess().getBlockState(new BlockPos(6, y, 6));
				
				structureCompTransparent.initStructure();
				structureCompHudTransparent.initStructure();
			}
			
			return false;
		});
		
		tiled.add(clickArea);
		
		setClipY(Integer.MIN_VALUE, structureTransparent);
		setClipY(Integer.MIN_VALUE, structureTransparentHud);
		
		setOnlyY(Integer.MIN_VALUE, structure);
		setOnlyY(Integer.MIN_VALUE, structureHud);
	}
	
	public void setClipY(int y, Structure structure) {
		structure.getBlockAccess().resetSetBlocks();
		
		structure.getBlockAccess().setBlockState(structure.getOrigin(), originState);
		if(y == Integer.MIN_VALUE) {
			return;
		}
		
		for (Template.BlockInfo info : structure.blockInfos()) {
			if(info.pos.getY() >= y) {
				structure.getBlockAccess().setBlockState(info.pos, Blocks.AIR.getDefaultState());
			} else {
				info.pos.getY();
			}
		}
	}
	
	public void setOnlyY(int y, Structure structure) {
		structure.getBlockAccess().resetSetBlocks();
		
		structure.getBlockAccess().setBlockState(structure.getOrigin(), originState);
		if(y == Integer.MIN_VALUE) {
			return;
		}
		
		for (Template.BlockInfo info : structure.blockInfos()) {
			if(info.pos.getY() != y) {
				structure.getBlockAccess().setBlockState(info.pos, Blocks.AIR.getDefaultState());
			} else {
				info.pos.getY();
			}
		}
	}
}
