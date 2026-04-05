package net.shuuphe.mehadditions.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.ModItems;
import net.shuuphe.mehadditions.entity.LumidouceFireballEntity;
import net.shuuphe.mehadditions.util.RuneHelper;

public class LumidouceElegyScepterItem extends Item {

    public LumidouceElegyScepterItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(stack)) return ActionResult.FAIL;
        if (!user.isCreative() && !RuneHelper.hasRune(user, ModItems.FIRE_STONE)) return ActionResult.FAIL;

        if (world instanceof ServerWorld serverWorld) {
            RegistryWrapper.WrapperLookup reg = serverWorld.getRegistryManager();
            if (!user.isCreative() && !RuneHelper.consumeRune(user, ModItems.FIRE_STONE, reg))
                return ActionResult.FAIL;

            world.spawnEntity(LumidouceFireballEntity.create(world, user));
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0f,
                    1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

            stack.damage(1, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            user.getItemCooldownManager().set(stack, 20);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return ActionResult.SUCCESS;
    }
}
