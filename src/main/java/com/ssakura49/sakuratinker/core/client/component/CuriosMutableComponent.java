package com.ssakura49.sakuratinker.core.client.component;

import com.ssakura49.sakura_library.util.SafeIterationUtils;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class CuriosMutableComponent {
   public static final CuriosMutableComponent EMPTY = create();
   final MutableComponent component;
   public LoreStyle style;
   List<Object> content;

   CuriosMutableComponent(MutableComponent component) {
      this.style = LoreStyle.NONE;
      this.content = new ArrayList();
      this.component = component;
   }

   public static CuriosMutableComponent create(MutableComponent component, LoreStyle style) {
      return (new CuriosMutableComponent(component)).loreStyle(style);
   }

   public static CuriosMutableComponent create() {
      return (new CuriosMutableComponent(Component.literal(""))).loreStyle(LoreStyle.NONE);
   }

   public static CuriosMutableComponent create(LoreStyle style) {
      return (new CuriosMutableComponent(Component.literal(""))).loreStyle(style);
   }

   public List<CuriosMutableComponent> bake(List<MutableComponent> components) {
      return SafeIterationUtils.safelyForEach(components, (Function<MutableComponent, CuriosMutableComponent>) (mutable) -> create(mutable, this.style));
   }

   private CuriosMutableComponent loreStyle(LoreStyle style) {
      this.style = style;
      return this;
   }

   public CuriosMutableComponent appendFormat(String format, FormatDescFunction args, boolean onlyClient) {
      this.content.add(CuriosMutableComponent.FormatWrapper.wrap(CuriosMutableComponent.FormatPair.of(format, args)).onlyClient(onlyClient));
      return this;
   }

   public CuriosMutableComponent appendAttributeFormat(int p, FormatDescFunction args, boolean onlyClient) {
      this.content.add(CuriosMutableComponent.FormatWrapper.wrap(CuriosMutableComponent.FormatPair.of("%s+%." + p + "f%% %s%s", args)).onlyClient(onlyClient));
      return this;
   }

   public CuriosMutableComponent appendNegAttributeFormat(int p, FormatDescFunction args, boolean onlyClient) {
      this.content.add(CuriosMutableComponent.FormatWrapper.wrap(CuriosMutableComponent.FormatPair.of("%s-%." + p + "f%% %s%s", args)).onlyClient(onlyClient));
      return this;
   }

   public CuriosMutableComponent appendTranslation(String format, FormatDescFunction args, boolean onlyClient) {
      this.content.add(CuriosMutableComponent.FormatWrapper.wrap(CuriosMutableComponent.TranslationFormatPair.of(format, args)).onlyClient(onlyClient));
      return this;
   }

   public CuriosMutableComponent appendFormat(String format, FormatDescFunction args) {
      return this.appendFormat(format, args, false);
   }

   public CuriosMutableComponent appendAttributeFormat(int p, FormatDescFunction args) {
      return this.appendAttributeFormat(p, args, false);
   }

   public CuriosMutableComponent appendNegAttributeFormat(int p, FormatDescFunction args) {
      return this.appendNegAttributeFormat(p, args, false);
   }

   public CuriosMutableComponent appendTranslation(String format, FormatDescFunction args) {
      return this.appendTranslation(format, args, false);
   }

   public CuriosMutableComponent appendTranslation(String format) {
      return this.appendTranslation(format, (FormatDescFunction)null);
   }

   public CuriosMutableComponent appendComponent(Component component) {
      this.content.add(component);
      return this;
   }

   public MutableComponent build(ItemStack stack) {
      MutableComponent component = this.component.copy();
      component = (MutableComponent)this.style.getDelegate().apply(component);
      Dist dist = FMLEnvironment.dist;

      for(Object o : this.content) {
         if (o instanceof FormatWrapper formatWrapper) {
            Pair<String, FormatDescFunction> var10 = formatWrapper.pair;
            if (var10 instanceof FormatPair pair) {
               if (formatWrapper.canUse(dist)) {
                  component.append(Component.literal(String.format((String)pair.first(), ((FormatDescFunction)pair.right()).apply(stack))));
               } else {
                  component.append("[NULL]Only in dist:" + dist);
               }
            } else {
                if (var10 instanceof TranslationFormatPair pair) {
                  if (formatWrapper.canUse(dist)) {
                     if (pair.right() != null) {
                           component.append(Component.translatable((String)pair.first(), ((FormatDescFunction)pair.right()).apply(stack)));
                     } else {
                        component.append(Component.translatable((String)pair.first()));
                     }
                  } else {
                     component.append("[NULL]Only in dist:" + dist);
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

      public FormatWrapper onlyClient(boolean z) {
         this.onlyClient = z;
         return this;
      }

      public boolean canUse(Dist dist) {
         if (this.onlyClient) {
            return dist != Dist.DEDICATED_SERVER;
         } else {
            return true;
         }
      }
   }

   public interface FormatPair extends Pair<String, FormatDescFunction> {
      static FormatPair of(final String s, final FormatDescFunction function) {
         return new FormatPair() {
            public String left() {
               return s;
            }

            public FormatDescFunction right() {
               return function;
            }
         };
      }
   }

   public interface TranslationFormatPair extends Pair<String, FormatDescFunction> {
      static TranslationFormatPair of(final String s, final FormatDescFunction function) {
         return new TranslationFormatPair() {
            public String left() {
               return s;
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
