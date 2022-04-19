package greekfantasy.client.render.tileentity;

import greekfantasy.client.render.SpearRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;

import java.util.HashMap;
import java.util.Map;

public class ClientISTERProvider {

    private static ItemStackTileEntityRenderer orthusHead;
    private static ItemStackTileEntityRenderer cerberusHead;
    private static ItemStackTileEntityRenderer giganteHead;
    private static final Map<String, ItemStackTileEntityRenderer> spearMap = new HashMap<>();

    public static ItemStackTileEntityRenderer bakeOrthusHeadISTER() {
        if (orthusHead == null) {
            orthusHead = new MobHeadTileEntityRenderer.OrthusItemStackRenderer();
        }
        return orthusHead;
    }

    public static ItemStackTileEntityRenderer bakeGiganteHeadISTER() {
        if (giganteHead == null) {
            giganteHead = new MobHeadTileEntityRenderer.GiganteItemStackRenderer();
        }
        return giganteHead;
    }

    public static ItemStackTileEntityRenderer bakeCerberusHeadISTER() {
        if (cerberusHead == null) {
            cerberusHead = new MobHeadTileEntityRenderer.CerberusItemStackRenderer();
        }
        return cerberusHead;
    }

    public static ItemStackTileEntityRenderer bakeSpearISTER(final String itemName) {
        if (!spearMap.containsKey(itemName)) {
            spearMap.put(itemName, new SpearRenderer.SpearItemStackRenderer(itemName));
        }
        return spearMap.get(itemName);
    }
}
