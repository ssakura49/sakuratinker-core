package com.ssakura49.sakuratinker.core.utils;

import com.ssakura49.sakuratinker.STCrConfig;
import com.ssakura49.sakuratinker.core.entity.VisualScaledProjectile;
import com.ssakura49.sakuratinker.core.library.interfaces.entity.IToolEntity;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

public class EntityUtil {
   public static boolean checkTarget(Projectile projectile, Entity target) {
      if (projectile instanceof IToolEntity tool) {
         if (tool.shouldPreventPlayerDamage() && target instanceof Player) {
            return false;
         }

         if (tool.shouldPreventSelfDamage() && target == projectile.getOwner()) {
            return false;
         }
      }

      Entity var3 = projectile.getOwner();
      LivingEntity var10000;
      if (var3 instanceof LivingEntity living) {
         var10000 = living;
      } else {
         var10000 = null;
      }

      return checkTarget(var10000, target);
   }

   public static boolean checkTarget(@Nullable LivingEntity attacker, Entity target) {
      if (attacker instanceof IToolEntity tool) {
         if (tool.shouldPreventPlayerDamage() && target instanceof Player) {
            return false;
         }

         if (tool.shouldPreventSelfDamage() && target == attacker) {
            return false;
         }
      }

      if (!(Boolean)STCrConfig.COMMON.ALLOW_AOE_ATTACK_PLAYER.get() && target instanceof Player) {
         return false;
      } else if (target == attacker) {
         return false;
      } else if (!(target instanceof VisualScaledProjectile) && !(target instanceof ItemEntity) && !(target instanceof ExperienceOrb)) {
         if (attacker instanceof Player player) {
             if (target instanceof Player player1) {
                 return player.canHarmPlayer(player1);
            }
         }

         return attacker == null || !target.isAlliedTo(attacker);
      } else {
         return false;
      }
   }
}
