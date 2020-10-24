package greekfantasy.client.render.tileentity;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

public class ClientISTERProvider {
  
  private static ItemStackTileEntityRenderer orthusHead;
  private static ItemStackTileEntityRenderer cerberusHead;
  private static ItemStackTileEntityRenderer giganteHead;

  public static ItemStackTileEntityRenderer bakeOrthusHeadISTER() {
    if(orthusHead == null) {
      orthusHead = new MobHeadTileEntityRenderer.OrthusItemStackRenderer();
    }
    return orthusHead;
  }
  
  public static ItemStackTileEntityRenderer bakeGiganteHeadISTER() {
    if(giganteHead == null) {
      giganteHead = new MobHeadTileEntityRenderer.GiganteItemStackRenderer();
    }
    return giganteHead;
  }
  
  public static ItemStackTileEntityRenderer bakeCerberusHeadISTER() {
    if(cerberusHead == null) {
      cerberusHead = new MobHeadTileEntityRenderer.CerberusItemStackRenderer();
    }
    return cerberusHead;
  }
}
