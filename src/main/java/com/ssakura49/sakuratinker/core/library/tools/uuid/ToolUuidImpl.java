package com.ssakura49.sakuratinker.core.library.tools.uuid;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.utils.ModDataNBTExtra;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.Optional;
import java.util.UUID;

public class ToolUuidImpl implements IToolUuid {
    public static final ResourceLocation KEY = SakuraTinker.getResource("tool_uuid");

    private final IToolStackView tool;
    @Nullable
    private final ItemStack stack;

    public ToolUuidImpl(IToolStackView tool, @Nullable ItemStack stack) {
        this.tool = tool;
        this.stack = stack;
    }

    private Optional<UUID> read() {
        ModDataNBT data = tool.getPersistentData();
        if (data.contains(KEY, Tag.TAG_INT_ARRAY)) {
            return Optional.of(ModDataNBTExtra.getUUID(data, KEY));
        }
        return Optional.empty();
    }

    private boolean canOwn() {
        return stack == null || stack.getMaxStackSize() <= 1;
    }

    @Override
    public Optional<UUID> getUuid() {
        return read();
    }

    @Override
    public Optional<UUID> getOrCreateUuid() {
        Optional<UUID> existing = read();
        if (existing.isPresent()) {
            return existing;
        }
        if (!canOwn()) {
            return Optional.empty();
        }
        UUID uuid = UUID.randomUUID();
        ModDataNBTExtra.putUUID(tool.getPersistentData(), KEY, uuid);
        return Optional.of(uuid);
    }

    @Override
    public Optional<UUID> resetUuid() {
        if (!canOwn()) {
            return Optional.empty();
        }
        UUID uuid = UUID.randomUUID();
        ModDataNBTExtra.putUUID(tool.getPersistentData(), KEY, uuid);
        return Optional.of(uuid);
    }

    @Override
    public boolean isUuidSupported() {
        return canOwn();
    }
}
