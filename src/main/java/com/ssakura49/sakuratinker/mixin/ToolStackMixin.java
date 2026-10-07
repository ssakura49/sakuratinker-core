package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.core.library.tools.event.ToolStackChangeEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;


@Mixin(value = ToolStack.class, remap = false)
public class ToolStackMixin {
    @Inject(method = "setMaterials", at = @At("RETURN"))
    private void onSetMaterials(MaterialNBT materials, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ToolStackChangeEvent((ToolStack) (Object) this));
    }

    @Inject(method = "setUpgrades", at = @At("RETURN"))
    private void onSetUpgrades(ModifierNBT modifiers, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ToolStackChangeEvent((ToolStack) (Object) this));
    }

    @Inject(method = "removeModifier", at = @At("RETURN"))
    private void onRemoveModifier(ModifierId modifier, int level, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new ToolStackChangeEvent((ToolStack) (Object) this));
    }
}
