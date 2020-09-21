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
    return getNotes(treble, worldTime);
  }
  
  /**
   * Determines which bass note(s) should be played at this time.
   * Currently supports up to 2 notes
   * @param worldTime the world time
   * @return a set of notes to play
   **/
  public List<Integer> getBassNotes(final long worldTime) {
    return getNotes(bass, worldTime);
  }
  
  /**
   * Determines which note(s) should be played at this time.
   * Currently supports up to 2 notes
   * @param notes the note array to reference
   * @param worldTime the world time
   * @return a set of notes to play
   **/
  public List<Integer> getNotes(final int[] notes, final long worldTime) {
    final List<Integer> noteSet = new ArrayList<>();
    final int playSpeed = getPlaySpeed();
    // get the current note
    final int index1 = Math.abs((int)(worldTime / playSpeed)) % length;
    final int note1 = index1 >= notes.length ? 0 : MathHelper.clamp(notes[index1], 0, 24);
    if(note1 > 0) {
      noteSet.add(Integer.valueOf(note1));
      // get a note halfway between last note and current note
      if(index1 > 0) {
        final int index2 = index1 - 1;
        final int note2 = index2 >= notes.length ? 0 : MathHelper.clamp(notes[index2], 0, 24);
        if(note2 > 0) {
          noteSet.add(Integer.valueOf((note1 + note2) / 2));
        }
      }
    }
    return noteSet;
  }
}
