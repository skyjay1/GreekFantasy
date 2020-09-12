package greekfantasy.util;

import java.util.EnumMap;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.vector.Vector3f;

public class StatuePose implements IStringSerializable {
  
  private static final String KEY_NAME = "name";
  private static final String KEY_ANGLES = "angles";
  private final EnumMap<ModelPart, Vector3f> angles = new EnumMap<>(ModelPart.class);
  private String name;
  
  public StatuePose(final String nameIn) {
    name = nameIn;
    angles.put(ModelPart.HEAD, new Vector3f(0, 0, 0));
    angles.put(ModelPart.BODY, new Vector3f(0, 0, 0));
    angles.put(ModelPart.LEFT_ARM, new Vector3f(0, 0, 0));
    angles.put(ModelPart.RIGHT_ARM, new Vector3f(0, 0, 0));
    angles.put(ModelPart.LEFT_LEG, new Vector3f(0, 0, 0));
    angles.put(ModelPart.RIGHT_LEG, new Vector3f(0, 0, 0));
  }
  
  public StatuePose(final CompoundNBT tag) {
    read(tag);
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

  public void read(final CompoundNBT tag) {
    this.name = tag.getString(KEY_NAME);
    for (final ModelPart m : ModelPart.values()) {
      final CompoundNBT eTag = tag.getCompound(KEY_ANGLES + "_" + m.getString());
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

  public CompoundNBT write(final CompoundNBT tag) {
    tag.putString(KEY_NAME, this.name);
    for(final Entry<ModelPart, Vector3f> e : angles.entrySet()) {
      final CompoundNBT eTag = new CompoundNBT();
      eTag.put(KEY_ANGLES + ".x", FloatNBT.valueOf(e.getValue().getX()));
      eTag.put(KEY_ANGLES + ".y", FloatNBT.valueOf(e.getValue().getY()));
      eTag.put(KEY_ANGLES + ".z", FloatNBT.valueOf(e.getValue().getZ()));
      tag.put(KEY_ANGLES + "_" + e.getKey().getString(), eTag);
    }
    return tag;
  }
  
  @Override
  public String getString() {
    return name;
  }
}
