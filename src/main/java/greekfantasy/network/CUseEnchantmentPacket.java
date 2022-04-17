package greekfantasy.network;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.WhirlEntity;
import greekfantasy.item.ThunderboltItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created when a client-side event fires that involves an enchanted item,
 * such as right-clicking with the item. The packet sends the player and
 * enchantment to the server to respond.
 **/
public class CUseEnchantmentPacket {

    protected ResourceLocation enchantment;

    public CUseEnchantmentPacket() {
    }

    /**
     * @param enchantmentIn the ResourceLocation ID of the enchantment that was used
     **/
    public CUseEnchantmentPacket(final ResourceLocation enchantmentIn) {
        enchantment = enchantmentIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of a CUseEnchantmentPacket based on the PacketBuffer
     */
    public static CUseEnchantmentPacket fromBytes(final PacketBuffer buf) {
        return new CUseEnchantmentPacket(buf.readResourceLocation());
    }

    /*
     * Writes the raw packet data to the data stream.
     * @param msg the CUseEnchantmentPacket
     * @param buf the PacketBuffer
     */
    public static void toBytes(final CUseEnchantmentPacket msg, final PacketBuffer buf) {
        buf.writeResourceLocation(msg.enchantment);
    }

    /**
     * Handles the packet when it is received.
     *
     * @param message         the CUseEnchantmentPacket
     * @param contextSupplier the NetworkEvent.Context supplier
     */
    public static void handlePacket(final CUseEnchantmentPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.enqueueWork(() -> {
                final ServerPlayerEntity player = context.getSender();
                final ItemStack item = player.getMainHandItem();
                // make sure the player is holding an enchanted trident and in correct favor range
                if (GFRegistry.LORD_OF_THE_SEA_ENCHANTMENT.getRegistryName().equals(message.enchantment)
                        && GreekFantasy.CONFIG.isLordOfTheSeaEnabled() && item.getItem() == Items.TRIDENT
                        && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.LORD_OF_THE_SEA_ENCHANTMENT, item) > 0
                        && !player.getCooldowns().isOnCooldown(Items.TRIDENT)
                        // TODO
                        /*&& GreekFantasy.PROXY.getFavorConfiguration().getEnchantmentRange(FavorConfiguration.LORD_OF_THE_SEA_RANGE).isInFavorRange(player)*/) {
                    // The player has used an enchanted item and has the correct favor range, so the effect should be applied
                    useLordOfTheSea(player, item);
                }
                // make sure the player is holding an enchanted clock and in correct favor range
                if (GFRegistry.DAYBREAK_ENCHANTMENT.getRegistryName().equals(message.enchantment)
                        && GreekFantasy.CONFIG.isDaybreakEnabled() && item.getItem() == Items.CLOCK
                        && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.DAYBREAK_ENCHANTMENT, item) > 0
                        && player.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                        && player.getCommandSenderWorld().getDayTime() % 24000L > 13000L
                        // TODO
                        /*&& GreekFantasy.PROXY.getFavorConfiguration().getEnchantmentRange(FavorConfiguration.DAYBREAK_RANGE).isInFavorRange(player)*/) {
                    // The player has used an enchanted item and has the correct favor range, so the effect should be applied
                    useDaybreak(player, item);
                }
            });
        }
        context.setPacketHandled(true);
    }

    private static void useLordOfTheSea(final ServerPlayerEntity player, final ItemStack item) {
        final RayTraceResult raytrace = ThunderboltItem.raytraceFromEntity(player.getCommandSenderWorld(), player, 48.0F);
        // add a lightning bolt at the resulting position
        if (raytrace.getType() != RayTraceResult.Type.MISS) {
            final WhirlEntity whirl = GFRegistry.WHIRL_ENTITY.create(player.getCommandSenderWorld());
            final BlockPos pos = new BlockPos(raytrace.getLocation());
            // make sure there is enough water here
            if (player.getCommandSenderWorld().getFluidState(pos).is(FluidTags.WATER)
                    && player.getCommandSenderWorld().getFluidState(pos.below((int) Math.ceil(whirl.getBbHeight()))).is(FluidTags.WATER)) {
                // summon a powerful whirl with limited life and mob attracting turned on
                whirl.moveTo(raytrace.getLocation().x(), raytrace.getLocation().y() - whirl.getBbHeight(), raytrace.getLocation().z(), 0, 0);
                player.getCommandSenderWorld().addFreshEntity(whirl);
                whirl.setLimitedLife(GreekFantasy.CONFIG.getWhirlLifespan() * 20);
                whirl.setAttractMobs(true);
                whirl.playSound(SoundEvents.TRIDENT_THUNDER, 1.5F, 0.6F + whirl.getRandom().nextFloat() * 0.32F);
                // summon a lightning bolt
                LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(player.getCommandSenderWorld());
                bolt.setSilent(true);
                bolt.setPos(raytrace.getLocation().x(), raytrace.getLocation().y(), raytrace.getLocation().z());
                player.getCommandSenderWorld().addFreshEntity(bolt);
                // cooldown and item damage
                player.getCooldowns().addCooldown(item.getItem(), GreekFantasy.CONFIG.getWhirlLifespan() * 10);
                if (!player.isCreative()) {
                    item.hurtAndBreak(25, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND));
                }
            }
        }
    }

    private static void useDaybreak(final ServerPlayerEntity player, final ItemStack item) {
        final ServerWorld world = player.getLevel();
        long nextDay = world.getLevelData().getDayTime() + 24000L;
        world.setDayTime(nextDay - nextDay % 24000L);
        // break the item
        player.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
        if (!player.isCreative()) {
            item.shrink(1);
        }
    }
}
