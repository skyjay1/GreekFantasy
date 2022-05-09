package greekfantasy;

import greekfantasy.block.CappedPillarBlock;
import greekfantasy.block.GlowBlock;
import greekfantasy.block.GoldenStringBlock;
import greekfantasy.block.IchorInfusedBlock;
import greekfantasy.block.MobHeadBlock;
import greekfantasy.block.MysteriousBoxBlock;
import greekfantasy.block.NestBlock;
import greekfantasy.block.OilBlock;
import greekfantasy.block.OilLampBlock;
import greekfantasy.block.OrthusHeadBlock;
import greekfantasy.block.PomegranateSaplingBlock;
import greekfantasy.block.ReedsBlock;
import greekfantasy.block.VaseBlock;
import greekfantasy.block.WildRoseBlock;
import greekfantasy.crafting.SalveRecipe;
import greekfantasy.effect.MirrorEffect;
import greekfantasy.effect.PrisonerEffect;
import greekfantasy.effect.StunnedEffect;
import greekfantasy.effect.PigEffect;
import greekfantasy.enchantment.DeityEnchantment;
import greekfantasy.enchantment.HuntingEnchantment;
import greekfantasy.enchantment.MirrorEnchantment;
import greekfantasy.enchantment.OverstepEnchantment;
import greekfantasy.enchantment.PoisonEnchantment;
import greekfantasy.enchantment.SilkstepEnchantment;
import greekfantasy.enchantment.SmashingEnchantment;
import greekfantasy.entity.AraEntity;
import greekfantasy.entity.ArachneEntity;
import greekfantasy.entity.ArionEntity;
import greekfantasy.entity.BabySpiderEntity;
import greekfantasy.entity.BronzeBullEntity;
import greekfantasy.entity.CentaurEntity;
import greekfantasy.entity.CerastesEntity;
import greekfantasy.entity.CerberusEntity;
import greekfantasy.entity.CharybdisEntity;
import greekfantasy.entity.CirceEntity;
import greekfantasy.entity.CretanMinotaurEntity;
import greekfantasy.entity.CyclopesEntity;
import greekfantasy.entity.CyprianEntity;
import greekfantasy.entity.DrakainaEntity;
import greekfantasy.entity.DryadEntity;
import greekfantasy.entity.ElpisEntity;
import greekfantasy.entity.EmpusaEntity;
import greekfantasy.entity.FuryEntity;
import greekfantasy.entity.GeryonEntity;
import greekfantasy.entity.GiantBoarEntity;
import greekfantasy.entity.GiganteEntity;
import greekfantasy.entity.GoldenRamEntity;
import greekfantasy.entity.GorgonEntity;
import greekfantasy.entity.HarpyEntity;
import greekfantasy.entity.HydraEntity;
import greekfantasy.entity.HydraHeadEntity;
import greekfantasy.entity.LampadEntity;
import greekfantasy.entity.MadCowEntity;
import greekfantasy.entity.MakhaiEntity;
import greekfantasy.entity.MinotaurEntity;
import greekfantasy.entity.NaiadEntity;
import greekfantasy.entity.NemeanLionEntity;
import greekfantasy.entity.OrthusEntity;
import greekfantasy.entity.misc.PalladiumEntity;
import greekfantasy.entity.PegasusEntity;
import greekfantasy.entity.PythonEntity;
import greekfantasy.entity.SatyrEntity;
import greekfantasy.entity.ShadeEntity;
import greekfantasy.entity.SirenEntity;
import greekfantasy.entity.SpartiEntity;
import greekfantasy.entity.TalosEntity;
import greekfantasy.entity.UnicornEntity;
import greekfantasy.entity.WhirlEntity;
import greekfantasy.entity.misc.CurseEntity;
import greekfantasy.entity.misc.DiscusEntity;
import greekfantasy.entity.misc.DragonToothEntity;
import greekfantasy.entity.misc.GreekFireEntity;
import greekfantasy.entity.misc.HealingSpellEntity;
import greekfantasy.entity.misc.OrthusHeadItemEntity;
import greekfantasy.entity.misc.PoisonSpitEntity;
import greekfantasy.entity.misc.SpearEntity;
import greekfantasy.entity.misc.PigSpellEntity;
import greekfantasy.entity.misc.WebBallEntity;
import greekfantasy.feature.GoldenAppleTree;
import greekfantasy.feature.OliveTree;
import greekfantasy.feature.PomegranateTree;
import greekfantasy.item.AchillesArmorItem;
import greekfantasy.item.AmbrosiaItem;
import greekfantasy.item.BagOfWindItem;
import greekfantasy.item.BidentItem;
import greekfantasy.item.ClubItem;
import greekfantasy.item.ConchItem;
import greekfantasy.item.DiscusItem;
import greekfantasy.item.DragonToothItem;
import greekfantasy.item.EnchantedBowItem;
import greekfantasy.item.FlintKnifeItem;
import greekfantasy.item.GorgonBloodItem;
import greekfantasy.item.GreekFireItem;
import greekfantasy.item.HealingRodItem;
import greekfantasy.item.HelmOfDarknessItem;
import greekfantasy.item.HornOfPlentyItem;
import greekfantasy.item.IvorySwordItem;
import greekfantasy.item.LyreItem;
import greekfantasy.item.MirrorItem;
import greekfantasy.item.MobHeadItem;
import greekfantasy.item.NemeanLionHideItem;
import greekfantasy.item.OliveOilItem;
import greekfantasy.item.OrthusHeadItem;
import greekfantasy.item.PalladiumItem;
import greekfantasy.item.PanfluteItem;
import greekfantasy.item.PomegranateSeedsItem;
import greekfantasy.item.SalveItem;
import greekfantasy.item.SnakeskinArmorItem;
import greekfantasy.item.SpearItem;
import greekfantasy.item.PigWandItem;
import greekfantasy.item.ThunderboltItem;
import greekfantasy.item.UnicornHornItem;
import greekfantasy.item.WebBallItem;
import greekfantasy.item.WingedSandalsItem;
import greekfantasy.tileentity.MobHeadTileEntity;
import greekfantasy.tileentity.MobHeadTileEntity.HeadType;
import greekfantasy.tileentity.VaseTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class GFRegistry {

    private static final String MODID = GreekFantasy.MODID;

    @ObjectHolder(GreekFantasy.MODID)
    public static final class EntityReg {

        public static EntityType<AraEntity> ARA_ENTITY = buildEntityType(AraEntity::new, "ara", 0.67F, 1.8F, EntityClassification.CREATURE, b -> {});
        public static EntityType<ArachneEntity> ARACHNE_ENTITY = buildEntityType(ArachneEntity::new, "arachne", 0.94F, 1.9F, EntityClassification.MONSTER, b -> {});
        public static EntityType<ArionEntity> ARION_ENTITY = buildEntityType(ArionEntity::new, "arion", 1.39F, 1.98F, EntityClassification.CREATURE, b -> b.fireImmune());
        public static EntityType<BabySpiderEntity> BABY_SPIDER_ENTITY = buildEntityType(BabySpiderEntity::new, "baby_spider", 0.5F, 0.65F, EntityClassification.MONSTER, b -> {});
        public static EntityType<BronzeBullEntity> BRONZE_BULL_ENTITY = buildEntityType(BronzeBullEntity::new, "bronze_bull", 1.95F, 2.98F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<CentaurEntity> CENTAUR_ENTITY = buildEntityType(CentaurEntity::new, "centaur", 1.39F, 2.49F, EntityClassification.CREATURE, b -> {});
        public static EntityType<CerastesEntity> CERASTES_ENTITY = buildEntityType(CerastesEntity::new, "cerastes", 0.98F, 0.94F, EntityClassification.CREATURE, b -> {});
        public static EntityType<CerberusEntity> CERBERUS_ENTITY = buildEntityType(CerberusEntity::new, "cerberus", 1.98F, 1.9F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<CharybdisEntity> CHARYBDIS_ENTITY = buildEntityType(CharybdisEntity::new, "charybdis", 5.9F, 7.9F, EntityClassification.WATER_CREATURE, b -> b.fireImmune());
        public static EntityType<CirceEntity> CIRCE_ENTITY = buildEntityType(CirceEntity::new, "circe", 0.67F, 1.8F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<CretanMinotaurEntity> CRETAN_MINOTAUR_ENTITY = buildEntityType(CretanMinotaurEntity::new, "cretan_minotaur", 0.98F, 3.395F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<CurseEntity> CURSE_ENTITY = buildEntityType(CurseEntity::new, "curse", 0.25F, 0.25F, EntityClassification.MISC, b -> b.fireImmune().noSummon().clientTrackingRange(4).updateInterval(10));
        public static EntityType<CyclopesEntity> CYCLOPES_ENTITY = buildEntityType(CyclopesEntity::new, "cyclopes", 0.99F, 2.92F, EntityClassification.MONSTER, b -> {});
        public static EntityType<CyprianEntity> CYPRIAN_ENTITY = buildEntityType(CyprianEntity::new, "cyprian", 1.39F, 2.49F, EntityClassification.CREATURE, b -> {});
        public static EntityType<DiscusEntity> DISCUS_ENTITY = buildEntityType(DiscusEntity::new, "discus", 0.45F, 0.45F, EntityClassification.MISC, b -> b.noSummon().clientTrackingRange(4).updateInterval(10));
        public static EntityType<DragonToothEntity> DRAGON_TOOTH_ENTITY = buildEntityType(DragonToothEntity::new, "dragon_tooth", 0.25F, 0.25F, EntityClassification.MISC, b -> b.fireImmune().noSummon().clientTrackingRange(4).updateInterval(10));
        public static EntityType<DrakainaEntity> DRAKAINA_ENTITY = buildEntityType(DrakainaEntity::new, "drakaina", 0.9F, 1.9F, EntityClassification.MONSTER, b -> {});
        public static EntityType<DryadEntity> DRYAD_ENTITY = buildEntityType(DryadEntity::new, "dryad", 0.48F, 1.8F, EntityClassification.CREATURE, b -> {});
        public static EntityType<ElpisEntity> ELPIS_ENTITY = buildEntityType(ElpisEntity::new, "elpis", 0.4F, 0.8F, EntityClassification.CREATURE, b -> b.fireImmune());
        public static EntityType<EmpusaEntity> EMPUSA_ENTITY = buildEntityType(EmpusaEntity::new, "empusa", 0.67F, 1.8F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<FuryEntity> FURY_ENTITY = buildEntityType(FuryEntity::new, "fury", 0.67F, 1.4F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<GeryonEntity> GERYON_ENTITY = buildEntityType(GeryonEntity::new, "geryon", 1.98F, 4.96F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<GiantBoarEntity> GIANT_BOAR_ENTITY = buildEntityType(GiantBoarEntity::new, "giant_boar", 2.653F, 2.66F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<GiganteEntity> GIGANTE_ENTITY = buildEntityType(GiganteEntity::new, "gigante", 1.98F, 4.79F, EntityClassification.MONSTER, b -> {});
        public static EntityType<GoldenRamEntity> GOLDEN_RAM_ENTITY = buildEntityType(GoldenRamEntity::new, "golden_ram", 0.96F, 1.56F, EntityClassification.CREATURE, b -> b.fireImmune());
        public static EntityType<GorgonEntity> GORGON_ENTITY = buildEntityType(GorgonEntity::new, "gorgon", 0.9F, 1.9F, EntityClassification.MONSTER, b -> {});
        public static EntityType<GreekFireEntity> GREEK_FIRE_ENTITY = buildEntityType(GreekFireEntity::new, "greek_fire", 0.45F, 0.45F, EntityClassification.MISC, b -> b.noSummon().clientTrackingRange(4).updateInterval(10));
        public static EntityType<HarpyEntity> HARPY_ENTITY = buildEntityType(HarpyEntity::new, "harpy", 0.7F, 1.8F, EntityClassification.MONSTER, b -> {});
        public static EntityType<HealingSpellEntity> HEALING_SPELL_ENTITY = buildEntityType(HealingSpellEntity::new, "healing_spell", 0.25F, 0.25F, EntityClassification.MISC, b -> b.fireImmune().noSummon().clientTrackingRange(4).updateInterval(10));
        public static EntityType<HydraEntity> HYDRA_ENTITY = buildEntityType(HydraEntity::new, "hydra", 2.4F, 2.24F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<HydraHeadEntity> HYDRA_HEAD_ENTITY = buildEntityType(HydraHeadEntity::new, "hydra_head", 0.68F, 1.88F, EntityClassification.MISC, b -> b.noSummon());
        public static EntityType<LampadEntity> LAMPAD_ENTITY = buildEntityType(LampadEntity::new, "lampad", 0.48F, 1.8F, EntityClassification.CREATURE, b -> b.fireImmune());
        public static EntityType<MadCowEntity> MAD_COW_ENTITY = buildEntityType(MadCowEntity::new, "mad_cow", 0.9F, 1.4F, EntityClassification.CREATURE, b -> {});
        public static EntityType<MakhaiEntity> MAKHAI_ENTITY = buildEntityType(MakhaiEntity::new, "makhai", 0.67F, 1.8F, EntityClassification.CREATURE, b -> {});
        public static EntityType<MinotaurEntity> MINOTAUR_ENTITY = buildEntityType(MinotaurEntity::new, "minotaur", 0.7F, 1.94F, EntityClassification.MONSTER, b -> {});
        public static EntityType<NemeanLionEntity> NEMEAN_LION_ENTITY = buildEntityType(NemeanLionEntity::new, "nemean_lion", 1.92F, 2.28F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<NaiadEntity> NAIAD_ENTITY = buildEntityType(NaiadEntity::new, "naiad", 0.48F, 1.8F, EntityClassification.WATER_CREATURE, b -> {});
        public static EntityType<OrthusEntity> ORTHUS_ENTITY = buildEntityType(OrthusEntity::new, "orthus", 0.6F, 0.85F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<OrthusHeadItemEntity> ORTHUS_HEAD_ITEM_ENTITY = buildEntityType(OrthusHeadItemEntity::new, "orthus_head_item", 0.25F, 0.25F, EntityClassification.MISC, b -> b.noSummon().clientTrackingRange(6).updateInterval(20));
        public static EntityType<PalladiumEntity> PALLADIUM_ENTITY = buildEntityType(PalladiumEntity::new, "palladium", 0.98F, 2.24F, EntityClassification.MISC, b -> b.fireImmune());
        public static EntityType<PegasusEntity> PEGASUS_ENTITY = buildEntityType(PegasusEntity::new, "pegasus", 1.39F, 1.98F, EntityClassification.CREATURE, b -> {});
        public static EntityType<PoisonSpitEntity> POISON_SPIT_ENTITY = buildEntityType(PoisonSpitEntity::new, "poison_spit", 0.25F, 0.25F, EntityClassification.MISC, b -> b.fireImmune().noSummon().clientTrackingRange(4).updateInterval(10));
        public static EntityType<PythonEntity> PYTHON_ENTITY = buildEntityType(PythonEntity::new, "python", 1.4F, 1.9F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<SatyrEntity> SATYR_ENTITY = buildEntityType(SatyrEntity::new, "satyr", 0.67F, 1.8F, EntityClassification.CREATURE, b -> {});
        public static EntityType<ShadeEntity> SHADE_ENTITY = buildEntityType(ShadeEntity::new, "shade", 0.67F, 1.8F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<SirenEntity> SIREN_ENTITY = buildEntityType(SirenEntity::new, "siren", 0.6F, 1.9F, EntityClassification.WATER_CREATURE, b -> {});
        public static EntityType<SpartiEntity> SPARTI_ENTITY = buildEntityType(SpartiEntity::new, "sparti", 0.6F, 1.98F, EntityClassification.CREATURE, b -> {});
        public static EntityType<SpearEntity> SPEAR_ENTITY = buildEntityType(SpearEntity::new, "spear", 0.5F, 0.5F, EntityClassification.MISC, b -> b.noSummon().clientTrackingRange(4).updateInterval(20));
        public static EntityType<PigSpellEntity> PIG_SPELL_ENTITY = buildEntityType(PigSpellEntity::new, "pig_spell", 0.25F, 0.25F, EntityClassification.MISC, b -> b.fireImmune().noSummon().clientTrackingRange(4).updateInterval(10));
        public static EntityType<TalosEntity> TALOS_ENTITY = buildEntityType(TalosEntity::new, "talos", 1.98F, 4.96F, EntityClassification.MONSTER, b -> b.fireImmune());
        public static EntityType<UnicornEntity> UNICORN_ENTITY = buildEntityType(UnicornEntity::new, "unicorn", 1.39F, 1.98F, EntityClassification.CREATURE, b -> {});
        public static EntityType<WebBallEntity> WEB_BALL_ENTITY = buildEntityType(WebBallEntity::new, "web_ball", 0.25F, 0.25F, EntityClassification.MISC, b -> b.fireImmune().noSummon().clientTrackingRange(4).updateInterval(10));
        public static EntityType<WhirlEntity> WHIRL_ENTITY = buildEntityType(WhirlEntity::new, "whirl", 2.9F, 5.0F, EntityClassification.WATER_CREATURE, b -> {});

        protected static final Predicate<IServerWorld> DIMENSION_MOB_PLACEMENT = world -> {
            return GreekFantasy.CONFIG.IS_SPAWNS_WHITELIST.get() == GreekFantasy.CONFIG.SPAWNS_DIMENSION_WHITELIST.get().contains(world.getLevel().dimension().location().toString());
        };

        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            GreekFantasy.LOGGER.debug("registerEntities");
            // entity types have already been created, now they are actually registered (along with placements)
            registerEntityType(event, EntityReg.ARA_ENTITY, "ara", AraEntity::canAraSpawnOn);
            registerEntityType(event, EntityReg.ARACHNE_ENTITY, "arachne", null);
            registerEntityType(event, EntityReg.ARION_ENTITY, "arion", ArionEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.BABY_SPIDER_ENTITY, "baby_spider", BabySpiderEntity::checkMonsterSpawnRules);
            registerEntityType(event, EntityReg.BRONZE_BULL_ENTITY, "bronze_bull", null);
            registerEntityType(event, EntityReg.CENTAUR_ENTITY, "centaur", CentaurEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.CERASTES_ENTITY, "cerastes", CerastesEntity::canCerastesSpawnOn);
            registerEntityType(event, EntityReg.CERBERUS_ENTITY, "cerberus", null);
            registerEntityType(event, EntityReg.CHARYBDIS_ENTITY, "charybdis", null);
            registerEntityType(event, EntityReg.CIRCE_ENTITY, "circe", null);
            registerEntityType(event, EntityReg.CRETAN_MINOTAUR_ENTITY, "cretan_minotaur", null);
            registerEntityType(event, EntityReg.CYPRIAN_ENTITY, "cyprian", CyprianEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.CYCLOPES_ENTITY, "cyclopes", CyclopesEntity::canCyclopesSpawnOn);
            registerEntityType(event, EntityReg.DRAKAINA_ENTITY, "drakaina", DrakainaEntity::checkMonsterSpawnRules);
            registerEntityType(event, EntityReg.DRYAD_ENTITY, "dryad", DryadEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.ELPIS_ENTITY, "elpis", null);
            registerEntityType(event, EntityReg.EMPUSA_ENTITY, "empusa", EmpusaEntity::checkMonsterSpawnRules);
            registerEntityType(event, EntityReg.FURY_ENTITY, "fury", FuryEntity::checkAnyLightMonsterSpawnRules);
            registerEntityType(event, EntityReg.GERYON_ENTITY, "geryon", null);
            registerEntityType(event, EntityReg.GIANT_BOAR_ENTITY, "giant_boar", null);
            registerEntityType(event, EntityReg.GIGANTE_ENTITY, "gigante", GiganteEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.GOLDEN_RAM_ENTITY, "golden_ram", null);
            registerEntityType(event, EntityReg.GORGON_ENTITY, "gorgon", GorgonEntity::checkMonsterSpawnRules);
            registerEntityType(event, EntityReg.HARPY_ENTITY, "harpy", HarpyEntity::checkAnyLightMonsterSpawnRules);
            registerEntityType(event, EntityReg.HYDRA_ENTITY, "hydra", HydraEntity::checkMonsterSpawnRules);
            registerEntityType(event, EntityReg.HYDRA_HEAD_ENTITY, "hydra_head", null);
            registerEntityType(event, EntityReg.LAMPAD_ENTITY, "lampad", LampadEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.MAD_COW_ENTITY, "mad_cow", MadCowEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.MAKHAI_ENTITY, "makhai", null);
            registerEntityType(event, EntityReg.MINOTAUR_ENTITY, "minotaur", MinotaurEntity::checkMonsterSpawnRules);
            registerEntityType(event, EntityReg.NEMEAN_LION_ENTITY, "nemean_lion", null);
            registerEntityType(event, EntityReg.NAIAD_ENTITY, "naiad", NaiadEntity::canNaiadSpawnOn);
            registerEntityType(event, EntityReg.ORTHUS_ENTITY, "orthus", OrthusEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.PEGASUS_ENTITY, "pegasus", PegasusEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.PYTHON_ENTITY, "python", null);
            registerEntityType(event, EntityReg.SATYR_ENTITY, "satyr", SatyrEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.SHADE_ENTITY, "shade", ShadeEntity::checkMonsterSpawnRules);
            registerEntityType(event, EntityReg.SIREN_ENTITY, "siren", SirenEntity::canSirenSpawnOn);
            registerEntityType(event, EntityReg.SPARTI_ENTITY, "sparti", null);
            registerEntityType(event, EntityReg.TALOS_ENTITY, "talos", null);
            registerEntityType(event, EntityReg.UNICORN_ENTITY, "unicorn", UnicornEntity::checkMobSpawnRules);
            registerEntityType(event, EntityReg.WHIRL_ENTITY, "whirl", WhirlEntity::canWhirlSpawnOn);
            event.getRegistry().register(EntityReg.SPEAR_ENTITY.setRegistryName(MODID, "spear"));
            event.getRegistry().register(EntityReg.CURSE_ENTITY.setRegistryName(MODID, "curse"));
            event.getRegistry().register(EntityReg.DISCUS_ENTITY.setRegistryName(MODID, "discus"));
            event.getRegistry().register(EntityReg.DRAGON_TOOTH_ENTITY.setRegistryName(MODID, "dragon_tooth"));
            event.getRegistry().register(EntityReg.GREEK_FIRE_ENTITY.setRegistryName(MODID, "greek_fire"));
            event.getRegistry().register(EntityReg.HEALING_SPELL_ENTITY.setRegistryName(MODID, "healing_spell"));
            event.getRegistry().register(EntityReg.ORTHUS_HEAD_ITEM_ENTITY.setRegistryName(MODID, "orthus_head_item"));
            event.getRegistry().register(EntityReg.PALLADIUM_ENTITY.setRegistryName(MODID, "palladium"));
            event.getRegistry().register(EntityReg.POISON_SPIT_ENTITY.setRegistryName(MODID, "poison_spit"));
            event.getRegistry().register(EntityReg.PIG_SPELL_ENTITY.setRegistryName(MODID, "pig_spell"));
            event.getRegistry().register(EntityReg.WEB_BALL_ENTITY.setRegistryName(MODID, "web_ball"));
        }

        @SubscribeEvent
        public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
            GreekFantasy.LOGGER.debug("registerEntityAttributes");
            // entity types have already been created, now register the attributes
            event.put(EntityReg.ARA_ENTITY, AraEntity.createAttributes().build());
            event.put(EntityReg.ARACHNE_ENTITY, ArachneEntity.createAttributes().build());
            event.put(EntityReg.ARION_ENTITY, ArionEntity.createAttributes().build());
            event.put(EntityReg.BABY_SPIDER_ENTITY, BabySpiderEntity.createAttributes().build());
            event.put(EntityReg.BRONZE_BULL_ENTITY, BronzeBullEntity.createAttributes().build());
            event.put(EntityReg.CENTAUR_ENTITY, CentaurEntity.createAttributes().build());
            event.put(EntityReg.CERASTES_ENTITY, CerastesEntity.createAttributes().build());
            event.put(EntityReg.CERBERUS_ENTITY, CerberusEntity.createAttributes().build());
            event.put(EntityReg.CHARYBDIS_ENTITY, CharybdisEntity.createAttributes().build());
            event.put(EntityReg.CIRCE_ENTITY, CirceEntity.createAttributes().build());
            event.put(EntityReg.CRETAN_MINOTAUR_ENTITY, CretanMinotaurEntity.createAttributes().build());
            event.put(EntityReg.CYCLOPES_ENTITY, CyclopesEntity.createAttributes().build());
            event.put(EntityReg.CYPRIAN_ENTITY, CyprianEntity.createAttributes().build());
            event.put(EntityReg.DRAKAINA_ENTITY, DrakainaEntity.createAttributes().build());
            event.put(EntityReg.DRYAD_ENTITY, DryadEntity.createAttributes().build());
            event.put(EntityReg.ELPIS_ENTITY, ElpisEntity.createAttributes().build());
            event.put(EntityReg.EMPUSA_ENTITY, EmpusaEntity.createAttributes().build());
            event.put(EntityReg.FURY_ENTITY, FuryEntity.createAttributes().build());
            event.put(EntityReg.GERYON_ENTITY, GeryonEntity.createAttributes().build());
            event.put(EntityReg.GIANT_BOAR_ENTITY, GiantBoarEntity.createAttributes().build());
            event.put(EntityReg.GIGANTE_ENTITY, GiganteEntity.createAttributes().build());
            event.put(EntityReg.GOLDEN_RAM_ENTITY, GoldenRamEntity.createAttributes().build());
            event.put(EntityReg.GORGON_ENTITY, GorgonEntity.createAttributes().build());
            event.put(EntityReg.HARPY_ENTITY, HarpyEntity.createAttributes().build());
            event.put(EntityReg.HYDRA_ENTITY, HydraEntity.createAttributes().build());
            event.put(EntityReg.HYDRA_HEAD_ENTITY, HydraHeadEntity.createAttributes().build());
            event.put(EntityReg.LAMPAD_ENTITY, LampadEntity.createAttributes().build());
            event.put(EntityReg.MAD_COW_ENTITY, MadCowEntity.createAttributes().build());
            event.put(EntityReg.MAKHAI_ENTITY, MakhaiEntity.createAttributes().build());
            event.put(EntityReg.MINOTAUR_ENTITY, MinotaurEntity.createAttributes().build());
            event.put(EntityReg.NAIAD_ENTITY, NaiadEntity.createAttributes().build());
            event.put(EntityReg.NEMEAN_LION_ENTITY, NemeanLionEntity.createAttributes().build());
            event.put(EntityReg.ORTHUS_ENTITY, OrthusEntity.createAttributes().build());
            event.put(EntityReg.PALLADIUM_ENTITY, PalladiumEntity.createAttributes().build());
            event.put(EntityReg.PEGASUS_ENTITY, PegasusEntity.createAttributes().build());
            event.put(EntityReg.PYTHON_ENTITY, PythonEntity.createAttributes().build());
            event.put(EntityReg.SATYR_ENTITY, SatyrEntity.createAttributes().build());
            event.put(EntityReg.SHADE_ENTITY, ShadeEntity.createAttributes().build());
            event.put(EntityReg.SIREN_ENTITY, SirenEntity.createAttributes().build());
            event.put(EntityReg.SPARTI_ENTITY, SpartiEntity.createAttributes().build());
            event.put(EntityReg.TALOS_ENTITY, TalosEntity.createAttributes().build());
            event.put(EntityReg.UNICORN_ENTITY, UnicornEntity.createAttributes().build());
            event.put(EntityReg.WHIRL_ENTITY, WhirlEntity.createAttributes().build());
        }

        /**
         * Builds and returns (but does not register) an entity type with the given information
         *
         * @param <T>            a class that inherits from Entity
         * @param factoryIn      the entity factory, usually [EntityClass]::new
         * @param name           the entity name for use in registration later
         * @param width          the horizontal size of the entity
         * @param height         the vertical size of the entity
         * @param classification the entity classification
         * @param builderSpecs   a consumer to add other arguments to the builder before the entity type is built
         * @return an entity type
         **/
        private static <T extends Entity> EntityType<T> buildEntityType(final IFactory<T> factoryIn, final String name, final float width, final float height,
                                                                        final EntityClassification classification, final Consumer<EntityType.Builder<T>> builderSpecs) {
            EntityType.Builder<T> entityTypeBuilder = EntityType.Builder.of(factoryIn, classification).sized(width, height).clientTrackingRange(8);
            builderSpecs.accept(entityTypeBuilder);
            EntityType<T> entityType = entityTypeBuilder.build(name);
            return entityType;
        }

        /**
         * Registers the given entity type and its associated attributes and placement settings
         *
         * @param <T>                a class that inherits from MobEntity
         * @param event              the registry event
         * @param entityType         the entity type to register
         * @param name               the registry name suffix (not including mod id)
         * @param placementPredicate the spawn placement predicate, usually a reference to a static method.
         *                           If this value is null, no placement will be registered.
         **/
        private static <T extends MobEntity> void registerEntityType(final RegistryEvent.Register<EntityType<?>> event,
                                                                     final EntityType<T> entityType, final String name,
                                                                     @Nullable final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placementPredicate) {
            // register the entity type
            entityType.setRegistryName(MODID, name);
            event.getRegistry().register(entityType);
            // register placement (not used unless spawn information is registered with a biome)
            if (placementPredicate != null) {
                final PlacementType placementType = entityType.getCategory() == EntityClassification.WATER_CREATURE ? PlacementType.IN_WATER : PlacementType.ON_GROUND;
                // wrap the spawn predicate in one that also checks dimension predicate
                final EntitySpawnPlacementRegistry.IPlacementPredicate<T> placement = (entity, world, reason, pos, rand) -> DIMENSION_MOB_PLACEMENT.test(world) && placementPredicate.test(entity, world, reason, pos, rand);
                EntitySpawnPlacementRegistry.register(entityType, placementType, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, placement);
            }
        }

    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class BlockReg {

        // Block //
        @ObjectHolder("reeds")
        public static final Block REEDS = null;
        @ObjectHolder("olive_log")
        public static final Block OLIVE_LOG = null;
        @ObjectHolder("stripped_olive_log")
        public static final Block STRIPPED_OLIVE_LOG = null;
        @ObjectHolder("olive_wood")
        public static final Block OLIVE_WOOD = null;
        @ObjectHolder("stripped_olive_wood")
        public static final Block STRIPPED_OLIVE_WOOD = null;
        @ObjectHolder("olive_planks")
        public static final Block OLIVE_PLANKS = null;
        @ObjectHolder("olive_slab")
        public static final Block OLIVE_SLAB = null;
        @ObjectHolder("olive_stairs")
        public static final Block OLIVE_STAIRS = null;
        @ObjectHolder("olive_door")
        public static final Block OLIVE_DOOR = null;
        @ObjectHolder("olive_trapdoor")
        public static final Block OLIVE_TRAPDOOR = null;
        @ObjectHolder("olive_leaves")
        public static final Block OLIVE_LEAVES = null;
        @ObjectHolder("olive_sapling")
        public static final Block OLIVE_SAPLING = null;
        @ObjectHolder("pomegranate_log")
        public static final Block POMEGRANATE_LOG = null;
        @ObjectHolder("stripped_pomegranate_log")
        public static final Block STRIPPED_POMEGRANATE_LOG = null;
        @ObjectHolder("pomegranate_wood")
        public static final Block POMEGRANATE_WOOD = null;
        @ObjectHolder("stripped_pomegranate_wood")
        public static final Block STRIPPED_POMEGRANATE_WOOD = null;
        @ObjectHolder("pomegranate_planks")
        public static final Block POMEGRANATE_PLANKS = null;
        @ObjectHolder("pomegranate_slab")
        public static final Block POMEGRANATE_SLAB = null;
        @ObjectHolder("pomegranate_stairs")
        public static final Block POMEGRANATE_STAIRS = null;
        @ObjectHolder("pomegranate_door")
        public static final Block POMEGRANATE_DOOR = null;
        @ObjectHolder("pomegranate_trapdoor")
        public static final Block POMEGRANATE_TRAPDOOR = null;
        @ObjectHolder("pomegranate_leaves")
        public static final Block POMEGRANATE_LEAVES = null;
        @ObjectHolder("pomegranate_sapling")
        public static final Block POMEGRANATE_SAPLING = null;
        @ObjectHolder("golden_apple_leaves")
        public static final Block GOLDEN_APPLE_LEAVES = null;
        @ObjectHolder("golden_apple_sapling")
        public static final Block GOLDEN_APPLE_SAPLING = null;
        @ObjectHolder("nest")
        public static final Block NEST_BLOCK = null;
        @ObjectHolder("wild_rose")
        public static final Block WILD_ROSE = null;
        @ObjectHolder("limestone")
        public static final Block LIMESTONE = null;
        @ObjectHolder("limestone_slab")
        public static final Block LIMESTONE_SLAB = null;
        @ObjectHolder("limestone_stairs")
        public static final Block LIMESTONE_STAIRS = null;
        @ObjectHolder("polished_limestone")
        public static final Block POLISHED_LIMESTONE = null;
        @ObjectHolder("polished_limestone_slab")
        public static final Block POLISHED_LIMESTONE_SLAB = null;
        @ObjectHolder("polished_limestone_stairs")
        public static final Block POLISHED_LIMESTONE_STAIRS = null;
        @ObjectHolder("limestone_pillar")
        public static final Block LIMESTONE_PILLAR = null;
        @ObjectHolder("marble")
        public static final Block MARBLE = null;
        @ObjectHolder("marble_slab")
        public static final Block MARBLE_SLAB = null;
        @ObjectHolder("marble_stairs")
        public static final Block MARBLE_STAIRS = null;
        @ObjectHolder("polished_marble")
        public static final Block POLISHED_MARBLE = null;
        @ObjectHolder("polished_marble_slab")
        public static final Block POLISHED_MARBLE_SLAB = null;
        @ObjectHolder("polished_marble_stairs")
        public static final Block POLISHED_MARBLE_STAIRS = null;
        @ObjectHolder("marble_pillar")
        public static final Block MARBLE_PILLAR = null;
        @ObjectHolder("cretan_stone")
        public static final Block CRETAN_STONE = null;
        @ObjectHolder("chiseled_cretan_stone")
        public static final Block CHISELED_CRETAN_STONE = null;
        @ObjectHolder("cretan_stone_brick")
        public static final Block CRETAN_STONE_BRICK = null;
        @ObjectHolder("chiseled_cretan_stone_brick")
        public static final Block CHISELED_CRETAN_STONE_BRICK = null;
        @ObjectHolder("cracked_cretan_stone_brick")
        public static final Block CRACKED_CRETAN_STONE_BRICK = null;
        @ObjectHolder("polished_cretan_stone")
        public static final Block POLISHED_CRETAN_STONE = null;
        @ObjectHolder("cracked_polished_cretan_stone")
        public static final Block CRACKED_POLISHED_CRETAN_STONE = null;
        @ObjectHolder("mysterious_box")
        public static final Block MYSTERIOUS_BOX = null;
        @ObjectHolder("gigante_head")
        public static final Block GIGANTE_HEAD = null;
        @ObjectHolder("orthus_head")
        public static final Block ORTHUS_HEAD = null;
        @ObjectHolder("cerberus_head")
        public static final Block CERBERUS_HEAD = null;
        @ObjectHolder("ichor_infused_block")
        public static final Block ICHOR_INFUSED_BLOCK = null;
        @ObjectHolder("golden_string")
        public static final Block GOLDEN_STRING_BLOCK = null;
        @ObjectHolder("oil")
        public static final Block OIL = null;
        @ObjectHolder("oil_lamp")
        public static final Block OIL_LAMP = null;
        @ObjectHolder("glow")
        public static final Block GLOW = null;
        // Vase //
        @ObjectHolder("terracotta_vase")
        public static final Block TERRACOTTA_VASE = null;
        @ObjectHolder("white_terracotta_vase")
        public static final Block WHITE_TERRACOTTA_VASE = null;
        @ObjectHolder("orange_terracotta_vase")
        public static final Block ORANGE_TERRACOTTA_VASE = null;
        @ObjectHolder("magenta_terracotta_vase")
        public static final Block MAGENTA_TERRACOTTA_VASE = null;
        @ObjectHolder("light_blue_terracotta_vase")
        public static final Block LIGHT_BLUE_TERRACOTTA_VASE = null;
        @ObjectHolder("yellow_terracotta_vase")
        public static final Block YELLOW_TERRACOTTA_VASE = null;
        @ObjectHolder("lime_terracotta_vase")
        public static final Block LIME_TERRACOTTA_VASE = null;
        @ObjectHolder("pink_terracotta_vase")
        public static final Block PINK_TERRACOTTA_VASE = null;
        @ObjectHolder("gray_terracotta_vase")
        public static final Block GRAY_TERRACOTTA_VASE = null;
        @ObjectHolder("light_gray_terracotta_vase")
        public static final Block LIGHT_GRAY_TERRACOTTA_VASE = null;
        @ObjectHolder("cyan_terracotta_vase")
        public static final Block CYAN_TERRACOTTA_VASE = null;
        @ObjectHolder("purple_terracotta_vase")
        public static final Block PURPLE_TERRACOTTA_VASE = null;
        @ObjectHolder("blue_terracotta_vase")
        public static final Block BLUE_TERRACOTTA_VASE = null;
        @ObjectHolder("brown_terracotta_vase")
        public static final Block BROWN_TERRACOTTA_VASE = null;
        @ObjectHolder("green_terracotta_vase")
        public static final Block GREEN_TERRACOTTA_VASE = null;
        @ObjectHolder("red_terracotta_vase")
        public static final Block RED_TERRACOTTA_VASE = null;
        @ObjectHolder("black_terracotta_vase")
        public static final Block BLACK_TERRACOTTA_VASE = null;


        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            GreekFantasy.LOGGER.debug("registerBlocks");

            registerLogPlanksEtc(event, "olive", AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD), 5, 5, 20);
            registerLeaves(event, "olive", 30, 60);
            registerLogPlanksEtc(event, "pomegranate", AbstractBlock.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.2F, 3.0F).sound(SoundType.WOOD), 0, 0, 0);
            registerLeaves(event, "pomegranate", 0, 0);
            registerLeaves(event, "golden_apple", 30, 60);

            registerBlockPolishedSlabAndStairs(event, AbstractBlock.Properties.of(Material.STONE, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(1.5F, 6.0F), "marble");
            registerBlockPolishedSlabAndStairs(event, AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F), "limestone");

            registerBlockPolishedChiseledAndBricks(event, AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(99.0F, 1200.0F), "cretan_stone");

            event.getRegistry().registerAll(
                    new ReedsBlock(AbstractBlock.Properties.of(Material.WATER_PLANT).noCollission().instabreak().randomTicks().sound(SoundType.CROP))
                            .setRegistryName(MODID, "reeds"),
                    new SaplingBlock(new OliveTree(), AbstractBlock.Properties.of(Material.PLANT).noCollission().randomTicks()
                            .instabreak().noOcclusion().sound(SoundType.GRASS))
                            .setRegistryName(MODID, "olive_sapling"),
                    new PomegranateSaplingBlock(new PomegranateTree(), AbstractBlock.Properties.of(Material.PLANT).noCollission().randomTicks()
                            .instabreak().noOcclusion().sound(SoundType.GRASS))
                            .setRegistryName(MODID, "pomegranate_sapling"),
                    new SaplingBlock(new GoldenAppleTree(), AbstractBlock.Properties.of(Material.PLANT).noCollission().randomTicks()
                            .instabreak().noOcclusion().sound(SoundType.GRASS))
                            .setRegistryName(MODID, "golden_apple_sapling"),
                    new NestBlock(AbstractBlock.Properties.of(Material.GRASS, MaterialColor.COLOR_BROWN).strength(0.5F).sound(SoundType.GRASS)
                            .hasPostProcess((s, r, p) -> true).noOcclusion())
                            .setRegistryName(MODID, "nest"),
                    new WildRoseBlock(Effects.SATURATION, 9, AbstractBlock.Properties.of(Material.PLANT).noCollission().instabreak().sound(SoundType.GRASS))
                            .setRegistryName(GreekFantasy.MODID, "wild_rose"),
                    new CappedPillarBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.QUARTZ).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
                            .hasPostProcess((s, r, p) -> true).noOcclusion())
                            .setRegistryName(MODID, "marble_pillar"),
                    new CappedPillarBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)
                            .hasPostProcess((s, r, p) -> true).noOcclusion())
                            .setRegistryName(MODID, "limestone_pillar"),
                    new MysteriousBoxBlock(AbstractBlock.Properties.of(Material.WOOD).strength(0.8F, 2.0F).sound(SoundType.WOOD).noOcclusion())
                            .setRegistryName(MODID, "mysterious_box"),
                    new MobHeadBlock(HeadType.GIGANTE, AbstractBlock.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion())
                            .setRegistryName(GreekFantasy.MODID, "gigante_head"),
                    new OrthusHeadBlock(HeadType.ORTHUS, AbstractBlock.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion())
                            .setRegistryName(GreekFantasy.MODID, "orthus_head"),
                    new MobHeadBlock(HeadType.CERBERUS, AbstractBlock.Properties.of(Material.DECORATION).strength(1.0F).noOcclusion())
                            .setRegistryName(GreekFantasy.MODID, "cerberus_head"),
                    new IchorInfusedBlock(AbstractBlock.Properties.copy(Blocks.GOLD_BLOCK))
                            .setRegistryName(MODID, "ichor_infused_block"),
                    new GoldenStringBlock(AbstractBlock.Properties.of(Material.DECORATION).lightLevel(b -> 8).instabreak().noCollission().noOcclusion())
                            .setRegistryName(MODID, "golden_string"),
                    new OilBlock(AbstractBlock.Properties.of(Material.FIRE).noOcclusion().noCollission().instabreak()
                            .randomTicks().lightLevel((state) -> 11).sound(SoundType.WET_GRASS))
                            .setRegistryName(MODID, "oil"),
                    new OilLampBlock(AbstractBlock.Properties.of(Material.STONE).noOcclusion().lightLevel(b -> b.getValue(OilLampBlock.LIT) ? 11 : 0).strength(0.2F, 0.1F))
                            .setRegistryName(MODID, "oil_lamp"),
                    new GlowBlock(AbstractBlock.Properties.of(Material.AIR).strength(-1F).noCollission().lightLevel(b -> 11).randomTicks())
                            .setRegistryName(MODID, "glow")
            );

            // Vase blocks
            event.getRegistry().register(new VaseBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_ORANGE).strength(0.5F, 1.0F).noOcclusion())
                    .setRegistryName(MODID, "terracotta_vase"));
            for (final DyeColor d : DyeColor.values()) {
                event.getRegistry().register(new VaseBlock(AbstractBlock.Properties.of(Material.STONE, d.getMaterialColor()).strength(0.5F, 1.0F).noOcclusion())
                        .setRegistryName(MODID, d.getSerializedName() + "_terracotta_vase"));
            }
        }

        /**
         * Creates and registers wood-related blocks (log, wood, stripped log, stripped wood, planks,
         * slab, stairs, door, trapdoor)
         *
         * @param event              the block registry event
         * @param registryName       the registry name to use as a prefix for the various blocks
         * @param properties         the block properties to use for each wood
         * @param fireSpread         the Fire Spread Speed (5)
         * @param logFlammability    the Flammability of log-based blocks (5)
         * @param planksFlammability the Flammability of planks-based blocks (20)
         */
        private static void registerLogPlanksEtc(final RegistryEvent.Register<Block> event, final String registryName,
                                                 final Block.Properties properties, final int fireSpread, final int logFlammability, final int planksFlammability) {
            // stripped log block
            final Block strippedLog = new RotatedPillarBlock(properties) {
                @Override
                public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return fireSpread;
                }

                @Override
                public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return logFlammability;
                }
            };
            // stripped wood block
            final Block strippedWood = new RotatedPillarBlock(properties) {
                @Override
                public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return fireSpread;
                }

                @Override
                public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return logFlammability;
                }
            };
            // log block
            final Block log = new RotatedPillarBlock(AbstractBlock.Properties.of(Material.WOOD, (state) -> {
                return state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MaterialColor.SAND : MaterialColor.WOOD;
            }).strength(2.0F).sound(SoundType.WOOD)) {
                @Override
                public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return fireSpread;
                }

                @Override
                public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return logFlammability;
                }

                @Override
                public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
                    if (toolType == ToolType.AXE) {
                        return strippedLog.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
                    }
                    return super.getToolModifiedState(state, world, pos, player, stack, toolType);
                }
            };
            // wood block
            final Block wood = new RotatedPillarBlock(properties) {
                @Override
                public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return fireSpread;
                }

                @Override
                public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return logFlammability;
                }

                @Override
                public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
                    if (toolType == ToolType.AXE) {
                        return strippedWood.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
                    }
                    return super.getToolModifiedState(state, world, pos, player, stack, toolType);
                }
            };
            // planks block
            final Block planks = new Block(properties) {
                @Override
                public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return fireSpread;
                }

                @Override
                public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return planksFlammability;
                }
            };
            // slab block
            final Block slab = new SlabBlock(properties) {
                @Override
                public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return fireSpread;
                }

                @Override
                public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return planksFlammability;
                }
            };
            // stairs block
            final Block stairs = new StairsBlock(() -> planks.defaultBlockState(), properties) {
                @Override
                public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return fireSpread;
                }

                @Override
                public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                    return planksFlammability;
                }
            };
            // register logs, planks, slab, stair
            event.getRegistry().registerAll(
                    log.setRegistryName(MODID, registryName + "_log"),
                    strippedLog.setRegistryName(MODID, "stripped_" + registryName + "_log"),
                    wood.setRegistryName(MODID, registryName + "_wood"),
                    strippedWood.setRegistryName(MODID, "stripped_" + registryName + "_wood"),
                    planks.setRegistryName(MODID, registryName + "_planks"),
                    slab.setRegistryName(MODID, registryName + "_slab"),
                    stairs.setRegistryName(MODID, registryName + "_stairs")
            );
            // register door and trapdoor
            final Block.Properties notSolid = Block.Properties.copy(planks).noOcclusion().isValidSpawn((b, i, p, a) -> false);
            event.getRegistry().registerAll(
                    new DoorBlock(notSolid).setRegistryName(MODID, registryName + "_door"),
                    new TrapDoorBlock(notSolid).setRegistryName(MODID, registryName + "_trapdoor")
            );
        }

        /**
         * Creates and registers a basic leaves block
         *
         * @param event        the block register event
         * @param registryName the block registry name
         * @param fireSpread   the FireSpreadSpeed (30)
         * @param flammability the Flammability (60)
         */
        private static void registerLeaves(final RegistryEvent.Register<Block> event, final String registryName, final int fireSpread, final int flammability) {
            event.getRegistry().register(
                    new LeavesBlock(AbstractBlock.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS)
                            .noOcclusion().isValidSpawn(GFRegistry.BlockReg::allowsSpawnOnLeaves).isSuffocating((s, r, p) -> false).isViewBlocking((s, r, p) -> false)) {
                        @Override
                        public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                            return fireSpread;
                        }

                        @Override
                        public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
                            return flammability;
                        }
                    }.setRegistryName(MODID, registryName + "_leaves")
            );
        }

        private static void registerBlockPolishedSlabAndStairs(final RegistryEvent.Register<Block> event, final Block.Properties properties, final String registryName) {
            final Block raw = new Block(properties).setRegistryName(MODID, registryName);
            final Block polished = new Block(properties).setRegistryName(MODID, "polished_" + registryName);
            // raw, slab, and stairs
            event.getRegistry().register(raw);
            event.getRegistry().register(new SlabBlock(properties).setRegistryName(MODID, registryName + "_slab"));
            event.getRegistry().register(new StairsBlock(() -> raw.defaultBlockState(), properties).setRegistryName(MODID, registryName + "_stairs"));
            // polished, slab, and stairs
            event.getRegistry().register(polished);
            event.getRegistry().register(new SlabBlock(properties).setRegistryName(MODID, "polished_" + registryName + "_slab"));
            event.getRegistry().register(new StairsBlock(() -> polished.defaultBlockState(), properties).setRegistryName(MODID, "polished_" + registryName + "_stairs"));
        }

        private static void registerBlockPolishedChiseledAndBricks(final RegistryEvent.Register<Block> event, final Block.Properties properties, final String registryName) {
            // raw, polished, chiseled, brick, and chiseled_brick
            event.getRegistry().register(new Block(properties).setRegistryName(MODID, registryName));
            event.getRegistry().register(new Block(properties).setRegistryName(MODID, "chiseled_" + registryName));
            event.getRegistry().register(new Block(properties).setRegistryName(MODID, "polished_" + registryName));
            event.getRegistry().register(new Block(properties).setRegistryName(MODID, "cracked_polished_" + registryName));
            event.getRegistry().register(new Block(properties).setRegistryName(MODID, registryName + "_brick"));
            event.getRegistry().register(new Block(properties).setRegistryName(MODID, "chiseled_" + registryName + "_brick"));
            event.getRegistry().register(new Block(properties).setRegistryName(MODID, "cracked_" + registryName + "_brick"));
        }

        private static Boolean allowsSpawnOnLeaves(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entity) {
            return entity == EntityType.OCELOT || entity == EntityType.PARROT || entity == EntityReg.DRYAD_ENTITY || entity == EntityReg.LAMPAD_ENTITY;
        }
    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class ItemReg {

        public static final ItemGroup GREEK_GROUP = new ItemGroup("greekfantasy") {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(ItemReg.PANFLUTE);
            }
        };

        @ObjectHolder("panflute")
        public static final Item PANFLUTE = null;
        @ObjectHolder("wooden_lyre")
        public static final Item WOODEN_LYRE = null;
        @ObjectHolder("gold_lyre")
        public static final Item GOLD_LYRE = null;
        @ObjectHolder("iron_club")
        public static final Item IRON_CLUB = null;
        @ObjectHolder("stone_club")
        public static final Item STONE_CLUB = null;
        @ObjectHolder("wooden_club")
        public static final Item WOODEN_CLUB = null;
        @ObjectHolder("flint_knife")
        public static final Item FLINT_KNIFE = null;
        @ObjectHolder("dragon_tooth")
        public static final Item DRAGON_TOOTH = null;
        @ObjectHolder("ichor")
        public static final Item ICHOR = null;
        @ObjectHolder("horn")
        public static final Item HORN = null;
        @ObjectHolder("thunderbolt")
        public static final Item THUNDERBOLT = null;
        @ObjectHolder("helm_of_darkness")
        public static final Item HELM_OF_DARKNESS = null;
        @ObjectHolder("winged_sandals")
        public static final Item WINGED_SANDALS = null;
        @ObjectHolder("magic_feather")
        public static final Item MAGIC_FEATHER = null;
        @ObjectHolder("golden_bridle")
        public static final Item GOLDEN_BRIDLE = null;
        @ObjectHolder("snakeskin")
        public static final Item SNAKESKIN = null;
        @ObjectHolder("purified_snakeskin")
        public static final Item PURIFIED_SNAKESKIN = null;
        @ObjectHolder("tough_snakeskin")
        public static final Item TOUGH_SNAKESKIN = null;
        @ObjectHolder("styxian_shard")
        public static final Item STYXIAN_SHARD = null;
        @ObjectHolder("boar_ear")
        public static final Item BOAR_EAR = null;
        @ObjectHolder("pig_wand")
        public static final Item PIG_WAND = null;
        @ObjectHolder("golden_ball")
        public static final Item GOLDEN_BALL = null;
        @ObjectHolder("golden_string")
        public static final Item GOLDEN_STRING = null;
        @ObjectHolder("discus")
        public static final Item DISCUS = null;
        @ObjectHolder("cursed_bow")
        public static final Item CURSED_BOW = null;
        @ObjectHolder("artemis_bow")
        public static final Item ARTEMIS_BOW = null;
        @ObjectHolder("apollo_bow")
        public static final Item APOLLO_BOW = null;
        @ObjectHolder("bident")
        public static final Item BIDENT = null;
        @ObjectHolder("wooden_spear")
        public static final Item WOODEN_SPEAR = null;
        @ObjectHolder("stone_spear")
        public static final Item STONE_SPEAR = null;
        @ObjectHolder("iron_spear")
        public static final Item IRON_SPEAR = null;
        @ObjectHolder("nemean_lion_hide")
        public static final Item NEMEAN_LION_HIDE = null;
        @ObjectHolder("golden_fleece")
        public static final Item GOLDEN_FLEECE = null;
        @ObjectHolder("fiery_gear")
        public static final Item FIERY_GEAR = null;
        @ObjectHolder("spider_banner_pattern")
        public static final Item SPIDER_PATTERN = null;
        @ObjectHolder("web_ball")
        public static final Item WEB_BALL = null;
        @ObjectHolder("olive_oil")
        public static final Item OLIVE_OIL = null;
        @ObjectHolder("salve")
        public static final Item SALVE = null;
        @ObjectHolder("greek_fire")
        public static final Item GREEK_FIRE = null;
        @ObjectHolder("palladium")
        public static final Item PALLADIUM = null;

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            GreekFantasy.LOGGER.debug("registerItems");
            final boolean nerfAmbrosia = GreekFantasy.CONFIG.NERF_AMBROSIA.get();
            // items
            event.getRegistry().registerAll(
                    new PanfluteItem(new Item.Properties().tab(GREEK_GROUP).stacksTo(1))
                            .setRegistryName(MODID, "panflute"),
                    new LyreItem(SoundEvents.NOTE_BLOCK_HARP, new Item.Properties().tab(GREEK_GROUP).stacksTo(1))
                            .setRegistryName(MODID, "wooden_lyre"),
                    new LyreItem(SoundEvents.NOTE_BLOCK_GUITAR, new Item.Properties().tab(GREEK_GROUP).stacksTo(1))
                            .setRegistryName(MODID, "gold_lyre"),
                    new FlintKnifeItem(ItemTier.WOOD, 3, -2.0F, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "flint_knife"),
                    new ClubItem(ItemTier.IRON, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "iron_club"),
                    new ClubItem(ItemTier.STONE, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "stone_club"),
                    new ClubItem(ItemTier.WOOD, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "wooden_club"),
                    new IvorySwordItem(ItemTier.DIAMOND, 3, -2.2F, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "ivory_sword"),
                    new ConchItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "conch"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "talos_heart"),
                    new ThunderboltItem(new Item.Properties().rarity(Rarity.RARE).tab(GREEK_GROUP)
                            .durability(GreekFantasy.CONFIG.THUNDERBOLT_DURABILITY.get()))
                            .setRegistryName(MODID, "thunderbolt"),
                    new BagOfWindItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(GREEK_GROUP)
                            .durability(GreekFantasy.CONFIG.BAG_OF_WIND_DURABILITY.get()))
                            .setRegistryName(MODID, "bag_of_wind"),
                    new UnicornHornItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(GREEK_GROUP)
                            .durability(GreekFantasy.CONFIG.UNICORN_HORN_DURABILITY.get()))
                            .setRegistryName(MODID, "unicorn_horn"),
                    new HealingRodItem(new Item.Properties().rarity(Rarity.RARE).tab(GREEK_GROUP)
                            .durability(GreekFantasy.CONFIG.HEALING_ROD_DURABILITY.get()))
                            .setRegistryName(MODID, "healing_rod"),
                    new DragonToothItem(new Item.Properties().rarity(Rarity.UNCOMMON).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "dragon_tooth"),
                    new PigWandItem(new Item.Properties().rarity(Rarity.RARE).tab(GREEK_GROUP)
                            .durability(GreekFantasy.CONFIG.PIG_WAND_DURABILITY.get()))
                            .setRegistryName(MODID, "pig_wand"),
                    new EnchantedBowItem.CursedBowItem(new Item.Properties().durability(384).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "cursed_bow"),
                    new EnchantedBowItem.ApolloBowItem(new Item.Properties().durability(620).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "apollo_bow"),
                    new EnchantedBowItem.ArtemisBowItem(new Item.Properties().durability(562).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "artemis_bow"),
                    new BidentItem(ItemTier.DIAMOND, new Item.Properties().tab(GREEK_GROUP)
                            .setISTER(() -> () -> greekfantasy.client.render.tileentity.ClientISTERProvider.bakeSpearISTER("bident")))
                            .setRegistryName(MODID, "bident"),
                    new SpearItem(ItemTier.WOOD, new Item.Properties().tab(GREEK_GROUP)
                            .setISTER(() -> () -> greekfantasy.client.render.tileentity.ClientISTERProvider.bakeSpearISTER("wooden_spear")))
                            .setRegistryName(MODID, "wooden_spear"),
                    new SpearItem(ItemTier.STONE, new Item.Properties().tab(GREEK_GROUP)
                            .setISTER(() -> () -> greekfantasy.client.render.tileentity.ClientISTERProvider.bakeSpearISTER("stone_spear")))
                            .setRegistryName(MODID, "stone_spear"),
                    new SpearItem(ItemTier.IRON, new Item.Properties().tab(GREEK_GROUP)
                            .setISTER(() -> () -> greekfantasy.client.render.tileentity.ClientISTERProvider.bakeSpearISTER("iron_spear")))
                            .setRegistryName(MODID, "iron_spear"),
                    new PalladiumItem(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "palladium"),
                    new MirrorItem(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "mirror"),
                    new SnakeskinArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "snakeskin_helmet"),
                    new SnakeskinArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "snakeskin_chestplate"),
                    new SnakeskinArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "snakeskin_leggings"),
                    new SnakeskinArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "snakeskin_boots"),
                    new AchillesArmorItem(EquipmentSlotType.HEAD, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "achilles_helmet"),
                    new AchillesArmorItem(EquipmentSlotType.CHEST, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "achilles_chestplate"),
                    new AchillesArmorItem(EquipmentSlotType.LEGS, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "achilles_leggings"),
                    new AchillesArmorItem(EquipmentSlotType.FEET, new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "achilles_boots"),
                    new WingedSandalsItem(new Item.Properties().rarity(Rarity.RARE).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "winged_sandals"),
                    new HelmOfDarknessItem(new Item.Properties().rarity(Rarity.RARE).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "helm_of_darkness"),
                    new NemeanLionHideItem(new Item.Properties().rarity(Rarity.EPIC).fireResistant().setNoRepair().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "nemean_lion_hide"),
                    new GorgonBloodItem(new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "gorgon_blood"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "horn"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "pomegranate"),
                    new PomegranateSeedsItem(new Item.Properties().tab(GREEK_GROUP).food(PomegranateSeedsItem.POMEGRANATE_SEEDS))
                            .setRegistryName(MODID, "pomegranate_seeds"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "olives"),
                    new OliveOilItem(new Item.Properties().tab(GREEK_GROUP).stacksTo(16))
                            .setRegistryName(MODID, "olive_oil"),
                    new SalveItem(new Item.Properties().tab(GREEK_GROUP).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE))
                            .setRegistryName(MODID, "salve")
            );

            event.getRegistry().registerAll(
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "boar_ear"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "boar_tusk"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "golden_bridle"),
                    new AmbrosiaItem(new Item.Properties().food(nerfAmbrosia ? Foods.GOLDEN_APPLE : Foods.ENCHANTED_GOLDEN_APPLE)
                            .tab(GREEK_GROUP).rarity(nerfAmbrosia ? Rarity.RARE : Rarity.EPIC)) {
                    }.setRegistryName(MODID, "ambrosia"),
                    new HornOfPlentyItem(new Item.Properties().durability(24).rarity(Rarity.RARE).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "horn_of_plenty"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "magic_feather"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "snakeskin"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "purified_snakeskin"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "tough_snakeskin"),
                    new Item(new Item.Properties().rarity(Rarity.UNCOMMON).tab(GREEK_GROUP)) {
                        @Override
                        public boolean isFoil(ItemStack stack) {
                            return true;
                        }
                    }.setRegistryName(MODID, "ichor"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "dog_claw"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "fiery_hide"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "styxian_shard"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "snake_fang"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "cursed_hair"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "fiery_bat_wing"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "fiery_gear"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "golden_string"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "golden_ball"),
                    new Item(new Item.Properties().tab(GREEK_GROUP))
                            .setRegistryName(MODID, "golden_fleece"),
                    new DiscusItem(new Item.Properties().stacksTo(16).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "discus"),
                    new WebBallItem(new Item.Properties().stacksTo(16).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "web_ball"),
                    new GreekFireItem(new Item.Properties().stacksTo(16).tab(GREEK_GROUP))
                            .setRegistryName(MODID, "greek_fire")
            );

            // block items
            registerItemBlocks(event,
                    BlockReg.REEDS, BlockReg.GOLDEN_APPLE_SAPLING, BlockReg.NEST_BLOCK, BlockReg.WILD_ROSE, BlockReg.GOLDEN_APPLE_LEAVES,
                    BlockReg.OLIVE_SAPLING, BlockReg.OLIVE_LOG, BlockReg.STRIPPED_OLIVE_LOG, BlockReg.OLIVE_WOOD, BlockReg.STRIPPED_OLIVE_WOOD,
                    BlockReg.OLIVE_PLANKS, BlockReg.OLIVE_SLAB, BlockReg.OLIVE_STAIRS, BlockReg.OLIVE_TRAPDOOR, BlockReg.OLIVE_LEAVES,
                    BlockReg.POMEGRANATE_SAPLING, BlockReg.POMEGRANATE_LOG, BlockReg.STRIPPED_POMEGRANATE_LOG, BlockReg.POMEGRANATE_WOOD, BlockReg.STRIPPED_POMEGRANATE_WOOD,
                    BlockReg.POMEGRANATE_PLANKS, BlockReg.POMEGRANATE_SLAB, BlockReg.POMEGRANATE_STAIRS, BlockReg.POMEGRANATE_TRAPDOOR, BlockReg.POMEGRANATE_LEAVES,
                    BlockReg.MARBLE, BlockReg.MARBLE_SLAB, BlockReg.MARBLE_STAIRS, BlockReg.POLISHED_MARBLE, BlockReg.POLISHED_MARBLE_SLAB,
                    BlockReg.POLISHED_MARBLE_STAIRS, BlockReg.MARBLE_PILLAR,
                    BlockReg.CRETAN_STONE, BlockReg.CHISELED_CRETAN_STONE, BlockReg.CRETAN_STONE_BRICK, BlockReg.CHISELED_CRETAN_STONE_BRICK,
                    BlockReg.CRACKED_CRETAN_STONE_BRICK, BlockReg.POLISHED_CRETAN_STONE, BlockReg.CRACKED_POLISHED_CRETAN_STONE,
                    BlockReg.LIMESTONE, BlockReg.LIMESTONE_SLAB, BlockReg.LIMESTONE_STAIRS, BlockReg.POLISHED_LIMESTONE, BlockReg.POLISHED_LIMESTONE_SLAB,
                    BlockReg.POLISHED_LIMESTONE_STAIRS, BlockReg.LIMESTONE_PILLAR, BlockReg.ICHOR_INFUSED_BLOCK,
                    BlockReg.TERRACOTTA_VASE, BlockReg.WHITE_TERRACOTTA_VASE, BlockReg.ORANGE_TERRACOTTA_VASE, BlockReg.MAGENTA_TERRACOTTA_VASE,
                    BlockReg.LIGHT_BLUE_TERRACOTTA_VASE, BlockReg.YELLOW_TERRACOTTA_VASE, BlockReg.LIME_TERRACOTTA_VASE,
                    BlockReg.PINK_TERRACOTTA_VASE, BlockReg.GRAY_TERRACOTTA_VASE, BlockReg.LIGHT_GRAY_TERRACOTTA_VASE,
                    BlockReg.CYAN_TERRACOTTA_VASE, BlockReg.PURPLE_TERRACOTTA_VASE, BlockReg.BLUE_TERRACOTTA_VASE,
                    BlockReg.BROWN_TERRACOTTA_VASE, BlockReg.GREEN_TERRACOTTA_VASE, BlockReg.RED_TERRACOTTA_VASE, BlockReg.BLACK_TERRACOTTA_VASE,
                    BlockReg.OIL_LAMP);
            // tall block items
            registerTallItemBlocks(event, BlockReg.OLIVE_DOOR, BlockReg.POMEGRANATE_DOOR);

            event.getRegistry().register(new MobHeadItem(BlockReg.GIGANTE_HEAD, new Item.Properties()
                    .tab(GREEK_GROUP).setISTER(() -> greekfantasy.client.render.tileentity.ClientISTERProvider::bakeGiganteHeadISTER))
                    .setRegistryName(MODID, "gigante_head"));
            event.getRegistry().register(new OrthusHeadItem(BlockReg.ORTHUS_HEAD, new Item.Properties()
                    .tab(GREEK_GROUP).setISTER(() -> greekfantasy.client.render.tileentity.ClientISTERProvider::bakeOrthusHeadISTER))
                    .setRegistryName(MODID, "orthus_head"));
            event.getRegistry().register(new MobHeadItem(BlockReg.CERBERUS_HEAD, new Item.Properties()
                    .tab(GREEK_GROUP).setISTER(() -> greekfantasy.client.render.tileentity.ClientISTERProvider::bakeCerberusHeadISTER))
                    .setRegistryName(MODID, "cerberus_head"));

            // mysterious box item
            event.getRegistry().register(new BlockItem(BlockReg.MYSTERIOUS_BOX, new Item.Properties().tab(GREEK_GROUP)) {
                @OnlyIn(Dist.CLIENT)
                public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
                    tooltip.add(new TranslationTextComponent("block.greekfantasy.mysterious_box.tooltip").withStyle(TextFormatting.ITALIC, TextFormatting.GRAY));
                }
            }.setRegistryName(MODID, "mysterious_box"));

            // banner pattern
            final BannerPattern SPIDER_BANNER_PATTERN = BannerPattern.create("greekfantasy_spider", "greekfantasy_spider", "greekfantasy_spider", true);
            event.getRegistry().register(new BannerPatternItem(SPIDER_BANNER_PATTERN, new Item.Properties()
                    .tab(GREEK_GROUP).stacksTo(1).rarity(Rarity.RARE))
                    .setRegistryName(MODID, "spider_banner_pattern"));

            // spawn eggs
            registerSpawnEgg(event, EntityReg.ARA_ENTITY, "ara", 0xffffff, 0xbbbbbb);
            registerSpawnEgg(event, EntityReg.ARACHNE_ENTITY, "arachne", 0x9c7b50, 0xa80e0e);
            registerSpawnEgg(event, EntityReg.ARION_ENTITY, "arion", 0xdfc014, 0xb58614);
            registerSpawnEgg(event, EntityReg.CENTAUR_ENTITY, "centaur", 0x734933, 0x83251f);
            registerSpawnEgg(event, EntityReg.CERASTES_ENTITY, "cerastes", 0x847758, 0x997c4d);
            registerSpawnEgg(event, EntityReg.CHARYBDIS_ENTITY, "charybdis", 0x2e5651, 0x411e5e);
            registerSpawnEgg(event, EntityReg.CIRCE_ENTITY, "circe", 0x844797, 0xe8c669);
            registerSpawnEgg(event, EntityReg.CRETAN_MINOTAUR_ENTITY, "cretan_minotaur", 0x2a2a2a, 0x734933);
            registerSpawnEgg(event, EntityReg.CYCLOPES_ENTITY, "cyclopes", 0xda662c, 0x2c1e0e);
            registerSpawnEgg(event, EntityReg.CYPRIAN_ENTITY, "cyprian", 0x443626, 0x83251f);
            registerSpawnEgg(event, EntityReg.DRAKAINA_ENTITY, "drakaina", 0x724e36, 0x398046);
            registerSpawnEgg(event, EntityReg.DRYAD_ENTITY, "dryad", 0x443626, 0xfed93f);
            registerSpawnEgg(event, EntityReg.ELPIS_ENTITY, "elpis", 0xe7aae4, 0xeeeeee);
            registerSpawnEgg(event, EntityReg.EMPUSA_ENTITY, "empusa", 0x222222, 0x83251f);
            registerSpawnEgg(event, EntityReg.FURY_ENTITY, "fury", 0xbd4444, 0x6c2426);
            registerSpawnEgg(event, EntityReg.GIANT_BOAR_ENTITY, "giant_boar", 0x5b433a, 0xe8a074);
            registerSpawnEgg(event, EntityReg.GIGANTE_ENTITY, "gigante", 0xd3dba7, 0x6a602b);
            registerSpawnEgg(event, EntityReg.GOLDEN_RAM_ENTITY, "golden_ram", 0xdfc014, 0xd08d26);
            registerSpawnEgg(event, EntityReg.GORGON_ENTITY, "gorgon", 0x3a8228, 0xbcbcbc);
            registerSpawnEgg(event, EntityReg.HARPY_ENTITY, "harpy", 0x724e36, 0x332411);
            registerSpawnEgg(event, EntityReg.HYDRA_ENTITY, "hydra", 0x372828, 0x9d4217);
            registerSpawnEgg(event, EntityReg.LAMPAD_ENTITY, "lampad", 0x643026, 0xfed93f);
            registerSpawnEgg(event, EntityReg.MAD_COW_ENTITY, "mad_cow", 0x443626, 0xcf9797);
            registerSpawnEgg(event, EntityReg.MAKHAI_ENTITY, "makhai", 0x513f38, 0xf33531);
            registerSpawnEgg(event, EntityReg.MINOTAUR_ENTITY, "minotaur", 0x443626, 0x734933);
            registerSpawnEgg(event, EntityReg.NAIAD_ENTITY, "naiad", 0x7caba1, 0xe67830);
            registerSpawnEgg(event, EntityReg.NEMEAN_LION_ENTITY, "nemean_lion", 0xd08d26, 0x7d3107);
            registerSpawnEgg(event, EntityReg.ORTHUS_ENTITY, "orthus", 0x493569, 0xe42e2e);
            registerSpawnEgg(event, EntityReg.PEGASUS_ENTITY, "pegasus", 0x916535, 0xe8e8e8);
            registerSpawnEgg(event, EntityReg.PYTHON_ENTITY, "python", 0x3a8228, 0x1e4c11);
            registerSpawnEgg(event, EntityReg.SATYR_ENTITY, "satyr", 0x54371d, 0xa16648);
            registerSpawnEgg(event, EntityReg.SHADE_ENTITY, "shade", 0x222222, 0x000000);
            registerSpawnEgg(event, EntityReg.SIREN_ENTITY, "siren", 0x729f92, 0x398046);
            registerSpawnEgg(event, EntityReg.UNICORN_ENTITY, "unicorn", 0xeeeeee, 0xe8e8e8);
            registerSpawnEgg(event, EntityReg.WHIRL_ENTITY, "whirl", 0x1EF6FF, 0xededed);
        }

        private static void registerItemBlock(final RegistryEvent.Register<Item> event, final Block block) {
            event.getRegistry().register(new BlockItem(block, new Item.Properties().tab(GREEK_GROUP)).setRegistryName(block.getRegistryName()));
        }

        private static void registerItemBlocks(final RegistryEvent.Register<Item> event, final Block... blocks) {
            for (final Block b : blocks) {
                registerItemBlock(event, b);
            }
        }

        private static void registerTallItemBlock(final RegistryEvent.Register<Item> event, final Block block) {
            event.getRegistry().register(new TallBlockItem(block, new Item.Properties().tab(GREEK_GROUP)).setRegistryName(block.getRegistryName()));
        }

        private static void registerTallItemBlocks(final RegistryEvent.Register<Item> event, final Block... blocks) {
            for (final Block b : blocks) {
                registerTallItemBlock(event, b);
            }
        }

        private static void registerSpawnEgg(final RegistryEvent.Register<Item> event, final EntityType<?> entity,
                                             final String entityName, final int colorBase, final int colorSpots) {
            event.getRegistry().register(new SpawnEggItem(entity, colorBase, colorSpots, new Item.Properties().tab(GREEK_GROUP))
                    .setRegistryName(MODID, entityName + "_spawn_egg"));
        }
    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class MenuReg {

    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class BlockEntityReg {

        // Tile Entity //
        @ObjectHolder("vase_te")
        public static final TileEntityType<VaseTileEntity> VASE_TE = null;
        @ObjectHolder("mob_head_te")
        public static final TileEntityType<MobHeadTileEntity> BOSS_HEAD_TE = null;

        @SubscribeEvent
        public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
            GreekFantasy.LOGGER.debug("registerTileEntities");
            event.getRegistry().register(
                    TileEntityType.Builder.of(VaseTileEntity::new, BlockReg.TERRACOTTA_VASE,
                                    BlockReg.WHITE_TERRACOTTA_VASE, BlockReg.ORANGE_TERRACOTTA_VASE, BlockReg.MAGENTA_TERRACOTTA_VASE, BlockReg.LIGHT_BLUE_TERRACOTTA_VASE,
                                    BlockReg.YELLOW_TERRACOTTA_VASE, BlockReg.LIME_TERRACOTTA_VASE, BlockReg.PINK_TERRACOTTA_VASE, BlockReg.GRAY_TERRACOTTA_VASE,
                                    BlockReg.LIGHT_GRAY_TERRACOTTA_VASE, BlockReg.CYAN_TERRACOTTA_VASE, BlockReg.PURPLE_TERRACOTTA_VASE, BlockReg.BLUE_TERRACOTTA_VASE,
                                    BlockReg.BROWN_TERRACOTTA_VASE, BlockReg.GREEN_TERRACOTTA_VASE, BlockReg.RED_TERRACOTTA_VASE, BlockReg.BLACK_TERRACOTTA_VASE)
                            .build(null).setRegistryName(MODID, "vase_te")
            );
            event.getRegistry().register(
                    TileEntityType.Builder.of(MobHeadTileEntity::new, BlockReg.GIGANTE_HEAD, BlockReg.ORTHUS_HEAD, BlockReg.CERBERUS_HEAD)
                            .build(null).setRegistryName(MODID, "mob_head_te")
            );
        }
    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class MobEffectReg {

        // Effect //
        @ObjectHolder("stunned")
        public static final Effect STUNNED_EFFECT = null;
        @ObjectHolder("petrified")
        public static final Effect PETRIFIED_EFFECT = null;
        @ObjectHolder("mirror")
        public static final Effect MIRROR_EFFECT = null;
        @ObjectHolder("pig")
        public static final Effect PIG_EFFECT = null;
        @ObjectHolder("prisoner")
        public static final Effect PRISONER_EFFECT = null;

        @SubscribeEvent
        public static void registerEffects(final RegistryEvent.Register<Effect> event) {
            GreekFantasy.LOGGER.debug("registerEffects");
            event.getRegistry().registerAll(
                    new StunnedEffect().setRegistryName(MODID, "stunned"),
                    new StunnedEffect().setRegistryName(MODID, "petrified"),
                    new MirrorEffect().setRegistryName(MODID, "mirror"),
                    new PigEffect().setRegistryName(MODID, "pig"),
                    new PrisonerEffect().setRegistryName(MODID, "prisoner")
            );
        }
    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class EnchantmentReg {

        // Enchantment //
        @ObjectHolder("overstep")
        public static final Enchantment OVERSTEP_ENCHANTMENT = null;
        @ObjectHolder("smashing")
        public static final Enchantment SMASHING_ENCHANTMENT = null;
        @ObjectHolder("mirror")
        public static final Enchantment MIRROR_ENCHANTMENT = null;
        @ObjectHolder("poison")
        public static final Enchantment POISON_ENCHANTMENT = null;
        @ObjectHolder("flying")
        public static final Enchantment FLYING_ENCHANTMENT = null;
        @ObjectHolder("lord_of_the_sea")
        public static final Enchantment LORD_OF_THE_SEA_ENCHANTMENT = null;
        @ObjectHolder("fireflash")
        public static final Enchantment FIREFLASH_ENCHANTMENT = null;
        @ObjectHolder("daybreak")
        public static final Enchantment DAYBREAK_ENCHANTMENT = null;
        @ObjectHolder("raising")
        public static final Enchantment RAISING_ENCHANTMENT = null;
        @ObjectHolder("silkstep")
        public static final Enchantment SILKSTEP_ENCHANTMENT = null;

        @SubscribeEvent
        public static void registerEnchantments(final RegistryEvent.Register<Enchantment> event) {
            GreekFantasy.LOGGER.debug("registerEnchantments");
            event.getRegistry().registerAll(
                    new OverstepEnchantment(Enchantment.Rarity.UNCOMMON)
                            .setRegistryName(MODID, "overstep"),
                    new SmashingEnchantment(Enchantment.Rarity.RARE)
                            .setRegistryName(MODID, "smashing"),
                    new HuntingEnchantment(Enchantment.Rarity.COMMON)
                            .setRegistryName(MODID, "hunting"),
                    new PoisonEnchantment(Enchantment.Rarity.RARE)
                            .setRegistryName(MODID, "poison"),
                    new MirrorEnchantment(Enchantment.Rarity.VERY_RARE)
                            .setRegistryName(MODID, "mirror"),
                    new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.ARMOR_FEET, EquipmentSlotType.FEET,
                            TextFormatting.GOLD, 1, e -> e.getItem() == ItemReg.WINGED_SANDALS)
                            .setRegistryName(MODID, "flying"),
                    new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.TRIDENT, EquipmentSlotType.MAINHAND,
                            TextFormatting.DARK_AQUA, 1, e -> e.getItem() == Items.TRIDENT)
                            .setRegistryName(MODID, "lord_of_the_sea"),
                    new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND,
                            TextFormatting.GOLD, 3, e -> e.getItem() == ItemReg.THUNDERBOLT)
                            .setRegistryName(MODID, "fireflash"),
                    new DeityEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentType.BREAKABLE, EquipmentSlotType.MAINHAND,
                            TextFormatting.YELLOW, 1, e -> e.getItem() == Items.CLOCK)
                            .setRegistryName(MODID, "daybreak"),
                    new DeityEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND,
                            TextFormatting.RED, 1, e -> e.getItem() == ItemReg.BIDENT)
                            .setRegistryName(MODID, "raising"),
                    new SilkstepEnchantment(Enchantment.Rarity.RARE)
                            .setRegistryName(MODID, "silkstep")
            );
        }
    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class PotionReg {

        // Potion //
        @ObjectHolder("mirror")
        public static final Potion MIRROR_POTION = null;
        @ObjectHolder("long_mirror")
        public static final Potion LONG_MIRROR_POTION = null;
        @ObjectHolder("pig")
        public static final Potion PIG_POTION = null;
        @ObjectHolder("long_pig")
        public static final Potion LONG_PIG_POTION = null;

        @SubscribeEvent
        public static void registerPotions(final RegistryEvent.Register<Potion> event) {
            GreekFantasy.LOGGER.debug("registerPotions");
            event.getRegistry().registerAll(
                    new Potion(new EffectInstance(MobEffectReg.MIRROR_EFFECT, 3600)).setRegistryName(MODID, "mirror"),
                    new Potion("mirror", new EffectInstance(MobEffectReg.MIRROR_EFFECT, 9600)).setRegistryName(MODID, "long_mirror"),
                    new Potion(new EffectInstance(MobEffectReg.PIG_EFFECT, 3600)).setRegistryName(MODID, "pig"),
                    new Potion("pig", new EffectInstance(MobEffectReg.PIG_EFFECT, 9600)).setRegistryName(MODID, "long_pig")
            );
        }

        @SubscribeEvent
        public static void setup(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> finishBrewingRecipes());
        }

        public static void finishBrewingRecipes() {
            final ItemStack awkward = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD);
            // Mirror potion recipes
            if (GreekFantasy.CONFIG.isMirrorPotionEnabled()) {
                final ItemStack mirror = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionReg.MIRROR_POTION);
                final ItemStack splashMirror = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), PotionReg.MIRROR_POTION);
                final ItemStack lingeringMirror = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), PotionReg.MIRROR_POTION);
                // Add brewing recipes for Mirror potion
                BrewingRecipeRegistry.addRecipe(
                        Ingredient.of(awkward),
                        Ingredient.of(new ItemStack(ItemReg.SNAKESKIN)), mirror);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(mirror), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.POTION), PotionReg.LONG_MIRROR_POTION));
                BrewingRecipeRegistry.addRecipe(Ingredient.of(mirror), Ingredient.of(new ItemStack(Items.GUNPOWDER)), splashMirror);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(mirror), Ingredient.of(new ItemStack(Items.DRAGON_BREATH)), lingeringMirror);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(splashMirror), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), PotionReg.LONG_MIRROR_POTION));
                BrewingRecipeRegistry.addRecipe(Ingredient.of(lingeringMirror), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), PotionReg.LONG_MIRROR_POTION));
            }
            // Pig potion recipes
            if (GreekFantasy.CONFIG.isPigPotionEnabled()) {
                final ItemStack pig = PotionUtils.setPotion(new ItemStack(Items.POTION), PotionReg.PIG_POTION);
                final ItemStack splashPig = PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), PotionReg.PIG_POTION);
                final ItemStack lingeringPig = PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), PotionReg.PIG_POTION);
                // Add brewing recipes for Pig potion
                BrewingRecipeRegistry.addRecipe(
                        Ingredient.of(awkward),
                        Ingredient.of(new ItemStack(ItemReg.BOAR_EAR)), pig);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(pig), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.POTION), PotionReg.LONG_PIG_POTION));
                BrewingRecipeRegistry.addRecipe(Ingredient.of(pig), Ingredient.of(new ItemStack(Items.GUNPOWDER)), splashPig);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(pig), Ingredient.of(new ItemStack(Items.DRAGON_BREATH)), lingeringPig);
                BrewingRecipeRegistry.addRecipe(Ingredient.of(splashPig), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), PotionReg.LONG_PIG_POTION));
                BrewingRecipeRegistry.addRecipe(Ingredient.of(lingeringPig), Ingredient.of(new ItemStack(Items.REDSTONE)),
                        PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), PotionReg.LONG_PIG_POTION));
            }
        }
    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class ParticleReg {

        // Particle Type //
        @ObjectHolder("gorgon_face")
        public static final BasicParticleType GORGON_PARTICLE = new BasicParticleType(true);

        @SubscribeEvent
        public static void registerParticleTypes(final RegistryEvent.Register<ParticleType<?>> event) {
            GreekFantasy.LOGGER.debug("registerParticleTypes");
            event.getRegistry().register(new BasicParticleType(true).setRegistryName(MODID, "gorgon_face"));
        }
    }

    @ObjectHolder(GreekFantasy.MODID)
    public static final class RecipeReg {

        // Recipe Serializer //
        @ObjectHolder(SalveRecipe.CATEGORY)
        public static final IRecipeSerializer<ShapelessRecipe> SALVE_RECIPE_SERIALIZER = null;

        @SubscribeEvent
        public static void registerRecipeSerializers(final Register<IRecipeSerializer<?>> event) {
            GreekFantasy.LOGGER.debug("registerRecipeSerializers");
            event.getRegistry().register(new SalveRecipe.Factory().setRegistryName(MODID, SalveRecipe.CATEGORY));
        }
    }
}
