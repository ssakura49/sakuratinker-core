package com.ssakura49.sakuratinker.core.library.context;

import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;

import java.util.function.Predicate;

public interface TraitContext {
    IToolContext getToolContext();
    MaterialNBT getMaterials();
    boolean hasMaterial(MaterialId materialId);
    boolean hasMaterialFromMod(String modId);
    boolean hasMaterialMatching(Predicate<MaterialId> predicate);
    int getMaterialCount();
}
