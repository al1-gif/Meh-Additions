package net.shuuphe.mehadditions;

import net.shuuphe.mehadditions.item.OriginStaffItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    private static RegistryKey<Item> key(String path) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MehAdditions.MOD_ID, path));
    }

    public static final Item EYES_OF_ORIGIN = new Item(
            new Item.Settings().registryKey(key("eyes_of_origin"))
    );

    public static final OriginStaffItem ORIGIN_STAFF = new OriginStaffItem(
            new Item.Settings().maxDamage(250).maxCount(1).registryKey(key("origin_staff"))
    );

    public static void register() {
        Registry.register(Registries.ITEM,
                Identifier.of(MehAdditions.MOD_ID, "eyes_of_origin"), EYES_OF_ORIGIN);
        Registry.register(Registries.ITEM,
                Identifier.of(MehAdditions.MOD_ID, "origin_staff"), ORIGIN_STAFF);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(EYES_OF_ORIGIN);
            entries.add(ORIGIN_STAFF);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(ORIGIN_STAFF);
        });
    }
}