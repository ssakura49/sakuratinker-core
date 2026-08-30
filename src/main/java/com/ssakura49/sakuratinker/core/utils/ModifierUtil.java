package com.ssakura49.sakuratinker.core.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.ArrayList;
import java.util.List;

public class ModifierUtil {
    /** Checks if a tool can perform the given action */
    public static boolean canPerformAction(IToolStackView tool, ToolAction action) {
        if (!tool.isBroken()) {
            // can the tool do this action inherently?
            if (tool.getHook(ToolHooks.TOOL_ACTION).canPerformAction(tool, action)) {
                return true;
            }
            for (ModifierEntry entry : tool.getModifierList()) {
                if (entry.getHook(ModifierHooks.TOOL_ACTION).canPerformAction(tool, entry, action)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Makes the tool use the blocking animation if the blocking modifier is installed, falling back to the given animation.
     * Allows your tool to block while charging up.
     */
    public static UseAnim blockWhileCharging(IToolStackView tool, UseAnim fallback) {
        return canPerformAction(tool, ToolActions.SHIELD_BLOCK) ? UseAnim.BLOCK : fallback;
    }

    public static List<HeadMaterialStats> getMeleeStatsFromArmor(ToolStack armor) {
        ItemStack stack = armor.createStack();
        IMaterialRegistry registry = MaterialRegistry.getInstance();
        List<MaterialVariantId> materialIds = MaterialIdNBT.from(stack).getMaterials();
        List<HeadMaterialStats> result = new ArrayList<>();

        for (MaterialVariantId variant : materialIds) {
            MaterialId id = variant.getId();
            registry.getMaterialStats(id, MaterialRegistry.MELEE_HARVEST).ifPresent(stats -> {
                if (stats instanceof HeadMaterialStats headStats) {
                    result.add(headStats);
                }
            });
        }
        return result;
    }

    public static List<ModifierEntry> getToolModifiersFromArmorMaterials(ToolStack armor) {
        ItemStack stack = armor.createStack();
        List<MaterialVariantId> variants = MaterialIdNBT.from(stack).getMaterials();
        IMaterialRegistry registry = MaterialRegistry.getInstance();

        List<ModifierEntry> all = new ArrayList<>();

        for (MaterialVariantId variant : variants) {
            MaterialId matId = variant.getId();
            List<ModifierEntry> traits = registry.getTraits(matId, MaterialRegistry.MELEE_HARVEST);
            all.addAll(traits);
        }
        return all;
    }
}
