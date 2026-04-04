package net.shuuphe.mehadditions.client.screen;

import com.shuuphe.mehorigins.client.screen.RaceGuideScreen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.List;

public class AdditionsGuideScreen extends RaceGuideScreen {

    private static final String IMG_PREFIX = "\u00a7IMG\u00a7";
    private static final int IMG_W = 160;
    private static final int IMG_H = 90;

    private final String scrollToAnchor;
    private final java.util.Map<String, Integer> anchors = new java.util.HashMap<>();

    public AdditionsGuideScreen() { this(null); }

    public AdditionsGuideScreen(String scrollToAnchor) {
        super();
        this.scrollToAnchor = scrollToAnchor;
        buildAdditionsLines();
    }

    private void anchor(String key) { anchors.put(key, lines.size()); }

    private void image(String name) {
        stat(0xFFFFFFFF, IMG_PREFIX + name);
    }

    private boolean isImageEntry(Entry e) {
        return e instanceof TextEntry te && te.text().startsWith(IMG_PREFIX);
    }

    @Override
    protected int entryHeight(Entry e) {
        return isImageEntry(e) ? IMG_H + 6 : super.entryHeight(e);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int cx    = this.width / 2;
        int px    = cx - 180;
        int textX = px + 12;

        context.fill(0, 0, this.width, this.height, 1426063360);
        context.fill(0, 0, this.width, 1, -14540254);
        context.fill(0, this.height - 1, this.width, this.height, -14540254);

        String title = "✦  Meh-Origin  ─  Race Guide  ✦";
        context.drawText(this.textRenderer, title,
                cx - this.textRenderer.getWidth(title) / 2, 8, -10496, true);

        int bodyTop = 22;
        int bodyBot = this.height - 28;
        int bodyW   = 326;

        List<Entry> wrapped = buildWrapped(bodyW);
        int totalH = 0;
        for (Entry e : wrapped) totalH += entryHeight(e);

        int visibleH = bodyBot - bodyTop;
        this.maxScrollPx = Math.max(0, totalH - visibleH);
        this.scrollPx    = Math.max(0, Math.min(this.scrollPx, this.maxScrollPx));

        context.enableScissor(0, bodyTop, this.width, bodyBot);
        int lineY = bodyTop - this.scrollPx;

        for (Entry entry : wrapped) {
            int h = entryHeight(entry);
            if (lineY + h > bodyTop && lineY < bodyBot) {
                if (isImageEntry(entry)) {
                    String name = ((TextEntry) entry).text().substring(IMG_PREFIX.length());
                    Identifier tex = Identifier.of("mehadditions", "textures/guide/" + name + ".png");
                    context.drawTexture(RenderPipelines.GUI_TEXTURED,
                            tex, textX, lineY + 3,
                            0f, 0f, IMG_W, IMG_H, IMG_W, IMG_H);
                } else if (entry instanceof TextEntry te) {
                    String txt  = te.text();
                    boolean isH = txt.startsWith("§n");
                    String draw = isH ? txt.substring(2) : txt;
                    context.drawText(this.textRenderer, draw, textX, lineY, te.color(), isH);
                }
            }
            lineY += h;
        }

        context.disableScissor();

        if (this.maxScrollPx > 0) {
            int trackX     = px + 360 - 6 - 6;
            int thumbH     = Math.max(20, (int)((float) visibleH / totalH * visibleH));
            int thumbRange = visibleH - thumbH;
            int thumbY     = bodyTop + (thumbRange > 0
                    ? (int)((float) this.scrollPx / this.maxScrollPx * thumbRange) : 0);
            context.fill(trackX, bodyTop, trackX + 6, bodyBot, -15658735);
            boolean over = mouseX >= trackX && mouseX <= trackX + 6
                    && mouseY >= thumbY && mouseY <= thumbY + thumbH;
            context.fill(trackX, thumbY, trackX + 6, thumbY + thumbH,
                    over ? -8947849 : -12303292);
        }

        for (var child : this.children()) {
            if (child instanceof Drawable d) d.render(context, mouseX, mouseY, delta);
        }
    }

    private void buildAdditionsLines() {
        spacer();
        stat(COL_GREEN, "════  Meh Additions  ════");
        gap();
        stat(COL_RED, "The following content is added by the Meh Additions mod.");
        spacer();

        stat(COL_GREEN, "─  Meh Origins Table  ─");
        body("  A special crafting table used to craft meh origin tools and upgrade the Staff of Origins.");
        image("meh_origin_table");
        spacer();

        stat(COL_GREEN, "─  Staff of Origins  ─");
        body("  Crafted in the Meh Origins Table. Holds unlocked races and lets you switch between them.");
        image("origin_staff");
        gap();
        body("  Each race switch costs 1 charge. The staff has 250 charges.");
        spacer();

        anchor("recipes");
        stat(COL_GREEN, "─  Origin Unlock Recipes  ─");
        body("  All upgrades are crafted in the Meh Origins Table.");
        spacer();

        anchor("recipe_alfiq");    stat(COL_YELLOW, "  Alfiq");    image("alfiq");    gap();
        anchor("recipe_arachnae"); stat(COL_YELLOW, "  Arachnae"); image("arachnae"); gap();
        anchor("recipe_avian");    stat(COL_YELLOW, "  Avian");    image("avian");    gap();
        anchor("recipe_elytrian"); stat(COL_YELLOW, "  Elytrian"); image("elytrian"); gap();
        anchor("recipe_enderian"); stat(COL_YELLOW, "  Enderian"); image("enderian"); gap();
        anchor("recipe_fae");      stat(COL_YELLOW, "  Fae");      image("fae");      gap();
        anchor("recipe_human");    stat(COL_YELLOW, "  Human");    image("human");    gap();
        anchor("recipe_pixie");    stat(COL_YELLOW, "  Pixie");    image("pixie");    gap();
        anchor("recipe_revenant"); stat(COL_YELLOW, "  Revenant"); image("revenant"); gap();
        anchor("recipe_shulk");    stat(COL_YELLOW, "  Shulk");    image("shulk");    gap();

        anchor("recipe_shulk");    stat(COL_YELLOW, "  Shulk");    image("shulk");    gap();

        spacer();
        stat(COL_GREEN, "════  Tools  ════");
        spacer();

        stat(COL_YELLOW, "  Catalyst");          image("catalyst");        gap();
        stat(COL_YELLOW, "  Freedom Sworn");     image("freedom_sworn");   gap();
        stat(COL_YELLOW, "  Lumidouce Elegy");   image("lumidouce_elegy"); gap();
        stat(COL_YELLOW, "  Skyward Harp");      image("skyward_harp");    gap();
        stat(COL_YELLOW, "  Amos Bow");          image("amos_bow");        gap();
        stat(COL_YELLOW, "  Small Rune Pouch");  image("small_pouch");     gap();
        stat(COL_YELLOW, "  Large Rune Pouch");  image("large_pouch");     gap();

        spacer();
        stat(COL_GREEN, "════  Crafting Altar  ════");
        spacer();
        image("crafting_altar");
        spacer();

        stat(COL_YELLOW, "  Fire Rune");         image("fire_rune");       gap();
        stat(COL_YELLOW, "  Frost Rune");        image("frost_rune");      gap();
        stat(COL_YELLOW, "  Lightning Rune");    image("lightning_rune");  gap();
    }

    @Override
    protected void init() {
        super.init();
        if (scrollToAnchor == null) return;
        Integer idx = anchors.get(scrollToAnchor);
        if (idx == null) return;

        int bodyW  = 360 - 12 * 2 - 6 - 4;
        int target = 0;
        int i      = 0;
        for (Entry entry : lines) {
            if (i >= idx) break;
            if (isImageEntry(entry)) {
                target += entryHeight(entry);
            } else {
                TextEntry te = (TextEntry) entry;
                if (te.text().isEmpty()) {
                    target += super.entryHeight(te);
                } else {
                    boolean isH = te.text().startsWith("§n");
                    String raw  = isH ? te.text().substring(2) : te.text();
                    int rows    = wrapText(raw, bodyW).size();
                    target += rows * super.entryHeight(te);
                }
            }
            i++;
        }
        scrollPx = Math.max(0, target - 20);
    }
}