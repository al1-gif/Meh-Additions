package net.shuuphe.mehadditions.trance;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

public class TranceDataManager {

    private static final String KEY_MOBS           = "trance_mobs";
    private static final String KEY_POINTS         = "trance_points";
    private static final String KEY_SELECTED       = "trance_selected";
    private static final String KEY_MODE           = "trance_mode";
    private static final String KEY_ATTACK_CHARGES = "trance_attack_charges";

    private static final String ENTRY_TYPE      = "type";
    private static final String ENTRY_EQUIPMENT = "equipment";

    public static final String MODE_CAPTURE = "CAPTURE";
    public static final String MODE_SUMMON  = "SUMMON";
    public static final String MODE_ATTACK  = "ATTACK";
    public static int getAttackCharges(ItemStack stack) {
        return getNbt(stack).getInt(KEY_ATTACK_CHARGES).orElse(-1);
    }

    public static void setAttackCharges(ItemStack stack, int charges) {
        NbtCompound nbt = getNbt(stack);
        nbt.putInt(KEY_ATTACK_CHARGES, charges);
        writeNbt(stack, nbt);
    }

    public static void toggleMode(ItemStack stack) {
        String current = getMode(stack);
        NbtCompound nbt = getNbt(stack);
        String next = switch (current) {
            case MODE_CAPTURE -> MODE_SUMMON;
            case MODE_SUMMON  -> MODE_ATTACK;
            default           -> MODE_CAPTURE;
        };
        nbt.putString(KEY_MODE, next);
        writeNbt(stack, nbt);
    }

    public static List<String> getStoredMobs(ItemStack stack) {
        NbtList list = getRawList(stack);
        List<String> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof NbtCompound entry) {
                entry.getString(ENTRY_TYPE).ifPresent(result::add);
            }
        }
        return result;
    }

    public static void addMobEntry(ItemStack stack, String typeId, NbtCompound equipment) {
        NbtCompound nbt = getNbt(stack);
        NbtList list = nbt.contains(KEY_MOBS) ? nbt.getListOrEmpty(KEY_MOBS) : new NbtList();
        NbtCompound entry = new NbtCompound();
        entry.putString(ENTRY_TYPE, typeId);
        entry.put(ENTRY_EQUIPMENT, equipment);
        list.add(entry);
        nbt.put(KEY_MOBS, list);
        writeNbt(stack, nbt);
    }

    public static void addMob(ItemStack stack, String typeId) {
        addMobEntry(stack, typeId, new NbtCompound());
    }

    public static NbtCompound getMobEquipment(ItemStack stack, int index) {
        NbtList list = getRawList(stack);
        if (index < 0 || index >= list.size()) return new NbtCompound();
        if (list.get(index) instanceof NbtCompound entry) {
            return entry.getCompound(ENTRY_EQUIPMENT).orElse(new NbtCompound());
        }
        return new NbtCompound();
    }

    public static void removeMobAt(ItemStack stack, int index) {
        NbtCompound nbt = getNbt(stack);
        if (!nbt.contains(KEY_MOBS)) return;
        NbtList oldList = nbt.getListOrEmpty(KEY_MOBS);
        if (index < 0 || index >= oldList.size()) return;
        NbtList newList = new NbtList();
        for (int i = 0; i < oldList.size(); i++) {
            if (i != index) newList.add(oldList.get(i));
        }
        nbt.put(KEY_MOBS, newList);
        writeNbt(stack, nbt);

        int sel = getSelected(stack);
        if (sel >= newList.size()) setSelected(stack, Math.max(0, newList.size() - 1));
    }

    public static int getUsedPoints(ItemStack stack) {
        return getNbt(stack).getInt(KEY_POINTS).orElse(0);
    }

    public static void setUsedPoints(ItemStack stack, int points) {
        NbtCompound nbt = getNbt(stack);
        nbt.putInt(KEY_POINTS, points);
        writeNbt(stack, nbt);
    }

    public static int getSelected(ItemStack stack) {
        return getNbt(stack).getInt(KEY_SELECTED).orElse(0);
    }

    public static void setSelected(ItemStack stack, int index) {
        NbtCompound nbt = getNbt(stack);
        nbt.putInt(KEY_SELECTED, index);
        writeNbt(stack, nbt);
    }

    public static void scrollSelected(ItemStack stack, int direction) {
        List<String> mobs = getStoredMobs(stack);
        if (mobs.isEmpty()) return;
        int current = getSelected(stack);
        int next = (current + direction + mobs.size()) % mobs.size();
        setSelected(stack, next);
    }

    public static String getMode(ItemStack stack) {
        return getNbt(stack).getString(KEY_MODE).orElse(MODE_CAPTURE);
    }

    private static NbtList getRawList(ItemStack stack) {
        NbtCompound nbt = getNbt(stack);
        return nbt.contains(KEY_MOBS) ? nbt.getListOrEmpty(KEY_MOBS) : new NbtList();
    }

    private static NbtCompound getNbt(ItemStack stack) {
        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        return data != null ? data.copyNbt() : new NbtCompound();
    }

    private static void writeNbt(ItemStack stack, NbtCompound nbt) {
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
}