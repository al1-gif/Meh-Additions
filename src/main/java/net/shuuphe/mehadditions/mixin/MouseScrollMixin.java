package net.shuuphe.mehadditions.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.shuuphe.mehadditions.ModItems;
import net.shuuphe.mehadditions.network.TranceScrollPacket;
import net.shuuphe.mehadditions.trance.TranceDataManager;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseScrollMixin {

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen != null) return;

        var main = client.player.getMainHandStack();
        var off  = client.player.getOffHandStack();
        boolean holdingTrance = main.isOf(ModItems.TRANCE) || off.isOf(ModItems.TRANCE);
        if (!holdingTrance) return;

        var stack = main.isOf(ModItems.TRANCE) ? main : off;
        if (!TranceDataManager.getMode(stack).equals(TranceDataManager.MODE_SUMMON)) return;

        long handle = client.getWindow().getHandle();
        boolean altHeld = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_ALT) == GLFW.GLFW_PRESS;
        if (!altHeld) return;

        int direction = vertical > 0 ? -1 : 1;
        TranceScrollPacket.send(direction);
        ci.cancel();
    }
}