package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.core.library.context.STAttackContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;


@Mixin(value = ToolAttackUtil.class, remap = false, priority = 500)
public class ToolAttackUtilMixin {
    @Inject(method = "performAttack", at = @At("HEAD"))
    private static void sakura$processContext(IToolStackView tool, ToolAttackContext context, CallbackInfoReturnable<Boolean> cir) {
        STAttackContent.processContext(tool, context);
    }
}
