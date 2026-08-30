package com.ssakura49.sakuratinker.mixin.accessor;

import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Projectile.class)
public interface ProjectileAccessor {
    @Accessor("hasBeenShot")
    boolean getHasBeenShot();

    @Accessor("leftOwner")
    boolean getLeftOwner();
}