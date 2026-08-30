package com.ssakura49.sakuratinker.core.utils;

import lombok.Getter;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

@Getter
public class EffectResult {
    private static final Random RANDOM = new Random();
    private final Action action;
    private final MobEffectInstance modifiedEffect;

    public enum Action {
        PASS,
        BLOCK,
        MODIFY
    }

    private EffectResult(Action action, MobEffectInstance modifiedEffect) {
        this.action = action;
        this.modifiedEffect = modifiedEffect;
    }

    public static EffectResult pass() { return new EffectResult(Action.PASS, null); }
    public static EffectResult block() { return new EffectResult(Action.BLOCK, null); }
    public static EffectResult modify(MobEffectInstance newEffect) { return new EffectResult(Action.MODIFY, newEffect); }
    /**
     * 缩减药水持续时间（例如乘以 0.5 减半）
     */
    public static EffectResult scaleDuration(MobEffectInstance original, float factor) {
        int newDuration = Math.max(1, (int) (original.getDuration() * factor));
        return modify(new MobEffectInstance(
                original.getEffect(),
                newDuration,
                original.getAmplifier(),
                original.isAmbient(),
                original.isVisible(),
                original.showIcon()
        ));
    }

    /**
     * 降低药水等级（例如 amplifier - 1）
     */
    public static EffectResult reduceAmplifier(MobEffectInstance original, int reduceBy) {
        int newAmplifier = Math.max(0, original.getAmplifier() - reduceBy);
        return modify(new MobEffectInstance(
                original.getEffect(),
                original.getDuration(),
                newAmplifier,
                original.isAmbient(),
                original.isVisible(),
                original.showIcon()
        ));
    }

    /**
     * 替换药水类型（保持持续时间和等级不变，只换 Buff 类型）
     */
    public static EffectResult convertTo(MobEffectInstance original, MobEffect newEffectType) {
        return modify(new MobEffectInstance(
                newEffectType,
                original.getDuration(),
                original.getAmplifier(),
                original.isAmbient(),
                original.isVisible(),
                original.showIcon()
        ));
    }

    /**
     * 概率免疫
     */
    public static EffectResult blockChance(float chance) {
        return RANDOM.nextFloat() < chance ? block() : pass();
    }

    /**
     * 阈值保护(例如只有持续时间>20秒的Buff才缩短)
     */
    public static EffectResult capMaxDuration(MobEffectInstance original, int maxDurationTicks) {
        if (original.getDuration() > maxDurationTicks) {
            return modify(copyOf(original, original.getEffect(), maxDurationTicks, original.getAmplifier()));
        }
        return pass();
    }

    /**
     * 限制最高等级
     */
    public static EffectResult capMaxAmplifier(MobEffectInstance original, int maxAmplifier) {
        if (original.getAmplifier() > maxAmplifier) {
            return modify(copyOf(original, original.getEffect(), original.getDuration(), maxAmplifier));
        }
        return pass();
    }

    /**
     * 延长/增幅
     */
    public static EffectResult extendDuration(MobEffectInstance original, float extraPercent) {
        int newDuration = (int) (original.getDuration() * (1.0f + extraPercent));
        return modify(copyOf(original, original.getEffect(), newDuration, original.getAmplifier()));
    }

    /**
     * 反射Buff(拦截该Buff，并将其原封不动地施加给攻击者或者周围敌人)
     * @param target 转移目标
     */
    public static EffectResult reflectTo(MobEffectInstance original, LivingEntity target) {
        if (target != null && target.isAlive()) {
            target.addEffect(new MobEffectInstance(original));
        }
        return block();
    }

    /**
     * 免疫负面Buff的同时，按持续时间/等级恢复自身血量或饱和度)
     */
    public static EffectResult absorbToHeal(MobEffectInstance original, LivingEntity entity, float healPerLevel) {
        if (entity != null) {
            float healAmount = (original.getAmplifier() + 1) * healPerLevel;
            entity.heal(healAmount);
        }
        return block();
    }

    public static EffectResult blockIfHarmful(MobEffectInstance original) {
        if (original.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
            return block();
        }
        return pass();
    }

    private static MobEffectInstance copyOf(MobEffectInstance src, MobEffect effect, int duration, int amplifier) {
        return new MobEffectInstance(
                effect,
                duration,
                amplifier,
                src.isAmbient(),
                src.isVisible(),
                src.showIcon()
        );
    }
}
