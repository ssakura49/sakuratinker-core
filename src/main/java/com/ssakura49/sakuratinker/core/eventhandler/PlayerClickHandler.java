package com.ssakura49.sakuratinker.core.eventhandler;

import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.click.LeftClickModifierHook;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@EventBusSubscriber(
   modid = "sakuratinker",
   bus = Bus.FORGE
)
public class PlayerClickHandler {
   @SubscribeEvent
   public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
      Player player = event.getEntity();
      if (player != null && player.level().isClientSide) {
         ItemStack stack = player.getItemInHand(player.getUsedItemHand());
         if (stack.getItem() instanceof IModifiable) {
            EquipmentSlot slot = stack.getEquipmentSlot();
            LeftClickModifierHook.handleLeftClick(stack, player, slot);
         }
      }

   }

   @SubscribeEvent
   public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
      Player player = event.getEntity();
      BlockPos pos = event.getPos();
      if (player != null) {
         BlockState state = player.level().getBlockState(pos);
         ItemStack stack = player.getItemInHand(player.getUsedItemHand());
         if (stack.getItem() instanceof IModifiable) {
            EquipmentSlot slot = stack.getEquipmentSlot();
            LeftClickModifierHook.handleLeftClickBlock(stack, player, slot, state, pos);
         }
      }

   }
}
