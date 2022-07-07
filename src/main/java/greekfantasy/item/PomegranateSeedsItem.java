package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class PomegranateSeedsItem extends BlockNamedItem {

    public static final Food POMEGRANATE_SEEDS = new Food.Builder().fast().alwaysEat()
            .nutrition(2).saturationMod(0.1F)
            .effect(() -> new EffectInstance(Effects.DAMAGE_RESISTANCE, 120), 1.0F)
            .build();

    public PomegranateSeedsItem(final Item.Properties properties) {
        super(GFRegistry.BlockReg.POMEGRANATE_SAPLING, properties);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack item, World world, LivingEntity entity) {
        // determine how to eat the item
        if (world.dimension() == World.NETHER) {
            // normal eating when in nether
            item = super.finishUsingItem(item, world, entity);
            // give prisoner potion effect
            if (GreekFantasy.CONFIG.isPrisonerEnabled()) {
                entity.addEffect(new EffectInstance(GFRegistry.MobEffectReg.PRISONER_EFFECT, GreekFantasy.CONFIG.getPrisonerDuration()));
            }
        } else {
            // give hunger effect and shrink the itemstack
            entity.addEffect(new EffectInstance(Effects.HUNGER, 90));
            item.shrink(1);
        }
        return item;
    }
}
