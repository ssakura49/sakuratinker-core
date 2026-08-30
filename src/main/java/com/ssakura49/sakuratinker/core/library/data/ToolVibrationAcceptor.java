package com.ssakura49.sakuratinker.core.library.data;

import com.ssakura49.sakuratinker.core.library.context.VibrationContext;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior.VibrationListeningModifierHook;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

public class ToolVibrationAcceptor {
    public final UUID acceptorUUID;
    @Getter
    protected int totalLevel;
    public int listenRange;

    public final EquipmentSlot sourceSlot;

    @Nullable
    public String curiosIdentifier;
    public int curiosIndex;

    public int cooldownDuration;
    public int remainingCooldown;


    public ToolVibrationAcceptor(int listenRadius, UUID acceptorUUID, int level, EquipmentSlot sourceSlot, int cooldownDuration) {
        this.acceptorUUID = acceptorUUID;
        this.totalLevel = level;
        this.listenRange = listenRadius;
        this.sourceSlot = sourceSlot;
        this.cooldownDuration = cooldownDuration;
        this.remainingCooldown = 0;
    }
    public ToolVibrationAcceptor(int listenRadius, UUID acceptorUUID, int level, String curiosId, int curiosIndex, int cooldownDuration) {
        this.acceptorUUID = acceptorUUID;
        this.totalLevel = level;
        this.listenRange = listenRadius;
        this.sourceSlot = null;
        this.curiosIdentifier = curiosId;
        this.curiosIndex = curiosIndex;
        this.cooldownDuration = cooldownDuration;
        this.remainingCooldown = 0;
    }

    public ToolVibrationAcceptor mergeAcceptor(ToolVibrationAcceptor acceptor) {
        this.totalLevel += acceptor.totalLevel;
        this.listenRange = Math.max(acceptor.listenRange, this.listenRange);
        this.cooldownDuration = Math.max(acceptor.cooldownDuration, this.cooldownDuration);
        return this;
    }
    private ItemStack getStack(Player player) {
        if (this.sourceSlot != null) {
            return player.getItemBySlot(this.sourceSlot);
        } else if (this.curiosIdentifier != null) {
            return CuriosApi.getCuriosInventory(player).map(inv ->
                    inv.getStacksHandler(this.curiosIdentifier)
                            .map(handler -> handler.getStacks().getStackInSlot(this.curiosIndex))
                            .orElse(ItemStack.EMPTY)
            ).orElse(ItemStack.EMPTY);
        }
        return ItemStack.EMPTY;
    }

    private VibrationListeningModifierHook getHook(ModifierEntry entry) {
        return entry.getHook(STHooks.VIBRATION_LISTENING);
    }
    public void tickAcceptor(Player player) {
        if (this.remainingCooldown > 0) {
            this.remainingCooldown--;
        }
        if (player.level() instanceof ServerLevel serverLevel) {
            ItemStack stack = this.getStack(player);
            if (stack.getItem() instanceof IModifiable) {
                ToolStack toolStack = ToolStack.from(stack);
                toolStack.getModifierList().forEach((modifier) -> {
                    VibrationListeningModifierHook hook = this.getHook(modifier);
                    UUID currentUuid;
                    if (this.sourceSlot != null) {
                        currentUuid = hook.getAcceptorUUID(toolStack, modifier, player, serverLevel, this.sourceSlot);
                    } else {
                        currentUuid = hook.getAcceptorUUIDCurios(toolStack, modifier, player, serverLevel, this.curiosIdentifier, this.curiosIndex);
                    }
                    if (this.acceptorUUID.equals(currentUuid)) {
                        EquipmentSlot slot = this.sourceSlot != null ? this.sourceSlot : EquipmentSlot.MAINHAND;
                        hook.onAcceptorTick(toolStack, modifier, player, serverLevel, slot, this.totalLevel);
                    }
                });
            }
        }
    }
    public void onVibrationReceived(ServerLevel pLevel, Player player, BlockPos pPos, GameEvent pGameEvent, @Nullable Entity directEntity, @Nullable Entity projectileOwner, float pDistance, int frequency) {
        VibrationListeningModifierHook.handleVibration(player, this, pLevel, pPos, pGameEvent, directEntity, projectileOwner, pDistance, frequency);
    }
    public boolean canReceiveVibration(ServerLevel pLevel, Player player, BlockPos userPos, BlockPos pPos, GameEvent pGameEvent, GameEvent.Context pContext) {
        if (this.remainingCooldown > 0) {
            return false;
        }
        ItemStack stack = this.getStack(player);
        if (stack.getItem() instanceof IModifiable) {
            ToolStack toolStack = ToolStack.from(stack);
            for (ModifierEntry modifier : toolStack.getModifierList()) {
                VibrationListeningModifierHook hook = this.getHook(modifier);

                UUID uuid;
                if (this.sourceSlot != null) {
                    uuid = hook.getAcceptorUUID(toolStack, modifier, player, pLevel, this.sourceSlot);
                } else {
                    uuid = hook.getAcceptorUUIDCurios(toolStack, modifier, player, pLevel, this.curiosIdentifier, this.curiosIndex);
                }
                if (this.acceptorUUID.equals(uuid)) {
                    VibrationContext context = new VibrationContext(pLevel, pPos, pGameEvent, new VibrationInfo(pGameEvent, VibrationSystem.Listener.distanceBetweenInBlocks(pPos, userPos), new Vec3(pPos.getX(), pPos.getY(), pPos.getZ()), pContext.sourceEntity()), this);

                    EquipmentSlot slotParam = this.sourceSlot != null ? this.sourceSlot : EquipmentSlot.MAINHAND;

                    if (!hook.isListeningActive(toolStack, modifier, player, pLevel, slotParam)) {
                        return false;
                    }

                    if (hook.getResonantFrequency(toolStack, modifier, player, pLevel, slotParam).isPresent()) {
                        int targetFreq = hook.getResonantFrequency(toolStack, modifier, player, pLevel, slotParam).get();
                        if (targetFreq != context.frequency) return false;
                    }

                    if (hook.requiresLineOfSight(toolStack, modifier, player, pLevel, slotParam)) {
                        Vec3 start = new Vec3(pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5);
                        Vec3 end = player.getEyePosition(1.0f);
                        if (pLevel.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)).getType() != HitResult.Type.MISS) {
                            return false;
                        }
                    }
                    if (!hook.canAffordVibration(toolStack, modifier, player, pLevel, slotParam, context)) {
                        return false;
                    }
                    if (!hook.canReceiveVibration(toolStack, modifier, player, pLevel, slotParam, context)) {
                        return false;
                    }
                    hook.consumeVibrationCost(toolStack, modifier, player, pLevel, slotParam, context, 1);
                    this.remainingCooldown = this.cooldownDuration;
                    hook.interceptVibration(toolStack, modifier, player, pLevel, slotParam, context);
                    return true;
                }
            }
        }
        return false;
    }
}
