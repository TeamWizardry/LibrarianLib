package com.teamwizardry.librarianlib.client.gui.book.pages;

import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.common.network.data.DataNode;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class GuiPageError extends GuiPageCommon {

    ResourceLocation tex;
    List<String> dataLines;
    String dataStr;
    String errorCode;

    public GuiPageError(GuiScreen parent, DataNode data, DataNode globalData, String mod, String path, int page) {
        super(parent, data, globalData, mod, path, page);

        tex = new ResourceLocation(LibrarianLib.MODID, "textures/bookcomponents/book/error/" + data.get("type").asStringOr("error") + ".png");
        errorCode = data.get("errorCode").asString();
        dataStr = "";
        for (DataNode node : data.get("data").asList()) {
            dataStr += node.asString() + "\n";
        }
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
        if (dataLines == null) {
            dataLines = new ArrayList<>();
            List<String> list = mc.fontRendererObj.listFormattedStringToWidth(dataStr, viewWidth);
            for (String string : list) {
                dataLines.add(string);
            }
        }

        mc.renderEngine.bindTexture(new ResourceLocation("wizardry", "textures/bookcomponents/book/error/fof.png"));

        drawScaledCustomSizeModalRect((viewWidth / 2) - 50, 0, 0, 0, 100, 50, 100, 50, 100, 50);

        int codeWidth = mc.fontRendererObj.getStringWidth(errorCode);
        mc.fontRendererObj.drawString(errorCode, (viewWidth / 2) - (codeWidth / 2), 50, 0x000000);

        int y = 65;
        int x = 0;
        for (String line : dataLines) {
            mc.fontRendererObj.drawString(line, x, y, 0x000000);
            y += mc.fontRendererObj.FONT_HEIGHT;
        }
    }

}
