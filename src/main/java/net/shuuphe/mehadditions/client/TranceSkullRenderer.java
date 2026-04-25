package net.shuuphe.mehadditions.client;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.shuuphe.mehadditions.ModItems;
import net.shuuphe.mehadditions.item.TranceItem;
import net.shuuphe.mehadditions.trance.TranceDataManager;

public class TranceSkullRenderer {

    private static final Identifier TEXTURE =
            Identifier.of("mehadditions", "textures/entity/trance_skull.png");

    private static final float[][] ALL_POSITIONS = {
            {  0.00f, 2.90f, -0.40f },
            { -0.60f, 2.35f, -0.40f },
            {  0.60f, 2.35f, -0.40f },
            { -0.35f, 1.70f, -0.40f },
            {  0.35f, 1.70f, -0.40f },
    };

    private static Trance_Skull model;

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(TranceSkullRenderer::render);
    }

    private static void render(WorldRenderContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        if (model == null) {
            model = new Trance_Skull(Trance_Skull.getTexturedModelData().createModel());
        }

        VertexConsumerProvider consumers = ctx.consumers();
        if (consumers == null) return;

        VertexConsumer vertexConsumer = consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));

        float tickDelta = client.getRenderTickCounter().getTickProgress(true);
        Vec3d camPos = client.gameRenderer.getCamera().getPos();
        MatrixStack matrices = ctx.matrices();
        int light = 0xF000F0;

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            ItemStack main = player.getMainHandStack();
            ItemStack off  = player.getOffHandStack();
            ItemStack tranceStack = main.isOf(ModItems.TRANCE) ? main
                    : off.isOf(ModItems.TRANCE) ? off : null;
            if (tranceStack == null) continue;

            if (!TranceDataManager.getMode(tranceStack).equals(TranceDataManager.MODE_ATTACK)) continue;

            int charges = TranceDataManager.getAttackCharges(tranceStack);
            if (charges < 0) charges = TranceItem.MAX_CHARGES_DEFAULT;
            int skullCount = Math.min(charges, ALL_POSITIONS.length);
            if (skullCount <= 0) continue;

            Vec3d playerPos = player.getLerpedPos(tickDelta);
            float bodyYaw = player.getBodyYaw();
            float headYaw = player.getHeadYaw();

            float yawRad = (float) Math.toRadians(bodyYaw);
            float cosYaw = (float) Math.cos(yawRad);
            float sinYaw = (float) Math.sin(yawRad);

            for (int i = 0; i < skullCount; i++) {
                float[] p = ALL_POSITIONS[i];

                float rx = p[0] * cosYaw - p[2] * sinYaw;
                float rz = p[0] * sinYaw + p[2] * cosYaw;

                double dx = playerPos.x + rx - camPos.x;
                double dy = playerPos.y + p[1] - camPos.y;
                double dz = playerPos.z + rz - camPos.z;

                matrices.push();
                matrices.translate(dx, dy, dz);
                matrices.scale(0.55f, 0.55f, 0.55f);
                matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(180f));
                matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(-90f));
                matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(headYaw - 180f));

                model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

                matrices.pop();
            }
        }
    }
}