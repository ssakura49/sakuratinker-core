package com.ssakura49.sakuratinker.core.utils.mixin.extradata;

import com.ssakura49.sakura_library.entity.damagesource.LegacyDamageSource;

public interface ToolAttackContextExtraData {
    void sakuratinker$setCriticalModifier(float criticalModifier);
    void sakuratinker$setDamageSource(LegacyDamageSource source);
}
