package greekfantasy.deity.favor_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.Deity;
import greekfantasy.deity.IDeity;
import greekfantasy.deity.favor.IFavor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class FavorRange {

  public static final FavorRange EMPTY = new FavorRange(new ResourceLocation(GreekFantasy.MODID, "null"), 0, 0);
  
  public static final Codec<FavorRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("deity").forGetter(FavorRange::getDeityName),
      Codec.INT.fieldOf("minlevel").forGetter(FavorRange::getMinLevel),
      Codec.INT.fieldOf("maxlevel").forGetter(FavorRange::getMaxLevel)
    ).apply(instance, FavorRange::new));
  
  private final ResourceLocation deity;
  private final int minLevel;
  private final int maxLevel;
  
  public FavorRange(ResourceLocation deityIn, int minLevelIn, int maxLevelIn) {
    super();
    this.deity = deityIn;
    this.minLevel = Math.min(minLevelIn, maxLevelIn);
    this.maxLevel = Math.max(minLevelIn, maxLevelIn);
  }

  /** @return the IDeity for this favor range **/
  public IDeity getDeity() { return GreekFantasy.PROXY.DEITY.get(getDeityName()).orElse(Deity.EMPTY); }
  /** @return the IDeity for this favor range **/
  public ResourceLocation getDeityName() { return deity; }
  /** @return the minimum favor level **/
  public int getMinLevel() { return minLevel; }
  /** @return the maximum favor level **/
  public int getMaxLevel() {  return maxLevel; }
  
  /**
   * @param player the player
   * @return true if this is a server world and the player matches this favor range
   */
  public boolean isInFavorRange(final PlayerEntity player) {
    if(player.isServerWorld() && player.getCapability(GreekFantasy.FAVOR).isPresent() && this != FavorRange.EMPTY) {
      return isInFavorRange(player, player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()));
    }
    return false;
  }
  
  /**
   * @param player the player
   * @param f the player's favor 
   * @return true if the player's favor matches this favor range
   */
  public boolean isInFavorRange(final PlayerEntity player, final IFavor f) {
    if(this == EMPTY) {
      return false;
    }
    final int playerLevel = f.getFavor(getDeity()).getLevel();
    if(maxLevel > minLevel) {
      return playerLevel <= maxLevel && playerLevel >= minLevel;
    } else {
      return playerLevel <= minLevel && playerLevel >= maxLevel;
    }
  }
  
  @Override
  public String toString() {
    return "Favor Range: " + deity.toString() + " [" + minLevel + "," + maxLevel + "]";
  }
}
