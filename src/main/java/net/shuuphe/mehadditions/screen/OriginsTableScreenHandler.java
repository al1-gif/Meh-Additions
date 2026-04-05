package net.shuuphe.mehadditions.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.shuuphe.mehadditions.ModBlocks;
import net.shuuphe.mehadditions.ModRecipes;
import net.shuuphe.mehadditions.ModScreenHandlers;

import java.util.ArrayList;
import java.util.List;

public class OriginsTableScreenHandler extends ScreenHandler {

    private final CraftingInventory craftInput = new CraftingInventory(this, 3, 3);
    private final CraftingResultInventory craftResult = new CraftingResultInventory();

    private final ScreenHandlerContext context;

    public OriginsTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public OriginsTableScreenHandler(int syncId, PlayerInventory playerInventory,
                                     ScreenHandlerContext context) {
        super(ModScreenHandlers.ORIGINS_TABLE, syncId);
        this.context = context;

        this.addSlot(new CraftingResultSlot(playerInventory.player, craftInput,
                craftResult, 0, 124, 35));

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                this.addSlot(new Slot(craftInput, col + row * 3, 30 + col * 18, 17 + row * 18));

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));

        for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        context.run((world, pos) -> {
            if (world instanceof ServerWorld sw) updateCraftResult(sw);
        });
    }

    private void updateCraftResult(ServerWorld world) {
        // Build input list with pre-sized ArrayList to avoid resizing
        List<ItemStack> stacks = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) stacks.add(craftInput.getStack(i));
        var input = CraftingRecipeInput.create(3, 3, stacks);

        var upgrade = world.getRecipeManager()
                .getAllMatches(ModRecipes.ORIGINS_TABLE_UPGRADE, input, world)
                .findFirst();

        if (upgrade.isPresent()) {
            craftResult.setStack(0, upgrade.get().value().craft(input, world.getRegistryManager()));
            return;
        }

        var base = world.getRecipeManager()
                .getAllMatches(ModRecipes.ORIGINS_TABLE_BASE, input, world)
                .findFirst();

        craftResult.setStack(0, base
                .map(r -> r.value().craft(input, world.getRegistryManager()))
                .orElse(ItemStack.EMPTY));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return context.get((world, pos) ->
                (world.getBlockState(pos).isOf(ModBlocks.ORIGINS_TABLE) ||
                        world.getBlockState(pos).isOf(ModBlocks.CRAFTING_ALTAR)) &&
                        player.canInteractWithBlockAt(pos, 4.0), true);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasStack()) return result;

        ItemStack stack = slot.getStack();
        result = stack.copy();

        if (slotIndex == 0) {
            if (!this.insertItem(stack, 10, 46, true)) return ItemStack.EMPTY;
        } else if (slotIndex >= 10) {
            if (!this.insertItem(stack, 1, 10, false)) return ItemStack.EMPTY;
        } else {
            if (!this.insertItem(stack, 10, 46, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        if (stack.getCount() == result.getCount()) return ItemStack.EMPTY;

        slot.onTakeItem(player, stack);
        return result;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        context.run((world, pos) -> this.dropInventory(player, craftInput));
    }
}
