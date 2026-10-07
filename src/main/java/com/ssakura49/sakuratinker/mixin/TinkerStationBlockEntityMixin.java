package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.core.library.tools.event.ToolStackBuildEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;


@Mixin(value = TinkerStationBlockEntity.class, remap = false)
public class TinkerStationBlockEntityMixin {
    @Inject(method = "calcResult", at = @At("RETURN"))
    private void onToolBuilt(@Nullable Player player, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (!result.isEmpty()) {
            MinecraftForge.EVENT_BUS.post(new ToolStackBuildEvent(result));
        }
    }
}
