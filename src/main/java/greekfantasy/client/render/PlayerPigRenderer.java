package greekfantasy.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

public class PlayerPigRenderer<T extends LivingEntity>  extends LivingRenderer<T, PigModel<T>> {
  
  private static final ResourceLocation PIG_TEXTURES = new ResourceLocation("textures/entity/pig/pig.png");
  
  public PlayerPigRenderer(EntityRendererManager renderManagerIn) {
     super(renderManagerIn, new PigModel<>(), 0.7F);
  }

  @Override
  public ResourceLocation getEntityTexture(T entity) {
     return PIG_TEXTURES;
  }
}
