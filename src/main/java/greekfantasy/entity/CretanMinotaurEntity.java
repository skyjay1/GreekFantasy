package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class CretanMinotaurEntity extends MinotaurEntity {
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS));
 
  public CretanMinotaurEntity(final EntityType<? extends CretanMinotaurEntity> type, final World worldIn) {
    super(type, worldIn);
    bossInfo.setVisible(GreekFantasy.CONFIG.showCretanBossBar());
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 114.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.27D)
        .add(Attributes.ATTACK_DAMAGE, 7.5D)
        .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
        .add(Attributes.ARMOR, 4.0D);
  }
  
  @Override
  public void defineSynchedData() {
    super.defineSynchedData();
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
  
  @Override
  protected void registerChargeGoal() {
    this.goalSelector.addGoal(2, new ChargeAttackGoal(1.78D));
  }
 
  @Override
  public void aiStep() {
    super.aiStep();
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
  }
  
  // Boss //

  @Override
  public void startSeenByPlayer(ServerPlayerEntity player) {
    super.startSeenByPlayer(player);
    this.bossInfo.addPlayer(player);
  }

  @Override
  public void stopSeenByPlayer(ServerPlayerEntity player) {
    super.stopSeenByPlayer(player);
    this.bossInfo.removePlayer(player);
  }

  // Sound methods

  @Override
  protected float getSoundVolume() { return 1.2F; }
  
  @Override
  protected float getVoicePitch() { return super.getVoicePitch() - 0.2F; }
  
  @Override
  public int getAmbientSoundInterval() { return 240; }
}
