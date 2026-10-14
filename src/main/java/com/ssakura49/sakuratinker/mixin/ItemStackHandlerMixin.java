package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.core.api.IDisarmProtected;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = ItemStackHandler.class, remap = false)
public class ItemStackHandlerMixin {

    @Unique
    private static Field sakuratinker$stacksField = null;

    @Inject(method = "setStackInSlot", at = @At("HEAD"), cancellable = true)
    public void sakuratinker$preventDisarmClear(int slot, ItemStack stack, CallbackInfo ci) {
        ItemStack current = sakuratinker$getRawStack(slot);
        if (current == null || current.isEmpty()) {
            return;
        }
        boolean oldProtected = sakuratinker$isDisarmProtected(current);
        boolean newProtected = stack != null && !stack.isEmpty() && sakuratinker$isDisarmProtected(stack);
        if (oldProtected && !newProtected) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean sakuratinker$isDisarmProtected(ItemStack stack) {
        return stack.getItem() instanceof IDisarmProtected protectedItem && protectedItem.isDisarmProtected(stack);
    }

    @Unique
    private ItemStack sakuratinker$getRawStack(int slot) {
        try {
            if (sakuratinker$stacksField == null) {
                sakuratinker$stacksField = ItemStackHandler.class.getDeclaredField("stacks");
                sakuratinker$stacksField.setAccessible(true);
            }
            NonNullList<ItemStack> stacks = (NonNullList<ItemStack>) sakuratinker$stacksField.get(this);
            return stacks != null && slot >= 0 && slot < stacks.size() ? stacks.get(slot) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
