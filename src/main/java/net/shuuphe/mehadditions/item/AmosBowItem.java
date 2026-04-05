package net.shuuphe.mehadditions.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
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
import net.shuuphe.mehadditions.entity.RuneArrowEntity;
import net.shuuphe.mehadditions.util.RuneHelper;
import net.shuuphe.mehadditions.util.RuneType;

import java.util.function.Predicate;

public class AmosBowItem extends BowItem {

    public AmosBowItem(Settings settings) { super(settings); }

    @Override public Predicate<ItemStack> getHeldProjectiles() { return s -> s.isOf(ModItems.LIGHTNING_STONE); }
    @Override public Predicate<ItemStack> getProjectiles()     { return s -> s.isOf(ModItems.LIGHTNING_STONE); }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!user.isCreative() && !RuneHelper.hasRune(user, ModItems.LIGHTNING_STONE))
            return ActionResult.FAIL;
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;
        float power = BowItem.getPullProgress(getMaxUseTime(stack, user) - remainingUseTicks);
        if (power < 0.1f) return false;
        if (!(world instanceof ServerWorld serverWorld)) return true;

        RegistryWrapper.WrapperLookup reg = serverWorld.getRegistryManager();
        if (!player.isCreative() && !RuneHelper.consumeRune(player, ModItems.LIGHTNING_STONE, reg))
            return false;

        RuneArrowEntity arrow = RuneArrowEntity.create(world, player, RuneType.LIGHTNING);
        arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0f, power * 3f, 1f);
        arrow.setCritical(power >= 1f);
        world.spawnEntity(arrow);
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1f,
                1f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + power * 0.5f);
        player.incrementStat(Stats.USED.getOrCreateStat(this));
        Hand activeHand = player.getActiveHand();
        stack.damage(1, player, activeHand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        return true;
    }
}
