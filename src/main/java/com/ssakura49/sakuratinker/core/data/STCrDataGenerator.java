package com.ssakura49.sakuratinker.core.data;

import com.ssakura49.sakuratinker.core.data.provider.STCrItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "sakuratinker",
   bus = Bus.MOD
)
public class STCrDataGenerator {
   @SubscribeEvent
   public static void gatherData(GatherDataEvent event) {
      DataGenerator generator = event.getGenerator();
      PackOutput output = generator.getPackOutput();
      ExistingFileHelper helper = event.getExistingFileHelper();
      generator.addProvider(event.includeClient(), new STCrItemModelProvider(output, helper));
   }
}
