package net.shuuphe.mehadditions.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.ModEffects;
import net.shuuphe.mehadditions.item.Hanafubuki;

public class HudRenderer {

    private static final Identifier RESOURCE_BAR_03 =
            Identifier.of("mehorigins", "textures/gui/resource_bar_03.png");
    private static final Identifier BLOOD_OVERLAY =
            Identifier.of("mehadditions", "textures/misc/lowhealth0.png");

    private static final int TOTAL_W   = 80;
    private static final int BAR_MAX_W = 71;
    private static final int ROW_H     = 5;
    private static final int V_BG      = 0;
    private static final int V_FILL    = 20;

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            int screenW = client.getWindow().getScaledWidth();
            int screenH = client.getWindow().getScaledHeight();

            if (client.player.hasStatusEffect(ModEffects.BLOOD_LOSS)) {
                int duration = client.player.getStatusEffect(ModEffects.BLOOD_LOSS).getDuration();
                float pulse = (float)(Math.sin(System.currentTimeMillis() / 300.0) * 0.15 + 0.65);
                float alpha = Math.min(1f, duration / 120f) * pulse;
                int a = (int)(alpha * 255) & 0xFF;
                int color = (a << 24) | 0x00FFFFFF;

                context.drawTexture(RenderPipelines.GUI_TEXTURED,
                        BLOOD_OVERLAY, 0, 0, 0f, 0f,
                        screenW, screenH, screenW, screenH, color);
            }

            if (!client.player.isUsingItem()) return;
            if (!(client.player.getActiveItem().getItem() instanceof Hanafubuki)) return;

            int elapsed = Hanafubuki.MAX_USE_TICKS - client.player.getItemUseTimeLeft();
            float progress = Math.min((float) elapsed / Hanafubuki.CHARGE_TICKS, 1f);

            int barX = screenW / 2 - 91;
            int barY = screenH - 55;

            context.drawTexture(RenderPipelines.GUI_TEXTURED,
                    RESOURCE_BAR_03, barX, barY,
                    0, V_BG, TOTAL_W, ROW_H, 256, 256);

            int fillW = Math.round(progress * BAR_MAX_W);
            if (fillW > 0) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED,
                        RESOURCE_BAR_03, barX, barY,
                        0, V_FILL, fillW, ROW_H, 256, 256);
            }
        });
    }
}