package greekfantasy.proxy;

import java.util.HashMap;
import java.util.Map;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.NymphEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class Proxy {
  
  public static final Map<NymphEntity.Variant, EntityType<? extends NymphEntity>> nymphEntityMap = new HashMap<>();
  
  public void registerEntityRenders() { }

  public void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    // Nymph
    for(final NymphEntity.Variant t : NymphEntity.Variant.values()) {
      final EntityType<? extends NymphEntity> entityType = registerNymph(t, event);
      nymphEntityMap.put(t, entityType);
    }
  }

 
  public void registerItems(final RegistryEvent.Register<Item> event) {
    
  }

  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    
  }
  
  private EntityType<? extends NymphEntity> registerNymph(final NymphEntity.Variant t, final RegistryEvent.Register<EntityType<?>> event) {
    EntityType<? extends NymphEntity> entityType = EntityType.Builder.create(NymphEntity::new, EntityClassification.MISC)
        .size(0.6F, 1.9F).build(t.getString());
    entityType.setRegistryName(GreekFantasy.MODID, t.getString());
    event.getRegistry().register(entityType);
    GlobalEntityTypeAttributes.put(entityType, NymphEntity.getAttributes().create());
    return entityType;
  }
}
