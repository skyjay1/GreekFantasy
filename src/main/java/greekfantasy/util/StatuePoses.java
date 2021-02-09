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
      .set(ModelPart.LEFT_ARM, 30, 0, -2.5F)
      .set(ModelPart.RIGHT_ARM, -30, 0, 2.5F)
      .set(ModelPart.LEFT_LEG, -20, 0, -2)
      .set(ModelPart.RIGHT_LEG, 20, 0, 2);
  
  public static final StatuePose STANDING_HOLDING = new StatuePose()
      .set(ModelPart.HEAD, 5, 0, 0)
      .set(ModelPart.LEFT_ARM, 0, 0, -2.5F)
      .set(ModelPart.RIGHT_ARM, -30, 0, 2.5F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose STANDING_HOLDING_DRAMATIC = new StatuePose()
      .set(ModelPart.HEAD, -5, 0, 0)
      .set(ModelPart.LEFT_ARM, 0, 0, -2.5F)
      .set(ModelPart.RIGHT_ARM, -90, 0, 2.5F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose STANDING_RAISED = new StatuePose()
      .set(ModelPart.HEAD, -20, 0, 0)
      .set(ModelPart.LEFT_ARM, 0, -90F, -130F)
      .set(ModelPart.RIGHT_ARM, 0, 90F, 130F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose WEEPING = new StatuePose()
      .set(ModelPart.HEAD, 12, 0, 0)
      .set(ModelPart.LEFT_ARM, -125F, 0, -45F)
      .set(ModelPart.RIGHT_ARM, -125F, 0, 45F)
      .set(ModelPart.LEFT_LEG, 4, 0, -2)
      .set(ModelPart.RIGHT_LEG, -4, 0, 2);
  
  public static final StatuePose DAB = new StatuePose()
      .set(ModelPart.HEAD, 38, 0, 0)
      .set(ModelPart.LEFT_ARM, -100F, 45F, 0)
      .set(ModelPart.RIGHT_ARM, -108F, 64F, 0)
      .set(ModelPart.LEFT_LEG, 0, 0, -4)
      .set(ModelPart.RIGHT_LEG, 0, 0, 4);
  
  // Array to hold all the poses that can be applied randomly
  public static final StatuePose[] POSES = new StatuePose[] {
      STANDING_HOLDING, WALKING, STANDING_HOLDING_DRAMATIC, STANDING_RAISED, WEEPING, DAB
  };
  
  // ALTAR POSES //
	  
  public static final StatuePose APHRODITE_POSE = new StatuePose()
      .set(ModelPart.HEAD, -5, 0, -20)
      .set(ModelPart.LEFT_ARM, 0, 0, -15)
      .set(ModelPart.RIGHT_ARM, -75, -12, 0)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose APOLLO_POSE = new StatuePose()
      .set(ModelPart.HEAD, -30, 23, 0)
      .set(ModelPart.LEFT_ARM, 3, 6, 0)
      .set(ModelPart.RIGHT_ARM, -96, 10, 0)
      .set(ModelPart.LEFT_LEG, 0, 0, -4)
      .set(ModelPart.RIGHT_LEG, 0, 0, 4);
  
  public static final StatuePose ARES_POSE = new StatuePose()
      .set(ModelPart.HEAD, -5, 0, 0)
      .set(ModelPart.LEFT_ARM, -73, 52, -17)
      .set(ModelPart.RIGHT_ARM, -90, -58, 35)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose ARTEMIS_POSE = new StatuePose()
      .set(ModelPart.HEAD, -10, 0, 0)
      .set(ModelPart.LEFT_ARM, 3, 6, 0)
      .set(ModelPart.RIGHT_ARM, -96, 10, 0)
      .set(ModelPart.LEFT_LEG, 0, 0, -4)
      .set(ModelPart.RIGHT_LEG, 0, 0, 4);
  
  public static final StatuePose ATHENA_POSE = new StatuePose()
      .set(ModelPart.HEAD, 10, 0, 0)
      .set(ModelPart.LEFT_ARM, -60, -18, -2)
      .set(ModelPart.RIGHT_ARM, -96, 0, 0)
      .set(ModelPart.LEFT_LEG, 0, 0, 0)
      .set(ModelPart.RIGHT_LEG, -10, 0, 0);
  
  public static final StatuePose HADES_POSE = new StatuePose()
      .set(ModelPart.HEAD, 10, 0, 0)
      .set(ModelPart.LEFT_ARM, -60, 0, -10)
      .set(ModelPart.RIGHT_ARM, -30, 0, 2)
      .set(ModelPart.LEFT_LEG, -20, 0, -2)
      .set(ModelPart.RIGHT_LEG, 20, 0, 2);
  
  public static final StatuePose HECATE_POSE = new StatuePose()
      .set(ModelPart.HEAD, 5, 0, 0)
      .set(ModelPart.LEFT_ARM, 100, -150, -2)
      .set(ModelPart.RIGHT_ARM, -30, 0, 2)
      .set(ModelPart.LEFT_LEG, -20, 0, -2)
      .set(ModelPart.RIGHT_LEG, 20, 0, 2);
  
  public static final StatuePose HEPHAESTUS_POSE = new StatuePose()
      .set(ModelPart.HEAD, 10, 0, 0)
      .set(ModelPart.LEFT_ARM, -64, -3, 0)
      .set(ModelPart.RIGHT_ARM, -90, -17, 90)
      .set(ModelPart.LEFT_LEG, 18, 0, 0)
      .set(ModelPart.RIGHT_LEG, -10, 0, 0);
  
  public static final StatuePose HERA_POSE = new StatuePose()
      .set(ModelPart.HEAD, 10, 0, 0)
      .set(ModelPart.LEFT_ARM, -58, 35, 0)
      .set(ModelPart.RIGHT_ARM, -58, -35, 0)
      .set(ModelPart.LEFT_LEG, 0, 0, 0)
      .set(ModelPart.RIGHT_LEG, 0, 0, 0);
  
  public static final StatuePose HERMES_POSE = new StatuePose()
      .set(ModelPart.HEAD, 6, 0, 0)
      .set(ModelPart.LEFT_ARM, 0, 0, -2)
      .set(ModelPart.RIGHT_ARM, -70, 2, 0)
      .set(ModelPart.LEFT_LEG, 18, 0, 0)
      .set(ModelPart.RIGHT_LEG, -10, 0, 0);
  
  public static final StatuePose PERSEPHONE_POSE = new StatuePose()
      .set(ModelPart.HEAD, 5, 0, 0)
      .set(ModelPart.LEFT_ARM, -84, -29, 17)
      .set(ModelPart.RIGHT_ARM, -67, 23, 2)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose POSEIDON_POSE = new StatuePose()
      .set(ModelPart.HEAD, 10, 0, 0)
      .set(ModelPart.LEFT_ARM, 0, 0, -2)
      .set(ModelPart.RIGHT_ARM, -112, 0, 2)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose ZEUS_POSE = new StatuePose()
      .set(ModelPart.HEAD, 10, 0, 0)
      .set(ModelPart.LEFT_ARM, -12, 0F, -5F)
      .set(ModelPart.RIGHT_ARM, -150, 0F, -10F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);

  /**
   * @param rand a Random generator
   * @return a preset pose that is not 'NONE'
   **/
  public static final StatuePose getRandomPose(final Random rand) {
    return POSES[rand.nextInt(POSES.length)];
  }
}