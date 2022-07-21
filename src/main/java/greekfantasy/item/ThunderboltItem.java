package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.integration.RGCompat;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ThunderboltItem extends Item {

    public ThunderboltItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // prevent the item from being used up all the way
        if (stack.getMaxDamage() - stack.getDamageValue() <= 1) {
            return InteractionResultHolder.fail(stack);
        }
        // check for config settings and/or rain
        if (!level.isRaining() && !level.isThundering()) {
            player.displayClientMessage(new TranslatableComponent(getDescriptionId() + ".use.deny.rain"), true);
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide()) {
            // raytrace
            final BlockHitResult raytrace = raytraceFromEntity(player, 64.0F);
            if (raytrace.getType() != HitResult.Type.MISS) {
                // add a lightning bolt at the resulting position
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                bolt.setPos(raytrace.getLocation().x(), raytrace.getLocation().y(), raytrace.getLocation().z());
                level.addFreshEntity(bolt);
                // check for fireflash enchantment
                int damageAmount = 15;
                final int fireflashLevel = EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.FIREFLASH.get(), stack);
                final boolean fireflash = GreekFantasy.CONFIG.FIREFLASH_ENABLED.get() && fireflashLevel > 0
                        && (!GreekFantasy.isRGLoaded() || RGCompat.getInstance().canUseFireflash(player));
                // if enchanted with fireflash, cause an explosion
                if (fireflash) {
                    damageAmount = 25;
                    final Explosion.BlockInteraction mode = GreekFantasy.CONFIG.FIREFLASH_DESTROYS_BLOCKS.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                    level.explode(player, raytrace.getLocation().x(), raytrace.getLocation().y(), raytrace.getLocation().z(), fireflashLevel * 1.64F, true, mode);
                }
                // cooldown and item damage
                player.getCooldowns().addCooldown(this, GreekFantasy.CONFIG.THUNDERBOLT_COOLDOWN.get() / (fireflash ? 2 : 1));
                if (!player.isCreative()) {
                    stack.hurtAndBreak(damageAmount, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return toRepair.getItem() == this && toRepair.getDamageValue() < toRepair.getMaxDamage() && isRepairItem(repair);
    }

    private boolean isRepairItem(final ItemStack repair) {
        return repair.getItem() == GFRegistry.ItemReg.ICHOR.get();
    }

    public static BlockHitResult raytraceFromEntity(final LivingEntity player, final float range) {
        return raytraceFromEntity(player, range, ClipContext.Fluid.SOURCE_ONLY);
    }

    public static BlockHitResult raytraceFromEntity(final LivingEntity player, final float range, final ClipContext.Fluid fluidMode) {
        // raytrace to determine which block the player is looking at within the given range
        final Vec3 startVec = player.getEyePosition(1.0F);
        final float pitch = (float) Math.toRadians(-player.getXRot());
        final float yaw = (float) Math.toRadians(-player.getYRot());
        float cosYaw = Mth.cos(yaw - (float) Math.PI);
        float sinYaw = Mth.sin(yaw - (float) Math.PI);
        float cosPitch = -Mth.cos(pitch);
        float sinPitch = Mth.sin(pitch);
        final Vec3 endVec = startVec.add(sinYaw * cosPitch * range, sinPitch * range, cosYaw * cosPitch * range);
        return player.level.clip(new ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, fluidMode, player));
    }
}
