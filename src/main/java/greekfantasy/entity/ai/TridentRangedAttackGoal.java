package greekfantasy.entity.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Items;

public class TridentRangedAttackGoal extends RangedAttackGoal {
    private final PathfinderMob entity;

    public TridentRangedAttackGoal(RangedAttackMob rangedAttackMob, double speedMultiplier, int duration, float range) {
        super(rangedAttackMob, speedMultiplier, duration, range);
        this.entity = (PathfinderMob) rangedAttackMob;
    }

    public boolean canUse() {
        return super.canUse() && this.entity.getMainHandItem().is(Items.TRIDENT);
    }

    public void start() {
        super.start();
        this.entity.setAggressive(true);
        this.entity.startUsingItem(InteractionHand.MAIN_HAND);
    }

    public void stop() {
        super.stop();
        this.entity.stopUsingItem();
        this.entity.setAggressive(false);
    }
}
