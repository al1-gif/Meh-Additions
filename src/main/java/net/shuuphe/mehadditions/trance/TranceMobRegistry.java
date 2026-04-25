package net.shuuphe.mehadditions.trance;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.mob.BoggedEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TranceMobRegistry {

    public static final int CAPACITY_DEFAULT  = 200;
    public static final int CAPACITY_REVENANT = 250;

    private static final Map<Class<?>, Integer> POINT_COSTS = new HashMap<>();
    private static final Set<Class<?>> REVENANT_ONLY        = new HashSet<>();
    private static final Set<Class<?>> NEVER_CAPTURABLE     = new HashSet<>();

    static {
        POINT_COSTS.put(ZombieEntity.class,          20);
        POINT_COSTS.put(HuskEntity.class,            20);
        POINT_COSTS.put(ZombieVillagerEntity.class,  20);
        POINT_COSTS.put(SkeletonEntity.class,        20);
        POINT_COSTS.put(StrayEntity.class,           20);
        POINT_COSTS.put(BlazeEntity.class,           20);
        POINT_COSTS.put(CreeperEntity.class,         20);
        POINT_COSTS.put(SpiderEntity.class,          20);
        POINT_COSTS.put(CaveSpiderEntity.class,      20);
        POINT_COSTS.put(SilverfishEntity.class,      20);
        POINT_COSTS.put(SlimeEntity.class,           20);
        POINT_COSTS.put(PhantomEntity.class,         20);
        POINT_COSTS.put(WitherSkeletonEntity.class,  30);
        POINT_COSTS.put(VindicatorEntity.class,      30);
        POINT_COSTS.put(PillagerEntity.class,        30);
        POINT_COSTS.put(VexEntity.class,             30);
        POINT_COSTS.put(WitchEntity.class,           30);
        POINT_COSTS.put(ZombifiedPiglinEntity.class, 30);
        POINT_COSTS.put(BoggedEntity.class,          30);
        POINT_COSTS.put(EndermiteEntity.class,       40);
        POINT_COSTS.put(RavagerEntity.class,         80);
        REVENANT_ONLY.add(RavagerEntity.class);
        REVENANT_ONLY.add(VindicatorEntity.class);
        REVENANT_ONLY.add(BlazeEntity.class);
        REVENANT_ONLY.add(WitherSkeletonEntity.class);

        NEVER_CAPTURABLE.add(WitherEntity.class);
        NEVER_CAPTURABLE.add(ElderGuardianEntity.class);
        NEVER_CAPTURABLE.add(EnderDragonEntity.class);
        NEVER_CAPTURABLE.add(MagmaCubeEntity.class);
        NEVER_CAPTURABLE.add(EndermanEntity.class);
        NEVER_CAPTURABLE.add(GhastEntity.class);
        NEVER_CAPTURABLE.add(PiglinEntity.class);
        NEVER_CAPTURABLE.add(PiglinBruteEntity.class);
        NEVER_CAPTURABLE.add(HoglinEntity.class);
        NEVER_CAPTURABLE.add(EvokerEntity.class);
        NEVER_CAPTURABLE.add(GuardianEntity.class);
        NEVER_CAPTURABLE.add(WardenEntity.class);
        NEVER_CAPTURABLE.add(DrownedEntity.class);
    }

    public static boolean isCapturable(LivingEntity entity, boolean isRevenant) {
        Class<?> cls = entity.getClass();
        if (NEVER_CAPTURABLE.contains(cls)) return false;
        if (!POINT_COSTS.containsKey(cls)) return false;
        if (REVENANT_ONLY.contains(cls) && !isRevenant) return false;
        return true;
    }

    public static int getPoints(LivingEntity entity) {
        return POINT_COSTS.getOrDefault(entity.getClass(), -1);
    }

    public static boolean isRevenantOnly(LivingEntity entity) {
        return REVENANT_ONLY.contains(entity.getClass());
    }

    public static int getCaptureSpellCost(LivingEntity entity) {
        int pts = getPoints(entity);
        if (pts >= 80) return 100;
        return 20;
    }
}