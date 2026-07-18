package com.ssakura49.sakuratinker.core.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.ssakura49.sakuratinker.core.item.base.SimpleDescriptiveCurio;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class FoxCurio extends SimpleDescriptiveCurio {
   public FoxCurio(Item.Properties properties, String slotIdentifier, Multimap<Attribute, AttributeModifier> defaultModifiers) {
      super((new Item.Properties()).stacksTo(1).rarity(Rarity.EPIC), "fox_mask", (() -> {
         ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
         builder.put(Attributes.MAX_HEALTH, new AttributeModifier(UUID.fromString("4c5f72a1-73aa-4ba2-80b0-aa6aada17fc1"), "Curios modifier", (double)20.0F, Operation.ADDITION));
         return builder.build();
      }));
   }
}
