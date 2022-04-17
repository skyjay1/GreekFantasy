package greekfantasy.proxy;

import greekfantasy.GreekFantasy;
import greekfantasy.event.CommonForgeEventHandler;
import greekfantasy.network.SPanfluteSongPacket;
import greekfantasy.util.GenericJsonReloadListener;
import greekfantasy.util.Song;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;

public class Proxy {

    public final GenericJsonReloadListener<Song> PANFLUTE_SONGS = new GenericJsonReloadListener<>("songs", Song.class, Song.CODEC,
            l -> l.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SPanfluteSongPacket(e.getKey(), e.getValue().get()))));

    public void registerReloadListeners() {
    }

    public void registerEntityRenders() {
    }

    public void registerTileEntityRenders() {
    }

    public void registerContainerRenders() {
    }

    public void registerRenderLayers() {
    }

    public void registerModelProperties() {
    }

    public void registerPlayerLayers() {
    }

    public void registerEventHandlers() {
        GreekFantasy.LOGGER.debug("registerEventHandlers");
        MinecraftForge.EVENT_BUS.register(CommonForgeEventHandler.class);
    }
}
