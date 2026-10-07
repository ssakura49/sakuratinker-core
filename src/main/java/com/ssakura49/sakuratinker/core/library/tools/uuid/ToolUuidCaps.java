package com.ssakura49.sakuratinker.core.library.tools.uuid;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;


public final class ToolUuidCaps {
    public static final ResourceLocation NAME = SakuraTinker.getResource("tool_uuid");

    public static final Capability<IToolUuid> TOOL_UUID = CapabilityManager.get(new CapabilityToken<>() {});

    private ToolUuidCaps() {
    }

    public static void register() {
        ToolCapabilityProvider.register(ToolUuidCaps::newProvider);
    }

    private static ToolCapabilityProvider.IToolCapabilityProvider newProvider(ItemStack stack, Supplier<? extends IToolStackView> tool) {
        return new ToolUuidCapabilityProvider(stack, tool);
    }

    public static Optional<UUID> getUuid(IToolStackView tool) {
        return new ToolUuidImpl(tool, null).getUuid();
    }

    public static Optional<UUID> getOrCreateUuid(IToolStackView tool) {
        return new ToolUuidImpl(tool, null).getOrCreateUuid();
    }

    public static Optional<UUID> getOrCreateUuid(IToolStackView tool, ItemStack stack) {
        return new ToolUuidImpl(tool, stack).getOrCreateUuid();
    }

    public static Optional<UUID> resetUuid(IToolStackView tool, ItemStack stack) {
        return new ToolUuidImpl(tool, stack).resetUuid();
    }

    public static Optional<UUID> getUuidFromStack(ItemStack stack) {
        return stack.getCapability(TOOL_UUID)
                .map(IToolUuid::getUuid)
                .orElse(Optional.empty());
    }
}
