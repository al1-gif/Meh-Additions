package net.shuuphe.mehadditions.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;

public class StaffDataHelper {

    private static final String KEY = "unlocked_races";

    public static List<String> getUnlockedRaces(ItemStack stack) {
        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) return new ArrayList<>();
        NbtCompound nbt = data.copyNbt();
        if (!nbt.contains(KEY)) return new ArrayList<>();
        NbtList list = nbt.getListOrEmpty(KEY);
        List<String> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof NbtString s) {
                s.asString().ifPresent(result::add);
            }
        }
        return result;
    }

    /**
     * Fast containment check — scans the raw NBT list directly
     * instead of deserializing it into a full List<String>.
     */
    public static boolean hasRace(ItemStack stack, String raceId) {
        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) return false;
        NbtCompound nbt = data.copyNbt();
        if (!nbt.contains(KEY)) return false;
        NbtList list = nbt.getListOrEmpty(KEY);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof NbtString s
                    && s.asString().map(raceId::equals).orElse(false))
                return true;
        }
        return false;
    }

    public static void addRace(ItemStack stack, String raceId) {
        if (hasRace(stack, raceId)) return;
        List<String> existing = getUnlockedRaces(stack);
        existing.add(raceId);
        save(stack, existing);
    }

    /**
     * Copies all unlocked races from {@code from} to {@code to} in a single
     * NBT write instead of one write per race.
     */
    public static void copyFrom(ItemStack from, ItemStack to) {
        List<String> source = getUnlockedRaces(from);
        if (source.isEmpty()) return;

        List<String> dest = getUnlockedRaces(to);
        boolean changed = false;
        for (String race : source) {
            if (!dest.contains(race)) {
                dest.add(race);
                changed = true;
            }
        }
        if (changed) save(to, dest);
    }

    private static void save(ItemStack stack, List<String> races) {
        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbt = data != null ? data.copyNbt() : new NbtCompound();
        NbtList list = new NbtList();
        for (String race : races) list.add(NbtString.of(race));
        nbt.put(KEY, list);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
}
