package greekfantasy.entity;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class DrakainaEntity extends MonsterEntity {
  
  private static final DataParameter<Byte> DATA_VARIANT = EntityDataManager.createKey(DrakainaEntity.class, DataSerializers.BYTE);
  private static final String KEY_VARIANT = "Variant";
  private static final String KEY_BVARIANT = "BVariant";
  
  public DrakainaEntity(final EntityType<? extends DrakainaEntity> type, final World worldIn) {
    super(type, worldIn);
    this.experienceValue = 10;
    this.setPathPriority(PathNodeType.WATER, -0.5F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D);
  }
 
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new RestrictSunGoal(this));
    this.goalSelector.addGoal(2, new FleeSunGoal(this, 1.0D));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_VARIANT, DrakainaEntity.Variant.GREEN.getId());
  }

  @Override
  public void livingTick() {
    if (this.isAlive()) {
      boolean flag = this.shouldBurnInDay() && this.isInDaylight();
      if (flag) {
        ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.HEAD);
        if (!itemstack.isEmpty()) {
          if (itemstack.isDamageable()) {
            itemstack.setDamage(itemstack.getDamage() + this.rand.nextInt(2));
            if (itemstack.getDamage() >= itemstack.getMaxDamage()) {
              this.sendBreakAnimation(EquipmentSlotType.HEAD);
              this.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
            }
          }

          flag = false;
        }

        if (flag) {
          this.setFire(8);
        }
      }
    }

    super.livingTick();
  }
  
  // Sounds //

  @Override
  protected SoundEvent getAmbientSound() {return SoundEvents.ENTITY_CAT_HISS; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_SPIDER_AMBIENT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_SPIDER_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.68F; }
  
  // MISC //
  
  @Override
  public ResourceLocation getLootTable() {
    return this.getVariant().getLootTable();
  }

  @Override
  public boolean isImmuneToFire() {
    return getVariant() == DrakainaEntity.Variant.RED || super.isImmuneToFire();
  }
  
  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn)) {
      // use special attack
      if(entityIn instanceof LivingEntity && GreekFantasy.CONFIG.DRAKAINA_ATTACK.get()) {
        final LivingEntity entity = (LivingEntity)entityIn;
        final DrakainaEntity.Variant variant = getVariant();
        switch(variant) {
        case RED: entity.setFire(2 + rand.nextInt(4)); break;
        case BROWN: entity.addPotionEffect(new EffectInstance(Effects.HUNGER, 20 * (2 + rand.nextInt(5)))); break;
        case GREEN: entity.addPotionEffect(new EffectInstance(Effects.POISON, 20 * (2 + rand.nextInt(5)))); break;
        }
      }
      // light target on fire if burning
      float f = this.world.getDifficultyForLocation(this.getPosition()).getAdditionalDifficulty();
      if (this.getHeldItemMainhand().isEmpty() && this.isBurning() && this.rand.nextFloat() < f * 0.3F) {
         entityIn.setFire(2 * (int)f);
      }
      return true;
    }
    return false;
  }
  
  protected boolean shouldBurnInDay() {
    return getVariant() != DrakainaEntity.Variant.RED;
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final DrakainaEntity.Variant variant;
    if(reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER || reason == SpawnReason.DISPENSER) {
      variant = DrakainaEntity.Variant.getRandom(worldIn.getRandom());
    } else {
      variant = DrakainaEntity.Variant.getForBiome(worldIn.func_242406_i(this.getPosition()));
    }
    this.setVariant(variant);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  // NBT and Variant //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(KEY_BVARIANT, getVariant().getId());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    if(compound.contains(KEY_VARIANT)) {
      setVariant(DrakainaEntity.Variant.getByName(compound.getString(KEY_VARIANT)));
    } else {
      setVariant(DrakainaEntity.Variant.getById(compound.getByte(KEY_BVARIANT)));
    }
  }
  
  public void setVariant(final DrakainaEntity.Variant variant) { this.getDataManager().set(DATA_VARIANT, variant.getId()); }

  public DrakainaEntity.Variant getVariant() { return DrakainaEntity.Variant.getById(this.getDataManager().get(DATA_VARIANT)); }
  
  public static enum Variant implements IStringSerializable {
    GREEN("green"),
    BROWN("brown"),
    RED("red");
    
    private final String name;
    private final ResourceLocation lootTable;

    private Variant(final String nameIn) {
      name = nameIn;
      lootTable = new ResourceLocation(GreekFantasy.MODID, "entities/drakaina/" + name);
    }

    public static Variant getForBiome(final Optional<RegistryKey<Biome>> biome) {
      if(biome.isPresent()) {
        if(BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.NETHER)) {
          return RED;
        }
        if(BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.SANDY)
            || BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.BEACH)
            || BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.DEAD)
            || BiomeDictionary.hasType(biome.get(), BiomeDictionary.Type.SAVANNA)) {
          return BROWN;
        }
      }
      // defaults to GREEN
      return GREEN;
    }
    
    public static Variant getRandom(final Random rand) {
      int len = values().length;
      return values()[rand.nextInt(len)];
    }

    public static Variant getByName(final String n) {
      // check the given name against all types
      if(n != null && !n.isEmpty()) {
        for(final Variant t : values()) {
          if(t.getString().equals(n)) {
            return t;
          }
        }
      }
      // defaults to OAK
      return GREEN;
    }
    
    public static Variant getById(final byte id) {
      return values()[MathHelper.clamp(id, 0, values().length - 1)];
    }
    
    public byte getId() {
      return (byte)this.ordinal();
    }
    
    public ResourceLocation getLootTable() {
      return lootTable;
    }
  
    @Override
    public String getString() {
      return name;
    }
  }
}
