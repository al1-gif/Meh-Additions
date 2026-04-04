package net.shuuphe.mehadditions;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.entity.LumidouceFireballEntity;
import net.shuuphe.mehadditions.entity.RuneArrowEntity;

public class ModEntityTypes {

    public static EntityType<RuneArrowEntity> RUNE_ARROW;
    public static EntityType<LumidouceFireballEntity> LUMIDOUCE_FIREBALL;

    public static void register() {
        RegistryKey<EntityType<?>> arrowKey = RegistryKey.of(
                RegistryKeys.ENTITY_TYPE,
                Identifier.of(MehAdditions.MOD_ID, "rune_arrow")
        );
        RUNE_ARROW = Registry.register(
                Registries.ENTITY_TYPE, arrowKey,
                EntityType.Builder.<RuneArrowEntity>create(RuneArrowEntity::new, SpawnGroup.MISC)
                        .dimensions(0.5f, 0.5f)
                        .maxTrackingRange(4)
                        .trackingTickInterval(20)
                        .build(arrowKey)
        );

        RegistryKey<EntityType<?>> fireballKey = RegistryKey.of(
                RegistryKeys.ENTITY_TYPE,
                Identifier.of(MehAdditions.MOD_ID, "lumidouce_fireball")
        );
        LUMIDOUCE_FIREBALL = Registry.register(
                Registries.ENTITY_TYPE, fireballKey,
                EntityType.Builder.<LumidouceFireballEntity>create(LumidouceFireballEntity::new, SpawnGroup.MISC)
                        .dimensions(0.3125f, 0.3125f)
                        .maxTrackingRange(4)
                        .trackingTickInterval(10)
                        .build(fireballKey)
        );
    }
}