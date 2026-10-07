package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakura_library.entity.damagesource.LegacyDamageSource;
import com.ssakura49.sakuratinker.core.utils.mixin.extradata.ToolAttackContextExtraData;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

@Mixin(value = ToolAttackContext.class, remap = false)
public abstract class ToolAttackContextMixin implements ToolAttackContextExtraData {
    @Mutable
    @Shadow
    @Final
    private float criticalModifier;

    @Unique
    private LegacyDamageSource sakuratinker$sourceReplacement = null;

    @Inject(method = "makeDamageSource", at = @At("RETURN"), cancellable = true)
    private void modifyDamageSource(CallbackInfoReturnable<DamageSource> cir) {
        if (sakuratinker$sourceReplacement != null) {
            cir.setReturnValue(sakuratinker$sourceReplacement);
        }
    }

    @Override
    @Unique
    public void sakuratinker$setCriticalModifier(float criticalModifier) {
        this.criticalModifier = criticalModifier;
    }

    @Override
    @Unique
    public void sakuratinker$setDamageSource(LegacyDamageSource source) {
        this.sakuratinker$sourceReplacement = source;
    }
}
