package greekfantasy.integration;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;

public final class RGCompat {

    private static RGCompat instance;

    private static final ResourceLocation APOLLO = new ResourceLocation("greek", "apollo");
    private static final ResourceLocation HADES = new ResourceLocation("greek", "hades");
    private static final ResourceLocation HERMES = new ResourceLocation("greek", "hermes");
    private static final ResourceLocation POSEIDON = new ResourceLocation("greek", "poseidon");
    private static final ResourceLocation ZEUS = new ResourceLocation("greek", "zeus");

    public static RGCompat getInstance() {
        if(null == instance) {
            instance = new RGCompat();
        }
        return instance;
    }

    public boolean canUseRaising(final PlayerEntity player) {
        if(!GreekFantasy.isRGLoaded()) {
            return false;
        }
        return hasFavorRange(player, HADES, 9, 10) || hasPatron(player, HADES);
    }

    public boolean canUseLordOfTheSea(final PlayerEntity player) {
        return hasFavorRange(player, POSEIDON, 9, 10) || hasPatron(player, POSEIDON);
    }

    public boolean canUseFireflash(final PlayerEntity player) {
        return hasFavorRange(player, ZEUS, 9, 10) || hasPatron(player, ZEUS);
    }

    public boolean canUseFlying(final PlayerEntity player) {
        return hasFavorRange(player, HERMES, 9, 10) || hasPatron(player, HERMES);
    }

    public boolean canUseDaybreak(final PlayerEntity player) {
        return hasFavorRange(player, APOLLO, 9, 10) || hasPatron(player, APOLLO);
    }

    private boolean hasFavorRange(final PlayerEntity player, final ResourceLocation deity, final int min, final int max) {
        if(!GreekFantasy.isRGLoaded()) {
            return false;
        }
        LazyOptional<rpggods.favor.IFavor> ifavor = player.getCapability(rpggods.RPGGods.FAVOR);
        if(ifavor.isPresent()) {
            rpggods.favor.IFavor favor = ifavor.orElse(null);
            final int level = favor.getFavor(deity).getLevel();
            return level >= min && level <= max;
        }
        return false;
    }

    private boolean hasPatron(final PlayerEntity player, final ResourceLocation deity) {
        if(!GreekFantasy.isRGLoaded()) {
            return false;
        }
        LazyOptional<rpggods.favor.IFavor> ifavor = player.getCapability(rpggods.RPGGods.FAVOR);
        if(ifavor.isPresent()) {
            rpggods.favor.IFavor favor = ifavor.orElse(null);
            Optional<ResourceLocation> patron = favor.getPatron();
            return patron.isPresent() && deity.equals(patron.get());
        }
        return false;
    }
}
