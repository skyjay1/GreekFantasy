package greekfantasy.entity.util;

import net.minecraft.world.entity.animal.horse.Variant;

public interface HasHorseVariant {

    void setVariant(final Variant color);

    Variant getVariant();

    default int getTailCounter() {
        return 0;
    }

    default float getRearingAmount(float partialTick) {
        return 0.0F;
    }

    default boolean isRearing() {
        return false;
    }
}
