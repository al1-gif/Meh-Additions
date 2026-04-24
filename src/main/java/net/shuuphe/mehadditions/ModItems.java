package net.shuuphe.mehadditions;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.item.*;
import net.shuuphe.mehadditions.util.RuneType;
import net.minecraft.component.type.AttributeModifiersComponent;

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
            new Item.Settings()
                    .maxDamage(1561)
                    .maxCount(1)
                    .registryKey(key("freedom_sworn"))
                    .attributeModifiers(
                            AttributeModifiersComponent.builder()
                                    .add(EntityAttributes.ATTACK_DAMAGE,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, 7.0,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .add(EntityAttributes.ATTACK_SPEED,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, -2.4,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .build()
                    ));
    public static final TranceItem TRANCE = new TranceItem(
            new Item.Settings().maxDamage(512).maxCount(1).registryKey(key("trance")));
    public static final Hanafubuki HANAFUBUKI = new Hanafubuki(
            new Item.Settings()
                    .maxDamage(1561)
                    .maxCount(1)
                    .registryKey(key("hanafubuki"))
                    .attributeModifiers(
                            AttributeModifiersComponent.builder()
                                    .add(EntityAttributes.ATTACK_DAMAGE,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, 7.0,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .add(EntityAttributes.ATTACK_SPEED,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, -2.4,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .build()
                    ));
    public static final Bloodbath BLOODBATH = new Bloodbath(
            new Item.Settings()
                    .maxDamage(500)
                    .maxCount(1)
                    .registryKey(key("bloodbath"))
                    .attributeModifiers(
                            AttributeModifiersComponent.builder()
                                    .add(EntityAttributes.ATTACK_DAMAGE,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, 7.0,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .add(EntityAttributes.ATTACK_SPEED,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, -3.4,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .build()
                    ));
    public static final BluntForceItem BLUNT_FORCE = new BluntForceItem(
            new Item.Settings()
                    .maxDamage(1561)
                    .maxCount(1)
                    .registryKey(key("blunt_force"))
                    .attributeModifiers(
                            AttributeModifiersComponent.builder()
                                    .add(EntityAttributes.ATTACK_DAMAGE,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, 7.0,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .add(EntityAttributes.ATTACK_SPEED,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, -2.4,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .build()
                    ));

    public static final CrimsonMoonsSemblanceItem CRIMSON_MOONS_SEMBLANCE = new CrimsonMoonsSemblanceItem(
            new Item.Settings()
                    .maxDamage(2031)
                    .maxCount(1)
                    .registryKey(key("crimson_moons_semblance"))
                    .attributeModifiers(
                            AttributeModifiersComponent.builder()
                                    .add(EntityAttributes.ATTACK_DAMAGE,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID, 7.0,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .add(EntityAttributes.ATTACK_SPEED,
                                            new EntityAttributeModifier(Item.BASE_ATTACK_SPEED_MODIFIER_ID, -2.4,
                                                    EntityAttributeModifier.Operation.ADD_VALUE),
                                            AttributeModifierSlot.MAINHAND)
                                    .build()
                    ));

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
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "crimson_moons_semblance"), CRIMSON_MOONS_SEMBLANCE);

        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "trance"), TRANCE);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "hanafubuki"), HANAFUBUKI);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "bloodbath"), BLOODBATH);
        Registry.register(Registries.ITEM, Identifier.of(MehAdditions.MOD_ID, "blunt_force"), BLUNT_FORCE);

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
            entries.add(CRIMSON_MOONS_SEMBLANCE);
            entries.add(TRANCE);
            entries.add(HANAFUBUKI);
            entries.add(BLOODBATH);
            entries.add(BLUNT_FORCE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ModBlocks.ORIGINS_TABLE);
            entries.add(ModBlocks.CRAFTING_ALTAR);
        });
    }
}