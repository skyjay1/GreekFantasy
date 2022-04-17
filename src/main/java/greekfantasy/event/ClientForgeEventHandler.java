package greekfantasy.event;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.EntityAsPigRenderer;
import greekfantasy.entity.PegasusEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ClientForgeEventHandler {

    private static EntityAsPigRenderer<LivingEntity> pigRenderer;

    private static boolean wasJumping;

    /**
     * Used to render players as pigs when under the Swine effect
     *
     * @param event the RenderLivingEvent (Pre)
     **/
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderLiving(final RenderLivingEvent.Pre<LivingEntity, ?> event) {
        // swine
        if (isSwine(event.getEntity())) {
            event.setCanceled(true);
            // render pig instead
            if (null == pigRenderer) {
                Minecraft mc = Minecraft.getInstance();
                pigRenderer = new EntityAsPigRenderer<LivingEntity>(mc.getEntityRenderDispatcher());
            }
            pigRenderer.render(event.getEntity(), event.getEntity().yRot, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(),
                    pigRenderer.getPackedLightCoords(event.getEntity(), event.getPartialRenderTick()));
        }
    }

    /**
     * Used to hide the player and their armor / held items
     * while using the Helm of Darkness.
     *
     * @param event the RenderPlayerEvent (Pre)
     **/
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderPlayer(final RenderPlayerEvent.Pre event) {
        if (GreekFantasy.CONFIG.doesHelmHideArmor() && hasHelmOfDarkness(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    /**
     * Used to hide the first-person view of held items
     * while a player is using the Helm of Darkness.
     *
     * @param event the RenderHandEvent
     **/
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderPlayerHand(final RenderHandEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        if ((GreekFantasy.CONFIG.doesHelmHideArmor() && hasHelmOfDarkness(mc.player))
                || (isSwine(mc.player) && mc.player.getMainHandItem().isEmpty())) {
            event.setCanceled(true);
        }
    }

    /**
     * This method handles when the player is wearing the winged sandals item.
     * It's a little buggy because the stepHeight applied here doesn't seem to persist
     * more than a tick, but it works and hopefully doesn't break things too much.
     *
     * @param event the player tick event (only handles TickEvent.Phase.START)
     **/
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerTick(final PlayerTickEvent event) {
        // winged sandals logic
        if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT && GreekFantasy.CONFIG.isOverstepEnabled()
                && event.player instanceof ClientPlayerEntity) {
            final ClientPlayerEntity player = (ClientPlayerEntity) event.player;
            final Minecraft mc = Minecraft.getInstance();
            final boolean hasOverstep = hasOverstep(player);
            // apply step height changes
            if (hasOverstep && !player.isShiftKeyDown() && (player.maxUpStep < 1.0F || player.isAutoJumpEnabled())) {
                player.maxUpStep = 1.25F;
                // use Access Transformers to use/modify this field directly
                player.autoJumpEnabled = false;
            } else if (player.maxUpStep > 1.2F) {
                // restore defaults
                player.maxUpStep = 0.6F;
                player.autoJumpEnabled = mc.options.autoJump;
            }
        }
    }

    /**
     * Used to handle jumping when the player is riding a pegasus
     *
     * @param event the client tick event
     */
    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            final Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.isRidingJumpable() && mc.player.getVehicle() instanceof PegasusEntity) {
                mc.player.jumpRidingTicks = -10;
                if (mc.player.input.jumping && !wasJumping) {
                    // if starting to jump, set flag
                    wasJumping = true;
                } else if (!mc.player.input.jumping && wasJumping) {
                    // if not jumping but was previously, send jump packet
                    wasJumping = false;
                    ((PegasusEntity) mc.player.getVehicle()).flyingJump();
                }
            }
        }
    }

    /**
     * Prevents the screen from "zooming in" when negative move-speed modifiers
     * are applied, but only if the player has the Stunned or Petrified effect.
     *
     * @param event the FOVModifier event
     **/
    @SubscribeEvent
    public static void modifyFOV(final FOVModifier event) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc != null && GreekFantasy.CONFIG.isForceFOVReset()) {
            final PlayerEntity player = mc.player;
            if (player.isAlive() && (isStunned(player) || player.getItemBySlot(EquipmentSlotType.FEET).getItem() == GFRegistry.WINGED_SANDALS)) {
                event.setFOV(mc.options.fov);
            }
        }
    }

    /**
     * @return whether the player is wearing the Helm of Darkness
     **/
    private static boolean hasHelmOfDarkness(final PlayerEntity player) {
        return player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == GFRegistry.HELM_OF_DARKNESS;
    }

    /**
     * @return whether the player should have the client-side overstep step-height logic applied
     **/
    private static boolean hasOverstep(final PlayerEntity player) {
        return EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.OVERSTEP_ENCHANTMENT, player.getItemBySlot(EquipmentSlotType.FEET)) > 0;
    }

    /**
     * @return whether the player should have the client-side silkwalker step-height logic applied
     **/
    private static boolean hasSilkstep(final PlayerEntity player) {
        return EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.SILKSTEP_ENCHANTMENT, player.getItemBySlot(EquipmentSlotType.FEET)) > 0;
    }

    /**
     * @return whether the entity should have the client-side stun/petrify FOV or particle effects
     **/
    private static boolean isStunned(final LivingEntity livingEntity) {
        return livingEntity.hasEffect(GFRegistry.PETRIFIED_EFFECT) || livingEntity.hasEffect(GFRegistry.STUNNED_EFFECT);
    }

    /**
     * @return whether the entity should have the Swine effect applied
     **/
    private static boolean isSwine(final LivingEntity livingEntity) {
        return livingEntity.hasEffect(GFRegistry.PIG_EFFECT);
    }
}
