package net.shuuphe.mehadditions.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.MehAdditions;
import net.shuuphe.mehadditions.screen.CraftingAltarScreenHandler;

@Environment(EnvType.CLIENT)
public class CraftingAltarScreen extends HandledScreen<CraftingAltarScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.of(MehAdditions.MOD_ID, "textures/gui/crafting_altar.png");

    public CraftingAltarScreen(CraftingAltarScreenHandler handler,
                               PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE,
                x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}