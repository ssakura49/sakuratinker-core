package com.ssakura49.sakuratinker.core.library.registry.record;

import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;

public record MaterialStatsObject(IMaterialStats[] stats, PlatingMaterialStats.@Nullable Builder armorBuilder, boolean allowShield) {
}
