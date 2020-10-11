package greekfantasy.entity;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.Team;

public interface IHasOwner {

  static String KEY_OWNER = "Owner";

  /** @return an Optional containing the owner's UUID, or empty if no owner is set **/
  Optional<UUID> getOwnerID();
  
  /**
   * Sets the owner using the given UUID
   * @param uuid the UUID of the new owner
   **/
  void setOwner(@Nullable final UUID uuid);
  
  /** @return this entity's owner if present, otherwise null **/
  @Nullable
  LivingEntity getOwner();
  
  /**
   * @param target the entity to potentially attack
   * @param owner this entity's owner
   * @return whether this entity should attack the target, given its owner
   **/
  boolean shouldAttackEntity(final LivingEntity target, final LivingEntity owner);
  
  /** @return whether this entity has an owner **/
  default boolean hasOwner() { return getOwnerID().isPresent(); }

  /**
   * Sets this entity's owner as the given player
   * @param entity a player
   **/
  default void setOwner(final PlayerEntity entity) { setOwner(entity.getUniqueID()); }

  /**
   * @param entity the entity to check
   * @return whether the given entity is this entity's owner
   **/
  default boolean isOwner(final LivingEntity entity) { return hasOwner() && entity == getOwner(); }
  
  /**
   * @param fallback the Team to return if no owner is present
   * @return the owner's team if present, otherwise the given team
   **/
  default Team getOwnerTeam(final Team fallback) {
    if (hasOwner()) {
      LivingEntity owner = getOwner();
      if (owner != null) {
        return owner.getTeam();
      }
    }
    return fallback;
  }
  
  /**
   * @param entity the entity to check
   * @return whether this entity and the given entity are on the same team
   **/
  default boolean isOnSameTeamAs(final Entity entity) {
    if (hasOwner()) {
      LivingEntity owner = getOwner();
      if (entity == owner) {
        return true;
      }
      if (owner != null) {
        return owner.isOnSameTeam(entity);
      }
    }
    return false;
  }
  
  /**
   * Writes the owner UUID to the given tag compound
   * @param compound the tag compound
   **/
  default void writeOwner(final CompoundNBT compound) {
    if (hasOwner()) {
      compound.putUniqueId(KEY_OWNER, getOwnerID().get());
    }
  }

  /**
   * Reads the owner UUID from the given tag compound
   * @param compound the tag compound
   **/
  default void readOwner(final CompoundNBT compound) {
    if (compound.hasUniqueId(KEY_OWNER)) {
      setOwner(compound.getUniqueId(KEY_OWNER));
    }
  }
}
