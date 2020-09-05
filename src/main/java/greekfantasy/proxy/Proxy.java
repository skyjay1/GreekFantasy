package greekfantasy.proxy;

import java.util.function.Supplier;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.AraEntity;
import greekfantasy.entity.CentaurEntity;
import greekfantasy.entity.CerastesEntity;
import greekfantasy.entity.CyprianCentaurEntity;
import greekfantasy.entity.EmpusaEntity;
import greekfantasy.entity.GorgonEntity;
import greekfantasy.entity.HarpyEntity;
import greekfantasy.entity.MinotaurEntity;
import greekfantasy.entity.NymphEntity;
import greekfantasy.entity.SatyrEntity;
import greekfantasy.entity.ShadeEntity;
import greekfantasy.entity.SirenEntity;
import greekfantasy.item.Items;
import greekfantasy.item.PanfluteItem;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.IFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;

public class Proxy {
  
  public static EntityType<? extends NymphEntity> NYMPH_ENTITY;
  public static EntityType<? extends SatyrEntity> SATYR_ENTITY;
  public static EntityType<? extends CentaurEntity> CENTAUR_ENTITY;
  public static EntityType<? extends CyprianCentaurEntity> CYPRIAN_CENTAUR_ENTITY;
  public static EntityType<? extends MinotaurEntity> MINOTAUR_ENTITY;
  public static EntityType<? extends SirenEntity> SIREN_ENTITY;
  public static EntityType<? extends GorgonEntity> GORGON_ENTITY;
  public static EntityType<? extends ShadeEntity> SHADE_ENTITY;
  public static EntityType<? extends HarpyEntity> HARPY_ENTITY;
  public static EntityType<? extends EmpusaEntity> EMPUSA_ENTITY;
  public static EntityType<? extends AraEntity> ARA_ENTITY;
  public static EntityType<? extends CerastesEntity> CERASTES_ENTITY;
  
  public static ItemGroup GREEK_GROUP = new ItemGroup("greekfantasy") {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(Items.PANFLUTE);
    }
  };
  
  public void registerEntityRenders() { }

  public void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    NYMPH_ENTITY = registerEntityType(event, NymphEntity::new, NymphEntity::getAttributes, "nymph", 0.48F, 1.8F, false);
    SATYR_ENTITY = registerEntityType(event, SatyrEntity::new, SatyrEntity::getAttributes, "satyr", 0.7F, 1.8F, false);
    CENTAUR_ENTITY = registerEntityType(event, CentaurEntity::new, CentaurEntity::getAttributes, "centaur", 1.39F, 2.49F, false);
    CYPRIAN_CENTAUR_ENTITY = registerEntityType(event, CyprianCentaurEntity::new, CyprianCentaurEntity::getAttributes, "cyprian", 1.39F, 2.49F, false);
    MINOTAUR_ENTITY = registerEntityType(event, MinotaurEntity::new, MinotaurEntity::getAttributes, "minotaur", 0.7F, 1.8F, false);
    SIREN_ENTITY = registerEntityType(event, SirenEntity::new, SirenEntity::getAttributes, "siren", 0.6F, 1.9F, false);
    GORGON_ENTITY = registerEntityType(event, GorgonEntity::new, GorgonEntity::getAttributes, "gorgon", 0.9F, 1.9F, false);
    SHADE_ENTITY = registerEntityType(event, ShadeEntity::new, ShadeEntity::getAttributes, "shade", 0.7F, 1.8F, true);
    HARPY_ENTITY = registerEntityType(event, HarpyEntity::new, HarpyEntity::getAttributes, "harpy", 0.7F, 1.8F, false);
    EMPUSA_ENTITY = registerEntityType(event, EmpusaEntity::new, EmpusaEntity::getAttributes, "empusa", 0.7F, 1.8F, true);
    ARA_ENTITY = registerEntityType(event, AraEntity::new, AraEntity::getAttributes, "ara", 0.7F, 1.8F, true);
    CERASTES_ENTITY = registerEntityType(event, CerastesEntity::new, CerastesEntity::getAttributes, "cerastes", 0.98F, 0.94F, false);
  }

 
  public void registerItems(final RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(
        new PanfluteItem(new Item.Properties().group(GREEK_GROUP).maxDamage(100))
          .setRegistryName(new ResourceLocation(GreekFantasy.MODID, "panflute"))
    );
  }

  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    
  }

  private <T extends LivingEntity> EntityType<T> registerEntityType(final RegistryEvent.Register<EntityType<?>> event, 
      final IFactory<T> factoryIn, final Supplier<AttributeModifierMap.MutableAttribute> mapSupplier, final String name, 
      final float width, final float height, final boolean fireproof) {
    EntityType.Builder<T> entityTypeBuilder = EntityType.Builder.create(factoryIn, EntityClassification.MISC)
        .size(width, height);
    if(fireproof) entityTypeBuilder.immuneToFire();
    EntityType<T> entityType = entityTypeBuilder.build(name);
    entityType.setRegistryName(GreekFantasy.MODID, name);
    event.getRegistry().register(entityType);
    GlobalEntityTypeAttributes.put(entityType, mapSupplier.get().create());
    return entityType;
    
  }
  
  
  
}
