package greekfantasy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes.Name;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class PanfluteSong {
  
  public static final PanfluteSong DEFAULT = new PanfluteSong("Error");
  
  private String name;
  private String credits;
  private int interval;
  private int length;
  private int[] treble;
  private int [] bass;
  
  protected PanfluteSong() { }
  protected PanfluteSong(String def) {
    name = credits = def;
  }
  
  /** @return the translation key for the name **/
  public String getNameTranslationKey() { return name; }
  /** @return the translation key for the name **/
  public String getCreditsTranslationKey() { return credits; }
  /** @return an translated text component for the name **/
  public IFormattableTextComponent getName() { 
    return new TranslationTextComponent(getNameTranslationKey()); 
  }
  /** @return an translated text component for the name **/
  public IFormattableTextComponent getCredits() { 
    return new TranslationTextComponent(getCreditsTranslationKey()).mergeStyle(TextFormatting.ITALIC); 
  }
  /** @return the number of ticks between playing notes **/
  public int getInterval() { return interval; }
  /** @return the number of notes in the song **/
  public int getLength() { return length; }
  /** @return the treble notes **/
  public int[] getTreble() { return treble; }
  /** @return the bass notes **/
  public int[] getBass() { return bass; }
  
  /**
   * @param worldTime the world interval
   * @return Whether a note should be played at this interval
   **/
  public boolean shouldPlayNote(final long worldTime) {
    return (int)(worldTime % getInterval()) == 0;
  }
  
  /**
   * Determines which treble note(s) should be played at this interval.
   * Current implementation returns a list containing only one note.
   * @param worldTime the world interval
   * @return a set of notes to play
   **/
  public List<Integer> getTrebleNotes(final long worldTime) {
    return getNotes(treble, worldTime, getInterval(), length);
  }
  
  /**
   * Determines which bass note(s) should be played at this interval.
   * Current implementation returns a list containing only one note.
   * @param worldTime the world interval
   * @return a set of notes to play
   **/
  public List<Integer> getBassNotes(final long worldTime) {
    return getNotes(bass, worldTime, getInterval(), length);
  }
  
  /**
   * Determines which note(s) should be played at this interval.
   * Current implementation returns a list containing only one note.
   * @param notes the note array to reference
   * @param worldTime the world interval
   * @param playSpeed the number of notes to play per second
   * @param maxLength the maximum number of notes from the array to use
   * @return a set of notes to play
   **/
  public static List<Integer> getNotes(final int[] notes, final long worldTime, final int playSpeed, final int maxLength) {
    final List<Integer> noteSet = new ArrayList<>();
    // get the current note
    final int currentIndex = Math.abs((int)(worldTime / playSpeed)) % maxLength;
    final int currentNote = currentIndex >= notes.length ? 0 : MathHelper.clamp(notes[currentIndex], 0, 24);
    if(currentNote > 0) {
      noteSet.add(Integer.valueOf(currentNote));
    }
    return noteSet;
  }
}
