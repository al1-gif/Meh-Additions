package net.shuuphe.mehadditions;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.shuuphe.mehadditions.effect.FrostStatusEffect;

public class ModEffects {

    public static RegistryEntry<StatusEffect> FROST;

    public static void register() {
        FrostStatusEffect frostEffect = new FrostStatusEffect();
        Registry.register(Registries.STATUS_EFFECT,
                Identifier.of(MehAdditions.MOD_ID, "frost"), frostEffect);
        FROST = Registries.STATUS_EFFECT.getEntry(frostEffect);
    }
}