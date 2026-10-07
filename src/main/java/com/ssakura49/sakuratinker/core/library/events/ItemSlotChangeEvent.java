package com.ssakura49.sakuratinker.core.library.events;

import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;


@Getter
public class ItemSlotChangeEvent extends Event {
    private final Player player;
    private final int slot;
    private final ItemStack oldStack;
    private final ItemStack newStack;

    public ItemSlotChangeEvent(Player player, int slot, ItemStack oldStack, ItemStack newStack) {
        this.player = player;
        this.slot = slot;
        this.oldStack = oldStack;
        this.newStack = newStack;
    }

}
