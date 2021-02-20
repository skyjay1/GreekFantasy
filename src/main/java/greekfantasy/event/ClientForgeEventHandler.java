package greekfantasy.event;

import com.mojang.blaze3d.matrix.MatrixStack;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.PlayerSkyjayRenderer;
import greekfantasy.client.render.SwineRenderer;
import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.tileentity.VaseTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ClientForgeEventHandler {
  
  private static SwineRenderer<LivingEntity> pigRenderer;
  private static PlayerSkyjayRenderer<PlayerEntity> skyjayRenderer;
  
  /**
   * Used to render players as pigs when under the Swine effect
   * @param event the RenderLivingEvent (Pre)
   **/
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void renderLiving(final RenderLivingEvent.Pre<LivingEntity, ?> event) {
    if(isSwine(event.getEntity())) {
      event.setCanceled(true);
      // render pig instead
      if(null == pigRenderer) {
        Minecraft mc = Minecraft.getInstance();
        pigRenderer = new SwineRenderer<LivingEntity>(mc.getRenderManager());
      }
      pigRenderer.render(event.getEntity(), event.getEntity().rotationYaw, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), 
          pigRenderer.getPackedLight(event.getEntity(), event.getPartialRenderTick()));
    }
  }
    
  /**
   * Used to hide the player and their armor / held items
   * while using the Helm of Darkness.
   * @param event the RenderPlayerEvent (Pre)
   **/
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void renderPlayer(final RenderPlayerEvent.Pre event) {
    if(GreekFantasy.CONFIG.doesHelmHideArmor() && hasHelmOfDarkness(event.getPlayer())) {
      event.setCanceled(true);
    }
    final ItemStack item = event.getEntityLiving().getHeldItem(Hand.MAIN_HAND);
    if(!event.isCanceled() && !item.isEmpty() && item.hasDisplayName() && "skyjay1".equals(item.getDisplayName().getUnformattedComponentText())) {
      if(null == skyjayRenderer) {
        Minecraft mc = Minecraft.getInstance();
        skyjayRenderer = new PlayerSkyjayRenderer<PlayerEntity>(mc.getRenderManager());
      }
      skyjayRenderer.render(event.getPlayer(), event.getPlayer().rotationYaw, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), 15728880);
    }
  }
  
  /**
   * Used to hide the first-person view of held items
   * while a player is using the Helm of Darkness.
   * @param event the RenderHandEvent
   **/
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void renderPlayerHand(final RenderHandEvent event) {
    final Minecraft mc = Minecraft.getInstance();
    if((GreekFantasy.CONFIG.doesHelmHideArmor() && hasHelmOfDarkness(mc.player)) 
        || (isSwine(mc.player) && mc.player.getHeldItemMainhand().isEmpty())) {
      event.setCanceled(true);
    }
  }
    
  /**
   * This method handles when the player is wearing the winged sandals item.
   * It's a little buggy because the stepHeight applied here doesn't seem to persist
   * more than a tick, but it works and hopefully doesn't break things too much.
   * @param event the player tick event (only handles TickEvent.Phase.START)
   **/
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void onPlayerTick(final PlayerTickEvent event) {
    if(event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT && GreekFantasy.CONFIG.isOverstepEnabled() 
        && event.player instanceof ClientPlayerEntity) {
      final ClientPlayerEntity player = (ClientPlayerEntity)event.player;
      final Minecraft mc = Minecraft.getInstance();
      final boolean hasOverstep = hasOverstep(player);
      // apply step height changes      
      if(hasOverstep && !player.isSneaking() && (player.stepHeight < 1.0F || player.isAutoJumpEnabled())) {
        player.stepHeight = 1.25F;
        // use Access Transformers to use/modify this field directly
        player.autoJumpEnabled = false;
      } else if(player.stepHeight > 1.2F) {
        // restore defaults
        player.stepHeight = 0.6F;
        player.autoJumpEnabled = mc.gameSettings.autoJump;
      }
    }
  }
  
  /**
   * Prevents the screen from "zooming in" when negative move-speed modifiers
   * are applied, but only if the player has the Stunned or Petrified effect.
   * @param event the FOVModifier event
   **/
  @SubscribeEvent
  public static void modifyFOV(final FOVModifier event) {
    final Minecraft mc = Minecraft.getInstance();
    if(mc != null && GreekFantasy.CONFIG.isForceFOVReset()) {
      final PlayerEntity player = mc.player;
      if(player.isAlive() && (isStunned(player) || player.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() == GFRegistry.WINGED_SANDALS)) {
        event.setFOV(mc.gameSettings.fov);
      }
    }
  }
  
//  // The rendering code in renderName was not working, so this is commented out for now
//  @SubscribeEvent
//  public static void renderSelectedBlock(final DrawHighlightEvent.HighlightBlock event) {
//    final Minecraft mc = Minecraft.getInstance();
//    if(mc != null && mc.world != null) {
//      final TileEntity te = mc.world.getTileEntity(event.getTarget().getPos());
//      if(te instanceof VaseTileEntity) {
//        // try to render vase nameplate
//        renderVaseName((VaseTileEntity)te, event);
//      } else if (te instanceof StatueTileEntity) {
//        // try to render statue nameplate
//        renderStatueName((StatueTileEntity)te, event);
//      }
//    }
//  }
  
  private static void renderVaseName(final VaseTileEntity vase, final DrawHighlightEvent.HighlightBlock event) {
    final ItemStack item = vase.getStackInSlot(0);
    if(!item.isEmpty() && item.hasDisplayName()) {
      renderName(vase, item.getDisplayName(), 0.75F, event.getMatrix(), event.getBuffers(), 15728640);
    }
  }
  
  private static void renderStatueName(final StatueTileEntity statue, final DrawHighlightEvent.HighlightBlock event) {
    if(statue.isUpper()) {
      ITextComponent name = null;
      if(statue.hasDeity()) {
        name = statue.getDeity().getText();
      } else if(!statue.getTextureName().isEmpty()) {
        name = new StringTextComponent(statue.getTextureName());
      }
      // the name exists, render here
      if(name != null) {
        renderName(statue, name, 2.25F, event.getMatrix(), event.getBuffers(), 15728640);
      }
    }
  }
  
  private static void renderName(TileEntity entityIn, ITextComponent displayNameIn, float offsetY, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    final Minecraft mc = Minecraft.getInstance();
    final EntityRendererManager renderManager = mc.getRenderManager();
    matrixStackIn.push();
    matrixStackIn.translate(0.0D, offsetY, 0.0D);
    matrixStackIn.rotate(renderManager.getCameraOrientation());
    matrixStackIn.scale(-0.025F, -0.025F, 0.025F);
    Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
    float f1 = mc.gameSettings.getTextBackgroundOpacity(0.25F);
    int j = (int) (f1 * 255.0F) << 24;
    FontRenderer fontrenderer = renderManager.getFontRenderer();
    float f2 = (float) (-fontrenderer.getStringPropertyWidth(displayNameIn) / 2);
    // actually render the nameplate
    fontrenderer.func_243247_a(displayNameIn, f2, 0, 553648127, false, matrix4f, bufferIn, true, j, packedLightIn);
    fontrenderer.func_243247_a(displayNameIn, f2, 0, -1, false, matrix4f, bufferIn, false, 0, packedLightIn);
    matrixStackIn.pop();
  }

  /** @return whether the player is wearing the Helm of Darkness **/
  private static boolean hasHelmOfDarkness(final PlayerEntity player) {
    return player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == GFRegistry.HELM_OF_DARKNESS;
  }
  
  /** @return whether the player should have the client-side overstep step-height logic applied **/
  private static boolean hasOverstep(final PlayerEntity player) {
    return EnchantmentHelper.getEnchantmentLevel(GFRegistry.OVERSTEP_ENCHANTMENT, player.getItemStackFromSlot(EquipmentSlotType.FEET)) > 0;
  }
  
  /** @return whether the entity should have the client-side stun/petrify FOV or particle effects **/
  private static boolean isStunned(final LivingEntity livingEntity) {
    return livingEntity.isPotionActive(GFRegistry.PETRIFIED_EFFECT) || livingEntity.isPotionActive(GFRegistry.STUNNED_EFFECT);
  }
  
  /** @return whether the entity should have the Swine effect applied **/
  private static boolean isSwine(final LivingEntity livingEntity) {
    return livingEntity.isPotionActive(GFRegistry.SWINE_EFFECT);
  }
}
