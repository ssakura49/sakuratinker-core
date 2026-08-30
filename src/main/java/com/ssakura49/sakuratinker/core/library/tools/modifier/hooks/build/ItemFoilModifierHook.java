package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.build;

import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface ItemFoilModifierHook {
    boolean hasFoil(IToolStackView tool, ModifierEntry entry);
    record FirstMerger(Collection<ItemFoilModifierHook> modules) implements ItemFoilModifierHook {
        @Override
        public boolean hasFoil(IToolStackView tool, ModifierEntry entry) {
            for (ItemFoilModifierHook module:this.modules){
                if (module.hasFoil(tool,entry)){
                    return true;
                }
            }
            return false;
        }
    }
}
