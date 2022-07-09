package greekfantasy.integration;

import greekfantasy.GreekFantasy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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
        if (null == instance) {
            instance = new RGCompat();
        }
        return instance;
    }

    public boolean canUseRaising(final Player player) {
        return hasFavorRange(player, HADES, 9, 10) || hasPatron(player, HADES);
    }

    public boolean canUseLordOfTheSea(final Player player) {
        return hasFavorRange(player, POSEIDON, 9, 10) || hasPatron(player, POSEIDON);
    }

    public boolean canUseFireflash(final Player player) {
        return hasFavorRange(player, ZEUS, 9, 10) || hasPatron(player, ZEUS);
    }

    public boolean canUseFlying(final Player player) {
        return hasFavorRange(player, HERMES, 9, 10) || hasPatron(player, HERMES);
    }

    public boolean canUseDaybreak(final Player player) {
        return hasFavorRange(player, APOLLO, 9, 10) || hasPatron(player, APOLLO);
    }

    public boolean canRemovePrisonerEffect(final Player player) {
        return hasFavorRange(player, HADES, 4, 10) || hasPatron(player, HADES);
    }

    private boolean hasFavorRange(final Player player, final ResourceLocation deity, final int min, final int max) {
        if (!GreekFantasy.isRGLoaded()) {
            return false;
        }
        LazyOptional<rpggods.favor.IFavor> ifavor = player.getCapability(rpggods.RPGGods.FAVOR);
        if (ifavor.isPresent()) {
            rpggods.favor.IFavor favor = ifavor.orElse(null);
            final int level = favor.getFavor(deity).getLevel();
            return level >= min && level <= max;
        }
        return false;
    }

    private boolean hasPatron(final Player player, final ResourceLocation deity) {
        if (!GreekFantasy.isRGLoaded()) {
            return false;
        }
        LazyOptional<rpggods.favor.IFavor> ifavor = player.getCapability(rpggods.RPGGods.FAVOR);
        if (ifavor.isPresent()) {
            rpggods.favor.IFavor favor = ifavor.orElse(null);
            Optional<ResourceLocation> patron = favor.getPatron();
            return patron.isPresent() && deity.equals(patron.get());
        }
        return false;
    }
}
