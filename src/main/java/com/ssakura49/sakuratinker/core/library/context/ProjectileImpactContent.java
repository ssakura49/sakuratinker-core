package com.ssakura49.sakuratinker.core.library.context;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

public class ProjectileImpactContent {
   private final ProjectileImpactEvent event;
   private final AbstractArrow arrow;

   public ProjectileImpactContent(ProjectileImpactEvent event, AbstractArrow arrow) {
      this.event = event;
      this.arrow = arrow;
   }

   public AbstractArrow arrow() {
      return this.arrow;
   }

   public Entity getOwner() {
      return this.arrow().getOwner();
   }

   public Projectile getProjectile() {
      return this.event.getProjectile();
   }

   public Entity getTarget() {
      return this.event.getEntity();
   }

   public HitResult getResult() {
      return this.event.getRayTraceResult();
   }

   public ModDataNBT getPersistent() {
      return PersistentDataCapability.getOrWarn(this.arrow);
   }

   @Nullable
   public ModifierNBT getModifiers() {
      return EntityModifierCapability.getOrEmpty(this.arrow);
   }

   @Nullable
   public EntityHitResult getEntityResult() {
      return this.getResult().getType().equals(Type.ENTITY) ? (EntityHitResult)this.getResult() : null;
   }

   @Nullable
   public BlockHitResult getBlockResult() {
      return this.getResult().getType().equals(Type.BLOCK) ? (BlockHitResult)this.getResult() : null;
   }
}
