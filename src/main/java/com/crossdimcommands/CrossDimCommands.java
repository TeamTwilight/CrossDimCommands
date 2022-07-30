package com.crossdimcommands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.impl.WorldBorderCommand;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;
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

    @SubscribeEvent
    public static void weatherCommandListener(CommandEvent event) {
        ParseResults<CommandSource> parseResults = event.getParseResults();
        String[] command = parseResults.getReader().getRead().split(" ");
        CommandSource sourceStack = parseResults.getContext().getSource();

        if (sourceStack != null && !sourceStack.getLevel().dimensionType().equalTo(sourceStack.getServer().registryAccess().dimensionTypes().getOrThrow(DimensionType.OVERWORLD_LOCATION))) {
            if ((command[0].equals("/weather") || command[0].equals("weather"))) {
                switch (command[1]) {
                    case "clear" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(6000, 0, false, false);
                    case "rain" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(0, 6000, true, false);
                    case "thunder" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(0, 6000, true, true);
                }
            } else if (command[0].equals("/worldborder") || command[0].equals("worldborder")) {
                ParseResults<CommandSource> newParseResults = new ParseResults<CommandSource>(parseResults.getContext().withSource(parseResults.getContext().getSource().withLevel(sourceStack.getLevel().getServer().overworld())), parseResults.getReader(), parseResults.getExceptions());
                event.setParseResults(newParseResults);

                if (command[1].equals("center")) {
                    try {
                        event.setCanceled(true);
                        Vector3d pos = newParseResults.getContext().build(newParseResults.getReader().getRead()).getArgument("pos", ILocationArgument.class).getPosition(sourceStack);
                        double scale = sourceStack.getLevel().dimensionType().coordinateScale();
                        WorldBorderCommand.setCenter(newParseResults.getContext().getSource(), new Vector2f((float)(pos.x * scale), (float)(pos.z * scale)));
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
