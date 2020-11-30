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
    return !entity.isNonBoss() || entity.hasNoGravity() || entity.getType() == GFRegistry.GIGANTE_ENTITY
        || entity.isSpectator()
        || (entity instanceof PlayerEntity && ((PlayerEntity)entity).isCreative());
  }
  
  private void useSmashAttack(final LivingEntity user, final Entity target, final int level) {
    // if entitiy is touching the ground, knock it into the air and apply stun
    if(target.isOnGround() && !isExemptFromSmashAttack(target)) {
      target.addVelocity(0.0D, 0.35D + (0.05D * level), 0.0D);
      target.attackEntityFrom(DamageSource.causeMobDamage(user), 0.25F);
      // stun effect (for living entities)
      if(target instanceof LivingEntity) {
        final LivingEntity entity = (LivingEntity)target;
        final int STUN_DURATION = 40 + level * 20;
        if(GreekFantasy.CONFIG.isStunningNerf()) {
          entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, STUN_DURATION, 0));
          entity.addPotionEffect(new EffectInstance(Effects.WEAKNESS, STUN_DURATION, 0));
        } else {
          entity.addPotionEffect(new EffectInstance(GFRegistry.STUNNED_EFFECT, STUN_DURATION, 0));
        }
      }
    }
  }
  
  @Override
  public void onEntityDamaged(LivingEntity user, Entity target, int level) {
    // check for cooldown
    final ItemStack item = user.getHeldItem(Hand.MAIN_HAND);
    if(!(item.getItem() instanceof ClubItem)) {
      return;
    }
    // get a bounding box of an area to affect
    final double range = BASE_RANGE + 1.5D * level;
    final Vector3d facing = Vector3d.fromPitchYaw(user.getPitchYaw()).normalize();
    final AxisAlignedBB aabb = new AxisAlignedBB(target.getPosition().up()).grow(range, BASE_RANGE, range).offset(facing.scale(range));
    // smash attack entities within range
    user.getEntityWorld().getEntitiesWithinAABBExcludingEntity(user, aabb)
      .forEach(e -> useSmashAttack(user, e, level));
  }
  
  @Override 
  public int getMinEnchantability(int level) { return 999; }
  @Override
  public int getMaxEnchantability(int level) { return 999; }
  @Override
  public boolean isTreasureEnchantment() { return false; }
  @Override
  public boolean canVillagerTrade() { return false; }
  @Override
  public boolean canGenerateInLoot() { return false; }
  @Override
  public int getMaxLevel() { return 3; }
  @Override
  public boolean canApplyAtEnchantingTable(ItemStack stack) { return false; }
  @Override
  public boolean isAllowedOnBooks() { return false; }
}
