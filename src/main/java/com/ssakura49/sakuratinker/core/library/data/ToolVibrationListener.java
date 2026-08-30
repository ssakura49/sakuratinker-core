package com.ssakura49.sakuratinker.core.library.data;

import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ToolVibrationListener implements GameEventListener.Holder<VibrationSystem.Listener>, VibrationSystem {
    public final Player playerHolder;
    public static Map<Player, ToolVibrationListener> listenerMap = new HashMap<>();
    private Map<UUID, ToolVibrationAcceptor> acceptorMap = new HashMap<>();
    private final VibrationSystem.Data vibrationData;
    private final VibrationSystem.User vibrationUser;
    private final VibrationSystem.Listener vibrationListener;
    @Getter
    @Setter
    private int listenRadius;
    public ToolVibrationListener(Player playerHolder) {
        this.playerHolder = playerHolder;
        this.vibrationData = new Data();
        this.vibrationUser = new VibrationUser();
        this.vibrationListener = new VibrationSystem.Listener(this);
    }
    public void tick() {
        if (playerHolder.isDeadOrDying() || !playerHolder.isAlive()) {
            removeAllAcceptor(playerHolder);
            return;
        }
        if (!playerHolder.level().isClientSide) {
            VibrationSystem.Ticker.tick(this.playerHolder.level(), this.vibrationData, this.vibrationUser);
        }
        this.acceptorMap.forEach(((uuid, acceptor) -> acceptor.tickAcceptor(playerHolder)));
    }
    public void addAcceptor(ToolVibrationAcceptor acceptor) {
        if (!this.acceptorMap.containsKey(acceptor.acceptorUUID)) {
            this.acceptorMap.put(acceptor.acceptorUUID, acceptor);
        } else {
            ToolVibrationAcceptor existingAcceptor = this.acceptorMap.get(acceptor.acceptorUUID);
            if (existingAcceptor != null) {
                this.acceptorMap.put(acceptor.acceptorUUID, existingAcceptor.mergeAcceptor(acceptor));
            } else {
                this.acceptorMap.put(acceptor.acceptorUUID, acceptor);
            }
        }
        this.updateRange();
    }
    public void updateRange() {
        this.listenRadius = 0;
        this.acceptorMap.forEach(((uuid, acceptor) -> listenRadius = Math.max(this.listenRadius, acceptor.listenRange)));
    }
    public static void tickIfPresent(ServerPlayer player) {
        ToolVibrationListener listener = listenerMap.get(player);
        if (listener != null) {
            listener.tick();
        }
    }
    public static void validate(Player player) {
        ArrayList<UUID> listExistingAcceptors = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof IModifiable) {
                ToolStack toolStack = ToolStack.from(stack);
                toolStack.getModifierList().forEach((modifier) -> {
                    UUID uuid = modifier.getHook(STHooks.VIBRATION_LISTENING).getAcceptorUUID(toolStack, modifier, player, player.level(), slot);
                    if (uuid != null) listExistingAcceptors.add(uuid);
                });
            }
        }
        ToolVibrationListener listener = ToolVibrationListener.listenerMap.get(player);
        if (listener == null || listener.acceptorMap.isEmpty()) return;
        ArrayList<UUID> uuids = new ArrayList<>(listener.acceptorMap.keySet());
        for (UUID uuid : uuids) {
            if (!listExistingAcceptors.contains(uuid)) {
                removeAcceptor(player, uuid);
            }
        }
    }
    public static void decreaseLevel(Player player, UUID uuid, int toDecrease) {
        if (listenerMap.get(player) != null) {
            ToolVibrationListener listener = listenerMap.get(player);
            Map<UUID, ToolVibrationAcceptor> acceptors = new HashMap<>(listener.acceptorMap);
            for (UUID acceptorUUID : acceptors.keySet()) {
                if (acceptorUUID.equals(uuid)) {
                    ToolVibrationAcceptor acceptor = acceptors.get(acceptorUUID);
                    acceptor.totalLevel -= toDecrease;
                    if (acceptor.totalLevel <= 0) acceptors.remove(acceptorUUID);
                }
            }
            listener.acceptorMap = acceptors;
            listener.updateRange();
            if (listener.acceptorMap.isEmpty()) ToolVibrationListener.listenerMap.remove(listener.playerHolder);
        }
    }
    public static void removeAcceptor(Player player, UUID uuid) {
        if (listenerMap.get(player) != null) {
            ToolVibrationListener listener = listenerMap.get(player);
            listener.acceptorMap.remove(uuid);
            listener.updateRange();
            if (listener.acceptorMap.isEmpty()) ToolVibrationListener.listenerMap.remove(listener.playerHolder);
        }
    }
    public static void removeAllAcceptor(Player player) {
        listenerMap.remove(player);
    }
    @Override
    public @NotNull Listener getListener() {
        return this.vibrationListener;
    }
    @Override
    public @NotNull Data getVibrationData() {
        return vibrationData;
    }
    @Override
    public @NotNull User getVibrationUser() {
        return vibrationUser;
    }
    private void onVibrationReceived(ServerLevel pLevel, BlockPos pPos, GameEvent pGameEvent, Entity pEntity, Entity projectileOwner, float pDistance, int gameEventFrequency) {
        ArrayList<UUID> uuids = new ArrayList<>(this.acceptorMap.keySet());
        for (UUID uuid : uuids) {
            ToolVibrationAcceptor acceptor = this.acceptorMap.get(uuid);
            if (acceptor != null) {
                acceptor.onVibrationReceived(pLevel, playerHolder, pPos, pGameEvent, pEntity, projectileOwner, pDistance, gameEventFrequency);
            }
        }
    }
    private boolean canReceiveVibration(ServerLevel pLevel, BlockPos pPos, GameEvent pGameEvent, GameEvent.Context pContext) {
        BlockPos userPos = vibrationUser.getPositionSource().getPosition(pLevel).map(BlockPos::containing).orElse(pPos);
        for (ToolVibrationAcceptor acceptor : this.acceptorMap.values()) {
            if (acceptor.canReceiveVibration(pLevel, playerHolder, userPos, pPos, pGameEvent, pContext)) {
                return true;
            }
        }
        return false;
    }
    class VibrationUser implements VibrationSystem.User {
        @Override
        public int getListenerRadius() {
            return ToolVibrationListener.this.listenRadius;
        }
        @Override
        public PositionSource getPositionSource() {
            return new EntityPositionSource(ToolVibrationListener.this.playerHolder, ToolVibrationListener.this.playerHolder.getEyeHeight());
        }
        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.VIBRATIONS;
        }
        @Override
        public boolean canReceiveVibration(ServerLevel pLevel, BlockPos pPos, GameEvent pGameEvent, GameEvent.Context pContext) {
            return ToolVibrationListener.this.canReceiveVibration(pLevel, pPos, pGameEvent, pContext);
        }
        @Override
        public void onReceiveVibration(ServerLevel pLevel, BlockPos pPos, GameEvent pGameEvent, @Nullable Entity pEntity, @Nullable Entity projectileOwner, float pDistance) {
            ToolVibrationListener.this.onVibrationReceived(pLevel, pPos, pGameEvent, pEntity, projectileOwner, pDistance, VibrationSystem.getGameEventFrequency(pGameEvent));
        }
    }
}
