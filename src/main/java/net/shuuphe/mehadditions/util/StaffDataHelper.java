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
        List<String> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof NbtString s) {
                s.asString().ifPresent(result::add);
            }
        }
        return result;
    }

    public static boolean hasRace(ItemStack stack, String raceId) {
        return getUnlockedRaces(stack).contains(raceId);
    }

    public static void addRace(ItemStack stack, String raceId) {
        List<String> existing = getUnlockedRaces(stack);
        if (existing.contains(raceId)) return;
        existing.add(raceId);
        save(stack, existing);
    }

    public static void copyFrom(ItemStack from, ItemStack to) {
        for (String race : getUnlockedRaces(from)) addRace(to, race);
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