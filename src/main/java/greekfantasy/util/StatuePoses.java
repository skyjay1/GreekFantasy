package greekfantasy.util;

import java.util.Random;

/**
 * This class holds several StatuePose presets
 **/
public final class StatuePoses {
  
  private StatuePoses() { }
  
  public static final StatuePose NONE = new StatuePose();
 
  public static final StatuePose WALKING = new StatuePose()
      .set(ModelPart.HEAD, 5, 0, 0)
      .set(ModelPart.RIGHT_ARM, -30, 0, 2.5F)
      .set(ModelPart.LEFT_ARM, 30, 0, -2.5F)
      .set(ModelPart.LEFT_LEG, -20, 0, -2)
      .set(ModelPart.RIGHT_LEG, 20, 0, 2);
  
  public static final StatuePose STANDING_HOLDING = new StatuePose()
      .set(ModelPart.HEAD, 5, 0, 0)
      .set(ModelPart.RIGHT_ARM, -30, 0, 2.5F)
      .set(ModelPart.LEFT_ARM, 0, 0, -2.5F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose STANDING_HOLDING_DRAMATIC = new StatuePose()
      .set(ModelPart.HEAD, -5, 0, 0)
      .set(ModelPart.RIGHT_ARM, -90, 0, 2.5F)
      .set(ModelPart.LEFT_ARM, 0, 0, -2.5F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose STANDING_RAISED = new StatuePose()
      .set(ModelPart.HEAD, -20, 0, 0)
      .set(ModelPart.RIGHT_ARM, 0, 90F, 130F)
      .set(ModelPart.LEFT_ARM, 0, -90F, -130F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose WEEPING = new StatuePose()
      .set(ModelPart.HEAD, 12, 0, 0)
      .set(ModelPart.RIGHT_ARM, -125F, 0, 45F)
      .set(ModelPart.LEFT_ARM, -125F, 0, -45F)
      .set(ModelPart.LEFT_LEG, 4, 0, -2)
      .set(ModelPart.RIGHT_LEG, -4, 0, 2);
  
  public static final StatuePose DAB = new StatuePose()
      .set(ModelPart.HEAD, 38, 0, 0)
      .set(ModelPart.RIGHT_ARM, -108F, 64F, 0)
      .set(ModelPart.LEFT_ARM, -100F, 45F, 0)
      .set(ModelPart.LEFT_LEG, 0, 0, -4)
      .set(ModelPart.RIGHT_LEG, 0, 0, 4);
  
  public static final StatuePose ZEUS_POSE = new StatuePose()
      .set(ModelPart.HEAD, 10, 0, 0)
      .set(ModelPart.RIGHT_ARM, -150, 0F, -10F)
      .set(ModelPart.LEFT_ARM, -12, 0F, -5F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  // Array to hold all the poses in this class
  public static final StatuePose[] POSES = new StatuePose[] {
      STANDING_HOLDING, WALKING, STANDING_HOLDING_DRAMATIC, STANDING_RAISED, WEEPING, DAB
  };

  /**
   * @param rand a Random generator
   * @return a preset pose that is not 'NONE'
   **/
  public static final StatuePose getRandomPose(final Random rand) {
    return POSES[rand.nextInt(POSES.length)];
  }
}