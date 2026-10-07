package com.ssakura49.sakuratinker.core.eventhandler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ssakura49.sakuratinker.core.client.book.STCrBookData;
import com.ssakura49.sakuratinker.core.client.gui.item.ToolCustomBarDecorator;
import com.ssakura49.sakuratinker.core.client.model.FoxCurioModel;
import com.ssakura49.sakuratinker.core.client.renderer.entity.FoxCurioModelRenderer;
import com.ssakura49.sakuratinker.core.init.STCrItem;
import com.ssakura49.sakuratinker.core.init.STCrKeys;
import com.ssakura49.sakuratinker.core.init.STCrParticleTypes;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.gadgets.client.FancyItemFrameRenderer;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.shared.CommonsClientEvents;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@EventBusSubscriber(
   modid = "sakuratinker",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ClientEventHandler {
   @SubscribeEvent
   static void registerModels(ModelEvent.RegisterAdditional event) {
      Collection<ResourceLocation> locations = FancyItemFrameRenderer.LOCATIONS_MODEL.values();
      Objects.requireNonNull(event);
      Objects.requireNonNull(event);
      locations.forEach(event::register);
      locations = FancyItemFrameRenderer.LOCATIONS_MODEL_MAP.values();
      Objects.requireNonNull(event);
      Objects.requireNonNull(event);
      locations.forEach(event::register);
   }

   @SubscribeEvent
   public static void registerKeys(RegisterKeyMappingsEvent event) {
      STCrKeys.registerAll(event);
   }

   @SubscribeEvent
   public static void registerParticleFactories(final RegisterParticleProvidersEvent evt) {
      STCrParticleTypes.FactoryHandler.registerFactories(new STCrParticleTypes.FactoryHandler.Consumer() {
         public <T extends ParticleOptions> void register(ParticleType<T> type, Function<SpriteSet, ParticleProvider<T>> constructor) {
            Objects.requireNonNull(constructor);
            evt.registerSpriteSet(type, constructor::apply);
         }
      });
   }

   @SubscribeEvent
   public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
      event.registerLayerDefinition(FoxCurioModel.LAYER_LOCATION, FoxCurioModel::createLayer);
   }

   @SubscribeEvent
   public static void clientSetup(FMLClientSetupEvent event) {
      CuriosRendererRegistry.register(STCrItem.FOX_MASK.get(), () -> new ICurioRenderer() {
            public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
               LivingEntity entity = slotContext.entity();
               if (!entity.isInvisible()) {
                  FoxCurioModelRenderer.INSTANCE.render(stack, slotContext, poseStack, renderLayerParent, buffer, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
               }
            }
         });
   }

   @SubscribeEvent
   public static void registerItemDecoration(RegisterItemDecorationsEvent event){
      ForgeRegistries.ITEMS.getValues().stream().filter(item -> item instanceof IModifiable).forEach(item -> {
         event.register(item, ToolCustomBarDecorator.INSTANCE);
      });
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event){
      event.enqueueWork(STCrBookData::intiBook);
      STCrBookData.SOUL_SAKURA_BOOK.fontRenderer = CommonsClientEvents.unicodeFontRender();
   }
}
