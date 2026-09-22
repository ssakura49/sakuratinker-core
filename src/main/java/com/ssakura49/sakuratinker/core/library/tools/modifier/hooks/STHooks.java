package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.library.context.VibrationContext;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.armor.WearerDamagePreHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.armor.WearerDamageTakeHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.armor.WearerKnockBackHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.armor.WearerTakeHealHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior.EffectApplicableModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior.LivingOnDeathModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior.LivingOnPreventDeathModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior.VibrationListeningModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.build.AdvancedModifierTraitHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.build.ItemFoilModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.build.ReplaceMaterialModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.click.KeyPressModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.click.LeftClickModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat.*;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.display.CustomBarDisplayModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.mining.HarvestDropModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.range.ArrowHitModifierHook;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.range.ProjectileTickModifierHook;
import com.ssakura49.sakuratinker.core.utils.EffectResult;
import com.ssakura49.sakuratinker.core.utils.SlotType;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.UUID;

public class STHooks {
   public static final IdAwareComponentRegistry<ModuleHook<?>> LOADER = new IdAwareComponentRegistry<>("Modifier Hook");
   public static final ModuleHook<WearerDamagePreHook> WEARER_DAMAGE_PRE = ModifierHooks.register(SakuraTinker.getResource("wearer_damage_pre"), WearerDamagePreHook.class, WearerDamagePreHook.AllMerger::new, new WearerDamagePreHook() {
   });
   public static final ModuleHook<WearerKnockBackHook> WEARER_KNOCK_BACK = ModifierHooks.register(SakuraTinker.getResource("wearer_knock_back"), WearerKnockBackHook.class, WearerKnockBackHook.AllMerger::new, new WearerKnockBackHook() {
   });
   public static final ModuleHook<WearerDamageTakeHook> WEARER_DAMAGE_TAKE = ModifierHooks.register(SakuraTinker.getResource("wearer_damage_take"), WearerDamageTakeHook.class, WearerDamageTakeHook.AllMerger::new, new WearerDamageTakeHook() {
   });
   public static final ModuleHook<WearerTakeHealHook> WEARER_TAKE_HEAL = ModifierHooks.register(SakuraTinker.getResource("wearer_take_heal"), WearerTakeHealHook.class, WearerTakeHealHook.AllMerger::new, new WearerTakeHealHook() {
   });
   public static final ModuleHook<GenericCombatModifierHook> GENERIC_COMBAT = ModifierHooks.register(SakuraTinker.getResource("generic_combat"), GenericCombatModifierHook.class, GenericCombatModifierHook.AllMerger::new, new GenericCombatModifierHook() {
   });
   public static final ModuleHook<MeleeCooldownModifierHook> MELEE_COOLDOWN = ModifierHooks.register(SakuraTinker.getResource("melee_cooldown"), MeleeCooldownModifierHook.class, MeleeCooldownModifierHook.AllMerger::new, new MeleeCooldownModifierHook() {
   });
   public static final ModuleHook<HolderDamageTakeModifierHook> HOLDER_DAMAGE_TAKE = ModifierHooks.register(SakuraTinker.getResource("holder_damage_take"), HolderDamageTakeModifierHook.class, HolderDamageTakeModifierHook.AllMerge::new, new HolderDamageTakeModifierHook() {
   });
   public static final ModuleHook<ShieldBlockingModifierHook> SHIELD_BLOCKING = ModifierHooks.register(SakuraTinker.getResource("shield_blocking"), ShieldBlockingModifierHook.class, ShieldBlockingModifierHook.AllMerge::new, new ShieldBlockingModifierHook() {
   });
   public static final ModuleHook<ModifyDamageSourceModifierHook> MODIFY_DAMAGE_SOURCE = ModifierHooks.register(SakuraTinker.getResource("modify_damage_source"), ModifyDamageSourceModifierHook.class, ModifyDamageSourceModifierHook.AllMerger::new, new ModifyDamageSourceModifierHook() {
   });
   public static final ModuleHook<LeftClickModifierHook> LEFT_CLICK = ModifierHooks.register(SakuraTinker.getResource("left_click"), LeftClickModifierHook.class, LeftClickModifierHook.AllMerger::new, new LeftClickModifierHook() {
   });
   public static final ModuleHook<CriticalAttackModifierHook> CRITICAL_ATTACK = ModifierHooks.register(SakuraTinker.getResource("critical_attack"), CriticalAttackModifierHook.class, CriticalAttackModifierHook.FirstMerger::new, (CriticalAttackModifierHook)(tool, entry, attacker, hand, target, sourceSlot, isFullyCharged, isExtraAttack, isCritical) -> isCritical);
   public static final ModuleHook<ReplaceMaterialModifierHook> REPLACE_MATERIAL_MODIFIER = ModifierHooks.register(SakuraTinker.getResource("replace_material_modifier"), ReplaceMaterialModifierHook.class, ReplaceMaterialModifierHook.AllMerger::new, (ReplaceMaterialModifierHook)(context, inputIndex, secondary) -> false);
   public static final ModuleHook<KeyPressModifierHook> KEY_PRESS = ModifierHooks.register(SakuraTinker.getResource("key_press"), KeyPressModifierHook.class, KeyPressModifierHook.AllMerger::new, new KeyPressModifierHook() {
   });

   public static final ModuleHook<ProjectileTickModifierHook> PROJECTILE_TICK = ModifierHooks.register(SakuraTinker.getResource("projectile_tick"), ProjectileTickModifierHook.class, ProjectileTickModifierHook.AllMerger::new, new ProjectileTickModifierHook() {
      @Override
      public void onProjectileTick(ModifierNBT modifiers, ModifierEntry entry, Level level, @NotNull Projectile projectile, ModDataNBT persistentData, boolean hasBeenShot, boolean leftOwner) {
      }
      @Override
      public void onArrowTick(ModifierNBT modifiers, ModifierEntry entry, Level level, @NotNull AbstractArrow arrow, ModDataNBT persistentData, boolean hasBeenShot, boolean leftOwner, boolean inGround, @Nullable IntOpenHashSet piercingIgnoreEntityIds) {
      }
   });

   public static final ModuleHook<ArrowHitModifierHook> ARROW_HIT = ModifierHooks.register(SakuraTinker.getResource("arrow_hit"), ArrowHitModifierHook.class, ArrowHitModifierHook.AllMerger::new, new ArrowHitModifierHook() {
      @Override
      public void afterArrowHit(ModDataNBT persistentData, ModifierEntry entry, ModifierNBT modifiers, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull LivingEntity target, float damageDealt) {
          ArrowHitModifierHook.super.afterArrowHit(persistentData, entry, modifiers, arrow, attacker, target, damageDealt);
      }
   });

   public static final ModuleHook<LivingOnPreventDeathModifierHook> PREVENT_DEATH = ModifierHooks.register(SakuraTinker.getResource("holding_death"), LivingOnPreventDeathModifierHook.class, LivingOnPreventDeathModifierHook.FirstMerger::new, new LivingOnPreventDeathModifierHook() {
      @Override
      public float onHoldingPreventDeath(LivingEntity livingEntity, IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source) {
         return 0;
      }

      @Override
      public SlotType getValidSlots() {
         return SlotType.ANY;
      }
   });

   public static final ModuleHook<LivingOnDeathModifierHook> ON_DEATH = ModifierHooks.register(SakuraTinker.getResource("on_death"), LivingOnDeathModifierHook.class, LivingOnDeathModifierHook.AllMerger::new, (tool, modifier, context, slotType, source, victim, isAliveSource) -> {});
   public static final ModuleHook<EffectApplicableModifierHook> EFFECT_APPLICABLE =
           ModifierHooks.register(
                   SakuraTinker.getResource("effect_applicable"),
                   EffectApplicableModifierHook.class,
                   EffectApplicableModifierHook.AllMerger::new,
                   (tool, entry, slot, instance, entity) -> EffectResult.pass()
           );

   public static final ModuleHook<ArmorBypassModifierHook> ARMOR_BYPASS = ModifierHooks.register(
           SakuraTinker.getResource("armor_bypass"),
           ArmorBypassModifierHook.class,
           ArmorBypassModifierHook.AllMerger::new,
           (tool, entry, source, attacker, target, rawDamage, vanillaArmorDamage, currentBypass) -> currentBypass
   );

   public static final ModuleHook<MagicBypassModifierHook> MAGIC_BYPASS = ModifierHooks.register(
           SakuraTinker.getResource("magic_bypass"),
           MagicBypassModifierHook.class,
           MagicBypassModifierHook.AllMerger::new,
           (tool, entry, source, attacker, target, damageAfterArmor, vanillaMagicDamage, currentBypass) -> currentBypass
   );

   public static final ModuleHook<PostDamageModifierHook> POST_DAMAGE = ModifierHooks.register(
           SakuraTinker.getResource("post_damage"),
           PostDamageModifierHook.class,
           PostDamageModifierHook.AllMerger::new,
           (tool, entry, source, attacker, target, currentDamage) -> currentDamage
   );

   public static final ModuleHook<HarvestDropModifierHook> HARVEST_DROP = ModifierHooks.register(
           SakuraTinker.getResource("harvest_drop"),
           HarvestDropModifierHook.class,
           HarvestDropModifierHook.AllMerger::new,
           (tool, entry, source, drop) -> drop
   );

   public static final ModuleHook<ItemFoilModifierHook> ITEM_FOIL = ModifierHooks.register(
           SakuraTinker.getResource("item_foil"),
           ItemFoilModifierHook.class,
           ItemFoilModifierHook.FirstMerger::new,
           (tool, entry) -> false
   );

   public static final ModuleHook<CustomBarDisplayModifierHook> CUSTOM_BAR = ModifierHooks.register(
           SakuraTinker.getResource("custom_bar"),
           CustomBarDisplayModifierHook.class,
           CustomBarDisplayModifierHook.AllMerger::new, // 推荐使用 AllMerger 允许堆叠
           new CustomBarDisplayModifierHook() {
              @Override
              public String getBarId(IToolStackView tool, ModifierEntry entry, int barsShown) {
                 return "";
              }
              @Override
              public boolean shouldShowBar(IToolStackView tool, ModifierEntry entry, int barsShown) {
                 return false;
              }
              @Override
              public float getBarProgress(IToolStackView tool, ModifierEntry entry, int barsShown) {
                 return 0.0F;
              }
              @Override
              public int getBarColor(IToolStackView tool, ModifierEntry entry, int barsShown) {
                 return 0;
              }
           }
   );

   public static final ModuleHook<VibrationListeningModifierHook> VIBRATION_LISTENING = ModifierHooks.register(SakuraTinker.getResource("vibration_listening"), VibrationListeningModifierHook.class, VibrationListeningModifierHook.AllMerger::new, new VibrationListeningModifierHook() {
      @Override
      public UUID getAcceptorUUID(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot) {
         return null;
      }
      @Override
      public int listenRange(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot, int range) {
         return 0;
      }
      @Override
      public boolean canReceiveVibration(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context) {
         return false;
      }
      @Override
      public void onReceivingVibration(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context) {
      }
   });

   public static final ModuleHook<AdvancedModifierTraitHook> MODIFIER_TRAIT = ModifierHooks.register(
           SakuraTinker.getResource("modifier_trait"),
           AdvancedModifierTraitHook.class,
           AdvancedModifierTraitHook.AllMerger::new,
           (context, modifier, builder, firstEncounter) -> {}
   );
}
