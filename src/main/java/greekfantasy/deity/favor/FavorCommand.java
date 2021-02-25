package greekfantasy.deity.favor;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.Deity;
import greekfantasy.deity.DeityArgument;
import greekfantasy.deity.IDeity;
import greekfantasy.event.FavorChangedEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class FavorCommand {
  private static final DynamicCommandExceptionType FAVOR_DISABLED_EXCEPTION = new DynamicCommandExceptionType(o -> new TranslationTextComponent("commands.favor.enabled.disabled", o));
  
  public static void register(CommandDispatcher<CommandSource> commandSource) {
    LiteralCommandNode<CommandSource> commandNode = commandSource.register(
        Commands.literal("favor")
        .requires(p -> p.hasPermissionLevel(2))
        .then(Commands.literal("add")
            .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("deity", DeityArgument.deity())
                    .then((Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(command -> addFavor(command.getSource(), EntityArgument.getPlayers(command, "targets"), DeityArgument.getDeityId(command, "deity"), IntegerArgumentType.getInteger(command, "amount"), Type.LEVELS))
                        .then(Commands.literal("points")
                            .executes(command -> addFavor(command.getSource(), EntityArgument.getPlayers(command, "targets"), DeityArgument.getDeityId(command, "deity"), IntegerArgumentType.getInteger(command, "amount"), Type.POINTS)))
                        .then(Commands.literal("levels")
                            .executes(command -> addFavor(command.getSource(), EntityArgument.getPlayers(command, "targets"), DeityArgument.getDeityId(command, "deity"), IntegerArgumentType.getInteger(command, "amount"), Type.LEVELS))))))))
        .then(Commands.literal("set")
            .then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("deity", DeityArgument.deity())
                    .then((Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(command -> setFavor(command.getSource(), EntityArgument.getPlayers(command, "targets"), DeityArgument.getDeityId(command, "deity"), IntegerArgumentType.getInteger(command, "amount"), Type.LEVELS))
                        .then(Commands.literal("points")
                            .executes(command -> setFavor(command.getSource(), EntityArgument.getPlayers(command, "targets"), DeityArgument.getDeityId(command, "deity"), IntegerArgumentType.getInteger(command, "amount"), Type.POINTS)))
                        .then(Commands.literal("levels")
                            .executes(command -> setFavor(command.getSource(), EntityArgument.getPlayers(command, "targets"), DeityArgument.getDeityId(command, "deity"), IntegerArgumentType.getInteger(command, "amount"), Type.LEVELS))))))
                .then(Commands.literal("enabled")
                    .then(Commands.argument("flag", BoolArgumentType.bool())
                        .executes(command -> setEnabled(command.getSource(), EntityArgument.getPlayers(command, "targets"), BoolArgumentType.getBool(command, "flag")))))
                .then(Commands.literal("cooldown")
                    .then(Commands.argument("amount", LongArgumentType.longArg(0))
                        .executes(command -> setCooldown(command.getSource(), EntityArgument.getPlayers(command, "targets"), LongArgumentType.getLong(command, "amount")))))))
        .then(Commands.literal("query")
            .then(Commands.argument("target", EntityArgument.player())
                .then(Commands.argument("deity", DeityArgument.deity())
                    .executes(command -> queryFavor(command.getSource(), EntityArgument.getPlayer(command, "target"), DeityArgument.getDeityId(command, "deity"), Type.LEVELS))
                    .then(Commands.literal("points")
                        .executes(command -> queryFavor(command.getSource(), EntityArgument.getPlayer(command, "target"), DeityArgument.getDeityId(command, "deity"), Type.POINTS)))
                    .then(Commands.literal("levels")
                        .executes(command -> queryFavor(command.getSource(), EntityArgument.getPlayer(command, "target"), DeityArgument.getDeityId(command, "deity"), Type.LEVELS))))
                .then(Commands.literal("enabled")
                    .executes(command -> queryEnabled(command.getSource(), EntityArgument.getPlayer(command, "target"))))))
        .then(Commands.literal("cap")
            .then(Commands.argument("target", EntityArgument.players())
                .then(Commands.argument("deity", DeityArgument.deity())
                    .then(Commands.argument("min", IntegerArgumentType.integer())
                        .then(Commands.argument("max", IntegerArgumentType.integer())
                            .executes(command -> setCap(command.getSource(), EntityArgument.getPlayers(command, "target"), DeityArgument.getDeityId(command, "deity"), 
                                IntegerArgumentType.getInteger(command, "min"), IntegerArgumentType.getInteger(command, "max"), Type.LEVELS))
                            .then(Commands.literal("points")
                                .executes(command -> setCap(command.getSource(), EntityArgument.getPlayers(command, "target"), DeityArgument.getDeityId(command, "deity"), 
                                IntegerArgumentType.getInteger(command, "min"), IntegerArgumentType.getInteger(command, "max"), Type.POINTS)))
                            .then(Commands.literal("levels")
                                .executes(command -> setCap(command.getSource(), EntityArgument.getPlayers(command, "target"), DeityArgument.getDeityId(command, "deity"), 
                                IntegerArgumentType.getInteger(command, "min"), IntegerArgumentType.getInteger(command, "max"), Type.LEVELS)))))))) 
    );
    
    commandSource.register(Commands.literal("favor")
        .requires(p -> p.hasPermissionLevel(2))
        .redirect(commandNode));
  }
  
  private static int queryFavor(CommandSource source, ServerPlayerEntity player, ResourceLocation deity, Type type) throws CommandSyntaxException {
    final IDeity ideity = GreekFantasy.PROXY.DEITY.get(deity).orElse(Deity.EMPTY);
    final IFavor favor = player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance());
    if(!favor.isEnabled()) {
      throw FAVOR_DISABLED_EXCEPTION.create(player.getDisplayName());
    }
    int amount = type.favorGetter.accept(player, favor, ideity, 0);
    source.sendFeedback(new TranslationTextComponent("commands.favor.query." + type.name, player.getDisplayName(), amount, ideity.getText()), false);
    return amount;
  }
  
  private static int setFavor(CommandSource source, Collection<? extends ServerPlayerEntity> players, ResourceLocation deity, int amount, Type type) throws CommandSyntaxException {
    // set favor for each player in the collection
    final IDeity ideity = GreekFantasy.PROXY.DEITY.get(deity).orElse(Deity.EMPTY);
    IFavor favor;
    for(final ServerPlayerEntity player : players) {
      favor = player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance());
      if(!favor.isEnabled()) {
        throw FAVOR_DISABLED_EXCEPTION.create(player.getDisplayName());
      }
      type.favorSetter.accept(player, favor, ideity, amount);
    }
    // send command feedback
    if (players.size() == 1) {
      source.sendFeedback(new TranslationTextComponent("commands.favor.set." + type.name + ".success.single", amount, ideity.getText(), players.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent("commands.favor.set." + type.name + ".success.multiple", amount, ideity.getText(), players.size()), true);
    } 
    
    return players.size();
  }
  
  private static int addFavor(CommandSource source, Collection<? extends ServerPlayerEntity> players, ResourceLocation deity, int amount, Type type) throws CommandSyntaxException {
    // add favor to each player in the collection
    final IDeity ideity = GreekFantasy.PROXY.DEITY.get(deity).orElse(Deity.EMPTY);
    IFavor favor;
    for(final ServerPlayerEntity player : players) {
      favor = player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance());
      if(!favor.isEnabled()) {
        throw FAVOR_DISABLED_EXCEPTION.create(player.getDisplayName());
      }
      type.favorAdder.accept(player, favor, ideity, amount);
    }
    // send command feedback
    if (players.size() == 1) {
      source.sendFeedback(new TranslationTextComponent("commands.favor.add." + type.name + ".success.single", amount, ideity.getText(), players.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent("commands.favor.add." + type.name + ".success.multiple", amount, ideity.getText(), players.size()), true);
    } 
    
    return players.size();
  }
  
  private static int setEnabled(CommandSource source, Collection<? extends ServerPlayerEntity> players, boolean enabled) {
    for(final ServerPlayerEntity player : players) {
      player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()).setEnabled(enabled);
    }
    // send command feedback
    final String sub = (enabled ? "enabled" : "disabled");
    if (players.size() == 1) {
      source.sendFeedback(new TranslationTextComponent("commands.favor." + sub + ".success.single", players.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent("commands.favor." + sub + ".success.multiple", players.size()), true);
    } 
    return 0;
  }
  
  private static int queryEnabled(CommandSource source, ServerPlayerEntity player) {
    final boolean enabled = player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()).isEnabled();
    // send command feedback
    source.sendFeedback(new TranslationTextComponent("commands.favor.enabled." + (enabled ? "enabled" : "disabled"), player.getDisplayName()), true);
    return enabled ? 1 : 0;
  }
  
  private static int setCooldown(CommandSource source, Collection<? extends ServerPlayerEntity> players, long cooldown) throws CommandSyntaxException {
    // add favor to each player in the collection
    IFavor favor;
    long time;
    for(final ServerPlayerEntity player : players) {
      time = IFavor.calculateTime(player);
      favor = player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance());
      if(!favor.isEnabled()) {
        throw FAVOR_DISABLED_EXCEPTION.create(player.getDisplayName());
      }
      favor.setEffectTime(time, cooldown);
      favor.setTriggeredTime(time, cooldown);
    }
    // send command feedback
    if (players.size() == 1) {
      source.sendFeedback(new TranslationTextComponent("commands.favor.cooldown.success.single", cooldown, players.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent("commands.favor.cooldown.success.multiple", cooldown, players.size()), true);
    } 
    
    return players.size();
  }
  
  private static int setCap(CommandSource source, Collection<ServerPlayerEntity> players, ResourceLocation deity, int min, int max, Type type) throws CommandSyntaxException {
    // cap favor for each player in the collection
    final IDeity ideity = GreekFantasy.PROXY.DEITY.get(deity).orElse(Deity.EMPTY);
    int actualMin = Math.min(min, max);
    int actualMax = Math.max(min, max);
    IFavor favor;
    for(final ServerPlayerEntity player : players) {
      favor = player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance());
      if(!favor.isEnabled()) {
        throw FAVOR_DISABLED_EXCEPTION.create(player.getDisplayName());
      }
      type.favorCapper.accept(player, favor, ideity, actualMin, actualMax);
    }
    // send command feedback
    if (players.size() == 1) {
      source.sendFeedback(new TranslationTextComponent("commands.favor.cap." + type.name + ".success.single", actualMin, actualMax, ideity.getText(), players.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent("commands.favor.cap." + type.name + ".success.multiple", actualMin, actualMax, ideity.getText(), players.size()), true);
    } 
    
    return players.size();
  }

  
  static enum Type {
    POINTS("points", 
      (p, f, d, a) -> (int)f.getFavor(d).getFavor(), 
      (p, f, d, a) -> {
        f.getFavor(d).setFavor(p, d, a, FavorChangedEvent.Source.COMMAND);
        return a;
      },
      (p, f, d, a) -> {
        f.getFavor(d).addFavor(p, d, a, FavorChangedEvent.Source.COMMAND);
        return a;
      },
      (p, f, d, a1, a2) -> {
        f.getFavor(d).setLevelBounds(FavorLevel.calculateLevel(a1), FavorLevel.calculateLevel(a2));
        return a2 - a1;
      }
    ),
    LEVELS("levels", 
      (p, f, d, a) -> f.getFavor(d).getLevel(), 
      (p, f, d, a) -> {
        f.getFavor(d).setFavor(p, d, FavorLevel.calculateFavor(a), FavorChangedEvent.Source.COMMAND);
        return a;
      },
      (p, f, d, a) -> {
        f.getFavor(d).addFavor(p, d, FavorLevel.calculateFavor(a) + (long)Math.signum(a), FavorChangedEvent.Source.COMMAND);
        return a;
      },
      (p, f, d, a1, a2) -> {
        f.getFavor(d).setLevelBounds(a1, a2);
        return a2 - a1;
      }
    );
    
    public final String name;
    public final IFavorFunction favorGetter;
    public final IFavorFunction favorSetter;
    public final IFavorFunction favorAdder;
    public final IXFavorFunction favorCapper;

    Type(final String key, final IFavorFunction getter, final IFavorFunction setter, final IFavorFunction adder, final IXFavorFunction capper) {
      name = key;
      favorGetter = getter;
      favorSetter = setter;
      favorAdder = adder;
      favorCapper = capper;
    }
  }
  
  @FunctionalInterface
  private interface IFavorFunction {
    public int accept(final ServerPlayerEntity player, final IFavor favor, final IDeity deity, final int amount);
  }
  
  @FunctionalInterface
  private interface IXFavorFunction {
    public int accept(final ServerPlayerEntity player, final IFavor favor, final IDeity deity, final int amount1, final int amount2);
  }
}
