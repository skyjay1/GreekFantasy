package greekfantasy.util;

import greekfantasy.GreekFantasy;
import greekfantasy.item.InstrumentItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;

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
        final Optional<Song> song = GreekFantasy.PROXY.PANFLUTE_SONGS.get(songName);
        if (song.isPresent() && song.get().shouldPlayNote(worldTime)) {
            final List<Integer> treble = song.get().getTrebleNotes(worldTime);
            final List<Integer> bass = song.get().getBassNotes(worldTime);
            for (final Integer note : treble) {
                playNoteAt(entity, instrument, note.intValue(), volume);
            }
            for (final Integer note : bass) {
                playNoteAt(entity, instrument, note.intValue(), volumeBass);
            }
            return !treble.isEmpty() || !bass.isEmpty();
        }
        return false;
    }

    private static void playNoteAt(final LivingEntity entity, final InstrumentItem instrument, final int note, final float volume) {
        final double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 0.15D;
        final double y = entity.getEyeY() + 0.15D;
        final double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 0.15D;
        final float pitch = instrument.getPitch(note);
        entity.getCommandSenderWorld().playLocalSound(x, y, z, instrument.getSound(), entity.getSoundSource(), volume, pitch, false);
        entity.getCommandSenderWorld().addParticle(ParticleTypes.NOTE, x, y, z, pitch / 24.0D, 0.0D, 0.0D);
    }
}
