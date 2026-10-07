package com.ssakura49.sakuratinker.core.library.tools.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;


public class ToolStackBuildEvent extends Event {
    private final ItemStack result;

    public ToolStackBuildEvent(ItemStack result) {
        this.result = result;
    }

    public ItemStack getItemStackResult() {
        return result;
    }

    /** 便捷：结果的工具视图（非工具物品时返回 null 需自行判断） */
    public ToolStack getTool() {
        return ToolStack.from(result);
    }
}
