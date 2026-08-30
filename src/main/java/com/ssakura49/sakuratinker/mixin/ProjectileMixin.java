package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity implements TraceableEntity {
    @Shadow private boolean hasBeenShot;
    @Shadow
    private boolean leftOwner;

    public ProjectileMixin(EntityType<?> type, Level level) {
        super(type, level);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (isRemoved()) return;
        Projectile projectile = (Projectile) (Object) this;
        if (projectile instanceof AbstractArrow) return;
        EntityModifierCapability.EntityModifiers cap = projectile.getCapability(EntityModifierCapability.CAPABILITY).orElse(null);
        if (cap != null && !cap.getModifiers().isEmpty()) {
            ModifierNBT nbt = cap.getModifiers();
            ModDataNBT projectileData = PersistentDataCapability.getOrWarn(projectile);

            for (ModifierEntry entry : nbt.getModifiers()) {
                entry.getHook(STHooks.PROJECTILE_TICK)
                        .onProjectileTick(nbt, entry, projectile.level(), projectile, projectileData, hasBeenShot, leftOwner);
            }
        }
    }
}
