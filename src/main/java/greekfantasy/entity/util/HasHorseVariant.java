package greekfantasy.entity.util;

import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;

public interface HasHorseVariant {

    void setPackedVariant(int packedColorsTypes);

    int getPackedVariant();

    default void setVariant(final Variant color) {
        setVariant(color, getMarkings());
    }

    default void setVariant(Variant color, Markings type) {
        this.setPackedVariant(color.getId() & 255 | type.getId() << 8 & '\uff00');
    }

    default Variant getVariant() {
        return Variant.byId(this.getPackedVariant() & 255);
    }

    default Markings getMarkings() {
        return Markings.byId((this.getPackedVariant() & '\uff00') >> 8);
    }

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
