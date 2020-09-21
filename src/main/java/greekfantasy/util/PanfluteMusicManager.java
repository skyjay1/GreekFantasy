package greekfantasy.util;

import java.util.List;
import java.util.Optional;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;

public final class PanfluteMusicManager {
    
  private PanfluteMusicManager() { }  
 
  public static boolean playMusic(final LivingEntity entity, final ResourceLocation songName, final long worldTime, final float volume, final float volumeBass) {
    final Optional<PanfluteSong> song = GreekFantasy.PROXY.PANFLUTE_SONGS.get(songName);
    if(song.isPresent() && song.get().shouldPlayNote(worldTime)) {
      final List<Integer> treble = song.get().getTrebleNotes(worldTime);
      final List<Integer> bass = song.get().getBassNotes(worldTime);
      for(final Integer note : treble) {
        playNoteAt(entity, note.intValue(), volume);
      }
      for(final Integer note : bass) {
        playNoteAt(entity, note.intValue(), volumeBass);
      }
      return !treble.isEmpty() || !bass.isEmpty();
    }
    return false;
  }
  
  private static void playNoteAt(final LivingEntity entity, final int note, final float volume) {
    final double x = entity.getPosX() + (entity.getRNG().nextDouble() - 0.5D) * 0.15D;
    final double y = entity.getPosYEye() + 0.15D;
    final double z = entity.getPosZ() + (entity.getRNG().nextDouble() - 0.5D) * 0.15D;
    final float pitch = (float)Math.pow(2.0D, (double)(note - 12) / 12.0D);
    entity.getEntityWorld().playSound(x, y, z, SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, 
        entity.getSoundCategory(), volume, pitch, false);
    entity.getEntityWorld().addParticle(ParticleTypes.NOTE, x, y, z, pitch / 24.0D, 0.0D, 0.0D);
  }
}
