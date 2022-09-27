package greekfantasy.util;

import greekfantasy.GreekFantasy;
import greekfantasy.item.InstrumentItem;
import greekfantasy.network.CPlayNotePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public final class SongManager {

    private SongManager() {
    }

    /**
     * Play part of the given song at the position of the given entity
     *
     * @param entity     the entity
     * @param instrument the instrument being used
     * @param songName   the resource location of the song
     * @param worldTime  the amount of time the song has been playing,
     *                   or the world time for universally synchronized songs
     * @param volume     the song volume for the treble
     * @param volumeBass the song volume for the bass
     * @return if the note was successfully played
     **/
    public static boolean playMusic(final LivingEntity entity, final InstrumentItem instrument, final ResourceLocation songName,
                                    final long worldTime, final float volume, final float volumeBass) {
        final Song song = GreekFantasy.SONG_MAP.get(songName);
        if (song != null && song.shouldPlayNote(worldTime)) {
            final List<Integer> treble = song.getTrebleNotes(worldTime);
            final List<Integer> bass = song.getBassNotes(worldTime);
            for (final Integer note : treble) {
                playNoteAt(entity, instrument, note, volume);
            }
            for (final Integer note : bass) {
                playNoteAt(entity, instrument, note, volumeBass);
            }
            return !treble.isEmpty() || !bass.isEmpty();
        }
        return false;
    }

    public static void playNoteAt(final LivingEntity entity, final InstrumentItem instrument, final int note, final float volume) {
        GreekFantasy.CHANNEL.sendToServer(new CPlayNotePacket(entity.getId(), note, instrument.getSound(), volume));
    }
}
