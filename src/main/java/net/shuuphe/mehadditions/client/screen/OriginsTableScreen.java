package net.shuuphe.mehadditions.client.screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.screen.OriginsTableScreenHandler;

public class OriginsTableScreen extends HandledScreen<OriginsTableScreenHandler> {

    private static final Identifier TEXTURE =
            Identifier.ofVanilla("textures/gui/container/crafting_table.png");

    public OriginsTableScreen(OriginsTableScreenHandler handler,
                              PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth  = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = 29;
        this.titleY = 6;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width  - this.backgroundWidth)  / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED,
                TEXTURE, x, y, 0, 0,
                this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}