package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.BabySpiderEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SpiderEntity;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

public class WebBallEntity extends ProjectileItemEntity {
  
  protected static final DataParameter<Byte> TYPE = EntityDataManager.createKey(WebBallEntity.class, DataSerializers.BYTE);
  protected static final String KEY_TYPE = "WebType";
  
  public static final byte WEB = 1;
  public static final byte SPIDER = 2;
  public static final byte ITEM = 4;
  
  public WebBallEntity(EntityType<? extends WebBallEntity> entityType, World world) {
    super(entityType, world);
  }

  private WebBallEntity(World worldIn, LivingEntity thrower) {
    super(GFRegistry.WEB_BALL_ENTITY, thrower, worldIn);
  }

  private WebBallEntity(World worldIn, double x, double y, double z) {
    super(GFRegistry.WEB_BALL_ENTITY, x, y, z, worldIn);
  }
  
  public static WebBallEntity create(World worldIn, double x, double y, double z) {
    return new WebBallEntity(worldIn, x, y, z);
  }
  
  public static WebBallEntity create(World worldIn, LivingEntity thrower) {
    return new WebBallEntity(worldIn, thrower);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.dataManager.register(TYPE, Byte.valueOf(WEB));
  }

  @Override
  protected Item getDefaultItem() {
    return GFRegistry.WEB_BALL;
  }

  @Override
  protected void onEntityHit(EntityRayTraceResult raytrace) {
    super.onEntityHit(raytrace);
    if (!this.world.isRemote() && world instanceof IServerWorld && this.isAlive() && raytrace.getEntity() != null) {
      onWebImpact(raytrace, raytrace.getEntity().getPosition());
      remove();
    }
  }

  @Override
  protected void onImpact(RayTraceResult raytrace) {
    super.onImpact(raytrace);
    if (!this.world.isRemote() && world instanceof IServerWorld && this.isAlive()) {
      onWebImpact(raytrace, getPosition());
      remove();
    }
  }

  @Override
  public void tick() {
    Entity entity = getShooter();
    if (entity instanceof net.minecraft.entity.player.PlayerEntity && !entity.isAlive()) {
      remove();
    } else {
      super.tick();
    }
  }

  @Override
  public Entity changeDimension(ServerWorld serverWorld, ITeleporter iTeleporter) {
    Entity entity = getShooter();
    if (entity != null && entity.world.getDimensionKey() != serverWorld.getDimensionKey()) {
      setShooter((Entity) null);
    }
    return super.changeDimension(serverWorld, iTeleporter);
  }
  
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  protected float getGravityVelocity() {
    return 0.08F;
  }
  
  @Override
  public void readAdditional(CompoundNBT tag) {
    super.readAdditional(tag);
    setWebType(tag.getByte(KEY_TYPE));
  }

  @Override
  public void writeAdditional(CompoundNBT tag) {
    super.writeAdditional(tag);
    tag.putByte(KEY_TYPE, getWebType());
  }
  
  private void onWebImpact(final RayTraceResult raytrace, final BlockPos webPos) {
    final byte type = getWebType();
    // nothing (drop string)
    if(type == 0) {
      entityDropItem(new ItemStack(Items.STRING));
      return;
    }
    // web
    if(hasWeb(type) && world.isAirBlock(webPos)) {
      world.setBlockState(webPos, Blocks.COBWEB.getDefaultState());
    }
    // spider
    if(hasSpider(type)) {
      BabySpiderEntity spider = GFRegistry.BABY_SPIDER_ENTITY.create(world);
      spider.copyLocationAndAnglesFrom(this);
      spider.setHomePosAndDistance(webPos, 12);
      world.addEntity(spider);
    }
    // item
    if(hasItem(type)) {
      ResourceLocation resourcelocation = getType().getLootTable();
      LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
      LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world))
          .withRandom(this.rand)
          .withParameter(LootParameters.THIS_ENTITY, this)
          .withParameter(LootParameters.ORIGIN, this.getPositionVec())
          .withParameter(LootParameters.DAMAGE_SOURCE, DamageSource.FALL)
          .withNullableParameter(LootParameters.KILLER_ENTITY, getShooter())
          .withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, null);
      LootContext ctx = lootcontext$builder.build(LootParameterSets.ENTITY);
      loottable.generate(ctx).forEach(this::entityDropItem);
    }
  }
  
  public void setWebType(final byte b) {
    getDataManager().set(TYPE, Byte.valueOf(b));
  }
  
  public byte getWebType() {
    return getDataManager().get(TYPE).byteValue();
  }
  
  public byte setWebType(final boolean web, final boolean spider, final boolean item) {
    // determine type bits
    byte type = 0;
    if(web) type = (byte) (type + WEB);
    if(spider) type = (byte) (type + SPIDER);
    if(item) type = (byte) (type + ITEM);
    // actually set the web type
    setWebType(type);
    // return the web type
    return type;
  }
  
  public boolean hasWeb(final byte webType) { return (webType & WEB) != 0; }
  public boolean hasSpider(final byte webType) { return (webType & SPIDER) != 0; }
  public boolean hasItem(final byte webType) { return (webType & ITEM) != 0; }
}
