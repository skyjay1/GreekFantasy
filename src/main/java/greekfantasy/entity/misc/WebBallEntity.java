package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.entity.BabySpiderEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

public class WebBallEntity extends ProjectileItemEntity {

    protected static final DataParameter<Byte> TYPE = EntityDataManager.defineId(WebBallEntity.class, DataSerializers.BYTE);
    protected static final String KEY_TYPE = "WebType";

    public static final byte WEB = 1;
    public static final byte SPIDER = 2;
    public static final byte ITEM = 4;

    public WebBallEntity(EntityType<? extends WebBallEntity> entityType, World world) {
        super(entityType, world);
    }

    private WebBallEntity(World worldIn, LivingEntity thrower) {
        super(GFRegistry.EntityReg.WEB_BALL_ENTITY, thrower, worldIn);
    }

    private WebBallEntity(World worldIn, double x, double y, double z) {
        super(GFRegistry.EntityReg.WEB_BALL_ENTITY, x, y, z, worldIn);
    }

    public static WebBallEntity create(World worldIn, double x, double y, double z) {
        return new WebBallEntity(worldIn, x, y, z);
    }

    public static WebBallEntity create(World worldIn, LivingEntity thrower) {
        return new WebBallEntity(worldIn, thrower);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TYPE, Byte.valueOf(WEB));
    }

    @Override
    protected Item getDefaultItem() {
        return GFRegistry.ItemReg.WEB_BALL;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult raytrace) {
        super.onHitEntity(raytrace);
        if (!this.level.isClientSide() && level instanceof IServerWorld && this.isAlive() && raytrace.getEntity() != null) {
            onWebImpact(raytrace, raytrace.getEntity().blockPosition());
            remove();
        }
    }

    @Override
    protected void onHit(RayTraceResult raytrace) {
        super.onHit(raytrace);
        if (!this.level.isClientSide() && level instanceof IServerWorld && this.isAlive()) {
            onWebImpact(raytrace, blockPosition());
            remove();
        }
    }

    @Override
    public void tick() {
        Entity entity = getOwner();
        if (entity instanceof net.minecraft.entity.player.PlayerEntity && !entity.isAlive()) {
            remove();
        } else {
            super.tick();
        }
    }

    @Override
    public Entity changeDimension(ServerWorld serverWorld, ITeleporter iTeleporter) {
        Entity entity = getOwner();
        if (entity != null && entity.level.dimension() != serverWorld.dimension()) {
            setOwner(null);
        }
        return super.changeDimension(serverWorld, iTeleporter);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getGravity() {
        return 0.08F;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        setWebType(tag.getByte(KEY_TYPE));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte(KEY_TYPE, getWebType());
    }

    private void onWebImpact(final RayTraceResult raytrace, final BlockPos webPos) {
        final byte type = getWebType();
        // nothing (drop string)
        if (type == 0) {
            spawnAtLocation(new ItemStack(Items.STRING));
            return;
        }
        // web
        if (hasWeb(type) && level.isEmptyBlock(webPos)) {
            level.setBlockAndUpdate(webPos, Blocks.COBWEB.defaultBlockState());
        }
        // spider
        if (hasSpider(type)) {
            BabySpiderEntity spider = GFRegistry.EntityReg.BABY_SPIDER_ENTITY.create(level);
            spider.copyPosition(this);
            spider.restrictTo(webPos, 12);
            level.addFreshEntity(spider);
        }
        // item
        if (hasItem(type)) {
            ResourceLocation resourcelocation = getType().getDefaultLootTable();
            LootTable loottable = this.level.getServer().getLootTables().get(resourcelocation);
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld) this.level))
                    .withRandom(this.random)
                    .withParameter(LootParameters.THIS_ENTITY, this)
                    .withParameter(LootParameters.ORIGIN, this.position())
                    .withParameter(LootParameters.DAMAGE_SOURCE, DamageSource.FALL)
                    .withOptionalParameter(LootParameters.KILLER_ENTITY, getOwner())
                    .withOptionalParameter(LootParameters.DIRECT_KILLER_ENTITY, null);
            LootContext ctx = lootcontext$builder.create(LootParameterSets.ENTITY);
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
        if (web) type = (byte) (type + WEB);
        if (spider) type = (byte) (type + SPIDER);
        if (item) type = (byte) (type + ITEM);
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
