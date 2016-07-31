package com.teamwizardry.librarianlib.book.gui;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import com.teamwizardry.librarianlib.api.gui.components.Component3DView;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSpriteTiled;
import com.teamwizardry.librarianlib.api.gui.components.ComponentStructure;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.client.multiblock.Structure;
import com.teamwizardry.librarianlib.common.network.data.DataNode;
import com.teamwizardry.librarianlib.common.network.data.DataNodeParsers;

public class PageStructure extends GuiBook {

	public PageStructure(Book book, DataNode rootData, DataNode pageData, Page page) {
		super(book, rootData, pageData, page);
		
		IBlockState originState = DataNodeParsers.parseBlockState(pageData.get("block"));
		
		Structure structure = new Structure(new ResourceLocation( pageData.get("structure").asStringOr("minecraft:missingno") ));
		Structure structureTransparent = new Structure(new ResourceLocation( pageData.get("structure").asStringOr("minecraft:missingno") ));
		
		Component3DView view = new Component3DView(0, 0, PAGE_WIDTH, PAGE_HEIGHT);
		view.rotX = 22;
		view.rotY = 45;
		view.zoom = 10;
		
//		view.offset = view.offset.add( new Vec3d(structure.getOrigin()) );
		view.offset = view.offset.add( new Vec3d(-0.5, 0.5, -0.5) );
		
		ComponentStructure structureComp = new ComponentStructure(0, 0, structure);
		ComponentStructure structureCompTransparent = new ComponentStructure(0, 0, structureTransparent);
		structureCompTransparent.color.setValue(new Color(1, 1, 1, 0.5f));
		
		view.add(structureComp);
		view.add(structureCompTransparent);
		
		contents.add(view);
		
		ComponentSpriteTiled button = new ComponentSpriteTiled(GuiBook.BUTTON, 4, 0, 0, 15, 15);
		button.color.setValue(new Color(1, 0, 0, 1));
		
		contents.add(button);
		
	}

}
