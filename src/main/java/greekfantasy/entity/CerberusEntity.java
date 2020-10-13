package greekfantasy.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class CerberusEntity extends CreatureEntity {
  
  private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

  public CerberusEntity(final EntityType<? extends CerberusEntity> type, final World worldIn) {
    super(type, worldIn);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
    
    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
  }
  
  // Boss Logic

  @Override
  public void addTrackingPlayer(ServerPlayerEntity player) {
    super.addTrackingPlayer(player);
    this.bossInfo.addPlayer(player);
  }

  @Override
  public void removeTrackingPlayer(ServerPlayerEntity player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }
  
  // End Boss Logic
  

}
