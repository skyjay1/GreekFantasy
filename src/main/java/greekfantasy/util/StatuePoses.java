package greekfantasy.util;

public class StatuePoses {
  
  private StatuePoses() { }
  
  // TODO make GUI so player can create their own poses
  
  public static final StatuePose NONE = new StatuePose("none");
 
  public static final StatuePose WALKING_HOLDING = new StatuePose("walking_holding")
      .set(ModelPart.HEAD, 5, 0, 0)
      .set(ModelPart.RIGHT_ARM, -30, 0, 2.5F)
      .set(ModelPart.LEFT_ARM, 0, 0, -2.5F)
      .set(ModelPart.LEFT_LEG, -20, 0, -2)
      .set(ModelPart.RIGHT_LEG, 20, 0, 2);
  
  public static final StatuePose STANDING_HOLDING = new StatuePose("standing_holding")
      .set(ModelPart.HEAD, 5, 0, 0)
      .set(ModelPart.RIGHT_ARM, -30, 0, 2.5F)
      .set(ModelPart.LEFT_ARM, 0, 0, -2.5F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose STANDING_HOLDING_DRAMATIC = new StatuePose("standing_holding_dramatic")
      .set(ModelPart.HEAD, -5, 0, 0)
      .set(ModelPart.RIGHT_ARM, -90, 0, 2.5F)
      .set(ModelPart.LEFT_ARM, 0, 0, -2.5F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose STANDING_RAISED = new StatuePose("standing_raised")
      .set(ModelPart.HEAD, -15, 0, 0)
      .set(ModelPart.RIGHT_ARM, 0, 0, 130F)
      .set(ModelPart.LEFT_ARM, 0, 0, -130F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose WEEPING = new StatuePose("weeping")
      .set(ModelPart.HEAD, 12, 0, 0)
      .set(ModelPart.RIGHT_ARM, -125F, 0, 45F)
      .set(ModelPart.LEFT_ARM, -125F, 0, -45F)
      .set(ModelPart.LEFT_LEG, 0, 0, -2)
      .set(ModelPart.RIGHT_LEG, 0, 0, 2);
  
  public static final StatuePose[] ALL_POSES = new StatuePose[] {
      NONE, STANDING_HOLDING, WALKING_HOLDING, STANDING_HOLDING_DRAMATIC, STANDING_RAISED, WEEPING
  };
}