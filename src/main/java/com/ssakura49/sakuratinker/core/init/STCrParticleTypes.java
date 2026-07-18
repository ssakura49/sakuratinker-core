package com.ssakura49.sakuratinker.core.init;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.client.particles.wish.WispParticleData;
import com.ssakura49.sakuratinker.core.client.particles.wish.WispParticleType;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class STCrParticleTypes {
   public static final DeferredRegister<ParticleType<?>> PARTICLES;
   public static final RegistryObject<ParticleType<WispParticleData>> WISP;

   public static void registerParticles(BiConsumer<ParticleType<?>, ResourceLocation> r) {
      r.accept(WISP.get(), SakuraTinker.getResource("wisp"));
   }

   static {
      PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "sakuratinker");
      WISP = PARTICLES.register("wisp", WispParticleType::new);
   }

   public static class FactoryHandler {
      public static void registerFactories(Consumer consumer) {
         consumer.register(STCrParticleTypes.WISP.get(), WispParticleType.Factory::new);
      }

      public interface Consumer {
         <T extends ParticleOptions> void register(ParticleType<T> var1, Function<SpriteSet, ParticleProvider<T>> var2);
      }
   }
}
