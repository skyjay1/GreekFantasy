package greekfantasy.enchantment;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.ClubItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class SmashingEnchantment extends Enchantment {
  
  private static final double BASE_RANGE = 2.0D;

  public SmashingEnchantment(final Enchantment.Rarity rarity) {
    super(rarity, EnchantmentType.WEAPON, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
  }
  
  /**
   * @param entity the entity to check
   * @return whether the given entity should not be affected by smash attack
   **/
  private boolean isExemptFromSmashAttack(final Entity entity) {
    return !entity.canChangeDimensions() || entity.isNoGravity() || entity.getType() == GFRegistry.GIGANTE_ENTITY
        || entity.isSpectator()
        || (entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative());
  }
  
  private void useSmashAttack(final LivingEntity user, final Entity target, final int level) {
    // if entitiy is touching the ground, knock it into the air and apply stun
    if(target.isOnGround() && !isExemptFromSmashAttack(target)) {
      target.push(0.0D, 0.35D + (0.05D * level), 0.0D);
      target.hurt(DamageSource.mobAttack(user), 0.25F);
      // stun effect (for living entities)
      if(target instanceof LivingEntity) {
        final LivingEntity entity = (LivingEntity)target;
        final int STUN_DURATION = 40 + level * 20;
        if(GreekFantasy.CONFIG.isStunningNerf()) {
          entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, STUN_DURATION, 0));
          entity.addEffect(new EffectInstance(Effects.WEAKNESS, STUN_DURATION, 0));
        } else {
          entity.addEffect(new EffectInstance(GFRegistry.STUNNED_EFFECT, STUN_DURATION, 0));
        }
      }
    }
  }
  
  @Override
  public void doPostAttack(LivingEntity user, Entity target, int level) {
    // check for cooldown
    final ItemStack item = user.getItemInHand(Hand.MAIN_HAND);
    if(!(item.getItem() instanceof ClubItem)) {
      return;
    }
    // get a bounding box of an area to affect
    final double range = BASE_RANGE + 1.5D * level;
    final Vector3d facing = Vector3d.directionFromRotation(user.getRotationVector()).normalize();
    final AxisAlignedBB aabb = new AxisAlignedBB(target.blockPosition().above()).inflate(range, BASE_RANGE, range).move(facing.scale(range));
    // smash attack entities within range
    user.getCommandSenderWorld().getEntities(user, aabb)
      .forEach(e -> useSmashAttack(user, e, level));
  }
  
  @Override 
  public int getMinCost(int level) { return 999; }
  @Override
  public int getMaxCost(int level) { return 999; }
  @Override
  public boolean isTreasureOnly() { return false; }
  @Override
  public boolean isTradeable() { return false; }
  @Override
  public boolean isDiscoverable() { return false; }
  @Override
  public int getMaxLevel() { return 3; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }
  @Override
  public boolean isAllowedOnBooks() { return false; }
}
