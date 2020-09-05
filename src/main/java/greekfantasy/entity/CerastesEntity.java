package greekfantasy.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CerastesEntity extends CreatureEntity {
  
  private static final byte STANDING_START = 4;
  private static final byte STANDING_END = 5;
  
  private int tongueTime;
  private float standingTime;
  
  private boolean isStanding;
  
  public CerastesEntity(final EntityType<? extends CerastesEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.28D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // tongue-flick counter
    if(this.tongueTime == 0) {
      if(!this.isStanding() && rand.nextInt(100) == 0) {
        tongueTime = 1;
      }
    } else if (++this.tongueTime > 16) {
      this.tongueTime = 0;
    }
    // standing counters
    if(this.isStanding()) {
      standingTime = Math.min(1.0F, standingTime + 0.1F);
    } else if(standingTime > 0.0F) {
      standingTime -= 0.1F;
    }
    
    // TEST
    if(this.isServerWorld() && this.ticksExisted % 300 == 0) {
      this.setStanding(!this.isStanding());
    } 
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case STANDING_START:
      this.isStanding = true;
      break;
    case STANDING_END:
      this.isStanding = false;
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  public void setStanding(final boolean standing) {
    this.isStanding = standing;
    this.world.setEntityState(this, standing ? STANDING_START : STANDING_END);
  }
  
  public boolean isStanding() {
    return isStanding;
  }
  
  public float getTongueTime() {
    return ((float) this.tongueTime) / 16.0F;
  }
  
  public float getStandingTime(final float partialTick) {
    // for some reason, lerp breaks things immeasurably
    // return partialTick == 1.0F ? standingTime : MathHelper.lerp(partialTick, prevStandingTime, standingTime);
    return standingTime;
  }
  
}
