package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.build;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.library.context.TraitContext;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.ModifierBuilder;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public interface AdvancedModifierTraitHook {
    /**
     * @param context 上下文
     * @param modifier 当前修饰符
     * @param builder 词条构建器
     * @param firstEncounter 是否是该修饰符的第一次遍历
     */
    void addTraits(TraitContext context, ModifierEntry modifier, TraitBuilder builder, boolean firstEncounter);

    @RequiredArgsConstructor
    class TraitBuilder implements ModifierBuilder {
        private final Set<Modifier> seenModifiers = new HashSet<>();
        private final Set<Modifier> currentStack = new LinkedHashSet<>();
        private final TraitContext context;
        private final ModifierNBT.Builder builder;
        @Override
        public AdvancedModifierTraitHook.@NotNull TraitBuilder add(ModifierEntry entry) {
            if (entry.isBound()) {
                builder.add(entry);
                addTraits(entry);
            }
            return this;
        }
        private void addTraits(ModifierEntry entry) {
            Modifier modifier = entry.getModifier();
            AdvancedModifierTraitHook hook = modifier.getHooks().getOrNull(STHooks.MODIFIER_TRAIT);
            if (hook != null) {
                if (currentStack.contains(modifier)) {
                    SakuraTinker.LOGGER.error("Encountered {} as a child of itself, previous stack {}", modifier.getId(), currentStack);
                } else {
                    currentStack.add(modifier);
                    hook.addTraits(context, entry, this, !hasSeenModifier(modifier));
                    seenModifiers.add(modifier);
                    currentStack.remove(modifier);
                }
            }
        }

        public boolean hasSeenModifier(Modifier modifier) {
            return seenModifiers.contains(modifier);
        }

        @Override
        public @NotNull ModifierNBT build() {
            return builder.build();
        }
    }

    record AllMerger(Collection<AdvancedModifierTraitHook> modules) implements AdvancedModifierTraitHook {
        @Override
        public void addTraits(TraitContext context, ModifierEntry modifier, TraitBuilder builder, boolean firstEncounter) {
            for (AdvancedModifierTraitHook module : modules) {
                module.addTraits(context, modifier, builder, firstEncounter);
            }
        }
    }
}
