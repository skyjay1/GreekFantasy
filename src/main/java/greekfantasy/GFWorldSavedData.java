package greekfantasy;

import com.google.common.collect.ImmutableSet;
import greekfantasy.integration.RGCompat;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class GFWorldSavedData extends WorldSavedData {

    private final List<UUID> flyingPlayers = new ArrayList<>();
    private static final String KEY_PLAYERS = "FlyingPlayers";

    public GFWorldSavedData(String name) {
        super(name);
    }

    public static GFWorldSavedData getOrCreate(final ServerWorld server) {
        return server.getDataStorage().computeIfAbsent(() -> new GFWorldSavedData(GreekFantasy.MODID), GreekFantasy.MODID);
    }

    // Flying Player methods //

    public void addFlyingPlayer(final PlayerEntity player) {
        flyingPlayers.add(PlayerEntity.createPlayerUUID(player.getName().getContents()));
        player.abilities.mayfly = true;
        player.onUpdateAbilities();
    }

    public void removeFlyingPlayer(final PlayerEntity player) {
        flyingPlayers.remove(PlayerEntity.createPlayerUUID(player.getName().getContents()));
        player.abilities.mayfly = false;
        player.abilities.flying = false;
        player.onUpdateAbilities();
    }

    public boolean hasFlyingPlayer(final PlayerEntity player) {
        return flyingPlayers.contains(PlayerEntity.createPlayerUUID(player.getName().getContents()));
    }

    public List<UUID> getFlyingPlayers() {
        return flyingPlayers;
    }

    public void forEachFlyingPlayer(final World worldIn, final Consumer<PlayerEntity> action) {
        flyingPlayers.forEach(uuid -> {
            PlayerEntity p = worldIn.getPlayerByUUID(uuid);
            if (p != null) {
                action.accept(p);
            }
        });
    }

    // NBT methods //

    @Override
    public void load(CompoundNBT nbt) {
        // read flying players
        if (nbt.contains(KEY_PLAYERS)) {
            final ListNBT playerList = nbt.getList(KEY_PLAYERS, 8);
            for (int i = 0, il = playerList.size(); i < il; i++) {
                flyingPlayers.add(UUID.fromString(playerList.getString(i)));
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        // write player list
        final ListNBT flyingPlayerList = new ListNBT();
        for (final UUID uuid : flyingPlayers) {
            flyingPlayerList.add(StringNBT.valueOf(uuid.toString()));
        }
        compound.put(KEY_PLAYERS, flyingPlayerList);
        return compound;
    }

    // Static methods //

    /**
     * @param player the player
     * @return true if the player is wearing enchanted sandals and has high favor
     */
    public static boolean validatePlayer(final PlayerEntity player) {
        final ItemStack feet = player.getItemBySlot(EquipmentSlotType.FEET);
        return (feet.getItem() == GFRegistry.WINGED_SANDALS
                && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.FLYING_ENCHANTMENT, feet) > 0
                && (!GreekFantasy.isRGLoaded() || RGCompat.getInstance().canUseFlying(player)));
    }
}
