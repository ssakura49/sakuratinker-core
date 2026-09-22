package com.ssakura49.sakuratinker.core.init;

import com.ssakura49.sakura_library.util.annotation.LoadClass;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.STCrModule;
import com.ssakura49.sakuratinker.core.library.events.LoadRegistryClassEvent;
import com.ssakura49.sakuratinker.core.tools.modifiers.tool.SealTooltipModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

@Mod.EventBusSubscriber(modid = SakuraTinker.MODID)
public class STCrModifier {
   @SubscribeEvent
   public static void init(LoadRegistryClassEvent event) {}

   public static StaticModifier<SealTooltipModifier> SEAL_TOOLTIP = STCrModule.MODIFIERS.register("seal_tooltip", SealTooltipModifier::new);

}
