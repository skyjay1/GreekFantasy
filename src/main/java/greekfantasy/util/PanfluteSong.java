package greekfantasy.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.MathHelper;

public class PanfluteSong {
  
  private String credits;
  private int time;
  private int length;
  private int[] treble;
  private int [] bass;
  
  /** @return the credits for this song **/
  public String getCredits() { return credits; }
  /** @return the notes per second **/
  public int getTime() { return time; }
  /** @return the number of notes in the song **/
  public int getLength() { return length; }
  /** @return the value of 20/time **/
  public int getPlaySpeed() { return 20 / getTime(); }
  /** @return the treble notes **/
  public int[] getTreble() { return treble; }
  /** @return the bass notes **/
  public int[] getBass() { return bass; }
  
  /**
   * @param worldTime the world time
   * @return Whether a note should be played at this time
   **/
  public boolean shouldPlayNote(final long worldTime) {
    return (int)(worldTime % getPlaySpeed()) == 0;
  }
  
  /**
   * Determines which treble note(s) should be played at this time.
   * Currently supports up to 2 notes
   * @param worldTime the world time
   * @return a set of notes to play
   **/
  public List<Integer> getTrebleNotes(final long worldTime) {
    return getNotes(treble, worldTime, getPlaySpeed(), length);
  }
  
  /**
   * Determines which bass note(s) should be played at this time.
   * Currently supports up to 2 notes
   * @param worldTime the world time
   * @return a set of notes to play
   **/
  public List<Integer> getBassNotes(final long worldTime) {
    return getNotes(bass, worldTime, getPlaySpeed(), length);
  }
  
  /**
   * Determines which note(s) should be played at this time.
   * Currently supports up to 2 notes
   * @param notes the note array to reference
   * @param worldTime the world time
   * @return a set of notes to play
   **/
  public static List<Integer> getNotes(final int[] notes, final long worldTime, final int playSpeed, final int maxLength) {
    final List<Integer> noteSet = new ArrayList<>();
    // get the current note
    final int currentIndex = Math.abs((int)(worldTime / playSpeed)) % maxLength;
    final int currentNote = currentIndex >= notes.length ? 0 : MathHelper.clamp(notes[currentIndex], 0, 24);
    if(currentNote > 0) {
      noteSet.add(Integer.valueOf(currentNote));
      // get a note halfway between last note and current note
      if(currentIndex > 0) {
        final int lastIndex = currentIndex - 1;
        final int lastNote = lastIndex >= notes.length ? 0 : MathHelper.clamp(notes[lastIndex], 0, 24);
        final int middleNote = (currentNote + lastNote) / 2;
        if(middleNote > 0 && middleNote != currentNote) {
          noteSet.add(Integer.valueOf(middleNote));
        }
      }
    }
    return noteSet;
  }
}
