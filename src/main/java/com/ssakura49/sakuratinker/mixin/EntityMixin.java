package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.core.library.data.ToolVibrationListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"),method = "updateDynamicGameEventListener")
    public void addVibrationListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> pListenerConsumer, CallbackInfo ci){
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ServerPlayer player&& player.level() instanceof ServerLevel level){
            ToolVibrationListener.validate(player);
            if(ToolVibrationListener.listenerMap.get(player)!=null){
                pListenerConsumer.accept(new DynamicGameEventListener<GameEventListener>(ToolVibrationListener.listenerMap.get(player).getListener()),level);
            }
        }
    }

}
