package greekfantasy.entity.misc;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Hand;

public interface IHasOwner<E extends LivingEntity> {

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
   * @param item an itemstack
   * @return whether the use of this item should set the owner
   * or heal an entity that has a valid owner
   * @see #tryTameOrHeal(LivingEntity, PlayerEntity, Hand)
   **/
  boolean isTamingItem(final ItemStack item);
  
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
   * @param target the entity to potentially attack
   * @param owner this entity's owner
   * @return whether this entity should attack the target, given its owner
   **/
  default boolean shouldAttackEntity(final LivingEntity target, final LivingEntity owner) {
    // do not target creepers or ghasts
    if(target instanceof CreeperEntity || target instanceof GhastEntity) {
      return false;
    }
    // do not target owner
    if(owner != null && owner == target) {
      return false;
    }
    // do not target creatures belonging to the same owner
    if (target instanceof IHasOwner<?>) {
      IHasOwner<?> ihasowner = (IHasOwner<?>) target;
      return !ihasowner.hasOwner() || ihasowner.getOwner() != owner;
    }
    // donot target creatures that cannot be targeted by owner
    if (target instanceof PlayerEntity && owner instanceof PlayerEntity
        && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) target)) {
      return false;
    }
    // do not target tamed horses
    if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTame()) {
      return false;
    }
    // do not target tamed tameables
    return !(target instanceof TameableEntity) || !((TameableEntity) target).isTamed();
  }
  
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
  
  default boolean tryTameOrHeal(final E self, final PlayerEntity player, final Hand hand) {
    ItemStack itemstack = player.getHeldItem(hand);
    boolean hasOwner = hasOwner();
    float healAmount = getHealAmount(itemstack);
    int tameChance = isTamingItem(itemstack) ? getTameChance(player.getRNG()) : 0;
    boolean success = false;
    if (!hasOwner && tameChance > 0) {
      // attempt to tame the entity
      if(player.getRNG().nextInt(tameChance) == 0) {
        this.setOwner(player);
      }
      if(self.world.isRemote()) {
        for(int i = 0; i < 3; i++) {
          self.world.addParticle(ParticleTypes.HEART, self.getPosX(), self.getPosYEye(), self.getPosZ(), 0, 0, 0);
        }
      }
      success = true;
    } else if(hasOwner && healAmount > 0 && self.getHealth() < self.getMaxHealth()) {
      // attempt to heal the entity
      self.heal(getHealAmount(itemstack));
      if(self.world.isRemote()) {
        self.world.addParticle(ParticleTypes.HEART, self.getPosX(), self.getPosYEye(), self.getPosZ(), 0, 0, 0);
      }
      success = true;
    }
    
    if (success) {
      // attempt to consume the item
      if(!player.abilities.isCreativeMode) {
        itemstack.shrink(1);
      }
      // spawn particles
      if(self.world.isRemote()) {
        for(int i = 0; i < 3; i++) {
          self.world.addParticle(ParticleTypes.HEART, self.getPosX(), self.getPosYEye(), self.getPosZ(), 0, 0, 0);
        }
      }
    }
        
    return success;
  }
  
  default int getTameChance(final Random rand) {
    return 4;
  }
  
  default float getHealAmount(final ItemStack stack) {
    return isTamingItem(stack) ? 2.0F : 0.0F;
  }
  
  default boolean hasTamingItemInHand(PlayerEntity player) {
    for(Hand hand : Hand.values()) {
       ItemStack itemstack = player.getHeldItem(hand);
       if (isTamingItem(itemstack)) {
          return true;
       }
    }
    return false;
 }
}
