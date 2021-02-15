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

public class CretanEntity extends MinotaurEntity {
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS));
 
  public CretanEntity(final EntityType<? extends CretanEntity> type, final World worldIn) {
    super(type, worldIn);
    bossInfo.setVisible(GreekFantasy.CONFIG.showCretanBossBar());
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 114.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.27D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 7.5D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.5D)
        .createMutableAttribute(Attributes.ARMOR, 4.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
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
  public void livingTick() {
    super.livingTick();
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
  }
  
  // Boss //

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

  // Sound methods

  @Override
  protected float getSoundVolume() { return 1.2F; }
  
  @Override
  protected float getSoundPitch() { return super.getSoundPitch() - 0.2F; }
  
  @Override
  public int getTalkInterval() { return 240; }
}
