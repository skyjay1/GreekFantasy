package greekfantasy.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HydraEntity extends MonsterEntity {
  
  public List<HydraHeadEntity> hydraHeads = new ArrayList<>();
  
  public HydraEntity(final EntityType<? extends HydraEntity> type, final World worldIn) {
    super(type, worldIn);
    hydraHeads.add(new HydraHeadEntity(this, "head1"));
    hydraHeads.add(new HydraHeadEntity(this, "bipedHead2"));
    HydraHeadEntity head;
    for(int j = 0, l = hydraHeads.size(); j < l; j++) {
      head = hydraHeads.get(j);
      head.setPosition(this.getPosX(), this.getPosY(), this.getPosZ());
      worldIn.addEntity(head);
    }
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 204.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.24D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 6.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
//    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
//    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
//    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
//    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
//    this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setCallsForHelp());
//    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
  
    final int numParts = hydraHeads.size();
    HydraHeadEntity part;
    
    // get the positions for each part
    Vector3d[] partPrevPos = new Vector3d[numParts];
    for (int j = 0; j < numParts; ++j) {
      partPrevPos[j] = new Vector3d(hydraHeads.get(j).getPosX(), hydraHeads.get(j).getPosY(), hydraHeads.get(j).getPosZ());
    }
    // move each part
    updatePartPositions();    
    // update previous positions for each part
    Vector3d partPos;
    for(int l = 0; l < numParts; ++l) {
      part = hydraHeads.get(l);
      partPos = partPrevPos[l];
      part.prevPosX = partPos.x;
      part.prevPosY = partPos.y;
      part.prevPosZ = partPos.z;
      part.lastTickPosX = partPos.x;
      part.lastTickPosY = partPos.y;
      part.lastTickPosZ = partPos.z;
   }
  }
  
  // Parts //
  
  public List<HydraHeadEntity> getHydraHeads() {
    return hydraHeads;
  }
  
  public void updatePartPositions() {
    HydraHeadEntity part;
    if(hydraHeads != null) {
      for(int k = 0, l = hydraHeads.size(); k < l; ++k) {
        part = hydraHeads.get(k);
        setPartPosition(part, 0.5D * k, 0.6D, -1.0D);
      }
    }
  }
  
  private void setPartPosition(HydraHeadEntity part, double offsetX, double offsetY, double offsetZ) {
    part.setPosition(this.getPosX() + offsetX, this.getPosY() + offsetY, this.getPosZ() + offsetZ);
    part.velocityChanged = true;
  }
  
  public boolean attackEntityPartFrom(HydraHeadEntity part, DamageSource source, float damage) {
    return false; // TODO
  }
  
  @Override
  public void remove() {
    super.remove();
    for(final HydraHeadEntity e : hydraHeads) {
      e.remove();
    }
  }
  
  @Override
  public void setPosition(double x, double y, double z) {
    super.setPosition(x, y, z);
    updatePartPositions();
  }
  
  @Override
  public void setMotion(Vector3d motionIn) {
    super.setMotion(motionIn);
    //updatePartPositions();
  }
  
  @Override
  public void move(MoverType typeIn, Vector3d pos) {
    super.move(typeIn, pos);
    for(int k = 0, l = hydraHeads.size(); k < l; ++k) {
      hydraHeads.get(k).move(typeIn, pos);
    }
  }
  

  /**
   * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained by
   * a minecart, such as a command block).
   */
  @OnlyIn(Dist.CLIENT)
  public AxisAlignedBB getRenderBoundingBox() {
    float f = 1.0F;
    return super.getRenderBoundingBox().grow(f, f, f);
  }
  
  // Sounds //
  
  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.ENTITY_BLAZE_AMBIENT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_GENERIC_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_GENERIC_DEATH; }

  @Override
  protected float getSoundVolume() { return 1.2F; }
  
  // NBT methods //
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    
  }
}
