package com.ssakura49.sakuratinker;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.ssakura49.sakura_library.api.config.ConfigProperty;
import com.ssakura49.sakura_library.api.config.SakuraTomlConfig;
import com.ssakura49.sakura_library.api.config.TomlBuilder;
import com.ssakura49.sakura_library.util.RegisterAccessUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

public class STCrConfig {
   public static final SakuraTomlConfig CLIENT_INSTANCE;
   public static final Client CLIENT;

   public static final SakuraTomlConfig COMMON_INSTANCE;
   public static final Common COMMON;
   static {
      CLIENT_INSTANCE = new SakuraTomlConfig(SakuraTinker.MODID, "ssakura49/sakuratinker-core-client.toml");
      TomlBuilder client = new TomlBuilder(CLIENT_INSTANCE);
      CLIENT = new Client(client);
      client.build();

      COMMON_INSTANCE = new SakuraTomlConfig(SakuraTinker.MODID,"ssakura49/sakuratinker-core-common.toml");
      TomlBuilder common = new TomlBuilder(COMMON_INSTANCE);
      COMMON = new Common(common);
      common.build();
   }

   public static class Client {
      public Client(TomlBuilder builder) {
      }
   }

   public static class Common {
      public final ConfigProperty<Boolean> HIDE_BANNED_MATERIAL;
      public final ConfigProperty<Boolean> ALLOW_ORIGINAL_MATERIALS;
      public final ConfigProperty<Boolean> ALLOW_GENERAL_MATERIALS;

      //联动黑白名单
      public final ConfigProperty<List<String>> LIST_BLACKLIST_COMPAT;
      public final ConfigProperty<List<String>> LIST_BLACKLIST_COMPAT_ORIGINAL;
      public final ConfigProperty<List<String>> LIST_WHITELIST_COMPAT;
      public final ConfigProperty<List<String>> LIST_WHITELIST_COMPAT_ORIGINAL;

      //通用材料黑白名单
      public final ConfigProperty<List<String>> LIST_BLACKLIST_GENERAL;
      public final ConfigProperty<List<String>> LIST_WHITELIST_GENERAL;

      //战斗与伤害控制
      public final ConfigProperty<Boolean> ALLOW_AOE_ATTACK_PLAYER;
      public final ConfigProperty<Boolean> TOOL_ENTITY_CAN_HURT_PLAYER;
      public final ConfigProperty<Boolean> TOOL_ENTITY_CAN_HURT_SELF;

      //通用词条黑名单
      public final ConfigProperty<List<String>> MODIFIER_BLACKLIST;

      public final ConfigProperty<Boolean> ALLOW_PROJECTILE_TICK_INGROUND;

      public final ConfigProperty<String> SLIME_CRYSTAL_EARTH;
      public final ConfigProperty<String> SLIME_CRYSTAL_SKY;
      public final ConfigProperty<String> SLIME_CRYSTAL_NETHER;
      public final ConfigProperty<String> GOLD_BLOCK_ITEM;

      public Common(TomlBuilder builder) {
         builder.comment("***注意！", "***Notice",
                         "这些配置并不会同步修改语言文件中的描述，当你修改了某个工具/工具属性的性质后如果想要更改描述则需要手动覆盖语言文件！",
                         "This configure won't affect language display. When a certain modifier or tool is configured, please manually replace the description of the relating language contents.")
                 .pushCategory("Gameplay (WIP)");

         //材料隐藏
         this.HIDE_BANNED_MATERIAL = builder
                 .comment("在JEI和创造模式物品栏中隐藏被被关闭的材料，默认true。", "Hide blacklisted materials, true by default")
                 .getBoolean("HideBlacklistedMaterials", true);

         //原创材料控制
         this.ALLOW_ORIGINAL_MATERIALS = builder
                 .comment("启用原创材料，默认true。",
                         "这是一个总控，用于进行全局开关。如果你想精准控制各种联动材料的开关请使用下面的黑白名单。",
                         "Toggle original materials, true by default.")
                 .getBoolean("AllowOriginalMaterials", true);

         this.LIST_BLACKLIST_COMPAT = builder
                 .comment("集成型联动模组ID黑名单，向表中添加ModId来隐藏该模组的集成型联动。",
                         "黑名单模组对应的联动内容会被隐藏（不会加入创造模式物品栏，被JEI隐藏且配方不加载）。",
                         "Integration ModId blacklist.")
                 .getStringList("IntegrationModBL", List.of("example_mod1", "example_mod2"));

         this.LIST_BLACKLIST_COMPAT_ORIGINAL = builder
                 .comment("原创型联动模组ID黑名单，向表中添加ModId来隐藏该模组的原创型联动。",
                         "黑名单模组对应的联动内容会被隐藏（不会加入创造模式物品栏，被JEI隐藏且配方不加载）。",
                         "Original Compat ModId blacklist.")
                 .getStringList("OriginalCompatModBL", List.of("example_mod1", "example_mod2"));

         this.LIST_WHITELIST_COMPAT = builder
                 .comment("集成型联动模组ID白名单，只有表中有的modId的模组的集成联动会被加载，会覆盖黑名单的效果。",
                         "Integration Compat ModId whitelist(Overrides blacklist).")
                 .getStringList("IntegrationModWL", List.of());

         this.LIST_WHITELIST_COMPAT_ORIGINAL = builder
                 .comment("原创型联动模组ID白名单，只有表中有的modId的模组的原创联动会被加载，会覆盖黑名单的效果。",
                         "Original Compat ModId whitelist(Overrides blacklist).")
                 .getStringList("OriginalCompatModWL", List.of());

         // 3. 通用材料控制
         this.ALLOW_GENERAL_MATERIALS = builder
                 .comment("启用通用材料，这些材料通常伴随多个联动模组，默认true。",
                         "这是一个总控，用于进行全局开关。如果你想精准控制各种联动材料的开关请使用GeneralMaterialBL或GeneralMaterialWL。",
                         "Toggle general materials, true by default.")
                 .getBoolean("AllowGeneralMaterials", true);

         this.LIST_BLACKLIST_GENERAL = builder
                 .comment("通用材料黑名单，填材料名。", "General material name blacklist.")
                 .getStringList("GeneralMaterialNameBL", List.of("name1", "name2"));

         this.LIST_WHITELIST_GENERAL = builder
                 .comment("通用材料白名单，填材料名，会覆盖黑名单的效果。", "General material name whitelist(Overrides blacklist).")
                 .getStringList("GeneralMaterialNameWL", List.of());

         // 4. 伤害机制控制
         this.ALLOW_AOE_ATTACK_PLAYER = builder
                 .comment("允许绝大部分魂樱工匠的范围伤害打到玩家，不建议启用这个，默认false。",
                         "Allow most of aoe damage from ST attacking player, false by default.")
                 .getBoolean("Allow Aoe Hit Players", false);

         this.TOOL_ENTITY_CAN_HURT_PLAYER = builder
                 .comment("工具的弹射物实体是否能够伤害玩家，默认false。",
                         "Whether tool's projectile entities can hurt players, false by default.")
                 .getBoolean("ToolEntityCanHurtPlayer", false);

         this.TOOL_ENTITY_CAN_HURT_SELF = builder
                 .comment("工具的弹射物实体是否能够伤害自己，默认false。",
                         "Whether tool's projectile entities can hurt self, false by default.")
                 .getBoolean("ToolEntityCanHurtSelf", false);

         this.MODIFIER_BLACKLIST = builder
                 .comment("Blacklist of modifiers (by ID) to skip in damage calculation")
                 .comment("Modifier黑名单")
                 .getStringList("blacklist", List.of("cloudertinker:steelbone"));

         this.ALLOW_PROJECTILE_TICK_INGROUND = builder
                 .comment("是否允许弹射物在命中方块后依然通过ProjectileTickModifierHook遍历全词条，默认false",
                         "Whether to allow projectiles to still go through all entries via ProjectileTickModifierHook after hitting a block, false by default.")
                 .getBoolean("allowProjectileTickInground",false);

         SLIME_CRYSTAL_EARTH = builder
                 .comment("大地史莱姆水晶 (default: sakuratinker:slime_crystal_earth)")
                 .getString("slimeCrystalEarth", "sakuratinker:slime_crystal_earth");
         SLIME_CRYSTAL_SKY = builder
                 .comment("天空史莱姆水晶 (default: sakuratinker:slime_crystal_sky)")
                 .getString("slimeCrystalSky", "sakuratinker:slime_crystal_sky");
         SLIME_CRYSTAL_NETHER = builder
                 .comment("下界史莱姆水晶 (default: sakuratinker:slime_crystal_nether)")
                 .getString("slimeCrystalNether", "sakuratinker:slime_crystal_nether");
         GOLD_BLOCK_ITEM = builder
                 .comment("金块 (default: minecraft:gold_block)")
                 .getString("goldBlock", "minecraft:gold_block");


         builder.popCategory();
      }
   }


   public static class SakuraSealConfig {
      private static Supplier<Item> getConfiguredItem(ConfigProperty<String> config, Supplier<Item> fallback) {
         return () -> {
            try {
               String id = (String)config.get();
               Item item = (Item) ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(id));
               return item != null ? item : (Item)fallback.get();
            } catch (Exception e) {
               SakuraTinker.LOGGER.error("配置中物品ID无效: {}", config.get(), e);
               return (Item)fallback.get();
            }
         };
      }

      public static Supplier<Item> slime_crystal_earth() {
         return getConfiguredItem(STCrConfig.COMMON.SLIME_CRYSTAL_EARTH, () -> (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("sakuratinker", "slime_crystal_earth")));
      }

      public static Supplier<Item> slime_crystal_sky() {
         return getConfiguredItem(STCrConfig.COMMON.SLIME_CRYSTAL_SKY, () -> (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("sakuratinker", "slime_crystal_sky")));
      }

      public static Supplier<Item> slime_crystal_nether() {
         return getConfiguredItem(STCrConfig.COMMON.SLIME_CRYSTAL_NETHER, () -> (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("sakuratinker", "slime_crystal_nether")));
      }

      public static Supplier<Item> getGoldBlock() {
         return getConfiguredItem(STCrConfig.COMMON.GOLD_BLOCK_ITEM, () -> Items.GOLD_BLOCK);
      }

   }
}
