package greekfantasy.entity.misc;

/**
 * Required to use the greekfantasy.entity.ai.SwimUpGoal
 **/
public interface ISwimmingMob {

    /**
     * @param swimmingUp whether the entity should be swimming up
     **/
    void setSwimmingUp(final boolean swimmingUp);

    /**
     * @return whether the entity 'swimming up' flag is true
     **/
    boolean isSwimmingUp();

    /**
     * @return whether the entity 'swimming up' flag is true OR it is targeting an entity in water
     **/
    boolean isSwimmingUpCalculated();
}
