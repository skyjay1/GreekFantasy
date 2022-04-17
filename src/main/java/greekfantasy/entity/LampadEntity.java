package greekfantasy.entity;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.util.BiomeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class LampadEntity extends DryadEntity {
  
  protected static final IOptionalNamedTag<Item> LAMPAD_TRADES = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "lampad_trade"));
  
  public LampadEntity(final EntityType<? extends LampadEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 24.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.26D)
        .add(Attributes.ATTACK_DAMAGE, 3.0D);
  }
  
  @Override
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    final LampadEntity.Variant variant;
    if(reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER || reason == SpawnReason.DISPENSER) {
      variant = LampadEntity.Variant.getRandom(worldIn.getRandom());
    } else {
      variant = LampadEntity.Variant.getForBiome(worldIn.getBiomeName(this.blockPosition()));
    }
    this.setVariant(variant);
    // sometimes carry soul torch
    if(worldIn.getRandom().nextFloat() < 0.2F) {
      this.setItemInHand(Hand.OFF_HAND, new ItemStack(Items.SOUL_TORCH));
    }
    return data;
  }
  
  @Override
  public IOptionalNamedTag<Item> getTradeTag() { return LAMPAD_TRADES; }
  
  @Override
  public DryadEntity.Variant getVariantByName(final String name) { return LampadEntity.Variant.getByName(name); }
  
  public static class Variant extends DryadEntity.Variant {
    public static final Variant CRIMSON = new LampadEntity.Variant("crimson", () -> Blocks.CRIMSON_FUNGUS);
    public static final Variant WARPED = new Variant("warped", () -> Blocks.WARPED_FUNGUS);
    public static final Variant POMEGRANATE = new Variant(GreekFantasy.MODID, "pomegranate", "lampad", "logs", () -> GFRegistry.POMEGRANATE_SAPLING);
    
    public static ImmutableMap<String, Variant> NETHER = ImmutableMap.<String, Variant>builder()
        .put(CRIMSON.getSerializedName(), CRIMSON).put(POMEGRANATE.getSerializedName(), POMEGRANATE).put(WARPED.getSerializedName(), WARPED)
        .build();
    
    protected Variant(final String nameIn, final Supplier<Block> saplingIn) {
      this("minecraft", nameIn, "lampad", "stems", saplingIn);
    }
    
    protected Variant(final String modid, final String nameIn, final String entityIn, final String tagSuffixIn, final Supplier<Block> saplingIn) {
      super(modid, nameIn, entityIn, tagSuffixIn, saplingIn);
    }
    
    public static Variant getForBiome(final Optional<RegistryKey<Biome>> biome) {
      return BiomeHelper.getLampadVariantForBiome(biome);
    }
    
    public static Variant getRandom(final Random rand) {
      int len = NETHER.size();
      return len > 0 ? NETHER.entrySet().asList().get(rand.nextInt(len)).getValue() : CRIMSON;
    }

    public static Variant getByName(final String n) {
      // check the given name in overworld and nether maps
      if(n != null && !n.isEmpty()) {
        return NETHER.getOrDefault(n, CRIMSON);
      }
      // defaults to CRIMSON
      return CRIMSON;
    }
  }
}
