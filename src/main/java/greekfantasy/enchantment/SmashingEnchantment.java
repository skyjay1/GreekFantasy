package greekfantasy.enchantment;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.ClubItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public class SmashingEnchantment extends Enchantment {

    private static final ResourceLocation CLUB = new ResourceLocation(GreekFantasy.MODID, "tools/club");
    private static final double BASE_RANGE = 2.0D;

    public SmashingEnchantment(final Enchantment.Rarity rarity) {
        super(rarity, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    /**
     * @param entity the entity to check
     * @return whether the given entity should not be affected by smash attack
     **/
    private static boolean isExemptFromSmashAttack(final Entity entity) {
        return !entity.canChangeDimensions() || entity.isNoGravity() // TODO || entity.getType() == GFRegistry.EntityReg.GIGANTE_ENTITY
                || entity.isSpectator()
                || (entity instanceof Player player && player.isCreative());
    }

    private static void useSmashAttack(final LivingEntity user, final Entity target, final int level) {
        // if entitiy is touching the ground, knock it into the air and apply stun
        if (target.isOnGround() && !isExemptFromSmashAttack(target)) {
            target.push(0.0D, 0.35D + (0.05D * level), 0.0D);
            target.hurt(DamageSource.mobAttack(user), 0.25F);
            // stun effect (for living entities)
            if (target instanceof LivingEntity) {
                final LivingEntity entity = (LivingEntity) target;
                final int duration = 40 + level * 20;
                if (GreekFantasy.CONFIG.SMASHING_NERF.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 0));
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 0));
                } else {
                    entity.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.STUNNED.get(), duration, 0));
                }
            }
        }
    }

    /*public static void apply(LivingEntity user, Entity target, int level, float attackStrength) {
        // check for club item
        final ItemStack item = user.getItemInHand(InteractionHand.MAIN_HAND);
        if (!item.is(ForgeRegistries.ITEMS.tags().createTagKey(CLUB))) {
            return;
        }
        // check for attack strength
        if(attackStrength < 0.9F) {
            return;
        }
        // get a bounding box of an area to affect
        final double range = BASE_RANGE + 1.5D * level;
        final Vec3 facing = Vec3.directionFromRotation(user.getRotationVector()).normalize();
        final AABB aabb = new AABB(target.blockPosition().above()).inflate(range, BASE_RANGE, range).move(facing.scale(range));
        // smash attack entities within range
        user.level.getEntities(user, aabb).forEach(e -> useSmashAttack(user, e, level));
    }*/

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        // check for club item
        final ItemStack item = user.getItemInHand(InteractionHand.MAIN_HAND);
        if (!item.is(ForgeRegistries.ITEMS.tags().createTagKey(CLUB))) {
            return;
        }
        // check for cooldown
        if(user instanceof Player player && player.getCooldowns().isOnCooldown(item.getItem())) {
            return;
        }
        // get a bounding box of an area to affect
        final double range = BASE_RANGE + 1.5D * level;
        final Vec3 facing = Vec3.directionFromRotation(user.getRotationVector()).normalize();
        final AABB aabb = new AABB(target.blockPosition().above()).inflate(range, BASE_RANGE, range).move(facing.scale(range));
        // smash attack entities within range
        user.level.getEntities(user, aabb).forEach(e -> useSmashAttack(user, e, level));
        // apply cooldown
        if(user instanceof Player player) {
            player.getCooldowns().addCooldown(item.getItem(), Mth.ceil(player.getCurrentItemAttackStrengthDelay()));
        }
    }

    @Override
    public int getMinCost(int level) {
        return 999;
    }

    @Override
    public int getMaxCost(int level) {
        return 999;
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.is(ForgeRegistries.ITEMS.tags().createTagKey(CLUB)) && super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}
