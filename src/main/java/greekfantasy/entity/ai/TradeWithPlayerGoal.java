package greekfantasy.entity.ai;

import greekfantasy.entity.util.TradingMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class TradeWithPlayerGoal<T extends PathfinderMob & TradingMob> extends Goal {

    protected final T entity;
    protected final int maxThinkingTime;
    protected int thinkingTime;

    public TradeWithPlayerGoal(final T entity, final int maxThinkingTimeIn) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.entity = entity;
        this.maxThinkingTime = maxThinkingTimeIn;
        this.thinkingTime = 0;
    }

    @Override
    public boolean canUse() {
        return !this.entity.isAggressive()
                && !this.entity.getOffhandItem().isEmpty()
                && this.entity.getOffhandItem().is(this.entity.getTradeTag());
    }

    @Override
    public boolean canContinueToUse() {
        return thinkingTime > 0 && thinkingTime <= maxThinkingTime && canUse();
    }

    @Override
    public void start() {
        thinkingTime = 1;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return thinkingTime > 0;
    }

    @Override
    public void tick() {
        // stop moving and look down
        this.entity.getNavigation().stop();
        this.entity.getLookControl().setLookAt(this.entity.getEyePosition(1.0F).add(0.0D, -0.25D, 0.0D));
        // if enough time has elapsed, commence the trade
        if (thinkingTime++ >= maxThinkingTime) {
            this.entity.trade(this.entity, this.entity.getTradingPlayer(), this.entity.getOffhandItem());
            stop();
        }
    }

    @Override
    public void stop() {
        thinkingTime = 0;
    }

}
