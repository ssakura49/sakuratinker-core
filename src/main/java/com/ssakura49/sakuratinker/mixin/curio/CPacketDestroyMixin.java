package com.ssakura49.sakuratinker.mixin.curio;

import com.ssakura49.sakuratinker.core.api.IDisarmProtected;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.network.client.CPacketDestroy;

import java.util.function.Supplier;


@Mixin(value = CPacketDestroy.class, remap = false)
public class CPacketDestroyMixin {

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private static void sakuratinker$preventDisarmDestroy(CPacketDestroy msg, Supplier<Context> ctx, CallbackInfo ci) {
        Context context = ctx.get();
        ServerPlayer sender = context.getSender();
        if (sender != null && sakuratinker$hasDisarmProtectedCurio(sender)) {
            context.setPacketHandled(true);
            ci.cancel();
        }
    }

    @Unique
    private static boolean sakuratinker$hasDisarmProtectedCurio(ServerPlayer player) {
        try {
            return CuriosApi.getCuriosInventory(player).map(handler -> {
                for (ICurioStacksHandler stacksHandler : handler.getCurios().values()) {
                    IDynamicStackHandler stacks = stacksHandler.getStacks();
                    for (int i = 0; i < stacks.getSlots(); i++) {
                        if (sakuratinker$isDisarmProtected(stacks.getStackInSlot(i))) {
                            return true;
                        }
                    }
                }
                return false;
            }).orElse(false);
        } catch (Exception ignored) {
            return false;
        }
    }

    @Unique
    private static boolean sakuratinker$isDisarmProtected(ItemStack stack) {
        return stack.getItem() instanceof IDisarmProtected protectedItem && protectedItem.isDisarmProtected(stack);
    }
}