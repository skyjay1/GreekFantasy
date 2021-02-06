package greekfantasy.deity.favor;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
                            .executes(command -> setFavor(command.getSource(), EntityArgument.getPlayers(command, "targets"), DeityArgument.getDeityId(command, "deity"), IntegerArgumentType.getInteger(command, "amount"), Type.LEVELS))))))))
            .then(Commands.literal("query")
                .then(Commands.argument("targets", EntityArgument.player())
                    .then(Commands .argument("deity", DeityArgument.deity())
                        .executes(command -> queryFavor(command.getSource(), EntityArgument.getPlayer(command, "targets"), DeityArgument.getDeityId(command, "deity"), Type.LEVELS))
                        .then(Commands.literal("points")
                            .executes(command -> queryFavor(command.getSource(), EntityArgument.getPlayer(command, "targets"), DeityArgument.getDeityId(command, "deity"), Type.POINTS)))
                        .then(Commands.literal("levels")
                            .executes(command -> queryFavor(command.getSource(), EntityArgument.getPlayer(command, "targets"), DeityArgument.getDeityId(command, "deity"), Type.LEVELS))))))  
    );
    
    commandSource.register(Commands.literal("favor")
        .requires(p -> p.hasPermissionLevel(2))
        .redirect(commandNode));
  }
  
  private static int queryFavor(CommandSource source, ServerPlayerEntity player, ResourceLocation deity, Type type) {
    IDeity ideity = GreekFantasy.PROXY.DEITY.get(deity).orElse(Deity.EMPTY);
    int amount = type.favorGetter.accept(player, player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()), ideity, 0);
    source.sendFeedback(new TranslationTextComponent("commands.favor.query." + type.name, player.getDisplayName(), amount, ideity.getText()), false);
    return amount;
  }
  
  private static int setFavor(CommandSource source, Collection<? extends ServerPlayerEntity> players, ResourceLocation deity, int amount, Type type) {
    // set favor for each player in the collection
    final IDeity ideity = GreekFantasy.PROXY.DEITY.get(deity).orElse(Deity.EMPTY);
    for(final ServerPlayerEntity player : players) {
      type.favorSetter.accept(player, player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()), ideity, amount);
    }
    // send command feedback
    if (players.size() == 1) {
      source.sendFeedback(new TranslationTextComponent("commands.favor.set." + type.name + ".success.single", amount, ideity.getText(), players.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent("commands.favor.set." + type.name + ".success.multiple", amount, ideity.getText(), players.size()), true);
    } 
    
    return players.size();
  }
  
  private static int addFavor(CommandSource source, Collection<? extends ServerPlayerEntity> players, ResourceLocation deity, int amount, Type type) {
    // add favor to each player in the collection
    final IDeity ideity = GreekFantasy.PROXY.DEITY.get(deity).orElse(Deity.EMPTY);
    for(final ServerPlayerEntity player : players) {
      type.favorAdder.accept(player, player.getCapability(GreekFantasy.FAVOR).orElse(GreekFantasy.FAVOR.getDefaultInstance()), ideity, amount);
    }
    // send command feedback
    if (players.size() == 1) {
      source.sendFeedback(new TranslationTextComponent("commands.favor.add." + type.name + ".success.single", amount, ideity.getText(), players.iterator().next().getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent("commands.favor.add." + type.name + ".success.multiple", amount, ideity.getText(), players.size()), true);
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
