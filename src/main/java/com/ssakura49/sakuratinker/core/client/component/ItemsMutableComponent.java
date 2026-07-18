package com.ssakura49.sakuratinker.core.client.component;

import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ItemsMutableComponent {
   public static final ItemsMutableComponent EMPTY = create();
   final MutableComponent base;
   public LoreStyle style;
   List<Object> parts;

   ItemsMutableComponent(MutableComponent base) {
      this.style = LoreStyle.NONE;
      this.parts = new ArrayList<>();
      this.base = base;
   }

   public static ItemsMutableComponent create() {
      return (new ItemsMutableComponent(Component.literal(""))).loreStyle(LoreStyle.NONE);
   }

   public static ItemsMutableComponent create(LoreStyle style) {
      return (new ItemsMutableComponent(Component.literal(""))).loreStyle(style);
   }

   public static ItemsMutableComponent create(MutableComponent base, LoreStyle style) {
      return (new ItemsMutableComponent(base)).loreStyle(style);
   }

   private ItemsMutableComponent loreStyle(LoreStyle style) {
      this.style = style;
      return this;
   }

   public ItemsMutableComponent appendFormat(String format, FormatDescFunction args, boolean onlyClient) {
      this.parts.add(ItemsMutableComponent.FormatWrapper.wrap(ItemsMutableComponent.FormatPair.of(format, args)).onlyClient(onlyClient));
      return this;
   }

   public ItemsMutableComponent appendAttributeFormat(int p, FormatDescFunction args, boolean onlyClient) {
      this.parts.add(ItemsMutableComponent.FormatWrapper.wrap(ItemsMutableComponent.FormatPair.of("%s+%." + p + "f%% %s%s", args)).onlyClient(onlyClient));
      return this;
   }

   public ItemsMutableComponent appendTranslation(String key, FormatDescFunction args, boolean onlyClient) {
      this.parts.add(ItemsMutableComponent.FormatWrapper.wrap(ItemsMutableComponent.TranslationFormatPair.of(key, args)).onlyClient(onlyClient));
      return this;
   }

   public ItemsMutableComponent appendComponent(Component component) {
      this.parts.add(component);
      return this;
   }

   public ItemsMutableComponent appendFormat(String format, FormatDescFunction args) {
      return this.appendFormat(format, args, false);
   }

   public ItemsMutableComponent appendAttributeFormat(int p, FormatDescFunction args) {
      return this.appendAttributeFormat(p, args, false);
   }

   public ItemsMutableComponent appendTranslation(String key, FormatDescFunction args) {
      return this.appendTranslation(key, args, false);
   }

   public ItemsMutableComponent appendTranslation(String key) {
      return this.appendTranslation(key, (FormatDescFunction)null, false);
   }

   public ItemsMutableComponent appendNegAttributeFormat(int p, FormatDescFunction args) {
      return this.appendNegAttributeFormat(p, args, false);
   }

   public ItemsMutableComponent appendNegAttributeFormat(int p, FormatDescFunction args, boolean onlyClient) {
      this.parts.add(ItemsMutableComponent.FormatWrapper.wrap(ItemsMutableComponent.FormatPair.of("%s-%." + p + "f%% %s%s", args)).onlyClient(onlyClient));
      return this;
   }

   public MutableComponent build(ItemStack stack) {
      MutableComponent component = this.base.copy();
      component = (MutableComponent)this.style.getDelegate().apply(component);
      Dist dist = FMLEnvironment.dist;

      for(Object o : this.parts) {
         if (o instanceof FormatWrapper wrapper) {
            if (!wrapper.canUse(dist)) {
               component.append(Component.literal("[NULL]Only in dist:" + dist));
            } else {
               Pair<String, FormatDescFunction> var10 = wrapper.pair;
               if (var10 instanceof FormatPair pair) {
                   component.append(Component.literal(String.format((String)pair.left(), ((FormatDescFunction)pair.right()).apply(stack))));
               } else {
                   if (var10 instanceof TranslationFormatPair tpair) {
                      if (tpair.right() != null) {
                        component.append(Component.translatable((String)tpair.left(), ((FormatDescFunction)tpair.right()).apply(stack)));
                     } else {
                        component.append(Component.translatable((String)tpair.left()));
                     }
                  }
               }
            }
         } else if (o instanceof Component c) {
            component.append(c);
         }
      }

      return component;
   }

   public static class FormatWrapper {
      private final Pair<String, FormatDescFunction> pair;
      public boolean onlyClient = false;

      private FormatWrapper(Pair<String, FormatDescFunction> pair) {
         this.pair = pair;
      }

      public static FormatWrapper wrap(Pair<String, FormatDescFunction> pair) {
         return new FormatWrapper(pair);
      }

      public FormatWrapper onlyClient(boolean flag) {
         this.onlyClient = flag;
         return this;
      }

      public boolean canUse(Dist dist) {
         return !this.onlyClient || dist != Dist.DEDICATED_SERVER;
      }
   }

   public interface FormatPair extends Pair<String, FormatDescFunction> {
      static FormatPair of(final String format, final FormatDescFunction function) {
         return new FormatPair() {
            public String left() {
               return format;
            }

            public FormatDescFunction right() {
               return function;
            }
         };
      }
   }

   public interface TranslationFormatPair extends Pair<String, FormatDescFunction> {
      static TranslationFormatPair of(final String key, final FormatDescFunction function) {
         return new TranslationFormatPair() {
            public String left() {
               return key;
            }

            public FormatDescFunction right() {
               return function;
            }
         };
      }
   }

   public interface FormatDescFunction extends Function<ItemStack, Object[]> {
   }
}
