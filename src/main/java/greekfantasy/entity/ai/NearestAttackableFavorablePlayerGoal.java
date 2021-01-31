package greekfantasy.entity.ai;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor_effect.ConfiguredFavorRange;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;

public class NearestAttackableFavorablePlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {

  public NearestAttackableFavorablePlayerGoal(final MobEntity host) {
    super(host, PlayerEntity.class, 10, true, false, l -> {
      if(l instanceof PlayerEntity) {
        ConfiguredFavorRange range = GreekFantasy.PROXY.getFavorConfiguration().get(host.getType());
        return range.hasHostileRange() && range.getHostileRange().isInFavorRange((PlayerEntity)l);
      }
      return false;
    });
  }

}
