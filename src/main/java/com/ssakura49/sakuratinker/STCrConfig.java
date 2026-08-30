package com.ssakura49.sakuratinker;

import java.util.List;
import java.util.Objects;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.apache.commons.lang3.tuple.Pair;

public class STCrConfig {
   public static final ForgeConfigSpec commonSpec;
   public static final Common COMMON;
   static {
      final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
      commonSpec = specPair.getRight();
      COMMON = specPair.getLeft();
   }
   public static final ForgeConfigSpec clientSpec;
   public static final Client CLIENT;

   static {
      final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
      clientSpec = specPair.getRight();
      CLIENT = specPair.getLeft();
   }

   @SuppressWarnings("removal")
   public static void init() {
      String common_path = "ssakura49/sakuratinker-core-common.toml";
      String client_path = "ssakura49/sakuratinker-core-client.toml";
      ModLoadingContext.get().registerConfig(Type.COMMON, commonSpec, common_path);
      ModLoadingContext.get().registerConfig(Type.CLIENT, clientSpec, client_path);
   }

   public static class Client {
      public Client(ForgeConfigSpec.Builder builder) {
         // 客户端配置预留
      }
   }

   public static class Common {
      public final ForgeConfigSpec.BooleanValue HIDE_BANNED_MATERIAL;
      public final ForgeConfigSpec.BooleanValue ALLOW_ORIGINAL_MATERIALS;
      public final ForgeConfigSpec.BooleanValue ALLOW_GENERAL_MATERIALS;

      //联动黑白名单
      public final ForgeConfigSpec.ConfigValue<List<? extends String>> LIST_BLACKLIST_COMPAT;
      public final ForgeConfigSpec.ConfigValue<List<? extends String>> LIST_BLACKLIST_COMPAT_ORIGINAL;
      public final ForgeConfigSpec.ConfigValue<List<? extends String>> LIST_WHITELIST_COMPAT;
      public final ForgeConfigSpec.ConfigValue<List<? extends String>> LIST_WHITELIST_COMPAT_ORIGINAL;

      //通用材料黑白名单
      public final ForgeConfigSpec.ConfigValue<List<? extends String>> LIST_BLACKLIST_GENERAL;
      public final ForgeConfigSpec.ConfigValue<List<? extends String>> LIST_WHITELIST_GENERAL;

      //战斗与伤害控制
      public final ForgeConfigSpec.BooleanValue ALLOW_AOE_ATTACK_PLAYER;
      public final ForgeConfigSpec.BooleanValue TOOL_ENTITY_CAN_HURT_PLAYER;
      public final ForgeConfigSpec.BooleanValue TOOL_ENTITY_CAN_HURT_SELF;

      //通用词条黑名单
      public final ForgeConfigSpec.ConfigValue<List<? extends String>> MODIFIER_BLACKLIST;

      public final ForgeConfigSpec.BooleanValue ALLOW_PROJECTILE_TICK_INGROUND;

      public Common(ForgeConfigSpec.Builder builder) {
         builder.comment("***注意！", "***Notice",
                         "这些配置并不会同步修改语言文件中的描述，当你修改了某个工具/工具属性的性质后如果想要更改描述则需要手动覆盖语言文件！",
                         "This configure won't affect language display. When a certain modifier or tool is configured, please manually replace the description of the relating language contents.")
                 .push("Gameplay (WIP)");

         //材料隐藏
         this.HIDE_BANNED_MATERIAL = builder
                 .comment("在JEI和创造模式物品栏中隐藏被被关闭的材料，默认true。", "Hide blacklisted materials, true by default")
                 .define("HideBlacklistedMaterials", true);

         //原创材料控制
         this.ALLOW_ORIGINAL_MATERIALS = builder
                 .comment("启用原创材料，默认true。",
                         "这是一个总控，用于进行全局开关。如果你想精准控制各种联动材料的开关请使用下面的黑白名单。",
                         "Toggle original materials, true by default.")
                 .define("AllowOriginalMaterials", true);

         this.LIST_BLACKLIST_COMPAT = builder
                 .comment("集成型联动模组ID黑名单，向表中添加ModId来隐藏该模组的集成型联动。",
                         "黑名单模组对应的联动内容会被隐藏（不会加入创造模式物品栏，被JEI隐藏且配方不加载）。",
                         "Integration ModId blacklist.")
                 .defineListAllowEmpty("IntegrationModBL", List.of("example_mod1", "example_mod2"), Objects::nonNull);

         this.LIST_BLACKLIST_COMPAT_ORIGINAL = builder
                 .comment("原创型联动模组ID黑名单，向表中添加ModId来隐藏该模组的原创型联动。",
                         "黑名单模组对应的联动内容会被隐藏（不会加入创造模式物品栏，被JEI隐藏且配方不加载）。",
                         "Original Compat ModId blacklist.")
                 .defineListAllowEmpty("OriginalCompatModBL", List.of("example_mod1", "example_mod2"), Objects::nonNull);

         this.LIST_WHITELIST_COMPAT = builder
                 .comment("集成型联动模组ID白名单，只有表中有的modId的模组的集成联动会被加载，会覆盖黑名单的效果。",
                         "Integration Compat ModId whitelist(Overrides blacklist).")
                 .defineListAllowEmpty("IntegrationModWL", List.of(), Objects::nonNull);

         this.LIST_WHITELIST_COMPAT_ORIGINAL = builder
                 .comment("原创型联动模组ID白名单，只有表中有的modId的模组的原创联动会被加载，会覆盖黑名单的效果。",
                         "Original Compat ModId whitelist(Overrides blacklist).")
                 .defineListAllowEmpty("OriginalCompatModWL", List.of(), Objects::nonNull);

         // 3. 通用材料控制
         this.ALLOW_GENERAL_MATERIALS = builder
                 .comment("启用通用材料，这些材料通常伴随多个联动模组，默认true。",
                         "这是一个总控，用于进行全局开关。如果你想精准控制各种联动材料的开关请使用GeneralMaterialBL或GeneralMaterialWL。",
                         "Toggle general materials, true by default.")
                 .define("AllowGeneralMaterials", true);

         this.LIST_BLACKLIST_GENERAL = builder
                 .comment("通用材料黑名单，填材料名。", "General material name blacklist.")
                 .defineListAllowEmpty("GeneralMaterialNameBL", List.of("name1", "name2"), Objects::nonNull);

         this.LIST_WHITELIST_GENERAL = builder
                 .comment("通用材料白名单，填材料名，会覆盖黑名单的效果。", "General material name whitelist(Overrides blacklist).")
                 .defineListAllowEmpty("GeneralMaterialNameWL", List.of(), Objects::nonNull);

         // 4. 伤害机制控制
         this.ALLOW_AOE_ATTACK_PLAYER = builder
                 .comment("允许绝大部分魂樱工匠的范围伤害打到玩家，不建议启用这个，默认false。",
                         "Allow most of aoe damage from ST attacking player, false by default.")
                 .define("Allow Aoe Hit Players", false);

         this.TOOL_ENTITY_CAN_HURT_PLAYER = builder
                 .comment("工具的弹射物实体是否能够伤害玩家，默认false。",
                         "Whether tool's projectile entities can hurt players, false by default.")
                 .define("ToolEntityCanHurtPlayer", false);

         this.TOOL_ENTITY_CAN_HURT_SELF = builder
                 .comment("工具的弹射物实体是否能够伤害自己，默认false。",
                         "Whether tool's projectile entities can hurt self, false by default.")
                 .define("ToolEntityCanHurtSelf", false);

         this.MODIFIER_BLACKLIST = builder
                 .comment("Blacklist of modifiers (by ID) to skip in damage calculation")
                 .comment("Modifier黑名单")
                 .defineList("blacklist", List.of("cloudertinker:steelbone"), (obj) -> obj instanceof String);

         this.ALLOW_PROJECTILE_TICK_INGROUND = builder
                 .comment("是否允许弹射物在命中方块后依然通过ProjectileTickModifierHook遍历全词条，默认false",
                         "Whether to allow projectiles to still go through all entries via ProjectileTickModifierHook after hitting a block, false by default.")
                 .define("allowProjectileTickInground",false);


         builder.pop();
      }
   }
}
