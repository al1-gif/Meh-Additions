package net.shuuphe.mehadditions.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.shuuphe.mehadditions.item.RuneItem;

public class RunePouchScreenHandler extends ScreenHandler {

    private final int pouchSize;

    public RunePouchScreenHandler(int syncId, PlayerInventory playerInv,
                                  SimpleInventory pouchInv, boolean large) {
        super(large ? ScreenHandlerType.GENERIC_9X3 : ScreenHandlerType.GENERIC_3X3, syncId);
        this.pouchSize = large ? 27 : 9;

        if (large) {
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 9; col++)
                    addSlot(new Slot(pouchInv, col + row * 9, 8 + col * 18, 18 + row * 18) {
                        @Override public boolean canInsert(ItemStack s) {
                            return s.getItem() instanceof RuneItem;
                        }
                    });
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 9; col++)
                    addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        } else {
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 3; col++)
                    addSlot(new Slot(pouchInv, col + row * 3, 62 + col * 18, 17 + row * 18) {
                        @Override public boolean canInsert(ItemStack s) {
                            return s.getItem() instanceof RuneItem;
                        }
                    });
            for (int row = 0; row < 3; row++)
                for (int col = 0; col < 9; col++)
                    addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasStack()) return result;
        ItemStack stack = slot.getStack();
        result = stack.copy();

        if (index < pouchSize) {
            if (!insertItem(stack, pouchSize, slots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof RuneItem) {
            if (!insertItem(stack, 0, pouchSize, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();

        return result;
    }

    @Override
    public boolean canUse(PlayerEntity player) { return true; }
}