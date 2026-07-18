package com.ssakura49.sakuratinker.core.client.particles.wish;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import org.jetbrains.annotations.NotNull;

public class FXWisp extends TextureSheetParticle {
   private final boolean depthTest;
   private final float moteParticleScale;
   private final int moteHalfLife;

   public FXWisp(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float size, float red, float green, float blue, boolean depthTest, float maxAgeMul, boolean noClip, float gravity) {
      super(world, x, y, z);
      this.xd = xSpeed;
      this.yd = ySpeed;
      this.zd = zSpeed;
      this.rCol = red;
      this.gCol = green;
      this.bCol = blue;
      this.alpha = 0.375F; // 默认带有一点透明度
      this.gravity = gravity;

      // 根据随机数和传入的基础尺寸计算出粒子的初始 quadSize
      this.quadSize = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F * size;
      this.moteParticleScale = this.quadSize; // 记录基准缩放大小

      // 计算粒子的生命周期总长度
      this.lifetime = (int) (28.0 / (Math.random() * 0.3 + 0.7) * maxAgeMul);
      this.depthTest = depthTest;
      this.moteHalfLife = this.lifetime / 2;

      this.setSize(0.01F, 0.01F); // 设置极小的物理碰撞箱

      // 初始化历史坐标
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.hasPhysics = !noClip; // 如果 noClip 为 true，则关闭物理碰撞（可以穿墙）
   }

   /**
    * 动态计算粒子的渲染大小。
    * 逻辑：随着年龄(age)的增长，在前半生(HalfLife)逐渐变大，后半生逐渐缩小
    */
   @Override
   public float getQuadSize(float partialTicks) {
      float ageScale = (float) this.age / (float) this.moteHalfLife;
      if (ageScale > 1.0F) {
         ageScale = 2.0F - ageScale; // 越过寿命中点后反转，使其缩回 0
      }

      this.quadSize = this.moteParticleScale * ageScale * 0.5F;
      return this.quadSize;
   }

   @Override
   protected int getLightColor(float partialTicks) {
      return 15728880;
   }

   @Override
   public @NotNull ParticleRenderType getRenderType() {
      return this.depthTest ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT : ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      }
      this.yd -= gravity;
      this.move(this.xd, this.yd, this.zd);
      if (this.gravity == 0.0F) {
         this.xd *= 0.98;
         this.yd *= 0.98;
         this.zd *= 0.98;
      }
   }
}
