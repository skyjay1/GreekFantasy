package greekfantasy.deity.favor;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
                        .executes(command -> setEnabled(command.getSource(), EntityArgument.getPlayers(command, "targets"), true)))
                .then(Commands.literal("disabled")
                        .executes(command -> setEnabled(command.getSource(), EntityArgument.getPlayers(command, "targets"), false)))))
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
      }
    );
    
    public final String name;
    public final IFavorFunction favorGetter;
    public final IFavorFunction favorSetter;
    public final IFavorFunction favorAdder;

    Type(final String key, final IFavorFunction getter, final IFavorFunction setter, final IFavorFunction adder) {
      name = key;
      favorGetter = getter;
      favorSetter = setter;
      favorAdder = adder;
    }
  }
  
  @FunctionalInterface
  private interface IFavorFunction {
    public int accept(final ServerPlayerEntity player, final IFavor favor, final IDeity deity, final int amount);
  }
}
