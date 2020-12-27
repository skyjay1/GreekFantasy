package greekfantasy.favor;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import greekfantasy.tileentity.AltarTileEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface IDeity {

  /**
   * @return the name of the deity
   */
  public ResourceLocation getName();
  
  default IFormattableTextComponent getText() {
    final ResourceLocation name = getName();
    return new TranslationTextComponent("deity." + name.getNamespace() + "." + name.getPath());
  }

  /**
   * @return the texture to apply to the altar used by the deity
   */
  public ResourceLocation getTexture();
  
  /**
   * @return a map of entity type and favor modifiers
   */
  public Map<ResourceLocation, Integer> getKillFavorModifiers();
  
  /**
   * @return a map of item offerings and favor modifiers
   */
  public Map<Item, Integer> getItemFavorModifiers();
  
  /**
   * @return a list of favor effects associated with the deity
   */
  public List<IFavorEffect> getFavorEffects();
  
  /**
   * Called when a new AltarTileEntity is created.
   * Used to set pose, male/female, etc.
   * @return a consumer that handles the creation of a new altar
   */
  public Consumer<AltarTileEntity> initAltar();
}
