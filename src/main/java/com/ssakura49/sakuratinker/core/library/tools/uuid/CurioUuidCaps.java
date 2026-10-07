package com.ssakura49.sakuratinker.core.library.tools.uuid;

import com.ssakura49.sakuratinker.SakuraTinker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public final class CurioUuidCaps {
    public static final ResourceLocation KEY = SakuraTinker.getResource("curio_uuid");

    private CurioUuidCaps() {
    }

    public static Optional<UUID> getUuid(ItemStack stack) {
        if (stack.getTag() == null) {
            return Optional.empty();
        }
        CompoundTag tag = stack.getTag();
        if (!tag.contains(KEY.toString(), Tag.TAG_INT_ARRAY)) {
            return Optional.empty();
        }
        return Optional.of(tag.getUUID(KEY.toString()));
    }

    public static Optional<UUID> getOrCreateUuid(ItemStack stack) {
        Optional<UUID> existing = getUuid(stack);
        if (existing.isPresent()) {
            return existing;
        }
        if (stack.getMaxStackSize() > 1) {
            return Optional.empty();
        }
        UUID uuid = UUID.randomUUID();
        stack.getOrCreateTag().putUUID(KEY.toString(), uuid);
        return Optional.of(uuid);
    }

    public static Optional<UUID> resetUuid(ItemStack stack) {
        if (stack.getMaxStackSize() > 1) {
            return Optional.empty();
        }
        UUID uuid = UUID.randomUUID();
        stack.getOrCreateTag().putUUID(KEY.toString(), uuid);
        return Optional.of(uuid);
    }

    public static UUID getUuidFromString(String name) {
        return UUID.nameUUIDFromBytes(name.getBytes());
    }
}
