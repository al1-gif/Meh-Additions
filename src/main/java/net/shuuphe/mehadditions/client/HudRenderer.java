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
    private static final Identifier HUD_1 =
            Identifier.of("mehadditions", "textures/gui/rev_hud.png");
    private static final Identifier HUD_2 =
            Identifier.of("mehadditions", "textures/gui/rev_hud1.png");
    private static final Identifier HUD_3 =
            Identifier.of("mehadditions", "textures/gui/rev_hud3.png");

    private static final int TOTAL_W   = 80;
    private static final int BAR_MAX_W = 71;
    private static final int ROW_H     = 5;
    private static final int V_BG      = 0;
    private static final int V_FILL    = 20;

    private static final int PANEL_W    = 145;
    private static final int PANEL_PAD  = 6;
    private static final int HEAD_SIZE  = 16;
    private static final int LINE_H     = 20;
    private static final int MAX_ROWS   = 3;
    private static final int MODE_BOX_W = 56;
    private static final int SEC_TOP_H  = 21;
    private static final int SEC_BOT_H  = 21;
    private static final int PANEL_H_1  = 68;
    private static final int PANEL_H_2  = 88;
    private static final int PANEL_H_3  = 108;

    public static void register() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.options.hudHidden) return;

            int screenW = client.getWindow().getScaledWidth();
            int screenH = client.getWindow().getScaledHeight();

            if (client.player.hasStatusEffect(ModEffects.BLOOD_LOSS)) {
                int duration = client.player.getStatusEffect(ModEffects.BLOOD_LOSS).getDuration();
                float pulse  = (float)(Math.sin(System.currentTimeMillis() / 300.0) * 0.15 + 0.65);
                float alpha  = Math.min(1f, duration / 120f) * pulse;
                int a        = (int)(alpha * 255) & 0xFF;
                context.drawTexture(RenderPipelines.GUI_TEXTURED,
                        BLOOD_OVERLAY, 0, 0, 0f, 0f,
                        screenW, screenH, screenW, screenH,
                        (a << 24) | 0x00FFFFFF);
            }

            if (client.player.isUsingItem()
                    && client.player.getActiveItem().getItem() instanceof Hanafubuki) {
                int elapsed    = Hanafubuki.MAX_USE_TICKS - client.player.getItemUseTimeLeft();
                float progress = Math.min((float) elapsed / Hanafubuki.CHARGE_TICKS, 1f);
                int barX = screenW / 2 - 91;
                int barY = screenH - 55;
                context.drawTexture(RenderPipelines.GUI_TEXTURED,
                        RESOURCE_BAR_03, barX, barY, 0, V_BG, TOTAL_W, ROW_H, 256, 256);
                int fillW = Math.round(progress * BAR_MAX_W);
                if (fillW > 0) {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED,
                            RESOURCE_BAR_03, barX, barY, 0, V_FILL, fillW, ROW_H, 256, 256);
                }
            }

            if (client.getDebugHud().shouldShowDebugHud()) return;

            var main        = client.player.getMainHandStack();
            var off         = client.player.getOffHandStack();
            var tranceStack = main.isOf(ModItems.TRANCE) ? main
                    : off.isOf(ModItems.TRANCE)   ? off
                      : null;
            if (tranceStack == null) return;

            String  mode    = TranceDataManager.getMode(tranceStack);
            boolean capture = mode.equals(TranceDataManager.MODE_CAPTURE);
            boolean attack  = mode.equals(TranceDataManager.MODE_ATTACK);
            if (attack) return;

            TextRenderer tr   = client.textRenderer;
            List<String> mobs = TranceDataManager.getStoredMobs(tranceStack);
            int used          = TranceDataManager.getUsedPoints(tranceStack);
            int sel           = TranceDataManager.getSelected(tranceStack);
            int capacity      = TranceMobRegistry.CAPACITY_DEFAULT;
            int mobCount      = mobs.size();

            Identifier panelTex;
            int panelH;
            if (mobCount >= 3) {
                panelTex = HUD_3;
                panelH   = PANEL_H_3;
            } else if (mobCount == 2) {
                panelTex = HUD_2;
                panelH   = PANEL_H_2;
            } else {
                panelTex = HUD_1;
                panelH   = PANEL_H_1;
            }

            int panelX = screenW - PANEL_W - 8;
            int panelY = 8;

            context.drawTexture(RenderPipelines.GUI_TEXTURED,
                    panelTex, panelX, panelY, 0f, 0f,
                    PANEL_W, panelH, PANEL_W, panelH);
            String modeText = capture ? "§cCAPTURE" : "§aSUMMON";
            int modeTextX   = panelX + (MODE_BOX_W - tr.getWidth(modeText)) / 2;
            int modeTextY   = panelY + (SEC_TOP_H - tr.fontHeight) / 2;
            context.drawText(tr, modeText, modeTextX, modeTextY, 0xFFFFFFFF, true);
            int midTop = panelY + SEC_TOP_H;
            int midBot = panelY + panelH - SEC_BOT_H;
            int midH   = midBot - midTop;
            if (mobs.isEmpty()) {
                int noMobsY = midTop + (midH - tr.fontHeight) / 2;
                int noMobsX = panelX + (PANEL_W - tr.getWidth("No mobs stored")) / 2;
                context.drawText(tr, "§cNo mobs stored", noMobsX, noMobsY, 0xFFFFFFFF, true);
            } else {
                int visibleRows  = Math.min(mobCount, MAX_ROWS);
                int totalRowsH   = visibleRows * LINE_H;
                int rowsStartY   = midTop + (midH - totalRowsH) / 2;

                int start = Math.max(0, sel - 1);
                if (start + MAX_ROWS > mobCount) start = Math.max(0, mobCount - MAX_ROWS);
                int end = Math.min(mobCount, start + MAX_ROWS);

                for (int i = start; i < end; i++) {
                    String typeId      = mobs.get(i);
                    boolean isSelected = (i == sel);
                    String mobName     = getMobName(typeId);
                    int rowY           = rowsStartY + (i - start) * LINE_H;

                    if (isSelected) {
                        context.fill(panelX + 1, rowY, panelX + PANEL_W - 1, rowY + LINE_H, 0x33FFFFFF);
                    }

                    Identifier headTex = getHeadTexture(typeId);
                    int headY = rowY + (LINE_H - HEAD_SIZE) / 2;
                    context.drawTexture(RenderPipelines.GUI_TEXTURED,
                            headTex, panelX + PANEL_PAD, headY,
                            0f, 0f, HEAD_SIZE, HEAD_SIZE, HEAD_SIZE, HEAD_SIZE);

                    int textX = panelX + PANEL_PAD + HEAD_SIZE + 4;
                    int textY = rowY + (LINE_H - tr.fontHeight) / 2;

                    if (isSelected) {
                        context.drawText(tr, "§f§l" + mobName, textX, textY, 0xFFFFFFFF, true);
                    } else {
                        context.drawText(tr, "§8" + mobName, textX, textY, 0xFFFFFFFF, true);
                    }
                }
            }

            int footerAreaTop = panelY + panelH - SEC_BOT_H;
            int footerTextY   = footerAreaTop + (SEC_BOT_H - tr.fontHeight) / 2;

            String capText    = "§7Cap: §f" + used + "§7/§f" + capacity;
            String storedText = "§7Mobs: §f" + mobCount;
            int storedW       = tr.getWidth(storedText);

            context.drawText(tr, capText,    panelX + PANEL_PAD,                     footerTextY, 0xFFFFFFFF, true);
            context.drawText(tr, storedText, panelX + PANEL_W - PANEL_PAD - storedW, footerTextY, 0xFFFFFFFF, true);
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