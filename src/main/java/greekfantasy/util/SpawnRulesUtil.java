package greekfantasy.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public class SpawnRulesUtil {

    /**
     * Duplicate of Monster::checkMonsterSpawnRules but without the Entity Type restriction
     * @param entityType the entity type
     * @param level the level
     * @param mobSpawnType the mob spawn type
     * @param pos the position
     * @param rand the random instance
     * @return true if the difficulty is not peaceful and it is dark enough for a monster to spawn
     */
    public static boolean checkMonsterSpawnRules(EntityType<? extends PathfinderMob> entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, Random rand) {
        return level.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(level, pos, rand) && Mob.checkMobSpawnRules(entityType, level, mobSpawnType, pos, rand);
    }

    /**
     * Duplicate of Monster::checkAnyLightMonsterSpawnRules but without the Entity Type restriction
     * @param entityType the entity type
     * @param level the level
     * @param mobSpawnType the mob spawn type
     * @param pos the position
     * @param rand the random instance
     * @return true if the difficulty is not peaceful
     */
    public static boolean checkAnyLightMonsterSpawnRules(EntityType<? extends PathfinderMob> entityType, LevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, Random rand) {
        return level.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(entityType, level, mobSpawnType, pos, rand);
    }

    /**
     * Checks if a water mob can spawn here
     * @param entityType the entity type
     * @param level the level
     * @param mobSpawnType the mob spawn type
     * @param pos the position
     * @param rand the random instance
     * @return true if the position is in water and deep enough
     */
    public static boolean checkWaterMobSpawnRules(EntityType<? extends PathfinderMob> entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, Random rand) {
        if (!level.getFluidState(pos.below()).is(FluidTags.WATER)) {
            return false;
        } else {
            boolean flag = (mobSpawnType == MobSpawnType.SPAWNER || level.getFluidState(pos).is(FluidTags.WATER));
            return rand.nextInt(30) == 0 && isDeepEnoughToSpawn(level, pos) && flag;
        }
    }

    /**
     * Checks if a surface water mob can spawn here
     * @param entityType the entity type
     * @param level the level
     * @param mobSpawnType the mob spawn type
     * @param pos the position
     * @param rand the random instance
     * @return true if the position is in water and between 0 and 13 blocks below sea level
     */
    public static boolean checkSurfaceWaterMobSpawnRules(EntityType<? extends PathfinderMob> entityType, LevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, Random rand) {
        int seaLevel = level.getSeaLevel();
        int belowSeaLevel = seaLevel - 13;
        return pos.getY() >= belowSeaLevel && pos.getY() <= seaLevel && level.getFluidState(pos.below()).is(FluidTags.WATER) && level.getBlockState(pos.above()).is(Blocks.WATER);
    }

    /**
     * Checks if a water monster can spawn here
     * @param entityType the entity type
     * @param level the level
     * @param mobSpawnType the mob spawn type
     * @param pos the position
     * @param rand the random instance
     * @return true if the difficulty is not peaceful and the position is in water and deep enough
     */
    public static boolean checkWaterMonsterSpawnRules(EntityType<? extends PathfinderMob> entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, Random rand) {
        return level.getDifficulty() != Difficulty.PEACEFUL && checkWaterMobSpawnRules(entityType, level, mobSpawnType, pos, rand);
    }

    private static boolean isDeepEnoughToSpawn(LevelAccessor level, BlockPos pos) {
        return pos.getY() < level.getSeaLevel() - 5;
    }
}
