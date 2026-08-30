package com.ssakura49.sakuratinker;

import com.ssakura49.sakuratinker.core.STCrModule;
import com.ssakura49.sakuratinker.core.content.tier.DreadSteelTiers;
import com.ssakura49.sakuratinker.core.content.tier.InfinityTiers;
import com.ssakura49.sakuratinker.core.network.PacketHandler;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.objectweb.asm.Type;
import slimeknights.tconstruct.library.utils.Util;

@Mod("sakuratinker")
public class SakuraTinker {
   public static final String MODID = "sakuratinker";
   public static final Logger LOGGER = LogManager.getLogger("sakuratinker");
   public static StringBuilder stringBuilder = new StringBuilder();
   public static boolean modInitialized = false;

   public static ResourceLocation getResource(String string) {
      return ResourceLocation.fromNamespaceAndPath("sakuratinker", string);
   }

   public SakuraTinker(FMLJavaModLoadingContext context) {
      IEventBus modEventBus = context.getModEventBus();
      IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
      MinecraftForge.EVENT_BUS.register(this);
      modEventBus.addListener(this::commonSetup);
      modEventBus.addListener(this::onRegisterTiers);
      modEventBus.addListener(this::registerSerializers);
      STCrModule.register(modEventBus);
      PacketHandler.init();
   }

   public void onRegisterTiers(RegisterEvent event) {
      if (event.getRegistryKey().equals(Registries.ITEM)) {
         TierSortingRegistry.registerTier(InfinityTiers.instance, ResourceLocation.fromNamespaceAndPath("sakuratinker", "infinity"), List.of(Tiers.NETHERITE), List.of());
         TierSortingRegistry.registerTier(DreadSteelTiers.instance, ResourceLocation.fromNamespaceAndPath("sakuratinker", "dread_steel"), List.of(Tiers.NETHERITE), List.of(InfinityTiers.instance));
      }

   }

   public void commonSetup(FMLCommonSetupEvent event) {
   }

   public void registerSerializers(RegisterEvent event) {
   }

   @SuppressWarnings("removal")
   public static IEventBus getModEventBus() {
      return FMLJavaModLoadingContext.get().getModEventBus();
   }

   public static String makeDescriptionId(String type, String name) {
      return type + ".sakuratinker." + name;
   }

   public static String makeTranslationKey(String base, String name) {
      return Util.makeTranslationKey(base, getResource(name));
   }

   public static MutableComponent makeTranslation(String base, String name) {
      return Component.translatable(makeTranslationKey(base, name));
   }

   public static synchronized void outArray(Object[] o) {
      PrintStream var10000 = System.out;
      String var10001 = stringBuilder.toString();
      var10000.println(var10001 + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + Arrays.toString(o));
   }

   public static synchronized void out(Object o) {
      PrintStream var10000 = System.out;
      String var10001 = stringBuilder.toString();
      var10000.println(var10001 + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + o);
   }

   public static synchronized void outV(Vector2f o) {
      System.out.printf(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "](%.2f, %.2f)", o.x, o.y);
      System.out.print("\n");
   }

   public static synchronized void outV(Vector3f o) {
      System.out.printf(stringBuilder.toString() + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "](%.2f, %.2f, %.2f)", o.x, o.y, o.z);
      System.out.print("\n");
   }

   public static synchronized void out(String text, Object... o) {
      PrintStream var10000 = System.out;
      String var10001 = stringBuilder.toString();
      var10000.printf(var10001 + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]" + text, o);
      System.out.print("\n");
   }

   public static synchronized void out(Object... o) {
      PrintStream var10000 = System.out;
      String var10001 = stringBuilder.toString();
      var10000.print(var10001 + "[SakuraTinker: " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "]");

      for(Object o1 : o) {
         System.out.print(o1 + ", ");
      }

      System.out.print("\n");
   }

   public static synchronized void push() {
      stringBuilder.append(" ");
   }

   public static synchronized void pop() {
      stringBuilder.deleteCharAt(stringBuilder.length() - 1);
   }

   public static synchronized void push2() {
      push();
      push();
   }

   public static synchronized void pop2() {
      pop();
      pop();
   }

   public static String transformClassName(String className) {
      if (className.startsWith("L")) {
         className = className.substring(1);
      }

      return className.replace('/', '.').replace(";", "");
   }

   public static Stream<Type> modClassTypes() {
      return ModList.get().getModFileById("sakuratinker").getFile().getScanResult().getClasses().stream().map(ModFileScanData.ClassData::clazz);
   }

   public static List<String> autoRegNamesInPackage(String packageName) {
      return modClassTypes().filter((type) -> type.getDescriptor().startsWith("L" + packageName.replace('.', '/'))).map(Type::getClassName).toList();
   }

   public static void sealSakuraTinkerClass(Object self, String base, String solution) {
      String name = self.getClass().getName();
      if (!name.startsWith("com.ssakura49.sakuratinker.")) {
         throw new IllegalStateException(base + " being extended from invalid package " + name + ". " + solution);
      }
   }
}
