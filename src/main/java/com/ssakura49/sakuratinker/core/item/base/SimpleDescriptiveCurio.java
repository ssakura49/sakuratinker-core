package com.ssakura49.sakuratinker.core.item.base;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.ssakura49.sakura_library.api.client.cmc.LoreHelper;
import com.ssakura49.sakura_library.util.SafeIterationUtils;
import com.ssakura49.sakura_library.util.time.TimeContext.Client;
import com.ssakura49.sakuratinker.core.client.component.CuriosMutableComponent;
import com.ssakura49.sakuratinker.core.client.component.LoreStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;

public class SimpleDescriptiveCurio extends CuriosBaseItem {
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;
   final @NotNull String slotIdentifier;
   public List<Component> tooltips;
   Style descriptionStyle;
   boolean showHeader;
   List<CuriosMutableComponent> defaultDesc;
   List<CuriosMutableComponent> head;
   List<CuriosMutableComponent> tail;
   List<Component> toArg;
   private List<CuriosMutableComponent> tempAttributeList;

   public SimpleDescriptiveCurio(Item.Properties properties, String slotIdentifier, Multimap<Attribute, AttributeModifier> defaultModifiers) {
      super(properties);
      this.tooltips = new ArrayList<>();
      this.defaultDesc = new ArrayList<>();
      this.head = new ArrayList<>();
      this.tail = new ArrayList<>();
      this.toArg = new ArrayList<>();
      this.tempAttributeList = new ArrayList<>();
      this.slotIdentifier = Objects.requireNonNull(slotIdentifier);
      this.showHeader = true;
      this.descriptionStyle = Style.EMPTY.withColor(ChatFormatting.YELLOW);
      this.defaultModifiers = defaultModifiers;
   }

   public SimpleDescriptiveCurio(Item.Properties properties, String slotIdentifier, Supplier<Multimap<Attribute, AttributeModifier>> defaultModifiers) {
      this(properties, slotIdentifier, defaultModifiers.get());
   }

   public SimpleDescriptiveCurio(Item.Properties properties, String slotIdentifier) {
      this(properties, slotIdentifier, ImmutableMultimap::of);
   }

   public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
      MutableComponent title = Component.translatable("curios.modifiers." + this.slotIdentifier).withStyle(ChatFormatting.GOLD);
      List<? extends Component> headList = this.getHeadDescriptionLines(stack);
      if (!headList.isEmpty()) {
         tooltips.add(Component.empty());
         tooltips.addAll(headList);
      }

      if (this.showHeader) {
         tooltips.add(Component.empty());
         tooltips.add(title);
      }

      List<? extends Component> descriptionLines = this.getDescriptionLines(stack);
      if (!descriptionLines.isEmpty()) {
         tooltips.addAll(descriptionLines);
      }

      return super.getSlotsTooltip(tooltips, stack);
   }

   public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, level, components, tooltipFlag);
   }

   static ChatFormatting textColorToCF(TextColor color) {
      return ChatFormatting.getByName(color.serialize());
   }

   public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
      try {
         if (this.tempAttributeList == null || Client.count % 5L == 0L) {
            this.tempAttributeList = SafeIterationUtils.safelyForEach(tooltips, (component) -> {
               if (!component.equals(Component.empty()) && component instanceof MutableComponent mutableComponent) {
                  ComponentContents patt4512$temp = mutableComponent.getContents();
                  if (patt4512$temp instanceof TranslatableContents contents) {
                     if (!I18n.exists(contents.getKey())) {
                        return null;
                     }

                     if (contents.getArgs().length != 2) {
                        return null;
                     }

                     String key = contents.getKey();
                     Object patt4842$temp = contents.getArgs()[1];
                     if (patt4842$temp instanceof Component cArg) {
                        CuriosMutableComponent.FormatDescFunction descFunction = (stack1) -> {
                           Object[] var10000;
                           String var10003;
                           label19: {
                              var10000 = new Object[]{LoreHelper.codeMode(ChatFormatting.DARK_AQUA), Float.valueOf(contents.getArgs()[0].toString()), null, null};
                              if (cArg instanceof MutableComponent mcArg) {
                                 if (mcArg.getStyle().getColor() != null && !mcArg.getStyle().getColor().serialize().equals(ChatFormatting.WHITE.getSerializedName())) {
                                    var10003 = LoreHelper.codeMode(textColorToCF(mcArg.getStyle().getColor()));
                                    break label19;
                                 }
                              }

                              var10003 = LoreHelper.codeMode(ChatFormatting.GOLD);
                           }

                           var10000[2] = var10003;
                           ComponentContents patt5567$temp = cArg.getContents();
                           if (patt5567$temp instanceof TranslatableContents tC) {
                              var10003 = tC.getKey();
                           } else {
                              var10003 = "";
                           }

                           var10000[3] = I18n.get(var10003, new Object[0]);
                           return var10000;
                        };
                        if (key.endsWith(".plus.1") || key.endsWith(".plus.2")) {
                           return CuriosMutableComponent.create(LoreStyle.ATTRIBUTE_PREFIX).appendAttributeFormat(1, descFunction);
                        }

                        if (key.endsWith(".plus.0")) {
                           return CuriosMutableComponent.create(LoreStyle.ATTRIBUTE_PREFIX).appendFormat("%s+%s %s%s", descFunction);
                        }

                        if (key.endsWith(".take.1") || key.endsWith(".take.2")) {
                           return CuriosMutableComponent.create(LoreStyle.ATTRIBUTE_PREFIX).appendNegAttributeFormat(1, descFunction);
                        }

                        if (key.endsWith(".take.0")) {
                           return CuriosMutableComponent.create(LoreStyle.ATTRIBUTE_PREFIX).appendFormat("%s-%s %s%s", descFunction);
                        }
                     }
                  }
               }

               return null;
            });
            if (this.toArg == null) {
               this.toArg = new ArrayList();
            } else {
               this.toArg.clear();
            }

            this.toArg.addAll(this.tempAttributeList.stream().map((cmc) -> cmc.build(stack)).toList());
            List<MutableComponent> tailList = this.getTailDescriptionLines(stack);
            if (!tailList.isEmpty()) {
               this.toArg.add(Component.empty());
               this.toArg.addAll(tailList);
            }
         }

         return super.getAttributesTooltip(this.toArg, stack);
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return tooltips;
      }
   }

   public List<? extends Component> getDescriptionLines(ItemStack stack) {
      return this.enableSimpleDesc() ? List.of(this.getDescription(stack)) : this.defaultDesc.stream().map((cmc) -> cmc.build(stack)).toList();
   }

   public List<MutableComponent> getHeadDescriptionLines(ItemStack stack) {
      return this.head.isEmpty() ? List.of() : this.head.stream().map((cmc) -> cmc.build(stack)).toList();
   }

   public List<MutableComponent> getTailDescriptionLines(ItemStack stack) {
      return this.tail.isEmpty() ? List.of() : this.tail.stream().map((cmc) -> cmc.build(stack)).toList();
   }

   public Component getDescription(ItemStack stack) {
      return Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc")).withStyle(this.descriptionStyle);
   }

   public boolean enableSimpleDesc() {
      return true;
   }

   protected Item withHead(List<CuriosMutableComponent> head) {
      this.head = head;
      return this;
   }

   protected Item withTail(List<CuriosMutableComponent> tail) {
      this.tail = tail;
      return this;
   }

   protected Item defaultDesc(List<CuriosMutableComponent> defaultDesc) {
      this.defaultDesc = defaultDesc;
      return this;
   }

   protected SimpleDescriptiveCurio withHead(CuriosMutableComponent... head) {
      this.head = List.of(head);
      return this;
   }

   protected SimpleDescriptiveCurio withTail(CuriosMutableComponent... tail) {
      this.tail = List.of(tail);
      return this;
   }

   protected SimpleDescriptiveCurio defaultDesc(CuriosMutableComponent... defaultDesc) {
      this.defaultDesc = List.of(defaultDesc);
      return this;
   }
}
