package net.shuuphe.mehadditions.network;

import com.shuuphe.mehorigins.RaceRegistry;
import com.shuuphe.mehorigins.race.RaceManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.ModItems;
import net.shuuphe.mehadditions.MehAdditions;

public record SelectRaceWithStaffPacket(String raceId) implements CustomPayload {

    public static final CustomPayload.Id<SelectRaceWithStaffPacket> ID =
            new CustomPayload.Id<>(Identifier.of(MehAdditions.MOD_ID, "select_race_staff"));

    public static final PacketCodec<PacketByteBuf, SelectRaceWithStaffPacket> CODEC =
            PacketCodecs.STRING.xmap(SelectRaceWithStaffPacket::new, SelectRaceWithStaffPacket::raceId)
                    .cast();

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() { return ID; }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, ctx) -> {
            ServerPlayerEntity player = ctx.player();
            ctx.server().execute(() -> {
                // Damage whichever hand holds the staff
                var main = player.getMainHandStack();
                var off  = player.getOffHandStack();
                if (main.isOf(ModItems.ORIGIN_STAFF)) {
                    main.damage(1, player, player.getPreferredEquipmentSlot(main));
                } else if (off.isOf(ModItems.ORIGIN_STAFF)) {
                    off.damage(1, player, player.getPreferredEquipmentSlot(off));
                }

                // Validate race exists before applying (removed dead targetRace variable)
                boolean valid = RaceRegistry.getAll().stream()
                        .anyMatch(r -> r.getId().equals(payload.raceId()));
                if (valid) {
                    RaceManager.setRace(player, payload.raceId());
                }
            });
        });
    }

    public static void send(String raceId) {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
                .send(new SelectRaceWithStaffPacket(raceId));
    }
}
