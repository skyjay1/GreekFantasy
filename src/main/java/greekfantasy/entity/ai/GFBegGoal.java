package greekfantasy.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class GFBegGoal extends Goal {

    protected final Mob mob;
    protected final double range;
    protected final int interval;
    protected final Predicate<Entity> hasBegItem;
    @Nullable
    protected LivingEntity player;

    /**
     * @param mob the entity
     * @param range the range to check for entities holding beg items
     * @param interval the number of ticks between goal updates
     * @param isBegItem the predicate for items that trigger this goal
     */
    protected GFBegGoal(final Mob mob, final double range, final int interval, final Predicate<ItemStack> isBegItem) {
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.TARGET, Flag.MOVE));
        this.mob = mob;
        this.range = range;
        this.interval = interval;
        this.hasBegItem = e -> (e instanceof LivingEntity livingEntity &&
                (isBegItem.test(livingEntity.getMainHandItem()) || isBegItem.test(livingEntity.getOffhandItem())));
    }

    @Override
    public boolean canUse() {
        if (mob.tickCount % interval == 0) {
            // find a player within range to cause begging
            final List<Player> list = mob.level.getEntitiesOfClass(Player.class, mob.getBoundingBox().inflate(range), hasBegItem);
            if (!list.isEmpty()) {
                player = list.get(0);
            } else {
                player = null;
            }
        }
        return player != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        mob.getLookControl().setLookAt(player, mob.getMaxHeadYRot(), mob.getMaxHeadXRot());
        if(player == mob.getTarget()) {
            mob.setTarget(null);
        }
    }

}
