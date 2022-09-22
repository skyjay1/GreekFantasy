package greekfantasy.entity.ai;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class MoveToStructureGoal extends RandomStrollGoal {

    protected final Holder<Structure> structure;
    protected final HolderSet<Structure> structureSet;
    protected final int rangeInChunks;
    protected final int distanceXZ;
    protected final int distanceY;
    protected final RandomPosFactory randomPosFactory;
    /**
     * The block position of the nearest structure start
     **/
    protected BlockPos structurePos;
    protected StructureStart structureStart;

    public MoveToStructureGoal(PathfinderMob mob, double speedModifier,
                               int rangeInChunks, int distanceXZ, int distanceY,
                               ResourceLocation structureId,
                               RandomPosFactory posFactory) {
        this(mob, speedModifier, rangeInChunks, distanceXZ, distanceY,
                createHolder(mob.level.registryAccess(), structureId),
                posFactory);
    }

    public MoveToStructureGoal(PathfinderMob mob, double speedModifier,
                               int rangeInChunks, int distanceXZ, int distanceY,
                               Holder<Structure> structure,
                               RandomPosFactory posFactory) {
        super(mob, speedModifier, 10);
        this.structure = structure;
        this.structureSet = HolderSet.direct(this.structure);
        this.rangeInChunks = rangeInChunks;
        this.distanceXZ = distanceXZ;
        this.distanceY = distanceY;
        this.randomPosFactory = posFactory;
        // initialize structure start to avoid null errors
        this.structureStart = StructureStart.INVALID_START;
    }

    @Override
    public boolean canUse() {
        ServerLevel level = (ServerLevel) this.mob.level;
        BlockPos blockpos = this.mob.blockPosition();
        if (isNearStructure(structureStart, blockpos, distanceXZ, distanceY)) {
            return false;
        }
        Pair<BlockPos, Holder<Structure>> pair = level.getChunkSource().getGenerator().findNearestMapStructure(level, structureSet, blockpos, rangeInChunks, false);
        if (null == pair) {
            return false;
        }
        structurePos = pair.getFirst();
        structureStart = level.structureManager().getStructureAt(structurePos, structure.value());
        return structureStart != StructureStart.INVALID_START && super.canUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        ServerLevel level = (ServerLevel) this.mob.level;
        Vec3 vec = randomPosFactory.apply(this.mob, distanceXZ, distanceY);
        if (vec != null) {
            // if vec is within structure, use vec
            BlockPos pos = new BlockPos(vec);
            if (isInStructure(structureStart, pos)) {
                return vec;
            }
            // choose random position towards the nearest structure
            BlockPos center = structureStart.getBoundingBox().getCenter();
            Vec3 towardsVec = DefaultRandomPos.getPosTowards(mob, distanceXZ, distanceY, Vec3.atBottomCenterOf(center), Math.PI / 2.0D);
            return towardsVec != null ? towardsVec : vec;
        }
        return null;
    }

    /**
     * @param start the structure start
     * @param pos   the block position
     * @return true if the position is within a structure that is in the structure tag key
     */
    protected static boolean isInStructure(final StructureStart start, final BlockPos pos) {
        return isNearStructure(start, pos, 1, 1);
    }

    /**
     * @param start     the structure start
     * @param pos       the block position
     * @param inflateXZ the inflated amount for the x and z axes
     * @param inflateY  the inflated amount for the y axis
     * @return true if the position is within a structure that is in the structure tag key
     */
    protected static boolean isNearStructure(final StructureStart start, final BlockPos pos, int inflateXZ, int inflateY) {
        // do not calculate for invalid start
        if (start == StructureStart.INVALID_START) {
            return false;
        }
        // create bounding box and check for intersect
        BoundingBox posBB = new BoundingBox(
                pos.getX() - inflateXZ, pos.getY() - inflateY, pos.getZ() - inflateXZ,
                pos.getX() + inflateXZ, pos.getY() + inflateY, pos.getZ() + inflateXZ
        );
        if (!start.getBoundingBox().intersects(posBB)) {
            return false;
        }
        // all checks passed
        return true;
    }

    public static Holder<Structure> createHolder(final RegistryAccess access, final ResourceLocation structureId) {
        Structure csf = access.registryOrThrow(Registry.STRUCTURE_REGISTRY).get(structureId);
        if (null == csf) {
            throw new IllegalArgumentException("Failed to create holder for unknown structure '" + structureId + "'");
        }
        return Holder.direct(csf);
    }

    public static interface RandomPosFactory {
        @Nullable
        Vec3 apply(PathfinderMob mob, int maxXZ, int maxY);
    }
}
