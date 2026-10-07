package com.ssakura49.sakuratinker.core.library.tools.uuid;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.UUID;


public record ToolLocation(UUID playerId, SlotType slotType, int slot, @Nullable String extra) {

    public ToolLocation(UUID playerId, SlotType slotType, int slot) {
        this(playerId, slotType, slot, null);
    }

    public enum SlotType {
        MAINHAND,
        OFFHAND,
        EQUIP,
        INVENTORY,
        CURIO
    }

    public Optional<ItemStack> resolve(Player player) {
        return switch (slotType) {
            case MAINHAND -> Optional.of(player.getMainHandItem());
            case OFFHAND -> Optional.of(player.getOffhandItem());
            case EQUIP -> Optional.of(player.getItemBySlot(EquipmentSlot.values()[slot]));
            case INVENTORY -> Optional.of(player.getInventory().getItem(slot));
            case CURIO -> {
                if (extra == null) {
                    yield Optional.empty();
                }
                yield CuriosApi.getCuriosInventory(player)
                        .map(inv -> inv.getStacksHandler(extra)
                                .map(handler -> handler.getStacks().getStackInSlot(slot))
                                .orElse(ItemStack.EMPTY));
            }
        };
    }
}
