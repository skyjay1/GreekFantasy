package greekfantasy.entity.boss;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.monster.Minotaur;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.List;

public class CretanMinotaur extends Minotaur {

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);

    public CretanMinotaur(final EntityType<? extends CretanMinotaur> type, final Level worldIn) {
        super(type, worldIn);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 114.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ARMOR, 8.0D);
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
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        if(!this.level.isClientSide() && this.tickCount % 60 == 1) {
            // clear player list
            this.bossInfo.removeAllPlayers();
            // locate nearby players and add them to the boss event when in range
            List<ServerPlayer> serverPlayers = level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(24.0D));
            for(ServerPlayer player : serverPlayers) {
                if(player != null && this.getSensing().hasLineOfSight(player)) {
                    this.bossInfo.addPlayer(player);
                }
            }
        }
    }

    // Boss //

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.setVisible(GreekFantasy.CONFIG.showCretanBossBar());
        if(this.hasCustomName()) {
            this.bossInfo.setName(this.getCustomName());
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // Sound methods

    @Override
    protected float getSoundVolume() {
        return 1.2F;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() - 0.2F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }
}
