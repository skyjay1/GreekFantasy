package greekfantasy.entity.ai;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.player.PlayerEntity;

public class FleeFromFavorablePlayerGoal extends AvoidEntityGoal<PlayerEntity> {
  
 public FleeFromFavorablePlayerGoal(final CreatureEntity entityIn) { this(entityIn, 8.0F); }
  
  public FleeFromFavorablePlayerGoal(final CreatureEntity entityIn, float distanceIn) {
    super(entityIn, PlayerEntity.class, distanceIn, 1.2D, 1.0D, 
        e -> entityIn.isAlive() && e instanceof PlayerEntity && e != entityIn.getRevengeTarget()
        && e.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()).isEnabled() 
        && GreekFantasy.PROXY.getFavorConfiguration().getEntity(entityIn.getType()).getFleeRange().isInFavorRange((PlayerEntity)e));
  }
}

