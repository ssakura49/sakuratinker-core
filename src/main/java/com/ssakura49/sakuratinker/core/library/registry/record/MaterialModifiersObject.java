package com.ssakura49.sakuratinker.core.library.registry.record;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

public record MaterialModifiersObject(List<Pair<@Nullable MaterialStatsId, ModifierEntry[]>> modifierPairs) {
}
