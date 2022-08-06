package greekfantasy;

import greekfantasy.integration.RGCompat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GFSavedData extends SavedData {

    private final List<UUID> flyingPlayers = new ArrayList<>();
    private static final String KEY_PLAYERS = "FlyingPlayers";

    public static GFSavedData getOrCreate(final ServerLevel server) {
        return server.getDataStorage().computeIfAbsent(GFSavedData::read, GFSavedData::new, GreekFantasy.MODID);
    }

    public static GFSavedData read(CompoundTag nbt) {
        GFSavedData instance = new GFSavedData();
        instance.load(nbt);
        return instance;
    }

    // Flying Player methods //

    public void addFlyingPlayer(final Player player) {
        flyingPlayers.add(player.getUUID());
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
    }

    public void removeFlyingPlayer(final Player player) {
        flyingPlayers.remove(player.getUUID());
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }

    public boolean hasFlyingPlayer(final Player player) {
        return flyingPlayers.contains(player.getUUID());
    }

    public List<UUID> getFlyingPlayers() {
        return flyingPlayers;
    }

    // NBT methods //

    public void load(CompoundTag nbt) {
        // read flying players
        if (nbt.contains(KEY_PLAYERS)) {
            final ListTag playerList = nbt.getList(KEY_PLAYERS, 8);
            for (int i = 0, il = playerList.size(); i < il; i++) {
                flyingPlayers.add(UUID.fromString(playerList.getString(i)));
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        // write player list
        final ListTag flyingPlayerList = new ListTag();
        for (final UUID uuid : flyingPlayers) {
            flyingPlayerList.add(StringTag.valueOf(uuid.toString()));
        }
        compound.put(KEY_PLAYERS, flyingPlayerList);
        return compound;
    }

    // Static methods //

    /**
     * @param player the player
     * @return true if the player is wearing enchanted sandals and has high favor
     */
    public static boolean validatePlayer(final Player player) {
        final ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        return (feet.getItem() == GFRegistry.ItemReg.WINGED_SANDALS.get()
                && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.FLYING.get(), feet) > 0
                && (!GreekFantasy.isRGLoaded() || RGCompat.getInstance().canUseFlying(player)));
    }
}
