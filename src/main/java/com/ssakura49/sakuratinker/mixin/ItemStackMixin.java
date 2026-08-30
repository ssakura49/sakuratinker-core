package com.ssakura49.sakuratinker.mixin;

import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.mining.HarvestDropModifierHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(at = @At("RETURN"), method = "isCorrectToolForDrops", cancellable = true)
    public void onIsCorrectToolForDrops(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof IModifiable) {
            ToolStack tool = ToolStack.from(stack);
            HarvestDropModifierHook hook = tool.getHook(STHooks.HARVEST_DROP);
            if (hook.isEffectiveForDrop(tool, ModifierEntry.EMPTY, state, false)) {
                cir.setReturnValue(true);
            }
        }
    }
    @Inject(at = @At(value = "RETURN"),method = "hasFoil",cancellable = true)
    public void setFoil(CallbackInfoReturnable<Boolean> cir){
        var stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(stack);
            if (cir.getReturnValueZ()) return;
            for (ModifierEntry entry:tool.getModifierList()){
                if (entry.getHook(STHooks.ITEM_FOIL).hasFoil(tool,entry)){
                    cir.setReturnValue(true);
                    break;
                }
            }
        }
    }
}
