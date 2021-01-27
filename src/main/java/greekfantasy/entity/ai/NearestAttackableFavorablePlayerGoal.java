package greekfantasy.entity.ai;

import greekfantasy.GreekFantasy;
import greekfantasy.favor.FavorRange;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;

public class NearestAttackableFavorablePlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {

  public NearestAttackableFavorablePlayerGoal(final MobEntity host) {
    super(host, PlayerEntity.class, 10, true, false, l -> {
      if(l instanceof PlayerEntity) {
        FavorRange range = GreekFantasy.PROXY.getFavorRangeTarget().get(host.getType());
        return range != FavorRange.EMPTY && range.isInFavorRange((PlayerEntity)l);
      }
      return false;
    });
  }

}
