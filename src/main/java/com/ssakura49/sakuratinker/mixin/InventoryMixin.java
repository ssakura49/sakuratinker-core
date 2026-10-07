package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.core.library.events.ItemSlotChangeEvent;
import com.ssakura49.sakuratinker.core.library.tools.uuid.STToolLocator;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Inject(method = "setItem", at = @At("HEAD"))
    private void onSetItem(int slot, ItemStack stack, CallbackInfo ci) {
        Inventory self = (Inventory) (Object) this;
        ItemStack old = self.getItem(slot);
        if (!old.isEmpty() || !stack.isEmpty()) {
            Player player = self.player;
            if (player != null && !player.level().isClientSide) {
                MinecraftForge.EVENT_BUS.post(new ItemSlotChangeEvent(player, slot, old, stack));
            }
        }
    }

    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void onRemoveItem(int index, int count, CallbackInfoReturnable<ItemStack> cir) {
        Inventory self = (Inventory) (Object) this;
        if (self.getItem(index).isEmpty()) {
            ItemStack removed = cir.getReturnValue();
            Player player = self.player;
            if (player != null && !removed.isEmpty() && !player.level().isClientSide) {
                MinecraftForge.EVENT_BUS.post(new ItemSlotChangeEvent(player, index, removed, ItemStack.EMPTY));
            }
        }
    }

    @Inject(method = "removeItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void onRemoveItemRef(ItemStack stack, CallbackInfo ci) {
        Inventory self = (Inventory) (Object) this;
        Player player = self.player;
        if (player != null && !stack.isEmpty() && !player.level().isClientSide) {
            MinecraftForge.EVENT_BUS.post(new ItemSlotChangeEvent(player, -1, stack, ItemStack.EMPTY));
        }
    }

    @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void onAdd(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            sakuratinker$handleAdd((Inventory) (Object) this, stack);
        }
    }

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    private void onAddToSlot(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            sakuratinker$handleAdd((Inventory) (Object) this, stack);
        }
    }

    @Inject(method = "dropAll", at = @At("RETURN"))
    private void onDropAll(CallbackInfo ci) {
        sakuratinker$clearPlayerIndex((Inventory) (Object) this);
    }

    @Inject(method = "clearContent", at = @At("RETURN"))
    private void onClearContent(CallbackInfo ci) {
        sakuratinker$clearPlayerIndex((Inventory) (Object) this);
    }

    @Unique
    private static void sakuratinker$handleAdd(Inventory self, ItemStack stack) {
        Player player = self.player;
        if (player.level().isClientSide) {
            return;
        }
        int slot = self.findSlotMatchingItem(stack);
        if (slot >= 0) {
            ItemStack inSlot = self.getItem(slot);
            if (!inSlot.isEmpty()) {
                MinecraftForge.EVENT_BUS.post(new ItemSlotChangeEvent(player, slot, ItemStack.EMPTY, inSlot));
            }
        }
    }

    @Unique
    private static void sakuratinker$clearPlayerIndex(Inventory self) {
        Player player = self.player;
        if (!player.level().isClientSide) {
            STToolLocator.clearPlayer(player.getUUID());
        }
    }
}
