package com.ssakura49.sakuratinker.core.library.context;

import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;

import java.util.function.Predicate;

public class TraitContextWrapper implements TraitContext{
    private final IToolContext context;
    private final MaterialNBT materials;
    public TraitContextWrapper(IToolContext context) {
        this.context = context;
        this.materials = context.getMaterials();
    }
    @Override
    public IToolContext getToolContext() {
        return context;
    }
    @Override
    public MaterialNBT getMaterials() {
        return this.materials;
    }
    @Override
    public boolean hasMaterial(MaterialId materialId) {
        for (int i = 0; i < materials.size(); i++) {
            if (materialId.equals(materials.get(i).getId())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean hasMaterialFromMod(String modId) {
        return hasMaterialMatching(id -> id.getNamespace().equals(modId));
    }
    @Override
    public boolean hasMaterialMatching(Predicate<MaterialId> predicate) {
        for (int i = 0; i < materials.size(); i++) {
            if (predicate.test(materials.get(i).getId())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int getMaterialCount() {
        return materials.size();
    }
}
