package net.shuuphe.mehadditions.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.shuuphe.mehadditions.ModRecipes;
import net.shuuphe.mehadditions.ModScreenHandlers;
import net.shuuphe.mehadditions.recipe.CraftingAltarInput;

import static net.minecraft.block.Block.dropStack;

public class CraftingAltarScreenHandler extends ScreenHandler {

    private final SimpleInventory craftInv = new SimpleInventory(3);
    private final ScreenHandlerContext context;
    private boolean updatingResult = false;

    public CraftingAltarScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, ScreenHandlerContext.EMPTY);
    }

    public CraftingAltarScreenHandler(int syncId, PlayerInventory playerInv, ScreenHandlerContext ctx) {
        super(ModScreenHandlers.CRAFTING_ALTAR, syncId);
        this.context = ctx;

        addSlot(new Slot(craftInv, 0, 27, 47));
        addSlot(new Slot(craftInv, 1, 76, 47));

        addSlot(new Slot(craftInv, 2, 134, 47) {
            @Override
            public boolean canInsert(ItemStack stack) { return false; }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack taken) {
                craftInv.getStack(0).decrement(1);
                craftInv.getStack(1).decrement(1);
                super.onTakeItem(player, taken);
            }
        });

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));

        craftInv.addListener(this::onContentChanged);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (updatingResult) return;
        context.run((world, pos) -> {
            if (!(world instanceof ServerWorld sw)) return;
            updatingResult = true;
            updateResult(sw);
            updatingResult = false;
        });
    }

    private void updateResult(ServerWorld world) {
        var input = new CraftingAltarInput(craftInv.getStack(0), craftInv.getStack(1));
        var match = world.getRecipeManager()
                .getAllMatches(ModRecipes.CRAFTING_ALTAR, input, world)
                .findFirst();
        craftInv.setStack(2, match
                .map(r -> r.value().craft(input, world.getRegistryManager()))
                .orElse(ItemStack.EMPTY));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasStack()) return result;
        ItemStack stack = slot.getStack();
        result = stack.copy();

        if (index == 2) {
            if (!insertItem(stack, 3, slots.size(), true)) return ItemStack.EMPTY;
            craftInv.getStack(0).decrement(1);
            craftInv.getStack(1).decrement(1);
            onContentChanged(craftInv);
        } else if (index < 2) {
            if (!insertItem(stack, 3, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (!insertItem(stack, 0, 2, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        return result;
    }

    @Override
    public boolean canUse(PlayerEntity player) { return true; }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        context.run((world, pos) -> {
            player.getInventory().offerOrDrop(craftInv.removeStack(0));
            player.getInventory().offerOrDrop(craftInv.removeStack(1));
        });
    }
}