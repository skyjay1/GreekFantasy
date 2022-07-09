package greekfantasy;

import greekfantasy.enchantment.SmashingEnchantment;
import greekfantasy.integration.RGCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public final class GFEvents {

    public static final class ForgeHandler {

        @SubscribeEvent
        public static void onAttackEntity(final AttackEntityEvent event) {
            // only handle event on server
            if(event.getEntityLiving().level.isClientSide()) {
                return;
            }
            // check if held item has Smashing enchantment
            ItemStack itemStack = event.getPlayer().getMainHandItem();
            int smashing = EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.SMASHING.get(), itemStack);
            if(smashing > 0) {
                // apply Smashing enchantment
                //SmashingEnchantment.apply(event.getPlayer(), event.getTarget(), smashing, event.getPlayer().getAttackStrengthScale(0.0F));
            }

        }

        @SubscribeEvent
        public static void onLivingTick(final LivingEvent.LivingUpdateEvent event) {
            // only handle event on server
            if(event.getEntityLiving().level.isClientSide()) {
                return;
            }
            // handle Prisoner of Hades mob effect
            final MobEffect prisonerOfHades = GFRegistry.MobEffectReg.PRISONER_OF_HADES.get();
            if(event.getEntityLiving().hasEffect(prisonerOfHades)) {
                // remove when not in nether
                if (event.getEntityLiving().level.dimension() != Level.NETHER
                        || (GreekFantasy.isRGLoaded() && event.getEntityLiving() instanceof Player player
                            && RGCompat.getInstance().canRemovePrisonerEffect(player))) {
                    GreekFantasy.LOGGER.debug("Removing effect from " + event.getEntityLiving());
                    event.getEntityLiving().removeEffect(prisonerOfHades);
                } else {
                    // set portal cooldown
                    event.getEntityLiving().setPortalCooldown();
                }
            }
        }
    }

    public static final class ModHandler {

    }
}
