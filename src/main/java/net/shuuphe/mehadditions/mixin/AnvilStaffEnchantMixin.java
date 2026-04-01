package net.shuuphe.mehadditions.mixin;

import net.shuuphe.mehadditions.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(AnvilScreenHandler.class)
public class AnvilStaffEnchantMixin {

    @Inject(method = "updateResult", at = @At("TAIL"))
    private void stripInvalidEnchants(CallbackInfo ci) {
        AnvilScreenHandler self = (AnvilScreenHandler) (Object) this;
        ItemStack output = self.getSlot(2).getStack();
        if (output.isEmpty()) return;
        if (!output.isOf(ModItems.ORIGIN_STAFF)) return;

        Set<Identifier> allowed = Set.of(
                Identifier.ofVanilla("unbreaking"),
                Identifier.ofVanilla("mending")
        );

        var enchants = EnchantmentHelper.getEnchantments(output);
        boolean hasInvalid = enchants.getEnchantments().stream().anyMatch(entry -> {
            Identifier id = entry.getKey().map(k -> k.getValue()).orElse(null);
            return id != null && !allowed.contains(id);
        });

        if (hasInvalid) {
            self.getSlot(2).setStack(ItemStack.EMPTY);
        }
    }
}