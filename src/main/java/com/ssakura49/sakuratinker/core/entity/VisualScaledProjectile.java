package com.ssakura49.sakuratinker.core.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class VisualScaledProjectile extends Projectile {
   public static final String KEY_SCALE = "scale";
   public static final String KEY_SCALE_OLD = "scale_old";
   public static final String KEY_DAMAGE = "damage";
   public float baseDamage = 1.0F;
   public static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(VisualScaledProjectile.class, EntityDataSerializers.FLOAT);
   public static final EntityDataAccessor<Float> DATA_SCALE_OLD = SynchedEntityData.defineId(VisualScaledProjectile.class, EntityDataSerializers.FLOAT);

   public VisualScaledProjectile(EntityType<? extends VisualScaledProjectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setScale(1.0F);
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_SCALE, 1.0F);
      this.entityData.define(DATA_SCALE_OLD, 1.0F);
   }

   public float getScale() {
      return this.entityData.get(DATA_SCALE);
   }

   public void setScale(float amount) {
      this.entityData.set(DATA_SCALE, amount);
   }

   public float getScaleOld() {
      return (Float)this.entityData.get(DATA_SCALE_OLD);
   }

   public void setScaleOld(float amount) {
      this.entityData.set(DATA_SCALE_OLD, amount);
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setScale(compoundTag.getFloat("scale"));
      this.setScaleOld(compoundTag.getFloat("scale_old"));
      this.baseDamage = compoundTag.getFloat("damage");
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putFloat("scale", this.getScale());
      compoundTag.putFloat("scale_old", this.getScaleOld());
      compoundTag.putFloat("damage", this.baseDamage);
   }

   static {


   }
}
