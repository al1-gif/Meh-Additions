package net.shuuphe.mehadditions.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.ModEffects;
import net.shuuphe.mehadditions.ModItems;
import net.shuuphe.mehadditions.item.Hanafubuki;
import net.shuuphe.mehadditions.trance.TranceDataManager;
import net.shuuphe.mehadditions.trance.TranceMobRegistry;

import java.util.List;

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

    private static final int PANEL_W   = 145;
    private static final int PANEL_PAD = 6;
    private static final int HEAD_SIZE = 16;
    private static final int LINE_H    = 20;
    private static final int HEADER_H  = 11;
    private static final int FOOTER_H  = 10;
    private static final int MAX_ROWS  = 3;

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

            if (client.player.isUsingItem()
                    && client.player.getActiveItem().getItem() instanceof Hanafubuki) {
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
            }

            if (client.getDebugHud().shouldShowDebugHud()) return;
            var main = client.player.getMainHandStack();
            var off  = client.player.getOffHandStack();
            var tranceStack = main.isOf(ModItems.TRANCE) ? main
                    : off.isOf(ModItems.TRANCE) ? off
                      : null;
            if (tranceStack == null) return;

            TextRenderer tr   = client.textRenderer;
            List<String> mobs = TranceDataManager.getStoredMobs(tranceStack);
            int used          = TranceDataManager.getUsedPoints(tranceStack);
            int sel           = TranceDataManager.getSelected(tranceStack);
            String mode       = TranceDataManager.getMode(tranceStack);
            boolean capture   = mode.equals(TranceDataManager.MODE_CAPTURE);
            int capacity      = TranceMobRegistry.CAPACITY_DEFAULT;

            int visibleCount  = Math.min(Math.max(mobs.size(), 1), MAX_ROWS);
            int contentH      = visibleCount * LINE_H;
            int panelH        = PANEL_PAD + HEADER_H
                    + PANEL_PAD
                    + 1
                    + PANEL_PAD
                    + contentH
                    + PANEL_PAD
                    + 1
                    + PANEL_PAD
                    + FOOTER_H
                    + PANEL_PAD;

            int panelX = screenW - PANEL_W - 8;
            int panelY = 8;

            context.fill(panelX, panelY, panelX + PANEL_W, panelY + panelH, 0xB0000000);
            context.fill(panelX,               panelY,              panelX + PANEL_W,    panelY + 1,          0x55FFFFFF);
            context.fill(panelX,               panelY + panelH - 1, panelX + PANEL_W,    panelY + panelH,     0x55FFFFFF);
            context.fill(panelX,               panelY,              panelX + 1,           panelY + panelH,     0x55FFFFFF);
            context.fill(panelX + PANEL_W - 1, panelY,              panelX + PANEL_W,     panelY + panelH,     0x55FFFFFF);

            int cy = panelY + PANEL_PAD;

            String modeText = capture ? "§cCAPTURE" : "§aSUMMON";
            context.drawText(tr, modeText, panelX + PANEL_PAD + 4, cy, 0xFFFFFFFF, true);
            cy += HEADER_H + PANEL_PAD;

            context.fill(panelX + PANEL_PAD, cy, panelX + PANEL_W - PANEL_PAD, cy + 1, 0x44FFFFFF);
            cy += 1 + PANEL_PAD;

            if (mobs.isEmpty()) {
                int textY = cy + (LINE_H - tr.fontHeight) / 2;
                context.drawText(tr, "§cNo mobs stored", panelX + PANEL_PAD + HEAD_SIZE + 4, textY, 0xFFFFFFFF, true);
                cy += LINE_H;
            } else {
                int start = Math.max(0, sel - 1);
                if (start + MAX_ROWS > mobs.size()) {
                    start = Math.max(0, mobs.size() - MAX_ROWS);
                }
                int end = Math.min(mobs.size(), start + MAX_ROWS);

                for (int i = start; i < end; i++) {
                    String typeId      = mobs.get(i);
                    boolean isSelected = (i == sel);
                    String mobName     = getMobName(typeId);
                    if (isSelected) {
                        context.fill(panelX + 1, cy, panelX + PANEL_W - 1, cy + LINE_H, 0x33FFFFFF);
                    }
                    Identifier headTex = getHeadTexture(typeId);
                    int headY = cy + (LINE_H - HEAD_SIZE) / 2;
                    context.drawTexture(RenderPipelines.GUI_TEXTURED,
                            headTex,
                            panelX + PANEL_PAD, headY,
                            0f, 0f,
                            HEAD_SIZE, HEAD_SIZE,
                            HEAD_SIZE, HEAD_SIZE);

                    int textX = panelX + PANEL_PAD + HEAD_SIZE + 4;
                    int textY = cy + (LINE_H - tr.fontHeight) / 2;

                    if (isSelected) {
                        context.drawText(tr, "§f§l" + mobName, textX, textY, 0xFFFFFFFF, true);
                    } else {
                        context.drawText(tr, "§8" + mobName, textX, textY, 0xFFFFFFFF, true);
                    }

                    cy += LINE_H;
                }
            }

            cy += PANEL_PAD;
            context.fill(panelX + PANEL_PAD, cy, panelX + PANEL_W - PANEL_PAD, cy + 1, 0x44FFFFFF);
            cy += 1 + PANEL_PAD;
            String capText    = "§7Cap: §f" + used + "§7/§f" + capacity;
            String storedText = "§7Mobs: §f" + mobs.size();
            int storedW       = tr.getWidth(storedText);

            context.drawText(tr, capText,    panelX + PANEL_PAD,                     cy, 0xFFFFFFFF, true);
            context.drawText(tr, storedText, panelX + PANEL_W - PANEL_PAD - storedW, cy, 0xFFFFFFFF, true);
        });
    }

    private static Identifier getHeadTexture(String typeId) {
        String name = typeId.contains(":") ? typeId.split(":")[1] : typeId;
        return Identifier.of("mehadditions", "textures/mob_head/" + name + ".png");
    }

    private static String getMobName(String typeId) {
        try {
            var type = Registries.ENTITY_TYPE.get(Identifier.of(typeId));
            if (type != null) return type.getName().getString();
        } catch (Exception ignored) {}
        return typeId;
    }
}