package greekfantasy.entity;

import com.google.common.collect.ImmutableMap;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.util.NymphVariant;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.function.Supplier;

public class Lampad extends Dryad {

    protected static final TagKey<Item> LAMPAD_TRADES = ItemTags.create(new ResourceLocation(GreekFantasy.MODID, "lampad_trade"));

    public Lampad(final EntityType<? extends Lampad> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    public NymphVariant getVariantByName(final String name) {
        return Lampad.Variant.getByName(name);
    }

    @Override
    public NymphVariant getRandomVariant(final RandomSource rand) {
        return Lampad.Variant.getRandom(rand);
    }

    @Override
    public NymphVariant getVariantForBiome(final Holder<Biome> biome) {
        return Lampad.Variant.getForBiome(biome);
    }

    @Override
    public TagKey<Item> getTradeTag() {
        return LAMPAD_TRADES;
    }

    public static class Variant extends Dryad.Variant {
        public static final Variant CRIMSON = new Lampad.Variant("crimson", new ResourceLocation("forge", "has_structure/huge_crimson_fungus"), () -> Blocks.CRIMSON_FUNGUS);
        public static final Variant WARPED = new Lampad.Variant("warped", new ResourceLocation("forge", "has_structure/huge_warped_fungus"), () -> Blocks.WARPED_FUNGUS);
        public static final Variant POMEGRANATE = new Lampad.Variant(GreekFantasy.MODID, "pomegranate",
                ForgeRegistries.BIOMES.tags().createTagKey(new ResourceLocation("forge", "is_nether_forest")),
                "lampad", "logs", GFRegistry.BlockReg.POMEGRANATE_SAPLING);

        public static ImmutableMap<String, Variant> NETHER = ImmutableMap.<String, Variant>builder()
                .put(CRIMSON.getSerializedName(), CRIMSON)
                .put(POMEGRANATE.getSerializedName(), POMEGRANATE)
                .put(WARPED.getSerializedName(), WARPED)
                .build();

        protected Variant(final String nameIn, final ResourceLocation biomeTag, final Supplier<Block> saplingIn) {
            this("minecraft", nameIn, ForgeRegistries.BIOMES.tags().createTagKey(biomeTag), "lampad", "stems", saplingIn);
        }

        protected Variant(final String modid, final String nameIn, final ResourceLocation biomeTag, final Supplier<Block> saplingIn) {
            this(modid, nameIn, ForgeRegistries.BIOMES.tags().createTagKey(biomeTag), "lampad", "stems", saplingIn);
        }

        protected Variant(final String modid, final String nameIn, final TagKey<Biome> biome,
                          final String entityIn, final String tagSuffixIn,
                          final Supplier<Block> saplingIn) {
            super(modid, nameIn, biome, entityIn, tagSuffixIn, saplingIn);
        }

        public static Variant getForBiome(final Holder<Biome> biome) {
            for (Variant variant : NETHER.values()) {
                if (biome.is(variant.biomeTag)) {
                    return variant;
                }
            }
            return Variant.CRIMSON;
        }

        public static Variant getRandom(final RandomSource rand) {
            int len = NETHER.size();
            return len > 0 ? NETHER.entrySet().asList().get(rand.nextInt(len)).getValue() : CRIMSON;
        }

        public static Variant getByName(final String n) {
            return NETHER.getOrDefault(n, CRIMSON);
        }
    }
}
