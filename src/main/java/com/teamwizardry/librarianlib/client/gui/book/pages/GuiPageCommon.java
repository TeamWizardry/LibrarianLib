package com.teamwizardry.librarianlib.client.gui.book.pages;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.teamwizardry.librarianlib.api.Const;
import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.api.gui.GuiComponentContainer;
import com.teamwizardry.librarianlib.api.util.gui.ScissorUtil;
import com.teamwizardry.librarianlib.api.util.math.Vec2;
import com.teamwizardry.librarianlib.api.util.misc.PathUtils;
import com.teamwizardry.librarianlib.client.gui.book.BookHandler;
import com.teamwizardry.librarianlib.client.gui.book.PageRegistry;
import com.teamwizardry.librarianlib.client.gui.book.bookcomponents.Tippable;
import com.teamwizardry.librarianlib.common.network.data.DataNode;

public abstract class GuiPageCommon extends Tippable {

    public String path, mod;
    public int page;
    public int viewWidth, viewHeight, viewLeft, viewTop;
    protected GuiScreen parent;
    protected DataNode data;
    protected DataNode globalData;
    
    
    { /* helpers */ }

    public GuiPageCommon(GuiScreen parent, DataNode data, DataNode globalData, String mod, String path, int page) {
        this.data = data;
        this.globalData = data;
        this.parent = parent;
        this.mod = mod;
        this.path = path;
        this.page = page;
        this.viewWidth = 115;
        this.viewHeight = 154;
        if (globalData.get("title").isString()) {
            this.title = globalData.get("title").asString();
        }
        setHasNavReturn(true);
        setHasNavNext(data.get("hasNext").exists());
        setHasNavPrev(data.get("hasPrev").exists());
    }
    
    public void mouseClickedPage(int mouseX, int mouseY, int button) {
    }

    public void mouseReleasedPage(int mouseX, int mouseY, int button) {
    }

    public void mouseClickMovePage(int mouseX, int mouseY, int button, long timeSinceLastClick) {
    }
    
    public void mouseScrollPage(int mouseX, int mouseY, int direction) {
    }

    public abstract void drawPage(int mouseX, int mouseY, float partialTicks);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        mc.fontRendererObj.setUnicodeFlag(true);

        viewLeft = left + 15;
        viewTop = top + 12;
        
        
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(viewLeft, viewTop, 100);
        ScissorUtil.set(viewLeft, viewTop, viewWidth, viewHeight);
        ScissorUtil.enable();
        drawPage(mouseX - viewLeft, mouseY - viewTop, partialTicks);
        GlStateManager.popMatrix();
        
        ScissorUtil.disable();
        
        mc.fontRendererObj.setUnicodeFlag(false);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        mouseClickedPage(mouseX - viewLeft, mouseY - viewTop, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        mouseReleasedPage(mouseX - viewLeft, mouseY - viewTop, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
        mouseClickMovePage(mouseX - viewLeft, mouseY - viewTop, mouseButton, timeSinceLastClick);
    }
    
    @Override
    public void handleMouseInput() throws IOException {
    	super.handleMouseInput();
    	int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
    	int wheelAmount = Mouse.getEventDWheel();

        if (wheelAmount != 0)
        {
            if (wheelAmount > 0)
            {
                wheelAmount = 1;
            }

            if (wheelAmount < 0)
            {
                wheelAmount = -1;
            }
            
            mouseScrollPage(mouseX - viewLeft, mouseY - viewTop, wheelAmount);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == Const.ButtonID.NAV_BAR_INDEX) {
            if (parent == null) {
                String indexPath = PathUtils.resolve(PathUtils.parent(path), "index");
                if (!indexPath.equals(path))
                    parent = PageRegistry.construct(null, mod, indexPath, 0); // parent is null because otherwise pressing back goes to the child bookcomponents
                if (parent == null) {
                    parent = PageRegistry.construct(null, mod, "/", 0); // see above ^
                }
            }
            mc.displayGuiScreen(parent);
        }
        if (button.id == Const.ButtonID.NAV_BAR_NEXT) {
            openPage(path, page + 1);
        }
        if (button.id == Const.ButtonID.NAV_BAR_BACK) {
            openPage(path, page - 1);
        }
    }

    public void openPageRelative(String path, int page) {
        openPage(PathUtils.resolve(PathUtils.parent(this.path), path), page);
    }

    public void openPage(String path, int page) {
        BookHandler.display(mod, this.path.equals(path) ? this.parent : this, path, page);
    }

    public ResourceLocation pageResource(String path) {
        return new ResourceLocation(mod, PathUtils.resolve("textures/" + PathUtils.resolve(PathUtils.parent(this.path), path)));
    }
}
