package greekfantasy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GFWorldSavedData extends SavedData {

    private final List<UUID> flyingPlayers = new ArrayList<>();
    private static final String KEY_PLAYERS = "FlyingPlayers";

    public static GFWorldSavedData getOrCreate(final ServerLevel server) {
        return server.getDataStorage().computeIfAbsent(GFWorldSavedData::read, GFWorldSavedData::new, GreekFantasy.MODID);
    }

    public static GFWorldSavedData read(CompoundTag nbt) {
        GFWorldSavedData instance = new GFWorldSavedData();
        instance.load(nbt);
        return instance;
    }

    // Flying Player methods //

    public void addFlyingPlayer(final Player player) {
        flyingPlayers.add(Player.createPlayerUUID(player.getName().getContents()));
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
    }

    public void removeFlyingPlayer(final Player player) {
        flyingPlayers.remove(Player.createPlayerUUID(player.getName().getContents()));
        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.onUpdateAbilities();
    }

    public boolean hasFlyingPlayer(final Player player) {
        return flyingPlayers.contains(Player.createPlayerUUID(player.getName().getContents()));
    }

    public List<UUID> getFlyingPlayers() {
        return flyingPlayers;
    }

    public void forEachFlyingPlayer(final Level worldIn, final Consumer<Player> action) {
        flyingPlayers.forEach(uuid -> {
            Player p = worldIn.getPlayerByUUID(uuid);
            if (p != null) {
                action.accept(p);
            }
        });
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
        return false;/*(feet.getItem() == GFRegistry.ItemReg.WINGED_SANDALS
                && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.FLYING_ENCHANTMENT, feet) > 0
                && (!GreekFantasy.isRGLoaded() || RGCompat.getInstance().canUseFlying(player)));*/
    }
}
