package greekfantasy.deity;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import greekfantasy.GreekFantasy;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class DeityArgument implements ArgumentType<ResourceLocation> {
  private static final Collection<String> EXAMPLES = Arrays.asList(new String[] { "greekfantasy:zeus", "hades" });
  public static final DynamicCommandExceptionType DEITY_UNKNOWN = new DynamicCommandExceptionType(o -> new TranslationTextComponent("commands.deity.not_found", o));

  public static DeityArgument deity() { return new DeityArgument(); }

  public static ResourceLocation getDeityId(CommandContext<CommandSource> source, String string)
      throws CommandSyntaxException {
    return checkIfDeityExists((ResourceLocation) source.getArgument(string, ResourceLocation.class));
  }

  private static ResourceLocation checkIfDeityExists(ResourceLocation id) throws CommandSyntaxException {
    GreekFantasy.PROXY.DEITY.get(id).orElseThrow(() -> DEITY_UNKNOWN.create(id));
    return id;
  }

  public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
    final ResourceLocation parsed = ResourceLocation.read(reader);
    return checkIfDeityExists("minecraft".equals(parsed.getNamespace()) ? new ResourceLocation(GreekFantasy.MODID, parsed.getPath()) : parsed);
  }

  public Collection<String> getExamples() {
    return EXAMPLES;
  }
  
  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) { 
    return ISuggestionProvider.suggestIterable(GreekFantasy.PROXY.DEITY.getKeys(), p_listSuggestions_2_); 
  }
}
