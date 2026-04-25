package net.shuuphe.mehadditions.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.MehAdditions;
import net.shuuphe.mehadditions.ModItems;
import net.shuuphe.mehadditions.trance.TranceDataManager;

public record TranceScrollPacket(int direction) implements CustomPayload {

    public static final CustomPayload.Id<TranceScrollPacket> ID =
            new CustomPayload.Id<>(Identifier.of(MehAdditions.MOD_ID, "trance_scroll"));

    public static final PacketCodec<PacketByteBuf, TranceScrollPacket> CODEC =
            PacketCodecs.INTEGER.xmap(TranceScrollPacket::new, TranceScrollPacket::direction)
                    .cast();

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, ctx) -> {
            ServerPlayerEntity player = ctx.player();
            ctx.server().execute(() -> {
                ItemStack stack = getTranceStack(player);
                if (stack == null) return;
                if (!TranceDataManager.getMode(stack).equals(TranceDataManager.MODE_SUMMON)) return;
                TranceDataManager.scrollSelected(stack, payload.direction());
            });
        });
    }

    public static void send(int direction) {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
                .send(new TranceScrollPacket(direction));
    }

    private static net.minecraft.item.ItemStack getTranceStack(ServerPlayerEntity player) {
        var main = player.getMainHandStack();
        var off  = player.getOffHandStack();
        if (main.isOf(ModItems.TRANCE)) return main;
        if (off.isOf(ModItems.TRANCE))  return off;
        return null;
    }
}