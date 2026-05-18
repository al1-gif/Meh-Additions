package net.shuuphe.mehadditions.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.shuuphe.mehadditions.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieEquipmentMixin {

    private static final float CHANCE = 0.30f;

    @Inject(method = "initialize", at = @At("RETURN"))
    private void onInitialize(ServerWorldAccess world, LocalDifficulty difficulty,
                              SpawnReason spawnReason, EntityData entityData,
                              CallbackInfoReturnable<EntityData> cir) {
        ZombieEntity self = (ZombieEntity)(Object)this;
        if (self.getRandom().nextFloat() >= CHANCE) return;
        ItemStack weapon = self.getRandom().nextBoolean()
                ? new ItemStack(ModItems.BLUNT_FORCE)
                : new ItemStack(ModItems.HANAFUBUKI);

        self.equipStack(EquipmentSlot.MAINHAND, weapon);
    }
}