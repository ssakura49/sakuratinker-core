package com.ssakura49.sakuratinker.core.init;

import com.ssakura49.sakura_library.util.annotation.LoadClass;
import com.ssakura49.sakuratinker.core.STCrModule;
import com.ssakura49.sakuratinker.core.tools.modifiers.tool.SealTooltipModifier;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

@LoadClass
public class STCrModifier {
   public static StaticModifier<SealTooltipModifier> SEAL_TOOLTIP = STCrModule.MODIFIERS.register("seal_tooltip", SealTooltipModifier::new);

}
