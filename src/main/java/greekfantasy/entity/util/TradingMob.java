package greekfantasy.entity.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public interface TradingMob {

    /**
     * @return the player who is trading, or null if the mob is not trading
     */
    @Nullable
    Player getTradingPlayer();

    /**
     * @param player the player to begin trading
     */
    void setTradingPlayer(@Nullable Player player);

    /**
     * @param self the trading mob
     * @param player the player
     * @return true if the given player is allowed to trade with this entity
     */
    default boolean canPlayerTrade(final PathfinderMob self, final Player player) {
        return player != null && player != self.getTarget();
    }

    /**
     * @return true if the trading player exists
     */
    default boolean isTrading() {
        return this.getTradingPlayer() != null;
    }

    /**
     * @return an Item Tag of items to accept from the player while trading
     **/
    TagKey<Item> getTradeTag();

    /**
     * @return the ID of a loot table for trade results
     */
    ResourceLocation getTradeLootTable();

    /**
     * @return true if angry particles should appear when a trade fails
     */
    default boolean sendAngryParticlesOnFail() {
        return true;
    }

    /**
     * Creates a list of trade results by querying the loot table
     * @param player the trading player
     * @param tradeItem the trade offering
     * @return a list of result items
     */
    default List<ItemStack> getTradeResult(final PathfinderMob self, @Nullable final Player player, final ItemStack tradeItem) {
        LootTable loottable = self.level.getServer().getLootTables().get(this.getTradeLootTable());
        return loottable.getRandomItems(new LootContext.Builder((ServerLevel) self.level)
                .withRandom(self.level.random)
                .withParameter(LootContextParams.THIS_ENTITY, self)
                .withParameter(LootContextParams.ORIGIN, self.position())
                .withParameter(LootContextParams.TOOL, tradeItem)
                .create(LootContextParamSets.PIGLIN_BARTER));
    }

    /**
     * Performs a trade by depleting the tradeItem and creating a resultItem
     *
     * @param self  the trading mob
     * @param player    the player, if any
     * @param tradeItem the item offered by the player
     */
    default void trade(final PathfinderMob self, @Nullable final Player player, final ItemStack tradeItem) {
        final Vec3 tradeTargetPos = getTradeTargetPosition(self, player);
        // determine list of trade results
        // drop trade results as item entities
        getTradeResult(self, player, tradeItem).forEach(item -> BehaviorUtils.throwItem(self, item, tradeTargetPos));
        // shrink/remove held item
        tradeItem.shrink(1);
        self.setItemInHand(InteractionHand.MAIN_HAND, tradeItem);
        if (tradeItem.getCount() <= 0) {
            this.setTradingPlayer(null);
        }
        // spawn xp orb
        if (player != null && self.getRandom().nextInt(3) == 0) {
            self.level.addFreshEntity(new ExperienceOrb(self.level, self.getX(), self.getY(), self.getZ(), 1 + self.getRandom().nextInt(2)));
        }
    }

    default InteractionResult startTrading(final PathfinderMob self, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // check if the tradingPlayer is holding a trade item and the entity is not already trading
        if (!isTrading() && !self.isAggressive() && self.getOffhandItem().isEmpty()
                && !stack.isEmpty() && stack.is(getTradeTag())) {
            // determine if player is eligible
            if(canPlayerTrade(self, player)) {
                // initiate trading
                this.setTradingPlayer(player);
                // take the item from the tradingPlayer
                self.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(stack.getItem()));
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                player.setItemInHand(hand, stack);
                return InteractionResult.SUCCESS;
            } else if (sendAngryParticlesOnFail() && self.level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, self.getX(), self.getEyeY(), self.getZ(), 4, 0, 0, 0, 0);
            }
        }

        return InteractionResult.PASS;
    }

    /**
     * Determines the landing zone for the trade results
     * @param self the trading mob
     * @param player the trading player, if any
     * @return the target position
     */
    default Vec3 getTradeTargetPosition(final PathfinderMob self, @Nullable final Player player) {
        Vec3 tradeTarget;
        if (player != null) {
            tradeTarget = player.getEyePosition();
        } else {
            tradeTarget = LandRandomPos.getPos(self, 4, 2);
            if (null == tradeTarget) {
                tradeTarget = self.getEyePosition();
            }
        }
        return tradeTarget;
    }
}
