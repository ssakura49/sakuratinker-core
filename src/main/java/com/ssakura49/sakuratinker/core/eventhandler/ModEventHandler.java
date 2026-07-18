package com.ssakura49.sakuratinker.core.eventhandler;

import com.ssakura49.sakuratinker.core.entity.VisualScaledProjectile;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;

@EventBusSubscriber(
   modid = "sakuratinker",
   bus = Bus.MOD
)
public class ModEventHandler {
   @SubscribeEvent
   public static void commonSetup(FMLCommonSetupEvent event) {
      EntityModifierCapability.registerEntityPredicate((entity) -> entity instanceof VisualScaledProjectile);
   }
}
