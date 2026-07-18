package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import com.ssakura49.sakura_library.entity.damagesource.LegacyDamageSource;
import java.util.Collection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public interface ModifyDamageSourceModifierHook {
   default LegacyDamageSource modifyDamageSource(IToolStackView tool, ModifierEntry entry, LivingEntity attacker, InteractionHand hand, Entity target, EquipmentSlot sourceSlot, boolean isFullyCharged, boolean isExtraAttack, boolean isCritical, LegacyDamageSource source) {
      return source;
   }

   default LegacyDamageSource modifyArrowDamageSource(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, AbstractArrow arrow, @Nullable LivingEntity attacker, @Nullable Entity target, LegacyDamageSource source) {
      return source;
   }

   public static record AllMerger(Collection<ModifyDamageSourceModifierHook> modules) implements ModifyDamageSourceModifierHook {
      public LegacyDamageSource modifyDamageSource(IToolStackView tool, ModifierEntry entry, LivingEntity attacker, InteractionHand hand, Entity target, EquipmentSlot sourceSlot, boolean isFullyCharged, boolean isExtraAttack, boolean isCritical, LegacyDamageSource source) {
         LegacyDamageSource damageSource = source;

         for(ModifyDamageSourceModifierHook module : this.modules) {
            damageSource = module.modifyDamageSource(tool, entry, attacker, hand, target, sourceSlot, isFullyCharged, isExtraAttack, isCritical, damageSource);
         }

         return damageSource;
      }

      public LegacyDamageSource modifyArrowDamageSource(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, @NotNull AbstractArrow arrow, @Nullable LivingEntity attacker, @Nullable Entity target, LegacyDamageSource source) {
         LegacyDamageSource damageSource = source;

         for(ModifyDamageSourceModifierHook module : this.modules) {
            damageSource = module.modifyArrowDamageSource(modifiers, persistentData, modifier, arrow, attacker, target, damageSource);
         }

         return damageSource;
      }
   }
}
