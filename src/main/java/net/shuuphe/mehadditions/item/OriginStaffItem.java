package net.shuuphe.mehadditions.item;

import com.shuuphe.mehorigins.race.Race;
import com.shuuphe.mehorigins.race.RaceManager;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.util.StaffDataHelper;

import java.util.function.Consumer;

public class OriginStaffItem extends Item {

    public static Consumer<ItemStack> clientOpenScreen = null;

    public OriginStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient() && clientOpenScreen != null) {
            clientOpenScreen.accept(user.getStackInHand(hand));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            Race race = RaceManager.getRace(serverPlayer);
            if (race != null) {
                StaffDataHelper.addRace(stack, race.getId());
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context,
                              TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.literal("§7Right-click to open Origin Selector."));
        var unlocked = StaffDataHelper.getUnlockedRaces(stack);
        if (!unlocked.isEmpty()) {
            textConsumer.accept(Text.literal("§8Unlocked: " + String.join(", ", unlocked)));
        }
    }
}