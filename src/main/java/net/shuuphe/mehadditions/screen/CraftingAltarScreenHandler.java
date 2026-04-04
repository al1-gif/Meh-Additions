package net.shuuphe.mehadditions.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.shuuphe.mehadditions.ModScreenHandlers;

public class CraftingAltarScreenHandler extends ScreenHandler {

    private final SimpleInventory craftInv = new SimpleInventory(3);

    public CraftingAltarScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, ScreenHandlerContext.EMPTY);
    }

    public CraftingAltarScreenHandler(int syncId, PlayerInventory playerInv, ScreenHandlerContext ctx) {
        super(ModScreenHandlers.CRAFTING_ALTAR, syncId);

        addSlot(new Slot(craftInv, 0, 27, 47));
        addSlot(new Slot(craftInv, 1, 76, 47));
        addSlot(new Slot(craftInv, 2, 134, 47) {
            @Override public boolean canInsert(ItemStack stack) { return false; }
        });

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasStack()) return result;
        ItemStack stack = slot.getStack();
        result = stack.copy();

        if (index < 3) {
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
}