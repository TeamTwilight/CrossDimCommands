package com.crossdimcommands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.tags.StaticTagHelper;
import net.minecraft.tags.StaticTags;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod(CrossDimCommands.MOD_ID)
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = CrossDimCommands.MOD_ID)
public class CrossDimCommands {
    public static final String MOD_ID = "crossdimcommands";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CrossDimCommands() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    protected static final StaticTagHelper<DimensionType> HELPER = StaticTags.create(Registry.DIMENSION_TYPE_REGISTRY, "tags/dimension_type");

    public static final Tags.IOptionalNamedTag<DimensionType> WEATHER_COMMAND_BLACKLIST = HELPER.createOptional(new ResourceLocation(MOD_ID, "weather_command_blacklist"), null);
    public static final Tags.IOptionalNamedTag<DimensionType> WORLDBORDER_COMMAND_BLACKLIST = HELPER.createOptional(new ResourceLocation(MOD_ID, "worldborder_command_blacklist"), null);

    @SubscribeEvent
    public static void weatherCommandListener(CommandEvent event) {
        ParseResults<CommandSourceStack> parseResults = event.getParseResults();
        String[] command = parseResults.getReader().getRead().split(" ");
        CommandSourceStack sourceStack = parseResults.getContext().getSource();

        if (sourceStack != null) {
            if ((command[0].equals("/weather") || command[0].equals("weather")) && !WEATHER_COMMAND_BLACKLIST.contains(sourceStack.getLevel().dimensionType())) {
                switch (command[1]) {
                    case "clear" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(6000, 0, false, false);
                    case "rain" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(0, 6000, true, false);
                    case "thunder" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(0, 6000, true, true);
                }
            } else if (command[0].equals("/worldborder") || command[0].equals("worldborder") && !WORLDBORDER_COMMAND_BLACKLIST.contains(sourceStack.getLevel().dimensionType())) {
                ParseResults<CommandSourceStack> newParseResults = new ParseResults<CommandSourceStack>(parseResults.getContext().withSource(parseResults.getContext().getSource().withLevel(sourceStack.getLevel().getServer().overworld())), parseResults.getReader(), parseResults.getExceptions());
                event.setParseResults(newParseResults);

                if (command[1].equals("center")) {
                    try {
                        event.setCanceled(true);
                        Vec3 pos = newParseResults.getContext().build(newParseResults.getReader().getRead()).getArgument("pos", Coordinates.class).getPosition(sourceStack);
                        double scale = sourceStack.getLevel().dimensionType().coordinateScale();
                        WorldBorderCommand.setCenter(newParseResults.getContext().getSource(), new Vec2((float)(pos.x * scale), (float)(pos.z * scale)));
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
