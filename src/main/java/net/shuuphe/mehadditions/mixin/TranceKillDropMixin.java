package net.shuuphe.mehadditions.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.shuuphe.mehadditions.ModItems;
import net.shuuphe.mehadditions.item.TranceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class TranceKillDropMixin {

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void cancelDropsOnTranceKill(ServerWorld world, DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (TranceItem.ALL_SUMMON_IDS.contains(self.getId())) {
            ci.cancel();
            return;
        }
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;
        ItemStack held = player.getMainHandStack();
        if (!held.isOf(ModItems.TRANCE)) held = player.getOffHandStack();
        if (held.isOf(ModItems.TRANCE)) {
            ci.cancel();
        }
    }
}