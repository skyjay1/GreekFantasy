package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.integration.RGCompat;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class ThunderboltItem extends Item {

    public ThunderboltItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // prevent the item from being used up all the way
        if (stack.getMaxDamage() - stack.getDamageValue() <= 1) {
            return ActionResult.fail(stack);
        }
        // check for config settings and/or rain
        if (GreekFantasy.CONFIG.isThunderboltStormsOnly() && !world.isRaining() && !world.isThundering()) {
            player.displayClientMessage(new TranslationTextComponent("message.thunderbolt_only_when_raining"), true);
            return ActionResult.fail(stack);
        }

        if (!world.isClientSide()) {
            // raytrace
            final RayTraceResult raytrace = raytraceFromEntity(world, player, 64.0F);
            if (raytrace.getType() != RayTraceResult.Type.MISS) {
                // add a lightning bolt at the resulting position
                LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
                bolt.setPos(raytrace.getLocation().x(), raytrace.getLocation().y(), raytrace.getLocation().z());
                world.addFreshEntity(bolt);
                // check for fireflash enchantment
                int damageAmount = 15;
                final int fireflashLevel = EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.FIREFLASH_ENCHANTMENT, stack);
                final boolean fireflash = GreekFantasy.CONFIG.isFireflashEnabled() && fireflashLevel > 0
                        && (!GreekFantasy.isRGLoaded() || RGCompat.getInstance().canUseFireflash(player));
                // if enchanted with fireflash, cause an explosion
                if (fireflash) {
                    damageAmount = 25;
                    final Explosion.Mode mode = GreekFantasy.CONFIG.doesFireflashDestroyBlocks() ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
                    world.explode(player, raytrace.getLocation().x(), raytrace.getLocation().y(), raytrace.getLocation().z(), fireflashLevel * 1.64F, true, mode);
                }
                // cooldown and item damage
                player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.getThunderboltCooldown() / (fireflash ? 2 : 1));
                if (!player.isCreative()) {
                    stack.hurtAndBreak(damageAmount, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
                }
            }
        }

        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return toRepair.getItem() == this && toRepair.getDamageValue() < toRepair.getMaxDamage() && isRepairItem(repair);
    }

    private boolean isRepairItem(final ItemStack repair) {
        return repair.getItem() == GFRegistry.ICHOR;
    }

    public static RayTraceResult raytraceFromEntity(final World world, final LivingEntity player, final float range) {
        // raytrace to determine which block the player is looking at within the given range
        final Vector3d startVec = player.getEyePosition(1.0F);
        final float pitch = (float) Math.toRadians(-player.xRot);
        final float yaw = (float) Math.toRadians(-player.yRot);
        float cosYaw = MathHelper.cos(yaw - (float) Math.PI);
        float sinYaw = MathHelper.sin(yaw - (float) Math.PI);
        float cosPitch = -MathHelper.cos(pitch);
        float sinPitch = MathHelper.sin(pitch);
        final Vector3d endVec = startVec.add(sinYaw * cosPitch * range, sinPitch * range, cosYaw * cosPitch * range);
        return player.level.clip(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.SOURCE_ONLY, player));
    }
}
