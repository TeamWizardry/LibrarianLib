package com.teamwizardry.librarianlib.book.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import com.teamwizardry.librarianlib.api.gui.components.Component3DView;
import com.teamwizardry.librarianlib.api.gui.components.ComponentStructure;
import com.teamwizardry.librarianlib.api.gui.components.mixin.gl.GlMixin;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.librarianlib.book.util.Page;
import com.teamwizardry.librarianlib.client.multiblock.Structure;
import com.teamwizardry.librarianlib.common.network.data.DataNode;

public class PageStructure extends GuiBook {

	public PageStructure(Book book, DataNode rootData, DataNode pageData, Page page) {
		super(book, rootData, pageData, page);
		
		Structure structure = new Structure(new ResourceLocation( pageData.get("structure").asStringOr("minecraft:missingno") ));
		
		Component3DView view = new Component3DView(0, 0, PAGE_WIDTH, PAGE_HEIGHT);
		
		ComponentStructure structureComp = new ComponentStructure(0, 0, structure);
		
		view.rotX = 22;
		view.rotY = 45;
		view.zoom = 10;
		
//		view.offset = view.offset.add( new Vec3d(structure.getOrigin()) );
		view.offset = view.offset.add( new Vec3d(-0.5, 0.5, -0.5) );
		
		view.add(structureComp);
		
		contents.add(view);
		
	}

}
