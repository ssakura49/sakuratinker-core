package com.ssakura49.sakuratinker.core.eventhandler;

import com.ssakura49.sakura_library.entity.damagesource.interfaces.IPercentageBypassArmor;
import com.ssakura49.sakura_library.entity.damagesource.interfaces.IPercentageBypassMagic;
import com.ssakura49.sakura_library.event.entity.LivingDamageCalculationEvent;
import com.ssakura49.sakura_library.event.entity.player.AttackSpeedModifyEvent;
import com.ssakura49.sakuratinker.core.library.events.TinkerToolCriticalEvent;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior.EffectApplicableModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior.LivingOnDeathModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior.LivingOnPreventDeathModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat.GenericCombatModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat.HolderDamageTakeModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat.MeleeCooldownModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat.ShieldBlockingModifierHook;
import com.ssakura49.sakuratinker.core.utils.EffectResult;
import com.ssakura49.sakuratinker.core.utils.ToolUtil;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@EventBusSubscriber(
   modid = "sakuratinker",
   bus = Bus.FORGE
)
public class CombatHandler {
   @SubscribeEvent
   public static void onArmorStage(LivingDamageCalculationEvent.Armor event) {
      LivingEntity attacker = event.getAttacker();
      if (attacker == null) return;
      ItemStack mainHand = attacker.getMainHandItem();
      if (mainHand.isEmpty()) return;
      IToolStackView tool = ToolStack.from(mainHand);
      if (tool.isBroken()) return;
      float bypass = (event.getSource() instanceof IPercentageBypassArmor b) ? b.getArmorBypassPercentage() : 0.0F;
      bypass = tool.getHook(STHooks.ARMOR_BYPASS).modifyArmorBypass(
              tool, ModifierEntry.EMPTY,
              event.getSource(), attacker, event.getTarget(),
              event.getOriginalDamage(), event.getVanillaArmorDamage(), bypass
      );

      event.scaleArmorBypass(bypass);
   }

   @SubscribeEvent
   public static void onMagicStage(LivingDamageCalculationEvent.Magic event) {
      LivingEntity attacker = event.getAttacker();
      if (attacker == null) return;
      ItemStack mainHand = attacker.getMainHandItem();
      if (mainHand.isEmpty()) return;
      IToolStackView tool = ToolStack.from(mainHand);
      if (tool.isBroken()) return;
      float bypass = (event.getSource() instanceof IPercentageBypassMagic b)
              ? b.getMagicBypassPercentage() : 0.0F;
      bypass = tool.getHook(STHooks.MAGIC_BYPASS).modifyMagicBypass(
              tool, ModifierEntry.EMPTY,
              event.getSource(), attacker, event.getTarget(),
              event.getOriginalDamage(), event.getVanillaMagicDamage(), bypass
      );
      event.scaleMagicBypass(bypass);
   }

   @SubscribeEvent
   public static void onPostStage(LivingDamageCalculationEvent.Post event) {
      LivingEntity attacker = event.getAttacker();
      if (attacker == null) return;
      ItemStack mainHand = attacker.getMainHandItem();
      if (mainHand.isEmpty()) return;
      IToolStackView tool = ToolStack.from(mainHand);
      if (tool.isBroken()) return;
      float damage = event.getDamage();
      damage = tool.getHook(STHooks.POST_DAMAGE).modifyPostDamage(
              tool, ModifierEntry.EMPTY,
              event.getSource(), attacker, event.getTarget(),
              damage
      );
      event.setDamage(damage);
   }

   @SubscribeEvent
   public static void onLivingDamage(LivingDamageEvent event) {
      LivingEntity entity = event.getEntity();
      EquipmentContext context = new EquipmentContext(entity);
      for (EquipmentSlot slotType : EquipmentSlot.values()) {
         IToolStackView tool = context.getToolInSlot(slotType);
         if (tool != null && !tool.isBroken()) {
            for (ModifierEntry e : tool.getModifierList()) {
               HolderDamageTakeModifierHook hook = e.getHook(STHooks.HOLDER_DAMAGE_TAKE);
               hook.onHolderTakeDamage(tool, e, event, entity, event.getSource());
            }
         }
      }

   }


//   @SubscribeEvent
//   public static void onLivingKill(LivingDeathEvent event) {
//      Entity entity = event.getSource().getEntity();
//      if (entity instanceof LivingEntity attacker) {
//         ToolStack tool = ToolUtil.getToolInHand(attacker);
//         if (!ToolUtil.isNotBrokenOrNull(tool)) {
//            return;
//         }
//
//         tool.getModifierList().forEach((e) -> {
//            GenericCombatModifierHook hook = (GenericCombatModifierHook)e.getHook(STHooks.GENERIC_COMBAT);
//            hook.onKillLivingTarget(tool, e, event, attacker, event.getEntity());
//         });
//      }
//
//   }

   @SubscribeEvent
   public static void onCriticalHit(TinkerToolCriticalEvent event) {
      LivingEntity attacker = event.getContext().getAttacker();
      LivingEntity target = event.getContext().getLivingTarget();
      if (target != null) {
         event.getTool().getModifierList().forEach((e) -> {
            GenericCombatModifierHook hook = (GenericCombatModifierHook)e.getHook(STHooks.GENERIC_COMBAT);
            hook.onMeleeCriticalHit(event.getTool(), e, event, attacker, target);
         });
      }

   }

   @SubscribeEvent
   public static void onShieldBlock(ShieldBlockEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity instanceof Player player) {
         if (player.getUseItem().isEmpty()) {
            return;
         }

         ToolStack tool = ToolStack.from(player.getUseItem());
         tool.getModifierList().forEach((e) -> {
            ShieldBlockingModifierHook hook = (ShieldBlockingModifierHook)e.getHook(STHooks.SHIELD_BLOCKING);
            hook.onShieldBlocked(tool, e, event, player, event.getDamageSource());
         });
      }

   }

   @SubscribeEvent
   public static void onModifyAttackCooldown(AttackSpeedModifyEvent event) {
      Player player = event.getPlayer();
      ToolStack tool = ToolUtil.getToolInHand(player);
      if (ToolUtil.isNotBrokenOrNull(tool)) {
         tool.getModifierList().forEach((e) -> {
            MeleeCooldownModifierHook hook = (MeleeCooldownModifierHook)e.getHook(STHooks.MELEE_COOLDOWN);
            hook.modifyAttackCooldown(tool, e, player, event);
         });
      }

   }


   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void onLivingDeath(LivingDeathEvent event) {
      LivingEntity victim = event.getEntity();
      DamageSource source = event.getSource();
      EquipmentContext context = new EquipmentContext(victim);
      boolean canIgnore = LivingOnPreventDeathModifierHook.canIgnorePassInvul(STHooks.PREVENT_DEATH, context);

      if (shouldPrevent(source, canIgnore)) {
         float healthRemain = LivingOnPreventDeathModifierHook.onHoldingPreventDeath(STHooks.PREVENT_DEATH, context, source, victim);

         if (healthRemain > 0) {
            event.setCanceled(true);
            victim.setHealth(healthRemain);
            return;
         }
      }

      LivingOnDeathModifierHook.handleDeath(STHooks.ON_DEATH,context,source,victim);

      if (source.getEntity() instanceof LivingEntity attacker && attacker != victim) {
         ToolStack tool = ToolUtil.getToolInHand(attacker);
         if (ToolUtil.isNotBrokenOrNull(tool)) {
            for (ModifierEntry e : tool.getModifierList()) {
               GenericCombatModifierHook hook = e.getHook(STHooks.GENERIC_COMBAT);
               hook.onKillLivingTarget(tool, e, event, attacker, victim);
            }
         }
      }
   }

   public static boolean shouldPrevent(DamageSource source, boolean canIgnore) {
      if (canIgnore) {
         return true;
      }
      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return false;
      }
      if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
         return false;
      }
      return true;
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void onEffectApply(MobEffectEvent.Applicable event) {
      LivingEntity entity = event.getEntity();
      EquipmentContext context = new EquipmentContext(entity);
      MobEffectInstance effect = event.getEffectInstance();
      MobEffectInstance currentEffect = effect;
      boolean isModified = false;
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = entity.getItemBySlot(slot);
         IToolStackView tool = context.getToolInSlot(slot);
         if (stack.getItem() instanceof IModifiable && tool!=null&&!tool.isBroken()) {
            for (ModifierEntry entry : tool.getModifierList()) {
               EffectApplicableModifierHook hook = entry.getHook(STHooks.EFFECT_APPLICABLE);
               EffectResult result = hook.onApplyEffect(tool, entry, slot, currentEffect, entity);
               if (result.getAction() == EffectResult.Action.BLOCK) {
                  event.setResult(Event.Result.DENY);
                  return;
               }
               if (result.getAction() == EffectResult.Action.MODIFY) {
                  currentEffect = result.getModifiedEffect();
                  isModified = true;
               }
            }
         }
      }
      if (isModified && currentEffect != null) {
         event.setResult(Event.Result.DENY);
         if (currentEffect != effect) {
            entity.addEffect(currentEffect);
         }
      }
   }
}
