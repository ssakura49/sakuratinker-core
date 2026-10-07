package com.ssakura49.sakuratinker.core.library.tools.uuid;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.function.Supplier;


public class ToolUuidCapabilityProvider implements ToolCapabilityProvider.IToolCapabilityProvider {
    private final ItemStack stack;
    @Getter
    private final Supplier<? extends IToolStackView> tool;

    public ToolUuidCapabilityProvider(ItemStack stack, Supplier<? extends IToolStackView> tool) {
        this.stack = stack;
        this.tool = tool;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull IToolStackView toolStack, @NotNull Capability<T> cap) {
        if (cap == ToolUuidCaps.TOOL_UUID && stack.getMaxStackSize() <= 1) {
            return LazyOptional.of(() -> new ToolUuidImpl(toolStack, stack)).cast();
        }
        return LazyOptional.empty();
    }

}
