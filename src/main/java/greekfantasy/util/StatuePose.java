package greekfantasy.util;

import java.util.EnumMap;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.INBTSerializable;

public class StatuePose implements INBTSerializable<CompoundNBT> {
  
  private static final String KEY_ANGLES = "angles";
  private final EnumMap<ModelPart, Vector3f> angles = new EnumMap<>(ModelPart.class);
  
  public StatuePose() {
    angles.put(ModelPart.HEAD, new Vector3f(0, 0, 0));
    angles.put(ModelPart.BODY, new Vector3f(0, 0, 0));
    angles.put(ModelPart.LEFT_ARM, new Vector3f(0, 0, 0));
    angles.put(ModelPart.RIGHT_ARM, new Vector3f(0, 0, 0));
    angles.put(ModelPart.LEFT_LEG, new Vector3f(0, 0, 0));
    angles.put(ModelPart.RIGHT_LEG, new Vector3f(0, 0, 0));
  }
  
  public StatuePose(final CompoundNBT tag) {
    deserializeNBT(tag);
  }
  
  /**
   * Adds a model rotation to the statuePose using degrees
   * @param p the model part
   * @param x the x rotation in degrees
   * @param y the y rotation in degrees
   * @param z the z rotation in degrees
   * @return the StatuePose for chaining instances
   **/
  public StatuePose set(final ModelPart p, final float x, final float y, final float z) {
    return setRadians(p, (float)Math.toRadians(x), (float)Math.toRadians(y), (float)Math.toRadians(z));
  }
  
  /**
   * Adds a model rotation to the statuePose using radians
   * @param p the model part
   * @param x the x rotation in radians
   * @param y the y rotation in radians
   * @param z the z rotation in radians
   * @return the StatuePose for chaining instances
   **/
  public StatuePose setRadians(final ModelPart p, final float x, final float y, final float z) {
    angles.put(p, new Vector3f(x, y, z));
    return this;
  }
  
  /**
   * @param p the model part
   * @return a vector of 3 floats representing x, y, and z angles in radians
   **/
  public Vector3f getAngles(final ModelPart p) {
    return angles.get(p);
  }

  public CompoundNBT serializeNBT(final CompoundNBT tag) {
    for(final Entry<ModelPart, Vector3f> e : angles.entrySet()) {
      final CompoundNBT eTag = new CompoundNBT();
      eTag.put(KEY_ANGLES + ".x", FloatNBT.valueOf(e.getValue().x()));
      eTag.put(KEY_ANGLES + ".y", FloatNBT.valueOf(e.getValue().y()));
      eTag.put(KEY_ANGLES + ".z", FloatNBT.valueOf(e.getValue().z()));
      tag.put(KEY_ANGLES + "_" + e.getKey().getSerializedName(), eTag);
    }
    return tag;
  }
  
  @Override
  public CompoundNBT serializeNBT() {
    return serializeNBT(new CompoundNBT());
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    for (final ModelPart m : ModelPart.values()) {
      final CompoundNBT eTag = nbt.getCompound(KEY_ANGLES + "_" + m.getSerializedName());
      float x = 0.0F;
      float y = 0.0F;
      float z = 0.0F;
      if (eTag != null && !eTag.isEmpty()) {
        x = eTag.getFloat(KEY_ANGLES + ".x");
        y = eTag.getFloat(KEY_ANGLES + ".y");
        z = eTag.getFloat(KEY_ANGLES + ".z");
      }
      this.angles.put(m, new Vector3f(x, y, z));
    }
  }
  
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder("StatuePose{\n");
    for(final Entry<ModelPart, Vector3f> entry : angles.entrySet()) {
      builder.append("  ");
      builder.append(entry.getKey().getSerializedName());
      builder.append(" : ");
      builder.append(entry.getValue().toString());
      builder.append("\n");
    }
    return builder.append("}").toString();
  }
}
