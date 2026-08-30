package com.ssakura49.sakuratinker.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.ssakura49.sakura_library.entity.damagesource.LegacyDamageSource;
import com.ssakura49.sakuratinker.STCrConfig;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import com.ssakura49.sakuratinker.mixin.accessor.ProjectileAccessor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Set;
import java.util.function.Supplier;

@Mixin(value = AbstractArrow.class,priority = 800)
public abstract class AbstractArrowMixin{
    @Unique
    private final Supplier<Boolean> ALLOW_PROJECTILE_TICK_INGROUND = STCrConfig.COMMON.ALLOW_PROJECTILE_TICK_INGROUND;
    @Shadow
    @Nullable
    private IntOpenHashSet piercingIgnoreEntityIds;
    @Shadow protected boolean inGround;

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!inGround || ALLOW_PROJECTILE_TICK_INGROUND.get()) {
            AbstractArrow arrow = (AbstractArrow) (Object) this;
            EntityModifierCapability.EntityModifiers cap = arrow.getCapability(EntityModifierCapability.CAPABILITY).orElse(null);

            if (cap != null && !cap.getModifiers().isEmpty()) {
                ModifierNBT nbt = cap.getModifiers();
                ModDataNBT projectileData = PersistentDataCapability.getOrWarn(arrow);
                ProjectileAccessor accessor = (ProjectileAccessor) arrow;
                boolean hasBeenShot = accessor.getHasBeenShot();
                boolean leftOwner = accessor.getLeftOwner();

                for (ModifierEntry entry : nbt.getModifiers()) {
                    entry.getHook(STHooks.PROJECTILE_TICK)
                            .onArrowTick(nbt, entry, arrow.level(), arrow, projectileData, hasBeenShot, leftOwner, inGround, piercingIgnoreEntityIds);
                }
            }
        }
    }

    @ModifyArg(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 0
    )
    private DamageSource modifyDamageSource(DamageSource source0, @Local(argsOnly = true) EntityHitResult hitResult) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Entity target = hitResult.getEntity();
        ModifierNBT modifiers = EntityModifierCapability.getOrEmpty(arrow);
        if (!modifiers.isEmpty() && target != null) {
            ModDataNBT nbt = PersistentDataCapability.getOrWarn(arrow);
            LivingEntity attacker = arrow.getOwner() instanceof LivingEntity living ? living : null;
            LegacyDamageSource damageSource = new LegacyDamageSource(source0);
            for (ModifierEntry entry : modifiers.getModifiers()) {
                damageSource = entry.getHook(STHooks.MODIFY_DAMAGE_SOURCE)
                        .modifyArrowDamageSource(modifiers, nbt, entry, arrow, attacker, target, damageSource);
            }
            return damageSource;
        }
        return source0;
    }

    @Inject(
            method = "onHitEntity",
            at = @At("HEAD")
    )
    private void capturePreHitHealth(EntityHitResult hitResult, CallbackInfo ci, @Share("preHealth") LocalRef<Float> ref) {
        if (hitResult.getEntity() instanceof LivingEntity living) {
            ref.set(living.getHealth());
        }
    }

    @Inject(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;)V")
    )
    private void doAfterArrowHit(EntityHitResult pResult, CallbackInfo ci, @Share("preHealth") LocalRef<Float> ref) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        ModifierNBT modifiers = EntityModifierCapability.getOrEmpty(arrow);
        if (!modifiers.isEmpty() && pResult.getEntity() instanceof LivingEntity living) {
            Float preHealthObj = ref.get();
            if (preHealthObj == null) return;
            float damageDealt = preHealthObj - living.getHealth();
            ModDataNBT nbt = PersistentDataCapability.getOrWarn(arrow);
            LivingEntity attacker = arrow.getOwner() instanceof LivingEntity entity ? entity : null;
            for (ModifierEntry entry : modifiers.getModifiers()) {
                entry.getHook(STHooks.ARROW_HIT)
                        .afterArrowHit(nbt, entry, modifiers, arrow, attacker, living, damageDealt);
            }
        }
    }
}
