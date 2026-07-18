package com.ssakura49.sakuratinker.core.eventhandler;

import com.ssakura49.sakuratinker.core.network.PacketHandler;
import com.ssakura49.sakuratinker.core.network.s2c.RefreshAllModifierConfigSpecs;
import com.ssakura49.sakuratinker.core.tools.modifiers.base.DynamicConfigDescModifier;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class DynamicConfigDescModifierHandler {
   @SubscribeEvent
   public static void updateConfig(ServerStartedEvent event) {
      DynamicConfigDescModifier.refreshAllModifierConfigSpecs();
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void updateConfig(OnDatapackSyncEvent event) {
      if (event.getPlayer() != null) {
         PacketHandler.sendToPlayer(new RefreshAllModifierConfigSpecs(), event.getPlayer());
      } else {
         PacketHandler.sendToClient(new RefreshAllModifierConfigSpecs());
      }

   }
}
