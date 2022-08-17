package greekfantasy.item;

import greekfantasy.GreekFantasy;
import greekfantasy.util.WeightedMobEffectInstance;
import greekfantasy.util.WeightedUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OliveSalveItem extends Item {
    public static final String KEY_USE_EFFECT = "UseEffect";
    public static final String KEY_BONUS_EFFECTS = "BonusEffects";
    public static final String KEY_ROLLS = "Rolls";
    public static final String KEY_ID = "Id";

    public OliveSalveItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if(!level.isClientSide()) {
            // create list of potion effects
            List<MobEffectInstance> effects = new ArrayList<>();
            // add primary effect to list
            effects.add(getPrimaryEffectInstance(itemStack));
            // add bonus effects to list
            int rolls = itemStack.getOrCreateTag().getInt(KEY_ROLLS);
            effects.addAll(getBonusEffectInstances(itemStack, rolls, entity.getRandom()));
            // apply potion effects
            for (MobEffectInstance effect : effects) {
                if (effect != null) {
                    entity.addEffect(effect);
                }
            }
        }
        return super.finishUsingItem(itemStack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        MobEffect primaryEffect = getPrimaryEffect(itemStack);
        if (primaryEffect != null) {
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip",
                    Component.translatable(primaryEffect.getDescriptionId()))
                    .withStyle(ChatFormatting.BLUE));
        } else {
            list.add(Component.translatable("effect.none").withStyle(ChatFormatting.BLUE));
        }
    }

    @Nullable
    private MobEffect getPrimaryEffect(final ItemStack itemStack) {
        if (!itemStack.hasTag()) {
            return null;
        }
        CompoundTag useEffectTag = itemStack.getTag().getCompound(KEY_USE_EFFECT);
        return MobEffect.byId(useEffectTag.getByte(KEY_ID));
    }

    @Nullable
    private MobEffectInstance getPrimaryEffectInstance(final ItemStack itemStack) {
        if (!itemStack.hasTag()) {
            return null;
        }
        return WeightedMobEffectInstance.fromTag(itemStack.getTag().getCompound(KEY_USE_EFFECT)).createMobEffectInstance();
    }

    private List<MobEffectInstance> getBonusEffectInstances(final ItemStack itemStack, final int rolls, final RandomSource random) {
        if (!itemStack.hasTag() || !itemStack.getTag().contains(KEY_BONUS_EFFECTS)) {
            return List.of();
        }
        ListTag effectList = itemStack.getTag().getList(KEY_BONUS_EFFECTS, Tag.TAG_COMPOUND);
        if (effectList.isEmpty()) {
            return List.of();
        }
        List<WeightedMobEffectInstance> weighted = new ArrayList();
        List<MobEffectInstance> mobEffects = new ArrayList<>();
        // read weighted mob effects from itemstack
        for(int i = 0, n = effectList.size(); i < n; i++) {
            weighted.add(WeightedMobEffectInstance.fromTag(effectList.getCompound(i)));
        }
        // add mob effects to the result list
        for(int i = 0; i < rolls; i++) {
            GreekFantasy.LOGGER.debug("Adding effect...");
            WeightedMobEffectInstance weightedEntry = WeightedUtil.sample(weighted, random);
            if(weightedEntry != null) {
                mobEffects.add(weightedEntry.createMobEffectInstance());
            }
        }
        return mobEffects;
    }

}
