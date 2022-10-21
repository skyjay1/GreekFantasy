package greekfantasy.entity.ai;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class MoveToStructureGoal extends RandomStrollGoal {


    protected final Holder<ConfiguredStructureFeature<?, ?>> structure;
    protected final HolderSet<ConfiguredStructureFeature<?, ?>> structureSet;
    protected final int rangeInSections;
    protected final int distanceXZ;
    protected final int distanceY;
    protected final RandomPosFactory randomPosFactory;
    /** The nearest structure start **/
    protected StructureStart structureStart;
    /** The number of ticks to wait before attempting to locate structures **/
    protected final int maxCooldown;
    protected int cooldown;

    /**
     * @param mob the entity
     * @param speedModifier the path navigation speed modifier
     * @param rangeInSections the distance to search for structures in chunk sections (16x16x16)
     * @param distanceXZ the maximum allowed x and z distance (in blocks) from a located structure
     * @param distanceY the maximum allowed y distance (in blocks) from a located structure
     * @param structureId the structure ID
     * @param posFactory a random position provider
     */
    public MoveToStructureGoal(PathfinderMob mob, double speedModifier,
                               int rangeInSections, int distanceXZ, int distanceY,
                               ResourceLocation structureId,
                               RandomPosFactory posFactory) {
        this(mob, speedModifier, rangeInSections, distanceXZ, distanceY,
                createHolder(mob.level.registryAccess(), structureId),
                posFactory);
    }

    /**
     * @param mob the entity
     * @param speedModifier the path navigation speed modifier
     * @param rangeInSections the distance to search for structures in chunk sections (16x16x16)
     * @param distanceXZ the maximum allowed x and z distance (in blocks) from a located structure
     * @param distanceY the maximum allowed y distance (in blocks) from a located structure
     * @param structure the structure
     * @param posFactory a random position provider
     */
    public MoveToStructureGoal(PathfinderMob mob, double speedModifier,
                               int rangeInSections, int distanceXZ, int distanceY,
                               Holder<ConfiguredStructureFeature<?, ?>> structure,
                               RandomPosFactory posFactory) {
        super(mob, speedModifier, 20);
        this.structure = structure;
        this.structureSet = HolderSet.direct(this.structure);
        this.rangeInSections = rangeInSections;
        this.distanceXZ = distanceXZ;
        this.distanceY = distanceY;
        this.randomPosFactory = posFactory;
        // initialize structure start to avoid null errors
        this.structureStart = StructureStart.INVALID_START;
        this.maxCooldown = 250 + mob.getId() % 200;
        this.cooldown = 10 + mob.getId() % 20;
    }

    @Override
    public boolean canUse() {
        ServerLevel level = (ServerLevel)this.mob.level;
        BlockPos blockpos = this.mob.blockPosition();
        // check if mob is near structure
        if(isNearStructure(structureStart, blockpos, distanceXZ, distanceY)) {
            return false;
        }
        // periodically check for nearby structure
        if(cooldown-- <= 0) {
            // reset cooldown
            cooldown = maxCooldown;
            // attempt to locate structure
            Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair = level.getChunkSource().getGenerator().findNearestMapFeature(level, structureSet, blockpos, rangeInSections, false);
            if(null == pair) {
                structureStart = StructureStart.INVALID_START;
                return false;
            }
            // determine structure position and section
            final BlockPos structurePos = pair.getFirst();
            final SectionPos sectionPos = SectionPos.of(structurePos);
            final int sectionX = SectionPos.blockToSectionCoord(structurePos.getX());
            final int sectionZ = SectionPos.blockToSectionCoord(structurePos.getZ());
            // load the chunk to query structure starts
            final ChunkAccess chunkAccess = level.getChunk(sectionX, sectionZ, ChunkStatus.STRUCTURE_STARTS);
            // locate structure start, or fallback to invalid start
            structureStart = Optional.ofNullable(level.structureFeatureManager().getStartForFeature(sectionPos, pair.getSecond().value(), chunkAccess)).orElse(StructureStart.INVALID_START);
            if(structureStart != StructureStart.INVALID_START) {
                this.trigger();
            }
        }
        // only execute when structure is valid
        return structureStart != StructureStart.INVALID_START && super.canUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 vec = randomPosFactory.apply(this.mob, distanceXZ, distanceY);
        if(vec != null) {
            // if vec is within structure, use vec
            BlockPos pos = new BlockPos(vec);
            if(isInStructure(structureStart, pos)) {
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
     * @param pos the block position
     * @return true if the position is within a structure that is in the structure tag key
     */
    protected static boolean isInStructure(final StructureStart start, final BlockPos pos) {
        return isNearStructure(start, pos, 1, 1);
    }

    /**
     * @param start the structure start
     * @param pos the block position
     * @param inflateXZ the inflated amount for the x and z axes
     * @param inflateY the inflated amount for the y axis
     * @return true if the position is within a structure that is in the structure tag key
     */
    protected static boolean isNearStructure(final StructureStart start, final BlockPos pos, int inflateXZ, int inflateY) {
        // do not calculate for invalid start
        if(start == StructureStart.INVALID_START) {
            return false;
        }
        // create bounding box and check for intersect
        BoundingBox posBB = new BoundingBox(
                pos.getX() - inflateXZ, pos.getY() - inflateY, pos.getZ() - inflateXZ,
                pos.getX() + inflateXZ, pos.getY() + inflateY, pos.getZ() + inflateXZ
        );
        if(!start.getBoundingBox().intersects(posBB)) {
            return false;
        }
        // all checks passed
        return true;
    }

    public static TagKey<ConfiguredStructureFeature<?, ?>> createTagKey(final ResourceLocation structureId) {
        return TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, structureId);
    }

    public static Holder<ConfiguredStructureFeature<?, ?>> createHolder(final RegistryAccess access, final ResourceLocation structureId) {

        ConfiguredStructureFeature<?, ?> csf = access.registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(structureId);
        if(null == csf) {
            throw new IllegalArgumentException("Failed to create holder for unknown configured structure feature '" + structureId + "'");
        }
        return Holder.direct(csf);
    }

    public static interface RandomPosFactory {
        @Nullable
        Vec3 apply(PathfinderMob mob, int maxXZ, int maxY);
    }
}
