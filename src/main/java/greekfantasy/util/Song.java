package greekfantasy.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class Song {

    public static final Song EMPTY = new Song("Error", "Error", 0, List.of(), List.of());

    public static final Codec<Song> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(Song::getNameTranslationKey),
            Codec.STRING.fieldOf("credits").forGetter(Song::getCreditsTranslationKey),
            Codec.INT.fieldOf("interval").forGetter(Song::getInterval),
            Codec.INT.listOf().fieldOf("treble").forGetter(Song::getTreble),
            Codec.INT.listOf().fieldOf("bass").forGetter(Song::getBass)
    ).apply(instance, Song::new));

    private final String name;
    private final String credits;
    private final int interval;
    private final int length;
    private final List<Integer> treble;
    private final List<Integer> bass;

    protected Song(final String name, final String credits, final int interval,
                   final List<Integer> treble, final List<Integer> bass) {
        this.name = name;
        this.credits = credits;
        this.interval = interval;
        this.length = Math.max(treble.size(), bass.size());
        this.treble = treble;
        this.bass = bass;
    }

    /**
     * @return the translation key for the name
     **/
    public String getNameTranslationKey() {
        return name;
    }

    /**
     * @return the translation key for the name
     **/
    public String getCreditsTranslationKey() {
        return credits;
    }

    /**
     * @return an translated text component for the name
     **/
    public Component getName() {
        return Component.translatable(getNameTranslationKey());
    }

    /**
     * @return an translated text component for the name
     **/
    public Component getCredits() {
        return Component.translatable(getCreditsTranslationKey()).withStyle(ChatFormatting.ITALIC);
    }

    /**
     * @return the number of ticks between playing notes
     **/
    public int getInterval() {
        return interval;
    }

    /**
     * @return the number of notes in the deity
     **/
    public int getLength() {
        return length;
    }

    /**
     * @return the treble notes
     **/
    public List<Integer> getTreble() {
        return treble;
    }

    /**
     * @return the bass notes
     **/
    public List<Integer> getBass() {
        return bass;
    }

    /**
     * @param worldTime the world interval
     * @return Whether a note should be played at this interval
     **/
    public boolean shouldPlayNote(final long worldTime) {
        return (int) (worldTime % getInterval()) == 0;
    }

    /**
     * Determines which treble note(s) should be played at this interval.
     * Current implementation returns a list containing only one note.
     *
     * @param worldTime the world interval
     * @return a set of notes to play
     **/
    public List<Integer> getTrebleNotes(final long worldTime) {
        return getNotes(treble, worldTime, getInterval(), length);
    }

    /**
     * Determines which bass note(s) should be played at this interval.
     * Current implementation returns a list containing only one note.
     *
     * @param worldTime the world interval
     * @return a set of notes to play
     **/
    public List<Integer> getBassNotes(final long worldTime) {
        return getNotes(bass, worldTime, getInterval(), length);
    }

    /**
     * Determines which note(s) should be played at this interval.
     * Current implementation returns a list containing only one note.
     *
     * @param notes     the note array to reference
     * @param worldTime the world interval
     * @param playSpeed the number of notes to play per second
     * @param maxLength the maximum number of notes from the array to use
     * @return a set of notes to play
     **/
    public static List<Integer> getNotes(final List<Integer> notes, final long worldTime, final int playSpeed, final int maxLength) {
        final List<Integer> noteSet = new ArrayList<>();
        // get the current note
        final int currentIndex = Math.abs((int) (worldTime / playSpeed)) % maxLength;
        final int currentNote = currentIndex >= notes.size() ? 0 : Mth.clamp(notes.get(currentIndex), 0, 24);
        if (currentNote > 0) {
            noteSet.add(Integer.valueOf(currentNote));
        }
        return noteSet;
    }
}
