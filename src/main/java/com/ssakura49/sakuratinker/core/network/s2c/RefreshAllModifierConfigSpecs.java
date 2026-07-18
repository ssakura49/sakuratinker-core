package com.ssakura49.sakuratinker.core.network.s2c;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.tools.modifiers.base.DynamicConfigDescModifier;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.mantle.client.SafeClientAccess;

public class RefreshAllModifierConfigSpecs {
   public RefreshAllModifierConfigSpecs() {
   }

   public RefreshAllModifierConfigSpecs(FriendlyByteBuf byteBuf) {
   }

   public void toByte(FriendlyByteBuf byteBuf) {
   }

   public void handle(Supplier<NetworkEvent.Context> supplier) {
      Player player = SafeClientAccess.getPlayer();
      if (player != null) {
         SakuraTinker.LOGGER.info("Initialize Modifier Config for client player: {}", player.getName());
      }

      DynamicConfigDescModifier.refreshAllModifierConfigSpecs();
      ((NetworkEvent.Context)supplier.get()).setPacketHandled(true);
   }
}
