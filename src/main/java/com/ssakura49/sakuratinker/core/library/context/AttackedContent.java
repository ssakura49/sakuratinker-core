package com.ssakura49.sakuratinker.core.library.context;

import com.ssakura49.sakura_library.util.entity.EntityUtil;
import com.ssakura49.sakuratinker.core.utils.AttackUtil;
import java.util.function.DoubleSupplier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

public record AttackedContent(DamageSource source, LivingEntity entity, EquipmentContext context, EquipmentSlot slot) {
   public boolean isDirect() {
      return OnAttackedModifierHook.isDirectDamage(this.source);
   }

   public boolean isNullAttacker() {
      return this.source.getDirectEntity() == null && this.source.getEntity() == null;
   }

   public Entity getDirect() {
      return this.source.getDirectEntity();
   }

   public ItemStack getSlotStack(LivingEntity entity) {
      return entity.getItemBySlot(this.slot);
   }

   public boolean isArrowSource() {
      return this.source.getDirectEntity() instanceof AbstractArrow;
   }

   public @Nullable LivingEntity getAttacker() {
      Entity var2 = this.source.getEntity();
      LivingEntity var10000;
      if (var2 instanceof LivingEntity liv) {
         var10000 = liv;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public LazyOptional<TinkerDataCapability.Holder> getTinkerData() {
      return this.context.getTinkerData();
   }

   public @Nullable Player getPlayerAttack() {
      Entity var2 = this.source.getEntity();
      Player var10000;
      if (var2 instanceof Player p) {
         var10000 = p;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public boolean isFullAttack() {
      Player player = this.getPlayerAttack();
      return player == null || EntityUtil.isFullChance(player);
   }

   public boolean isCritical() {
      Entity var2 = this.source.getEntity();
      if (var2 instanceof LivingEntity attacker) {
         return AttackUtil.isVanillaCritical(false, this.isFullAttack(), attacker, this.entity);
      } else {
         return false;
      }
   }

   public float getAttackCooldown() {
      Player player = this.getPlayerAttack();
      if (player != null) {
         getCooldownFunction(player, InteractionHand.MAIN_HAND).getAsDouble();
      }

      return 1.0F;
   }

   public static DoubleSupplier getCooldownFunction(Player player, InteractionHand hand) {
      return hand == InteractionHand.OFF_HAND ? () -> (double)OffhandCooldownTracker.getCooldown(player) : () -> (double)player.getAttackStrengthScale(0.5F);
   }

   public @Nullable ToolAttackContext getDefaultContext(boolean isExtraAttack) {
      if (this.getAttacker() == null) {
         return null;
      } else {
         ToolAttackContext.Builder builder = ToolAttackContext.attacker(this.getAttacker());
         builder.target(this.entity);
         builder.hand(InteractionHand.MAIN_HAND);
         builder.cooldown(this.getAttackCooldown());
         builder.applyAttributes();
         if (isExtraAttack) {
            builder.extraAttack();
         }

         return builder.build();
      }
   }
}
