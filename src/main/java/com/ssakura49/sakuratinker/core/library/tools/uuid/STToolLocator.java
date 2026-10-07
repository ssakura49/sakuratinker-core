package com.ssakura49.sakuratinker.core.library.tools.uuid;

import com.ssakura49.sakuratinker.core.library.events.ItemSlotChangeEvent;
import com.ssakura49.sakuratinker.core.library.tools.event.ToolStackBuildEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "sakuratinker", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class STToolLocator {
    private static final Map<UUID, ToolLocation> INDEX = new HashMap<>();

    private STToolLocator() {
    }

    public static Optional<ToolLocation> locate(UUID uuid) {
        return Optional.ofNullable(INDEX.get(uuid));
    }

    public static Optional<ItemStack> getToolStack(UUID uuid) {
        ToolLocation location = INDEX.get(uuid);
        if (location == null) {
            return Optional.empty();
        }
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return Optional.empty();
        }
        Player player = server.getPlayerList().getPlayer(location.playerId());
        if (player == null) {
            return Optional.empty();
        }
        return location.resolve(player);
    }

    @SubscribeEvent
    public static void onToolStackBuild(ToolStackBuildEvent event) {
        ItemStack result = event.getItemStackResult();
        if (result.isEmpty() || result.getMaxStackSize() > 1) {
            return;
        }
        if (result.getItem() instanceof IModifiable) {
            ToolUuidCaps.getOrCreateUuid(ToolStack.from(result), result);
        }
    }

    /** 登录：一次性初始化该玩家全部条目 */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) {
            refreshPlayer(player);
        }
    }

    public static void clearPlayer(UUID playerId) {
        INDEX.entrySet().removeIf(entry -> entry.getValue().playerId().equals(playerId));
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        INDEX.entrySet().removeIf(entry -> entry.getValue().playerId().equals(playerId));
    }

    @SubscribeEvent
    public static void onItemSlotChange(ItemSlotChangeEvent event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide) {
            return;
        }
        unbind(player, event.getOldStack());
        ItemStack newStack = event.getNewStack();
        if (!newStack.isEmpty()) {
            int slot = event.getSlot();
            putTool(player, player.getUUID(), newStack, mapSlotType(slot), mapSlotIndex(slot), null);
        }
    }

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide) {
            return;
        }
        unbind(player, event.getFrom());
        ItemStack to = event.getTo();
        if (!to.isEmpty()) {
            putTool(player, player.getUUID(), to, ToolLocation.SlotType.CURIO, event.getSlotIndex(), event.getIdentifier());
        }
    }

    private static void unbind(Player player, ItemStack stack) {
        if (stack.isEmpty() || stack.getMaxStackSize() > 1) {
            return;
        }
        UUID playerId = player.getUUID();
        getUuidReadonly(stack).ifPresent(uuid -> {
            ToolLocation location = INDEX.get(uuid);
            if (location != null && location.playerId().equals(playerId)) {
                INDEX.remove(uuid);
            }
        });
    }

    private static Optional<UUID> getUuidReadonly(ItemStack stack) {
        if (stack.getItem() instanceof IModifiable) {
            return ToolUuidCaps.getUuid(ToolStack.from(stack));
        }
        return CurioUuidCaps.getUuid(stack);
    }

    private static void putTool(Player player, UUID playerId, ItemStack stack, ToolLocation.SlotType type, int slot, @Nullable String extra) {
        if (stack.isEmpty() || stack.getMaxStackSize() > 1) {
            return;
        }
        if (stack.getItem() instanceof IModifiable) {
            ToolUuidCaps.getOrCreateUuid(ToolStack.from(stack), stack)
                    .ifPresent(uuid -> INDEX.put(uuid, new ToolLocation(playerId, type, slot, extra)));
        } else if (extra != null) {
            CurioUuidCaps.getOrCreateUuid(stack)
                    .ifPresent(uuid -> INDEX.put(uuid, new ToolLocation(playerId, type, slot, extra)));
        }
    }

    private static ToolLocation.SlotType mapSlotType(int slot) {
        if (slot < 36) {
            return ToolLocation.SlotType.INVENTORY;
        }
        if (slot < 40) {
            return ToolLocation.SlotType.EQUIP;
        }
        return ToolLocation.SlotType.OFFHAND;
    }

    private static int mapSlotIndex(int slot) {
        if (slot < 36) {
            return slot;
        }
        if (slot < 40) {
            return slot - 34;
        }
        return 0;
    }

    private static void refreshPlayer(Player player) {
        UUID playerId = player.getUUID();
        INDEX.entrySet().removeIf(entry -> entry.getValue().playerId().equals(playerId));

        putTool(player, playerId, player.getMainHandItem(), ToolLocation.SlotType.MAINHAND, 0, null);
        putTool(player, playerId, player.getOffhandItem(), ToolLocation.SlotType.OFFHAND, 0, null);
        EquipmentSlot[] equips = EquipmentSlot.values();
        for (int i = 0; i < equips.length; i++) {
            putTool(player, playerId, player.getItemBySlot(equips[i]), ToolLocation.SlotType.EQUIP, i, null);
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            putTool(player, playerId, inventory.getItem(i), ToolLocation.SlotType.INVENTORY, i, null);
        }
    }
}
