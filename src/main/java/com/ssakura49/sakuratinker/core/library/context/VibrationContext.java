package com.ssakura49.sakuratinker.core.library.context;

import com.ssakura49.sakuratinker.core.library.data.ToolVibrationAcceptor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.Nullable;

public class VibrationContext {
    public final ServerLevel serverLevel;
    public final BlockPos blockPos;
    public final GameEvent gameEvent;
    public final Entity directEntity;
    public final Entity projectileOwner;
    public final float distance;
    public final int frequency;
    public final ToolVibrationAcceptor acceptor;

    public VibrationContext(ServerLevel serverLevel, BlockPos pos, GameEvent gameEvent, @Nullable Entity directEntity, @Nullable Entity projectileOwner, float distance, int frequency, ToolVibrationAcceptor acceptor) {
        this.serverLevel = serverLevel;
        this.blockPos = pos;
        this.gameEvent = gameEvent;
        this.directEntity = directEntity;
        this.projectileOwner = projectileOwner;
        this.distance = distance;
        this.frequency = frequency;
        this.acceptor = acceptor;
    }
    public VibrationContext(ServerLevel pLevel, BlockPos pPos, GameEvent pGameEvent, VibrationInfo info, ToolVibrationAcceptor acceptor) {
        this(pLevel, pPos, pGameEvent, info.getEntity(pLevel).orElse(null), info.getProjectileOwner(pLevel).orElse(null), info.distance(), VibrationSystem.getGameEventFrequency(pGameEvent), acceptor);
    }
}
