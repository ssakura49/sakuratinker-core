package com.ssakura49.sakuratinker.core.library.tools.stats;

import com.ssakura49.sakuratinker.SakuraTinker;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.ArrayList;
import java.util.List;

public class STToolStats {
   private static final List<IToolStat<?>> ST_STATS = new ArrayList<>();
   private static FloatToolStat registerFloatStat(FloatToolStat stat){
      ST_STATS.add(stat);
      return stat;
   }

   public static final FloatToolStat MOVEMENT_SPEED = registerFloatStat(new FloatToolStat(name("movement_speed"), -8871731, 0.0F, 0.0F, 20480.0F));
   public static final FloatToolStat ARMOR = registerFloatStat(new FloatToolStat(name("armor"), -8042548, 0.0F, 0.0F, 300.0F));
   public static final FloatToolStat ARMOR_TOUGHNESS = registerFloatStat(new FloatToolStat(name("armor_toughness"), -8042548, 0.0F, 0.0F, 300.0F));
   public static final FloatToolStat ATTACK_DAMAGE = registerFloatStat(new FloatToolStat(name("damage"), -2661276, 0.0F, 0.0F, 20480.0F));
   public static final FloatToolStat ARROW_DAMAGE = registerFloatStat(new FloatToolStat(name("arrow_damage"), -2661276, 0.0F, 0.0F, 20480.0F));
   public static final FloatToolStat COOLDOWN = registerFloatStat(new FloatToolStat(name("cooldown"), -10887823, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat RANGE = registerFloatStat(new FloatToolStat(name("range"), -3135232, 1.0F, 1.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat MAX_HEALTH = registerFloatStat(new FloatToolStat(name("health"), -2661276, 0.0F, 0.0F, 20480.0F));
   public static final FloatToolStat ENERGY_STORAGE = registerFloatStat(new FloatToolStat(name("energy_storage"), -3135232, 0.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat BASE_SPELL_DAMAGE = registerFloatStat(new FloatToolStat(name("base_spell_damage"), -2661276, 0.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat SPELL_POWER = registerFloatStat(new FloatToolStat(name("spell_power"), -2661276, 0.0F, 0.0F, 20480.0F));
   public static final FloatToolStat SPELL_REDUCE = registerFloatStat(new FloatToolStat(name("spell_reduce"), -2661276, 0.0F, 0.0F, 20480.0F));
   public static final FloatToolStat CAST_TIME = registerFloatStat(new FloatToolStat(name("cast_time"), -10887823, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat ATTACK_BUFF_TIME = registerFloatStat(new FloatToolStat(name("attack_buff_time"), -8042548, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat DEFENCE_BUFF_TIME = registerFloatStat(new FloatToolStat(name("defence_buff_time"), -8042548, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat CHARGING_TIME = registerFloatStat(new FloatToolStat(name("charging_time"), -8042548, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat EMBER_STORAGE = registerFloatStat(new FloatToolStat(name("ember_storage"), -2661276, 0.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat TIME = registerFloatStat(new FloatToolStat(name("time"), -10887823, 100.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat MAX_COLLECTED = registerFloatStat(new FloatToolStat(name("max_collected"), -8042548, 1.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat ATTACK_INTERVAL = registerFloatStat(new FloatToolStat(name("attack_interval"), -2661276, 10.0F, 0.01F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat WEIGHT = registerFloatStat(new FloatToolStat(name("weight"), -8042548, 5.0F, 0.1F, 64.0F));
   public static final FloatToolStat LENGTH = registerFloatStat(new FloatToolStat(name("length"), -8042548, 2.0F, 1.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat PENETRATION = registerFloatStat(new FloatToolStat(name("penetration"), -2661276, 3.0F, 1.0F, 256.0F));
   public static final FloatToolStat FLUID_DAMAGE = registerFloatStat(new FloatToolStat(name("fluid_damage"), -2661276, 0.0F, 0.0F, 20480.0F));
   public static final FloatToolStat INVULNERABLE_TIME = registerFloatStat(new FloatToolStat(name("invulnerable_time"), -10887823, 0.0F, 0.0F, (float)Integer.MAX_VALUE));
   public static final FloatToolStat MOVE_SPEED = registerFloatStat(new FloatToolStat(name("move_speed"), -10887823, 1F, 0F, (float)Integer.MAX_VALUE));


   public static void initStats(){
      ST_STATS.forEach(ToolStats::register);
   }

   private static ToolStatId name(String name) {
      return new ToolStatId(SakuraTinker.getResource(name));
   }
}
