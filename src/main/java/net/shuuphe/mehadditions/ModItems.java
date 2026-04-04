package net.shuuphe.mehadditions;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.item.*;
import net.shuuphe.mehadditions.util.RuneType;

public class ModItems {

    private static RegistryKey<Item> key(String path) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MehAdditions.MOD_ID, path));
    }

    public static final Item EYES_OF_ORIGIN = new Item(
            new Item.Settings().registryKey(key("eyes_of_origin")));

    public static final OriginStaffItem ORIGIN_STAFF = new OriginStaffItem(
            new Item.Settings().maxDamage(250).maxCount(1).registryKey(key("origin_staff")));

    public static final CatalystItem CATALYST = new CatalystItem(
            new Item.Settings().maxDamage(180).maxCount(1).registryKey(key("catalyst")));

    public static final RuneItem FIRE_STONE = new RuneItem(
            new Item.Settings().registryKey(key("fire_stone")), RuneType.FIRE);

    public static final RuneItem FROST_STONE = new RuneItem(
            new Item.Settings().registryKey(key("frost_stone")), RuneType.FROST);

    public static final RuneItem LIGHTNING_STONE = new RuneItem(
            new Item.Settings().registryKey(key("lightning_stone")), RuneType.LIGHTNING);

    public static final RunePouchItem SMALL_RUNE_POUCH = new RunePouchItem(
            new Item.Settings().maxCount(1).registryKey(key("small_rune_pouch")), 9);

    public static final RunePouchItem LARGE_RUNE_POUCH = new RunePouchItem(
            new Item.Settings().maxCount(1).registryKey(key("large_rune_pouch")), 27);

    public static final AmosBowItem AMOS_BOW = new AmosBowItem(
            new Item.Settings().maxDamage(384).maxCount(1).registryKey(key("amos_bow")));

    public static final SkywardHarpItem SKYWARD_HARP = new SkywardHarpItem(
            new Item.Settings().maxDamage(384).maxCount(1).registryKey(key("skyward_harp")));

    public static final LumidouceElegyScepterItem LUMIDOUCE_ELEGY = new LumidouceElegyScepterItem(
            new Item.Settings().maxDamage(512).maxCount(1).registryKey(key("lumidouce_elegy")));

    public static final FreedomSwornItem FREEDOM_SWORN = new FreedomSwornItem(
            new Item.Settings().maxDamage(1561).maxCount(1).registryKey(key("freedom_sworn")));

    public static void register() {
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "eyes_of_origin"), EYES_OF_ORIGIN);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "origin_staff"), ORIGIN_STAFF);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "catalyst"), CATALYST);

        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "fire_stone"), FIRE_STONE);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "frost_stone"), FROST_STONE);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "lightning_stone"), LIGHTNING_STONE);

        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "small_rune_pouch"), SMALL_RUNE_POUCH);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "large_rune_pouch"), LARGE_RUNE_POUCH);

        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "amos_bow"), AMOS_BOW);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "skyward_harp"), SKYWARD_HARP);

        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "lumidouce_elegy"), LUMIDOUCE_ELEGY);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "freedom_sworn"), FREEDOM_SWORN);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(EYES_OF_ORIGIN);
            entries.add(ORIGIN_STAFF);
            entries.add(CATALYST);
            entries.add(FIRE_STONE);
            entries.add(FROST_STONE);
            entries.add(LIGHTNING_STONE);
            entries.add(SMALL_RUNE_POUCH);
            entries.add(LARGE_RUNE_POUCH);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(ORIGIN_STAFF);
            entries.add(CATALYST);
            entries.add(AMOS_BOW);
            entries.add(SKYWARD_HARP);
            entries.add(LUMIDOUCE_ELEGY);
            entries.add(FREEDOM_SWORN);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ModBlocks.ORIGINS_TABLE);
            entries.add(ModBlocks.CRAFTING_ALTAR);
        });
    }
}