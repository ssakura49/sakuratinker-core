package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior;

import com.ssakura49.sakuratinker.core.library.context.VibrationContext;
import com.ssakura49.sakuratinker.core.library.data.ToolVibrationAcceptor;
import com.ssakura49.sakuratinker.core.library.data.ToolVibrationListener;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import com.ssakura49.tinkercuriolib.hook.armor.CurioEquipmentChangeModifierHook;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface VibrationListeningModifierHook extends EquipmentChangeModifierHook, CurioEquipmentChangeModifierHook {
    static void handleVibration(Player player, ToolVibrationAcceptor acceptor, ServerLevel pLevel, BlockPos pPos, GameEvent pGameEvent, @Nullable Entity directEntity, @Nullable Entity projectileOwner, float pDistance, int frequency) {
        ItemStack stack = player.getItemBySlot(acceptor.sourceSlot);
        if (stack.getItem() instanceof IModifiable) {
            ToolStack toolStack = ToolStack.from(stack);
            VibrationContext context = new VibrationContext(pLevel, pPos, pGameEvent, directEntity, projectileOwner, pDistance, frequency, acceptor);

            toolStack.getModifierList().forEach((modifier) -> {
                VibrationListeningModifierHook hook = modifier.getHook(STHooks.VIBRATION_LISTENING);
                UUID uuid = hook.getAcceptorUUID(toolStack, modifier, player, pLevel, acceptor.sourceSlot);
                if (acceptor.acceptorUUID.equals(uuid)) {
                    hook.onReceivingVibration(toolStack, modifier, player, pLevel, acceptor.sourceSlot, context);
                }
            });
        }
    }

    UUID getAcceptorUUID(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot);
    default UUID getAcceptorUUIDCurios(IToolStackView tool, ModifierEntry modifier, Player player, Level level, String identifier, int index) {
        String key = modifier.getModifier().getId() + ":" + identifier + ":" + index;
        return UUID.nameUUIDFromBytes(key.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
    int listenRange(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot, int range);
    default int listenRangeCurios(IToolStackView tool, ModifierEntry modifier, Player player, Level level, String identifier, int index, int defaultRange) {
        return listenRange(tool, modifier, player, level, EquipmentSlot.MAINHAND, defaultRange);
    }
    boolean canReceiveVibration(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context);
    void onReceivingVibration(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context);
    default void onAcceptorTick(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, int acceptorLevel) {
    }
    default int getCooldownTicks(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot) {
        return 0;
    }

    default boolean isListeningActive(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot) {
        return false;
    }
    default boolean canAffordVibration(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context) {
        return true;
    }
    default void consumeVibrationCost(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context, int cost) {
        tool.setDamage(tool.getDamage()-cost);
    }
    default boolean requiresLineOfSight(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot) {
        return false;
    }
    default Optional<Integer> getResonantFrequency(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot) {
        return Optional.empty();
    }

    default void interceptVibration(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context) {
    }

    @Override
    default void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getEntity() instanceof Player player && context.getLevel() instanceof ServerLevel serverLevel) {
            UUID acceptorUUID = modifier.getHook(STHooks.VIBRATION_LISTENING).getAcceptorUUID(tool, modifier, player, serverLevel, context.getChangedSlot());
            if (acceptorUUID != null) {
                int range = modifier.getHook(STHooks.VIBRATION_LISTENING).listenRange(tool, modifier, player, serverLevel, context.getChangedSlot(), 16);
                int cooldown = modifier.getHook(STHooks.VIBRATION_LISTENING).getCooldownTicks(tool, modifier, player, serverLevel, context.getChangedSlot());

                ToolVibrationListener listener = ToolVibrationListener.listenerMap.get(player);
                boolean noListener = listener == null;
                if (noListener) {
                    listener = new ToolVibrationListener(player);
                }

                ToolVibrationAcceptor acceptor = new ToolVibrationAcceptor(range, acceptorUUID, modifier.getLevel(), context.getChangedSlot(), cooldown);

                listener.addAcceptor(acceptor);
                ToolVibrationListener.listenerMap.put(player, listener);
                if (noListener) player.updateDynamicGameEventListener(DynamicGameEventListener::add);
            }
        }
    }
    @Override
    default void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getEntity() instanceof Player player && context.getLevel() instanceof ServerLevel serverLevel) {
            UUID acceptorUUID = modifier.getHook(STHooks.VIBRATION_LISTENING).getAcceptorUUID(tool, modifier, player, serverLevel, context.getChangedSlot());
            if (acceptorUUID != null) {
                ToolVibrationListener.decreaseLevel(player, acceptorUUID, modifier.getLevel());
            }
        }
    }

    @Override
    default void onCurioEquip(IToolStackView curio, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack prevStack, ItemStack stack) {
        if (entity instanceof Player player && entity.level() instanceof ServerLevel serverLevel) {
            String identifier = context.identifier();
            int index = context.index();
            UUID acceptorUUID = entry.getHook(STHooks.VIBRATION_LISTENING).getAcceptorUUIDCurios(curio, entry, player, serverLevel, identifier, index);
            if (acceptorUUID != null) {
                int range = entry.getHook(STHooks.VIBRATION_LISTENING).listenRangeCurios(curio, entry, player, serverLevel, context.identifier(),context.index(), 16);
                int cooldown = entry.getHook(STHooks.VIBRATION_LISTENING).getCooldownTicks(curio, entry, player, serverLevel, EquipmentSlot.MAINHAND);
                ToolVibrationListener listener = ToolVibrationListener.listenerMap.get(player);
                boolean noListener = listener == null;
                if (noListener) {
                    listener = new ToolVibrationListener(player);
                }
                ToolVibrationAcceptor acceptor = new ToolVibrationAcceptor(range, acceptorUUID, entry.getLevel(), identifier, index, cooldown);
                listener.addAcceptor(acceptor);
                ToolVibrationListener.listenerMap.put(player, listener);
                if (noListener) player.updateDynamicGameEventListener(DynamicGameEventListener::add);
            }
        }
    }
    @Override
    default void onCurioUnequip(IToolStackView curio, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack newStack, ItemStack stack) {
        if (entity instanceof Player player && entity.level() instanceof ServerLevel) {
            String identifier = context.identifier();
            int index = context.index();
            UUID acceptorUUID = entry.getHook(STHooks.VIBRATION_LISTENING).getAcceptorUUIDCurios(curio, entry, player, entity.level(), identifier, index);
            if (acceptorUUID != null) {
                ToolVibrationListener.decreaseLevel(player, acceptorUUID, entry.getLevel());
            }
        }
    }

    record AllMerger(Collection<VibrationListeningModifierHook> modules) implements VibrationListeningModifierHook {
        @Override
        public UUID getAcceptorUUID(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot) {
            for (VibrationListeningModifierHook module : this.modules) {
                UUID uuid = module.getAcceptorUUID(tool, modifier, player, level, slot);
                if (uuid != null) return uuid;
            }
            return null;
        }
        @Override
        public int listenRange(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot, int range) {
            for (VibrationListeningModifierHook module : this.modules) {
                range = module.listenRange(tool, modifier, player, level, slot, range);
            }
            return range;
        }
        @Override
        public boolean canReceiveVibration(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context) {
            for (VibrationListeningModifierHook module : this.modules) {
                if (module.canReceiveVibration(tool, modifier, player, level, slot, context)) return true;
            }
            return false;
        }
        @Override
        public void onReceivingVibration(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, VibrationContext context) {
            for (VibrationListeningModifierHook module : this.modules) {
                if (module.canReceiveVibration(tool, modifier, player, level, slot, context))
                    module.onReceivingVibration(tool, modifier, player, level, slot, context);
            }
        }
        @Override
        public void onAcceptorTick(IToolStackView tool, ModifierEntry modifier, Player player, ServerLevel level, EquipmentSlot slot, int acceptorLevel) {
            for (VibrationListeningModifierHook module : this.modules) {
                module.onAcceptorTick(tool, modifier, player, level, slot, acceptorLevel);
            }
        }

        @Override
        public int getCooldownTicks(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot) {
            int max = 0;
            for (VibrationListeningModifierHook module : this.modules) {
                max = Math.max(max, module.getCooldownTicks(tool, modifier, player, level, slot));
            }
            return max;
        }
        @Override
        public boolean isListeningActive(IToolStackView tool, ModifierEntry modifier, Player player, Level level, EquipmentSlot slot) {
            for (VibrationListeningModifierHook module : this.modules) {
                if (module.isListeningActive(tool, modifier, player, level, slot)) return true;
            }
            return false;
        }
    }
}
