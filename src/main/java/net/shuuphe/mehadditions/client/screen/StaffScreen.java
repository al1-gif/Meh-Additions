package net.shuuphe.mehadditions.client.screen;

import com.shuuphe.mehorigins.RaceRegistry;
import com.shuuphe.mehorigins.client.ClientRaceData;
import com.shuuphe.mehorigins.race.Race;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.network.SelectRaceWithStaffPacket;
import net.shuuphe.mehadditions.util.StaffDataHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StaffScreen extends Screen {

    private static final Identifier TEX_BG       = Identifier.of("mehadditions", "textures/staffgui/background.png");
    private static final Identifier TEX_BORDER    = Identifier.of("mehadditions", "textures/staffgui/border.png");
    private static final Identifier TEX_NAMEPLATE = Identifier.of("mehadditions", "textures/staffgui/name_plate.png");
    private static final Identifier TEX_SCROLL    = Identifier.of("mehadditions", "textures/staffgui/scroll_bar.png");
    private static final Identifier TEX_SLOT      = Identifier.of("mehadditions", "textures/staffgui/slot.png");
    private static final Identifier TEX_ORB       = Identifier.of("mehadditions", "textures/staffgui/orb_of_origin.png");

    /**
     * Static icon map — replaces the switch-case in drawRaceIcon.
     * Initialised once at class-load; no per-frame allocation.
     */
    private static final Map<String, Identifier> RACE_ICONS = Map.ofEntries(
        Map.entry("fae",      Identifier.of("mehorigins", "textures/races/fae.png")),
        Map.entry("pixie",    Identifier.of("mehorigins", "textures/races/pixie.png")),
        Map.entry("arachnae", Identifier.of("mehorigins", "textures/races/arachnae.png")),
        Map.entry("alfiq",    Identifier.of("mehorigins", "textures/races/alfiq.png")),
        Map.entry("valkyrie", Identifier.of("mehorigins", "textures/races/valkyrie.png")),
        Map.entry("ogre",     Identifier.of("mehorigins", "textures/races/ogre.png")),
        Map.entry("siren",    Identifier.of("mehorigins", "textures/races/siren.png")),
        Map.entry("banshee",  Identifier.of("mehorigins", "textures/races/banshee.png")),
        Map.entry("wood_elf", Identifier.of("mehorigins", "textures/races/wood_elf.png")),
        Map.entry("dwarf",    Identifier.of("mehorigins", "textures/races/dwarf.png")),
        Map.entry("high_elf", Identifier.of("mehorigins", "textures/races/high_elf.png")),
        Map.entry("revenant", Identifier.of("mehorigins", "textures/races/revenant.png"))
    );

    /**
     * Static item-stack fallbacks — created once, never per-frame.
     */
    private static final Map<String, ItemStack> RACE_ITEMS = Map.of(
        "enderian", new ItemStack(Items.ENDER_PEARL),
        "elytrian", new ItemStack(Items.ELYTRA),
        "shulk",    new ItemStack(Items.SHULKER_SHELL),
        "avian",    new ItemStack(Items.FEATHER),
        "human",    new ItemStack(Items.PLAYER_HEAD)
    );
    private static final ItemStack FALLBACK_ITEM = new ItemStack(Items.BARRIER);

    private static final int PANEL_W    = 230;
    private static final int PANEL_H    = 182;
    private static final int LIST_PAD_X = 10;
    private static final int LIST_PAD_Y = 16;
    private static final int LIST_PAD_B = 10;
    private static final int PLATE_W    = 130;
    private static final int PLATE_H    = 24;
    private static final int PLATE_GAP  = 6;
    private static final int ORB_W      = 16;
    private static final int ORB_H      = 16;
    private static final int ICON_SIZE  = 16;
    private static final int BTN_W      = 44;
    private static final int BTN_H      = 18;
    private static final int SLOT_W     = 8;
    private static final int SCROLL_W   = 6;
    private static final int SCROLL_H   = 27;
    private static final int HEADER_H   = 16;
    private static final int DESC_PAD   = 4;
    private static final float ANIM_SPEED = 0.12f;

    private final List<Object> entries = new ArrayList<>();
    private ItemStack staffStack;

    private int scrollOffset    = 0;
    private boolean isDragging  = false;
    private int dragStartY      = 0;
    private int dragStartScroll = 0;

    private String expandedRaceId = null;
    private float  expandProgress = 0f;
    private boolean expanding     = false;

    /** Cached total content height — invalidated whenever entries or expansion changes. */
    private int cachedContentHeight = -1;

    public StaffScreen(ItemStack staffStack) {
        super(Text.literal("Origin Selector"));
        this.staffStack = staffStack;
    }

    private int panelX() { return this.width  / 2 - PANEL_W / 2; }
    private int panelY() { return this.height / 2 - PANEL_H / 2; }
    private int listX()  { return panelX() + LIST_PAD_X; }
    private int listY()  { return panelY() + LIST_PAD_Y; }
    private int listH()  { return PANEL_H - LIST_PAD_Y - LIST_PAD_B; }
    private int rowH()   { return PLATE_H + PLATE_GAP; }
    private int slotX()  { return panelX() + PANEL_W - SLOT_W - 9; }
    private int slotY()  { return listY(); }
    private int slotH()  { return listH(); }
    private int orbX()   { return listX() + PLATE_W + 4; }
    private int btnX()   { return orbX() + ORB_W + 4; }

    private int descMaxHeight(Race race) {
        if (this.textRenderer == null) return 60;
        List<String> lines = wrapText(race.getDescription(), PLATE_W - DESC_PAD * 2);
        int lineH = this.textRenderer.fontHeight + 2;
        return lines.size() * lineH + DESC_PAD * 2;
    }

    private int currentDescHeight(Race race) {
        return (int)(expandProgress * descMaxHeight(race));
    }

    private void invalidateContentHeight() {
        cachedContentHeight = -1;
    }

    private int totalContentHeight() {
        if (cachedContentHeight >= 0) return cachedContentHeight;
        int h = 0;
        for (Object e : entries) {
            if (e instanceof Text) {
                h += HEADER_H;
            } else {
                Race race = (Race) e;
                h += rowH();
                if (race.getId().equals(expandedRaceId)) {
                    h += currentDescHeight(race);
                }
            }
        }
        cachedContentHeight = h;
        return h;
    }

    private int maxScroll() {
        return Math.max(0, totalContentHeight() - listH());
    }

    private int thumbY() {
        int range = slotH() - SCROLL_H;
        return slotY() + (maxScroll() > 0 ? (int)(range * ((float) scrollOffset / maxScroll())) : 0);
    }

    private boolean isOwned(Race race) {
        return StaffDataHelper.hasRace(staffStack, race.getId());
    }

    private void refreshStaffStack() {
        var mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc.player == null) return;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            net.minecraft.item.ItemStack s = mc.player.getInventory().getStack(i);
            if (s.getItem() instanceof net.shuuphe.mehadditions.item.OriginStaffItem) {
                this.staffStack = s;
                return;
            }
        }
    }

    private void buildEntries() {
        refreshStaffStack();
        entries.clear();
        invalidateContentHeight();

        List<Race> all = new ArrayList<>(RaceRegistry.getAll());
        all.removeIf(r -> r.getId().equals("other") || r.getId().equals("OtherRace"));

        Race current = ClientRaceData.getLocalRace();
        List<Race> owned   = new ArrayList<>();
        List<Race> unowned = new ArrayList<>();

        for (Race r : all) {
            if (isOwned(r)) owned.add(r);
            else            unowned.add(r);
        }

        if (current != null) {
            owned.stream()
                    .filter(r -> r.getId().equals(current.getId()))
                    .findFirst().ifPresent(active -> {
                        owned.remove(active);
                        owned.add(0, active);
                    });
        }

        if (!owned.isEmpty()) {
            entries.add(Text.literal("— Owned Origins —").styled(s ->
                    s.withBold(true).withColor(0x55FF55)));
            entries.addAll(owned);
        }
        if (!unowned.isEmpty()) {
            entries.add(Text.literal("— Unowned Origins —").styled(s ->
                    s.withBold(true).withColor(0xFF5555)));
            entries.addAll(unowned);
        }
    }

    private void rebuildButtons() {
        this.clearChildren();
        Race current = ClientRaceData.getLocalRace();
        int y = listY() - scrollOffset;

        for (Object entry : entries) {
            if (entry instanceof Text) {
                y += HEADER_H;
            } else {
                Race race = (Race) entry;
                int btnY  = y + (PLATE_H - BTN_H) / 2;

                if (btnY >= listY() && btnY + BTN_H <= listY() + listH()) {
                    boolean isActive = current != null && current.getId().equals(race.getId());
                    boolean owned    = isOwned(race);

                    ButtonWidget btn;
                    if (owned) {
                        final Race r = race;
                        btn = ButtonWidget.builder(
                                        Text.literal("Select"),
                                        b -> { SelectRaceWithStaffPacket.send(r.getId()); this.close(); })
                                .dimensions(btnX(), btnY, BTN_W, BTN_H)
                                .build();
                        btn.active = !isActive;
                    } else {
                        final Race r = race;
                        btn = ButtonWidget.builder(
                                        Text.literal("Recipe"),
                                        b -> net.minecraft.client.MinecraftClient.getInstance()
                                                .setScreen(new AdditionsGuideScreen("recipe_" + r.getId())))
                                .dimensions(btnX(), btnY, BTN_W, BTN_H)
                                .build();
                    }
                    this.addDrawableChild(btn);
                }

                y += rowH();
                if (race.getId().equals(expandedRaceId)) {
                    y += currentDescHeight(race);
                }
            }
        }
    }

    @Override
    protected void init() {
        buildEntries();
        rebuildButtons();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_E) {
            this.close();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.gui.Click context, boolean consumed) {
        double mx = context.x();
        double my = context.y();

        if (context.button() == 0 && maxScroll() > 0) {
            if (isOverThumb(mx, my)) {
                isDragging = true; dragStartY = (int) my; dragStartScroll = scrollOffset;
                return true;
            }
            if (isOverSlot(mx, my)) {
                float frac = (float)((my - slotY()) / (slotH() - SCROLL_H));
                scrollOffset = (int) Math.max(0, Math.min(maxScroll(), frac * maxScroll()));
                invalidateContentHeight();
                rebuildButtons();
                return true;
            }
        }

        if (context.button() == 0) {
            int y = listY() - scrollOffset;
            for (Object entry : entries) {
                if (entry instanceof Text) {
                    y += HEADER_H;
                } else {
                    Race race = (Race) entry;
                    if (y + PLATE_H > listY() && y < listY() + listH()) {
                        if (mx >= listX() && mx <= listX() + PLATE_W
                                && my >= y && my <= y + PLATE_H
                                && isOwned(race)) {
                            toggleExpand(race);
                            return true;
                        }
                    }
                    y += rowH();
                    if (race.getId().equals(expandedRaceId)) {
                        y += currentDescHeight(race);
                    }
                }
            }
        }

        return super.mouseClicked(context, consumed);
    }

    private void toggleExpand(Race race) {
        if (expandedRaceId != null && expandedRaceId.equals(race.getId())) {
            expanding = false;
        } else {
            expandedRaceId = race.getId();
            expandProgress = 0f;
            expanding = true;
        }
        invalidateContentHeight();
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.gui.Click context, double dx, double dy) {
        if (isDragging && context.button() == 0 && maxScroll() > 0) {
            float scrollPerPixel = (float) maxScroll() / (slotH() - SCROLL_H);
            int delta = (int) context.y() - dragStartY;
            scrollOffset = (int) Math.max(0, Math.min(maxScroll(), dragStartScroll + delta * scrollPerPixel));
            rebuildButtons();
            return true;
        }
        return super.mouseDragged(context, dx, dy);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.gui.Click context) {
        if (context.button() == 0 && isDragging) { isDragging = false; return true; }
        return super.mouseReleased(context);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmt, double vAmt) {
        scrollOffset = (int) Math.max(0, Math.min(maxScroll(), scrollOffset - (int) Math.signum(vAmt) * rowH()));
        rebuildButtons();
        return true;
    }

    private boolean isOverThumb(double mx, double my) {
        return mx >= slotX() + 1 && mx <= slotX() + 1 + SCROLL_W
                && my >= thumbY() && my <= thumbY() + SCROLL_H;
    }

    private boolean isOverSlot(double mx, double my) {
        return mx >= slotX() && mx <= slotX() + SLOT_W
                && my >= slotY() && my <= slotY() + slotH();
    }

    private void drawRaceIcon(DrawContext context, Race race, int x, int y) {
        Identifier icon = RACE_ICONS.get(race.getId());
        if (icon != null) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED,
                    icon, x, y, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        } else {
            context.drawItem(RACE_ITEMS.getOrDefault(race.getId(), FALLBACK_ITEM), x, y);
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int cx = this.width / 2;

        boolean needsRebuild = false;
        if (expandedRaceId != null) {
            if (expanding && expandProgress < 1f) {
                expandProgress = Math.min(1f, expandProgress + ANIM_SPEED);
                invalidateContentHeight();
                needsRebuild = true;
            } else if (!expanding && expandProgress > 0f) {
                expandProgress = Math.max(0f, expandProgress - ANIM_SPEED);
                invalidateContentHeight();
                needsRebuild = true;
                if (expandProgress == 0f) expandedRaceId = null;
            }
        }
        if (needsRebuild) rebuildButtons();

        float eased = expandProgress < 0.5f
                ? 2f * expandProgress * expandProgress
                : 1f - (-2f * expandProgress + 2f) * (-2f * expandProgress + 2f) / 2f;

        context.fill(0, 0, this.width, this.height, 0xAA000000);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEX_BG,
                panelX(), panelY(), 0f, 0f, PANEL_W, PANEL_H, PANEL_W, PANEL_H);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEX_BORDER,
                panelX(), panelY(), 0f, 0f, PANEL_W, PANEL_H, PANEL_W, PANEL_H);

        String title = "Origin Selector";
        context.drawText(this.textRenderer, title,
                cx - this.textRenderer.getWidth(title) / 2,
                panelY() - 14, 0xFFFFFFFF, true);

        context.enableScissor(listX(), listY(), slotX() - 2, panelY() + PANEL_H - LIST_PAD_B);

        Race current = ClientRaceData.getLocalRace();
        int y = listY() - scrollOffset;

        for (Object entry : entries) {
            if (entry instanceof Text label) {
                if (y + HEADER_H > listY() && y < listY() + listH()) {
                    context.drawText(this.textRenderer, label,
                            listX() + 8,
                            y + (HEADER_H - this.textRenderer.fontHeight) / 2,
                            0xFFFFFFFF, true);
                }
                y += HEADER_H;
            } else {
                Race race        = (Race) entry;
                boolean isActive = current != null && current.getId().equals(race.getId());
                boolean owned    = isOwned(race);
                boolean isExpanded = race.getId().equals(expandedRaceId);

                if (y + PLATE_H > listY() && y < listY() + listH()) {
                    context.drawTexture(RenderPipelines.GUI_TEXTURED, TEX_NAMEPLATE,
                            listX(), y, 0f, 0f, PLATE_W, PLATE_H, PLATE_W, PLATE_H);

                    drawRaceIcon(context, race, listX() + 4, y + (PLATE_H - ICON_SIZE) / 2);

                    String name = race.getDisplayName();
                    int nameColor = (owned && isExpanded) ? 0xFFFFDD44 : 0xFFFFFFFF;
                    context.drawText(this.textRenderer, name,
                            listX() + ICON_SIZE + 8,
                            y + (PLATE_H - this.textRenderer.fontHeight) / 2,
                            nameColor, true);

                    if (isActive) {
                        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEX_ORB,
                                orbX(), y + (PLATE_H - ORB_H) / 2,
                                0f, 0f, ORB_W, ORB_H, ORB_W, ORB_H);
                    }

                    if (owned) {
                        String hint = isExpanded ? "▲" : "▼";
                        context.drawText(this.textRenderer, hint,
                                listX() + PLATE_W - this.textRenderer.getWidth(hint) - 4,
                                y + (PLATE_H - this.textRenderer.fontHeight) / 2,
                                0xFFAAAAAA, false);
                    }
                }

                y += rowH();

                if (isExpanded && expandProgress > 0f) {
                    int descH = (int)(eased * descMaxHeight(race));
                    if (descH > 0 && y < listY() + listH()) {
                        context.fill(listX(), y, listX() + PLATE_W, y + descH, 0x88000000);

                        context.disableScissor();
                        context.enableScissor(
                                listX() + DESC_PAD,
                                Math.max(listY(), y),
                                listX() + PLATE_W - DESC_PAD,
                                Math.min(panelY() + PANEL_H - LIST_PAD_B, y + descH));

                        List<String> lines = wrapText(race.getDescription(), PLATE_W - DESC_PAD * 2);
                        int lineH  = this.textRenderer.fontHeight + 2;
                        int lineY  = y + DESC_PAD;
                        for (String line : lines) {
                            if (lineY + this.textRenderer.fontHeight > y + descH) break;
                            boolean isHeader = line.contains("§n");
                            context.drawText(this.textRenderer, line,
                                    listX() + DESC_PAD, lineY,
                                    isHeader ? 0xFFFFFFFF : 0xFFDDDDDD, true);
                            lineY += lineH;
                        }

                        context.disableScissor();
                        context.enableScissor(listX(), listY(), slotX() - 2, panelY() + PANEL_H - LIST_PAD_B);
                    }
                    y += currentDescHeight(race);
                }
            }
        }

        context.disableScissor();

        if (maxScroll() > 0) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEX_SLOT,
                    slotX(), slotY(), 0f, 0f, SLOT_W, slotH(), SLOT_W, slotH());
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEX_SCROLL,
                    slotX() + 1, thumbY(), 0f, 0f, SCROLL_W, SCROLL_H, SCROLL_W, SCROLL_H);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) return lines;
        for (String paragraph : text.split("\n")) {
            if (paragraph.isBlank()) { lines.add(""); continue; }
            StringBuilder cur = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                String test = cur.isEmpty() ? word : cur + " " + word;
                if (this.textRenderer.getWidth(test) > maxWidth) {
                    if (!cur.isEmpty()) { lines.add(cur.toString()); cur = new StringBuilder(word); }
                    else lines.add(word);
                } else {
                    if (!cur.isEmpty()) cur.append(' ');
                    cur.append(word);
                }
            }
            if (!cur.isEmpty()) lines.add(cur.toString());
        }
        return lines;
    }

    @Override public boolean shouldPause()      { return false; }
    @Override public boolean shouldCloseOnEsc() { return true; }
}
