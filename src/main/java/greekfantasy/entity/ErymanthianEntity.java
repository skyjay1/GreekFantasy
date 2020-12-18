package greekfantasy.entity;

import java.util.EnumSet;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

public class ErymanthianEntity extends HoglinEntity {
  
  private static final DataParameter<Boolean> SPAWNING = EntityDataManager.createKey(ErymanthianEntity.class, DataSerializers.BOOLEAN);
  private static final String KEY_SPAWNING = "Spawning";
  private static final String KEY_SPAWN_TIME = "SpawnTime";

  
  private static final int MAX_SPAWN_TIME = 90;

  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS));
  
  private int spawnTime;
  
  public ErymanthianEntity(final EntityType<? extends ErymanthianEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 50;
    
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MonsterEntity.func_234295_eP_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 80.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.31D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.82D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0D);
  }
  
  public static ErymanthianEntity spawnErymanthian(final ServerWorld world, final HoglinEntity hoglin) {
    ErymanthianEntity entity = GFRegistry.ERYMANTHIAN_ENTITY.create(world);
    entity.copyLocationAndAnglesFrom(hoglin);
    entity.onInitialSpawn(world, world.getDifficultyForLocation(hoglin.getPosition()), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
    if(hoglin.hasCustomName()) {
      entity.setCustomName(hoglin.getCustomName());
      entity.setCustomNameVisible(hoglin.isCustomNameVisible());
    }
    entity.enablePersistence();
    entity.renderYawOffset = hoglin.renderYawOffset;
    world.addEntity(entity);
    // remove the old hoglin
    hoglin.remove();
    // trigger spawn for nearby players
    for (ServerPlayerEntity player : world.getEntitiesWithinAABB(ServerPlayerEntity.class, entity.getBoundingBox().grow(25.0D))) {
      CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
    }
    // play sound
    world.playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ENTITY_WITHER_SPAWN, entity.getSoundCategory(), 1.2F, 1.0F, false);
    return entity;
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(SPAWNING, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new ErymanthianEntity.SpawningGoal());
  }
  
  @Override
  public void livingTick() {
    super.livingTick();

    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    
    if(spawnTime > 0 && spawnTime++ >= MAX_SPAWN_TIME) {
      setSpawning(false);
    }
  }
  
  // Hoglin overrides //

  @Override
  public boolean isBreedingItem(ItemStack stack) {
    return false;
  }
  
  @Override
  public boolean canFallInLove() {
    return false;
  }
  
  @Override
  public boolean func_234365_eM_() { // canBeHunted
    return false;
  }
  
  @Override
  public boolean func_234364_eK_() { // canBeZombified
    return false;
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
     ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
     this.func_234370_t_(true); // set IsImmuneToZombification
     this.setChild(false);
     this.setSpawning(true);
     return data;
  }
  
  @Override
  protected void onGrowingAdult() {
    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0D);
  }
  
  @Override
  protected float getSoundVolume() { return 1.5F; }
  
  @Override
  protected float getSoundPitch() { return 0.54F + rand.nextFloat() * 0.24F; }

  // NBT methods //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putBoolean(KEY_SPAWNING, isSpawning());
    compound.putInt(KEY_SPAWN_TIME, spawnTime);
    
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    setSpawning(compound.getBoolean(KEY_SPAWNING));
    spawnTime = compound.getInt(KEY_SPAWN_TIME);
  }
  
  // Prevent entity collisions //
  
  @Override
  public boolean canBePushed() { return false; }
  
  @Override
  protected void collideWithNearbyEntities() { }
  
  // Boss //

  @Override
  public void addTrackingPlayer(ServerPlayerEntity player) {
    super.addTrackingPlayer(player);
    this.bossInfo.addPlayer(player);
  }

  @Override
  public void removeTrackingPlayer(ServerPlayerEntity player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }
  
  // Spawning //
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if(key == SPAWNING) {
      spawnTime = isSpawning() ? 1 : 0;
    }
  }
  
  public boolean isSpawning() { return spawnTime > 0 || this.getDataManager().get(SPAWNING).booleanValue(); }
  
  public void setSpawning(final boolean spawning) {
    spawnTime = spawning ? 1 : 0;
    this.getDataManager().set(SPAWNING, spawning);
  }
  
  public float getSpawnPercent(final float partialTick) { 
    if(spawnTime <= 0) {
      return 1.0F;
    }
    final float prevSpawnPercent = Math.max((float)spawnTime - partialTick, 0.0F) / (float)MAX_SPAWN_TIME;
    final float spawnPercent = (float)spawnTime / (float)MAX_SPAWN_TIME;
    return MathHelper.lerp(partialTick / 8, prevSpawnPercent, spawnPercent); 
  }
  
  // Goals //
  
  class SpawningGoal extends Goal {
    
    public SpawningGoal() { setMutexFlags(EnumSet.allOf(Goal.Flag.class)); }

    @Override
    public boolean shouldExecute() { return ErymanthianEntity.this.isSpawning(); }

    @Override
    public void tick() { ErymanthianEntity.this.getNavigator().clearPath(); }
  }

}
