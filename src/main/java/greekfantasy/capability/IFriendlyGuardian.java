package greekfantasy.capability;

import greekfantasy.GreekFantasy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFriendlyGuardian extends INBTSerializable<CompoundTag> {

    ResourceLocation REGISTRY_NAME = new ResourceLocation(GreekFantasy.MODID, "friendly_guardian");

    String KEY_ENABLED = "Enabled";

    void setEnabled(boolean enabled);

    boolean isEnabled();

    default boolean isNeutralTowardPlayer(final PathfinderMob mob, final Player player) {
        if(isEnabled() && player != mob.getLastHurtByMob()) {
            return true;
        }
        return false;
    }

    @Override
    default CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(KEY_ENABLED, isEnabled());
        return tag;
    }

    @Override
    default void deserializeNBT(CompoundTag tag) {
        setEnabled(tag.getBoolean(KEY_ENABLED));
    }
}
