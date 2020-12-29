package greekfantasy.favor;

import net.minecraft.util.math.MathHelper;

public class FavorLevel {
  
  public static final int MAX_LEVEL = 10;
  public static final long MAX_FAVOR = calculateFavor(MAX_LEVEL);
    
  private long favor;
  private int level;
  
  public FavorLevel() { }
  
  public FavorLevel(final long f) { 
    setFavor(f);
  }
  
  public long getFavor() { return favor; }
  
  public void addFavor(long toAdd) { setFavor(favor + toAdd); }

  public void setFavor(long favorIn) {
    if(favor != favorIn) {
      this.favor = MathHelper.clamp(favorIn, -MAX_FAVOR, MAX_FAVOR);
      this.level = calculateLevel();
    }
  }

  public int getLevel() { return level; }
  
  public int calculateLevel() {
    // calculate the current level based on favor
    // TODO math
    final long f = Math.abs(favor);
    final int sig = (int)Math.signum(favor + 1);
    return MathHelper.clamp(sig * Math.floorDiv(-100 + (int)Math.sqrt(10000 + 40 * f), 20), -MAX_LEVEL, MAX_LEVEL);
//    final long aFavor = Math.abs(favor);
//    final int sig = (int) Math.signum(favor);
//    if(aFavor < 100) return 0;
//    else if(aFavor < 225) return sig * 1;
//    else if(aFavor < 375) return sig * 2;
//    else if(aFavor < 550) return sig * 3;
//    else if(aFavor < 750) return sig * 4;
//    else if(aFavor < 975) return sig * 5;
//    else if(aFavor < 1225) return sig * 6;
//    else if(aFavor < 1500) return sig * 7;
//    else if(aFavor < 1800) return sig * 8;
//    else if (aFavor < 2125) return sig * 9;
//    else return sig * 10;
  }
  
  public long getFavorToNextLevel() {
    return calculateFavor(getLevel());
  }
  
  /** @return the percent of favor that has been earned (always positive) **/
  public double getPercentFavor() {
    return Math.abs((double)favor / (double)MAX_FAVOR);
  }
  
  /** @return the maximum amount of favor for a given level **/
  public static long calculateFavor(final int lv) {
    final int l = Math.abs(lv);
    final int sig = (int)Math.signum(lv);
    return l >= MAX_LEVEL ? 0 : sig * (10 * l * (l + 10 * sig));
  }
  
  public int compareToAbs(FavorLevel other) {
    return (int) (Math.abs(this.getFavor()) - Math.abs(other.getFavor()));
  }
}
