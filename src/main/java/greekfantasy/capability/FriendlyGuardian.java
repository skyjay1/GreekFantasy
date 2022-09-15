package greekfantasy.capability;

import greekfantasy.GreekFantasy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class FriendlyGuardian implements IFriendlyGuardian {

    public static final IFriendlyGuardian EMPTY = new FriendlyGuardian();

    private boolean enabled = true;

    public FriendlyGuardian() {
    }

    private FriendlyGuardian(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabledIn) {
        enabled = enabledIn;
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final IFriendlyGuardian instance;
        private final LazyOptional<IFriendlyGuardian> storage;

        public Provider() {
            instance = new FriendlyGuardian(false);
            storage = LazyOptional.of(() -> instance);
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if(cap == GreekFantasy.FRIENDLY_GUARDIAN_CAP) {
                return storage.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            return instance.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.deserializeNBT(nbt);
        }
    }
}
