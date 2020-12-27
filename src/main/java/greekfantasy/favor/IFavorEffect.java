package greekfantasy.favor;

import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface IFavorEffect {
  long performEffect(final PlayerEntity player, final FavorInfo favor);
}
