package com.ssakura49.sakuratinker.core.api;

import net.minecraft.world.item.ItemStack;


public interface IDisarmProtected {
    default boolean isDisarmProtected(ItemStack stack) {
        return true;
    }
}
