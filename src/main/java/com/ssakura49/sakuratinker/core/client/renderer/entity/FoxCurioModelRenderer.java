package com.ssakura49.sakuratinker.core.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ssakura49.sakuratinker.core.client.model.FoxCurioModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class FoxCurioModelRenderer implements ICurioRenderer.ModelRender<FoxCurioModel<LivingEntity>> {
   public static final FoxCurioModelRenderer INSTANCE = new FoxCurioModelRenderer();
   public static final FoxCurioModel<LivingEntity> MODEL = new FoxCurioModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(FoxCurioModel.LAYER_LOCATION));
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("sakuratinker", "textures/model/fox_curio.png");

   @Override
   public FoxCurioModel<LivingEntity> getModel(ItemStack stack, SlotContext slotContext) {
      return MODEL;
   }

   @Override
   public ResourceLocation getModelTexture(ItemStack stack, SlotContext slotContext) {
      return TEXTURE;
   }

   @Override
   public void prepareModel(ItemStack stack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> renderLayerParent, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      LivingEntity entity = slotContext.entity();
      FoxCurioModel<LivingEntity> model = this.getModel(stack, slotContext);

      model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
      model.Head.yRot = netHeadYaw * ((float)Math.PI / 180F);
      model.Head.xRot = headPitch * ((float)Math.PI / 180F);
      EntityModel<LivingEntity> baseModel = renderLayerParent.getModel();
      if (baseModel instanceof HumanoidModel<?> humanoidModel) {
         model.Body.xRot = humanoidModel.body.xRot;
         model.Body.yRot = humanoidModel.body.yRot;
         model.Body.zRot = humanoidModel.body.zRot;
      }

      this.syncEarRotations(model, entity, netHeadYaw, headPitch, partialTicks);
      this.animateTail(model, entity, limbSwingAmount, partialTicks);
   }

   private void syncEarRotations(FoxCurioModel<LivingEntity> model, LivingEntity entity, float netHeadYaw, float headPitch, float partialTicks) {
      float radians = 1.1868238F;
      model.Left_ear.setPos(4.1F, -8.5F, -1.0F);
      model.Right_ear.setPos(-4.1F, -8.5F, -1.0F);
      model.Right_ear.zRot = radians;
      model.Right_ear.yRot = (float)Math.PI;
      model.Left_ear.zRot = -radians;
      model.Left_ear.yRot = (float)Math.PI;
      ICurioRenderer.followHeadRotations(entity, model.Ear);

      float time = (float)entity.tickCount + partialTicks;
      float wiggle = Mth.sin(time * 0.2F) * 0.05F;

      model.Left_ear.xRot = wiggle;
      model.Right_ear.xRot = wiggle;
   }

   private void animateTail(FoxCurioModel<LivingEntity> model, LivingEntity entity, float limbSwingAmount, float partialTicks) {
      float time = (float)entity.tickCount + partialTicks;
      float baseSpeed = 0.15F;
      float swingStrength = 0.2F + limbSwingAmount * 0.5F;

      ModelPart[] tails = new ModelPart[]{model.Tail, model.Tail2, model.Tail3, model.Tail4, model.Tail5, model.Tail6, model.Tail7};
      boolean isCrouching = entity.isCrouching();
      if (isCrouching) {
         model.Tail.xRot += -0.13962634F;
      }

      for(int i = 0; i < tails.length; ++i) {
         float delay = (float)i * 0.4F;
         float segmentFactor = 1.0F - (float)i * 0.12F;
         float yaw = Mth.sin(time * baseSpeed - delay) * swingStrength * segmentFactor;
         float pitch = -Mth.cos(time * baseSpeed * 0.7F - delay) * swingStrength * 0.35F * segmentFactor;
         float tremor = Mth.sin(time * 0.3F + (float)i) * 0.02F;
         tails[i].yRot = yaw + tremor;
         tails[i].xRot = pitch + tremor * 0.5F;
      }
   }

}
