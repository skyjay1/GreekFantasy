package greekfantasy.entity.util;

import net.minecraft.nbt.CompoundTag;

public interface HasCustomCooldown {
    static final String KEY_COOLDOWN = "CustomCooldown";

    void setCustomCooldown(final int cooldown);

    int getCustomCooldown();

    default boolean hasNoCustomCooldown() {
        return getCustomCooldown() <= 0;
    }

    default void tickCustomCooldown() {
        setCustomCooldown(Math.max(getCustomCooldown() - 1, 0));
    }

    default void saveCustomCooldown(CompoundTag compound) {
        compound.putInt(KEY_COOLDOWN, getCustomCooldown());
    }

    default void readCustomCooldown(CompoundTag compound) {
        setCustomCooldown(compound.getInt(KEY_COOLDOWN));
    }
}
