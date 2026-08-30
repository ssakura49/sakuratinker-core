package com.ssakura49.sakuratinker.core.utils;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class SlotType {
    public static final SlotType NONE = new SlotType();
    public static final SlotType MAIN_HAND = new SlotType(EquipmentSlot.MAINHAND);
    public static final SlotType OFF_HAND = new SlotType(EquipmentSlot.OFFHAND);
    public static final SlotType ANY_HAND = new SlotType(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    public static final SlotType ARMOR = new SlotType(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
    public static final SlotType CHEST = new SlotType(EquipmentSlot.CHEST);
    public static final SlotType LEGS = new SlotType(EquipmentSlot.LEGS);
    public static final SlotType FEET = new SlotType(EquipmentSlot.FEET);
    public static final SlotType HEAD = new SlotType(EquipmentSlot.HEAD);
    public static final SlotType ANY = new SlotType(EquipmentSlot.values());

    private final Set<EquipmentSlot> validSlots;

    private SlotType() {
        this.validSlots = EnumSet.noneOf(EquipmentSlot.class);
    }

    public SlotType(EquipmentSlot... slots) {
        if (slots == null || slots.length == 0) {
            this.validSlots = EnumSet.noneOf(EquipmentSlot.class);
        } else {
            this.validSlots = EnumSet.copyOf(Arrays.asList(slots));
        }
    }

    public boolean matches(EquipmentSlot slot) {
        if (slot == null) return false;
        return this.validSlots.contains(slot);
    }

    public static SlotType of(EquipmentSlot... slots) {
        return new SlotType(slots);
    }
}