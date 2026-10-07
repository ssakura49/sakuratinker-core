package com.ssakura49.sakuratinker.core.library.context;

import com.ssakura49.sakura_library.entity.damagesource.LegacyDamageSource;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import com.ssakura49.sakuratinker.core.utils.mixin.extradata.ToolAttackContextExtraData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class STAttackContent {
    public static void processContext(@NotNull IToolStackView tool, @NotNull ToolAttackContext context) {
        ToolAttackContextExtraData ctx = (ToolAttackContextExtraData) context;
        LivingEntity attacker = context.getAttacker();
        Entity target = context.getTarget();
        boolean isCritical = context.isCritical();
        for (ModifierEntry entry : tool.getModifierList()) {
            isCritical = entry.getHook(STHooks.CRITICAL_ATTACK).setCritical(tool, entry, attacker,
                    context.getHand(), target, context.getSlotType(), context.isFullyCharged(),
                    context.isExtraAttack(), isCritical);
            if (isCritical) {
                break;
            }
        }
        if (isCritical && !context.isCritical()) {
            ctx.sakuratinker$setCriticalModifier(1.5f);
        }
        DamageSource vanilla = context.makeDamageSource();
        LegacyDamageSource source = vanilla instanceof LegacyDamageSource legacy ? legacy : LegacyDamageSource.any(vanilla);
        for (ModifierEntry entry : tool.getModifierList()) {
            source = entry.getHook(STHooks.MODIFY_DAMAGE_SOURCE).modifyDamageSource(tool, entry, attacker,
                    context.getHand(), target, context.getSlotType(), context.isFullyCharged(),
                    context.isExtraAttack(), context.isCritical(), source);
        }
        ctx.sakuratinker$setDamageSource(source);
    }
}
