package com.ssakura49.sakuratinker.core.item.base;

import com.ssakura49.sakura_library.api.client.cmc.LoreHelper;
import com.ssakura49.sakura_library.util.SafeIterationUtils;
import com.ssakura49.sakura_library.util.time.TimeContext.Client;
import com.ssakura49.sakuratinker.core.client.component.ItemsMutableComponent;
import com.ssakura49.sakuratinker.core.client.component.LoreStyle;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SimpleDescriptiveItem extends Item {
   public List<ItemsMutableComponent> defaultDesc = new ArrayList<>();
   public List<ItemsMutableComponent> head = new ArrayList<>();
   public List<ItemsMutableComponent> tail = new ArrayList<>();
   public List<Component> toArg = new ArrayList<>();
   private List<ItemsMutableComponent> tempAttributeList = new ArrayList<>();
   public boolean showHeader = false;
   public Style descriptionStyle;

   public SimpleDescriptiveItem(Item.Properties properties) {
      super(properties);
      this.descriptionStyle = Style.EMPTY.withColor(ChatFormatting.GRAY);
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
      if (!this.head.isEmpty()) {
         tooltip.add(Component.empty());
         tooltip.addAll(this.head.stream().map(h -> h.build(stack)).toList());
      }

      if (this.showHeader) {
         tooltip.add(Component.literal(I18n.get(this.getDescriptionId() + ".header")).withStyle(ChatFormatting.GOLD));
      }

      if (this.enableSimpleDesc()) {
         tooltip.add(this.getDescription(stack));
      } else {
         tooltip.addAll(this.defaultDesc.stream().map(c -> c.build(stack)).toList());
      }

      tooltip.addAll(this.getTailDescriptionLines(stack));
   }

   public List<Component> getAttributesTooltip(ItemStack stack, List<Component> vanillaAttributes) {
      if (this.tempAttributeList == null || Client.count % 5L == 0L) {
         this.tempAttributeList = SafeIterationUtils.safelyForEach(vanillaAttributes, (component) -> {

            if (!component.equals(Component.empty()) && component instanceof MutableComponent mutable) {
               ComponentContents contents = mutable.getContents();

               if (contents instanceof TranslatableContents translatableContents) {
                  if (!I18n.exists(translatableContents.getKey())) {
                     return null;
                  }

                  if (translatableContents.getArgs().length != 2) {
                     return null;
                  }

                  String key = translatableContents.getKey();
                  Object secondArg = translatableContents.getArgs()[1];

                  if (secondArg instanceof Component attributeNameComponent) {
                     ItemsMutableComponent.FormatDescFunction func = (stack1) -> {
                        Object[] formatArgs = new Object[4];
                        formatArgs[0] = LoreHelper.codeMode(ChatFormatting.AQUA);
                        formatArgs[1] = Float.parseFloat(translatableContents.getArgs()[0].toString());
                        formatArgs[2] = LoreHelper.codeMode(ChatFormatting.GOLD);
                        ComponentContents nameContents = attributeNameComponent.getContents();
                        String nameKey = nameContents instanceof TranslatableContents tC ? tC.getKey() : "";
                        formatArgs[3] = I18n.get(nameKey);
                        return formatArgs;
                     };
                     if (key.endsWith(".plus.1") || key.endsWith(".plus.2")) {
                        return ItemsMutableComponent.create(LoreStyle.ATTRIBUTE_PREFIX).appendAttributeFormat(1, func);
                     }

                     if (key.endsWith(".take.1") || key.endsWith(".take.2")) {
                        return ItemsMutableComponent.create(LoreStyle.ATTRIBUTE_PREFIX).appendNegAttributeFormat(1, func);
                     }
                  }
               }
            }
            return null;
         });
         this.toArg.clear();
         this.toArg.addAll(this.tempAttributeList.stream().map(a -> a.build(stack)).toList());
      }

      return this.toArg;
   }

   public List<? extends Component> getTailDescriptionLines(ItemStack stack) {
      return this.tail.isEmpty() ? List.of() : this.tail.stream().map(c -> c.build(stack)).toList();
   }

   public Component getDescription(ItemStack stack) {
      return Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc")).withStyle(this.descriptionStyle);
   }

   public boolean enableSimpleDesc() {
      return true;
   }

   public SimpleDescriptiveItem withHead(ItemsMutableComponent... head) {
      this.head = List.of(head);
      return this;
   }

   public SimpleDescriptiveItem withTail(ItemsMutableComponent... tail) {
      this.tail = List.of(tail);
      return this;
   }

   public SimpleDescriptiveItem defaultDesc(ItemsMutableComponent... defaultDesc) {
      this.defaultDesc = List.of(defaultDesc);
      return this;
   }
}
