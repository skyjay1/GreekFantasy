package greekfantasy.mob_effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CurseOfLycaonEffect extends MobEffect {

    public CurseOfLycaonEffect() {
        super(MobEffectCategory.NEUTRAL, 0x3F003E);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity thrower, LivingEntity entity, int amplifier, double factor) {

    }
}
