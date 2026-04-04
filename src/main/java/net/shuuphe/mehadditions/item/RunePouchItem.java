package net.shuuphe.mehadditions.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.shuuphe.mehadditions.screen.RunePouchScreenHandler;

public class RunePouchItem extends Item {

    final int slotCount;

    public RunePouchItem(Settings settings, int slotCount) {
        super(settings);
        this.slotCount = slotCount;
    }

    public int getSlotCount() { return slotCount; }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.SUCCESS;

        RegistryWrapper.WrapperLookup reg = serverWorld.getRegistryManager();
        ItemStack pouchStack = user.getStackInHand(hand);
        boolean large = slotCount > 9;

        SimpleInventory inv = new SimpleInventory(slotCount);
        loadInto(pouchStack, inv, reg);
        inv.addListener(i -> saveInventory(pouchStack, inv, reg));

        user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, playerInv, p) -> new RunePouchScreenHandler(syncId, playerInv, inv, large),
                pouchStack.getName()
        ));

        return ActionResult.SUCCESS;
    }

    public static void loadInto(ItemStack stack, SimpleInventory inv, RegistryWrapper.WrapperLookup reg) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (!nbt.contains("Slots")) return;
        NbtList list = nbt.getList("Slots").orElseGet(NbtList::new);
        RegistryOps<NbtElement> ops = reg.getOps(NbtOps.INSTANCE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i).orElse(null);
            if (entry == null) continue;
            int slot = entry.getInt("Slot").orElse(-1);
            NbtElement item = entry.get("Item");
            if (slot >= 0 && slot < inv.size() && item != null)
                ItemStack.CODEC.parse(ops, item).result().ifPresent(s -> inv.setStack(slot, s));
        }
    }

    public static void saveInventory(ItemStack stack, SimpleInventory inv, RegistryWrapper.WrapperLookup reg) {
        NbtCompound nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        NbtList list = new NbtList();
        RegistryOps<NbtElement> ops = reg.getOps(NbtOps.INSTANCE);
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty()) {
                final int slot = i;
                ItemStack.CODEC.encodeStart(ops, s).result().ifPresent(encoded -> {
                    NbtCompound entry = new NbtCompound();
                    entry.putInt("Slot", slot);
                    entry.put("Item", encoded);
                    list.add(entry);
                });
            }
        }
        nbt.put("Slots", list);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    /** Fast NBT scan — no codec, works on client and server. */
    public static boolean hasPouchWithRune(ItemStack pouchStack, Item runeItem) {
        String targetId = Registries.ITEM.getId(runeItem).toString();
        NbtCompound nbt = pouchStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        if (!nbt.contains("Slots")) return false;
        NbtList list = nbt.getList("Slots").orElseGet(NbtList::new);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i).orElse(null);
            if (entry == null) continue;
            NbtElement itemData = entry.get("Item");
            if (!(itemData instanceof NbtCompound itemNbt)) continue;
            if (itemNbt.getString("id").orElse("").equals(targetId)) return true;
        }
        return false;
    }

    public static boolean consumeRuneFromPouch(ItemStack pouchStack, Item runeItem,
                                               RegistryWrapper.WrapperLookup reg) {
        if (!(pouchStack.getItem() instanceof RunePouchItem rp)) return false;
        SimpleInventory inv = new SimpleInventory(rp.slotCount);
        loadInto(pouchStack, inv, reg);
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty() && s.isOf(runeItem)) {
                s.decrement(1);
                inv.setStack(i, s);
                saveInventory(pouchStack, inv, reg);
                return true;
            }
        }
        return false;
    }
}