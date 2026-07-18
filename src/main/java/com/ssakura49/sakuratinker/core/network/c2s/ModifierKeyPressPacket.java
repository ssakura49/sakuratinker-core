package com.ssakura49.sakuratinker.core.network.c2s;

import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.click.KeyPressModifierHook;
import com.ssakura49.sakuratinker.core.utils.ToolUtil;
import com.ssakura49.tinkercuriolib.utils.TCToolUtil;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class ModifierKeyPressPacket {
   private final String keyId;

   public ModifierKeyPressPacket(String keyId) {
      this.keyId = keyId;
   }

   public ModifierKeyPressPacket(FriendlyByteBuf buf) {
      this.keyId = buf.readUtf();
   }

   public void toBytes(FriendlyByteBuf buf) {
      buf.writeUtf(this.keyId);
   }

   public void handle(Supplier<NetworkEvent.Context> ctx) {
      ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
         ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
         if (player != null) {
            for(InteractionHand hand : InteractionHand.values()) {
               ItemStack stack = player.getItemInHand(hand);
               this.processItemStack(stack, player);
            }

            for(ItemStack armor : player.getArmorSlots()) {
               this.processItemStack(armor, player);
            }

            for(ItemStack curio : TCToolUtil.getStacks(player)) {
               this.processItemStack(curio, player);
            }
         }

      });
      ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
   }

   private void processItemStack(ItemStack stack, Player player) {
      if (ToolUtil.checkTool(stack)) {
         ToolStack tool = ToolStack.from(stack);

         for(ModifierEntry entry : tool.getModifierList()) {
            Modifier var7 = entry.getModifier();
            if (var7 instanceof KeyPressModifierHook hook) {
                hook.onKeyPress(tool, entry, player, this.keyId);
            }
         }
      }

   }
}
