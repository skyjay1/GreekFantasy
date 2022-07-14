package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.entity.monster.BabySpider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.NetworkHooks;

public class WebBall extends ThrowableItemProjectile {

    protected static final EntityDataAccessor<Byte> TYPE = SynchedEntityData.defineId(WebBall.class, EntityDataSerializers.BYTE);
    protected static final String KEY_TYPE = "WebType";

    public static final byte WEB = 1;
    public static final byte SPIDER = 2;
    public static final byte ITEM = 4;

    public WebBall(EntityType<? extends WebBall> entityType, Level level) {
        super(entityType, level);
    }

    private WebBall(Level level, LivingEntity thrower) {
        super(GFRegistry.EntityReg.WEB_BALL.get(), thrower, level);
    }

    private WebBall(Level level, double x, double y, double z) {
        super(GFRegistry.EntityReg.WEB_BALL.get(), x, y, z, level);
    }

    public static WebBall create(Level level, double x, double y, double z) {
        return new WebBall(level, x, y, z);
    }

    public static WebBall create(Level level, LivingEntity thrower) {
        return new WebBall(level, thrower);
    }

    @Override
    protected Item getDefaultItem() {
        return GFRegistry.ItemReg.WEB_BALL.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TYPE, WEB);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        onWebImpact(hitResult, hitResult.getLocation());
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        onWebImpact(hitResult, hitResult.getLocation());
        discard();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        // do not process when discarded
        if (this.level.isClientSide() || !this.isAlive()) {
            return;
        }
        // do not collide with cobwebs
        if (hitResult.getType() == HitResult.Type.BLOCK &&
                level.getBlockState(new BlockPos(hitResult.getLocation())).is(Blocks.COBWEB)) {
            return;
        }
        super.onHit(hitResult);
    }

    @Override
    public void tick() {
        Entity entity = getOwner();
        if (entity instanceof Player && !entity.isAlive()) {
            discard();
        } else {
            super.tick();
        }
    }

    @Override
    public Entity changeDimension(ServerLevel serverWorld, ITeleporter iTeleporter) {
        Entity entity = getOwner();
        if (entity != null && entity.level.dimension() != serverWorld.dimension()) {
            setOwner(null);
        }
        return super.changeDimension(serverWorld, iTeleporter);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getGravity() {
        return 0.08F;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setWebType(tag.getByte(KEY_TYPE));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte(KEY_TYPE, getWebType());
    }

    protected void onWebImpact(final HitResult raytrace, final Vec3 webPos) {
        if(level.isClientSide()) {
            return;
        }
        final BlockPos hitPos = new BlockPos(webPos);
        final byte type = getWebType();
        // nothing (drop string)
        if (type == 0) {
            spawnAtLocation(new ItemStack(Items.STRING));
            return;
        }
        // web
        if (hasWeb(type) && level.isEmptyBlock(hitPos)) {
            level.setBlockAndUpdate(hitPos, Blocks.COBWEB.defaultBlockState());
        }
        // spider
        if (hasSpider(type)) {
            BabySpider spider = GFRegistry.EntityReg.BABY_SPIDER.get().create(level);
            spider.copyPosition(this);
            spider.restrictTo(hitPos, 12);
            level.addFreshEntity(spider);
        }
        // item
        if (hasItem(type) && this.level instanceof ServerLevel serverLevel) {
            ResourceLocation resourcelocation = getType().getDefaultLootTable();
            LootTable loottable = serverLevel.getServer().getLootTables().get(resourcelocation);
            LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverLevel))
                    .withRandom(this.random)
                    .withParameter(LootContextParams.THIS_ENTITY, this)
                    .withParameter(LootContextParams.ORIGIN, webPos)
                    .withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.FALL)
                    .withOptionalParameter(LootContextParams.KILLER_ENTITY, getOwner())
                    .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, null);
            LootContext ctx = lootcontext$builder.create(LootContextParamSets.ENTITY);
            loottable.getRandomItems(ctx).forEach(this::spawnAtLocation);
        }
    }

    public void setWebType(final byte b) {
        getEntityData().set(TYPE, Byte.valueOf(b));
    }

    public byte getWebType() {
        return getEntityData().get(TYPE).byteValue();
    }

    public byte setWebType(final boolean web, final boolean spider, final boolean item) {
        // determine type bits
        byte type = 0;
        if (web) type = (byte) (type | WEB);
        if (spider) type = (byte) (type | SPIDER);
        if (item) type = (byte) (type | ITEM);
        /*
        TODO revert to this if needed
        if (web) type = (byte) (type + WEB);
        if (spider) type = (byte) (type + SPIDER);
        if (item) type = (byte) (type + ITEM);
         */
        // actually set the web type
        setWebType(type);
        // return the web type
        return type;
    }

    public boolean hasWeb(final byte webType) {
        return (webType & WEB) != 0;
    }

    public boolean hasSpider(final byte webType) {
        return (webType & SPIDER) != 0;
    }

    public boolean hasItem(final byte webType) {
        return (webType & ITEM) != 0;
    }
}
