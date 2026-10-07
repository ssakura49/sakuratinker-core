package com.ssakura49.sakuratinker.core.item;

import com.ssakura49.sakura_library.client.SafeClientAccess;
import com.ssakura49.sakuratinker.core.client.book.STCrBookData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.tconstruct.TConstruct;

import java.util.List;

import static slimeknights.tconstruct.library.tools.capability.inventory.InventorySlotMenuModule.isValidContainer;

public class SoulSakuraBook extends LecternBookItem {
   private static final Component CLICK_TO_OPEN = TConstruct.makeTranslation("item", "book.click_to_open").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC);

   public SoulSakuraBook(Item.Properties properties) {
      super(properties);
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
      if (world != null && world.isClientSide) {
         Player player = SafeClientAccess.getPlayer();
         if (player != null && isValidContainer(player.containerMenu)) {
            Inventory inventory = player.getInventory();
            if (inventory.items.contains(stack) || inventory.offhand.contains(stack)) {
               tooltip.add(CLICK_TO_OPEN);
            }
         }
      }
      super.appendHoverText(stack, world, tooltip, flagIn);
   }


   @Override
   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (world.isClientSide) {
         STCrBookData.SOUL_SAKURA_BOOK.openGui(hand,stack);
      }
      return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
   }

   @Override
   public void openLecternScreenClient(BlockPos pos, ItemStack stack) {
      STCrBookData.SOUL_SAKURA_BOOK.openGui(pos,stack);
   }

   @Override
   public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack held, Slot slot, ClickAction action, Player player, SlotAccess access) {
      if (action == ClickAction.SECONDARY && held.isEmpty() && slot.container == player.getInventory() && slot.allowModification(player) && isValidContainer(player.containerMenu)) {
         if (player.level().isClientSide) {
            player.containerMenu.resumeRemoteUpdates();
            player.closeContainer();
            STCrBookData.SOUL_SAKURA_BOOK.openGui(slot.getSlotIndex(), stack);
         }
         return true;
      }
      return false;
   }
}
