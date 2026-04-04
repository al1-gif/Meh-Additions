package net.shuuphe.mehadditions;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.entity.RuneArrowEntity;

public class ModEntityTypes {

    public static EntityType<RuneArrowEntity> RUNE_ARROW;

    public static void register() {
        RegistryKey<EntityType<?>> key = RegistryKey.of(
                RegistryKeys.ENTITY_TYPE,
                Identifier.of(MehAdditions.MOD_ID, "rune_arrow")
        );
        RUNE_ARROW = Registry.register(
                Registries.ENTITY_TYPE,
                key,
                EntityType.Builder.<RuneArrowEntity>create(RuneArrowEntity::new, SpawnGroup.MISC)
                        .dimensions(0.5f, 0.5f)
                        .maxTrackingRange(4)
                        .trackingTickInterval(20)
                        .build(key)
        );
    }
}