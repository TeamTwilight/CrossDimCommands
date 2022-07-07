package com.crossdimcommands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod(CrossDimCommands.MOD_ID)
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber(modid = CrossDimCommands.MOD_ID)
public class CrossDimCommands {
    public static final String MOD_ID = "crossdimcommands";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CrossDimCommands() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final TagKey<DimensionType> WEATHER_COMMAND_BLACKLIST = TagKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(MOD_ID, "weather_command_blacklist"));
    public static final TagKey<DimensionType> WORLDBORDER_COMMAND_BLACKLIST = TagKey.create(Registry.DIMENSION_TYPE_REGISTRY, new ResourceLocation(MOD_ID, "worldborder_command_blacklist"));

    @SubscribeEvent
    public static void weatherCommandListener(CommandEvent event) {
        ParseResults<CommandSourceStack> parseResults = event.getParseResults();
        String[] command = parseResults.getReader().getRead().split(" ");
        CommandSourceStack sourceStack = parseResults.getContext().getSource();

        if (sourceStack != null) {
            if ((command[0].equals("/weather") || command[0].equals("weather")) && !sourceStack.getLevel().dimensionTypeRegistration().is(WEATHER_COMMAND_BLACKLIST)) {
                switch (command[1]) {
                    case "clear" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(6000, 0, false, false);
                    case "rain" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(0, 6000, true, false);
                    case "thunder" -> sourceStack.getLevel().getServer().overworld().setWeatherParameters(0, 6000, true, true);
                }
            } else if ((command[0].equals("/worldborder") || command[0].equals("worldborder")) && command[1].equals("center") && !sourceStack.getLevel().dimensionTypeRegistration().is(WORLDBORDER_COMMAND_BLACKLIST)) {
                try {
                    WorldBorderCommand.setCenter(sourceStack, Vec2Argument.getVec2(parseResults.getContext().build(parseResults.getReader().getRead()), "pos").scale((float)sourceStack.getLevel().dimensionType().coordinateScale()));
                    event.setCanceled(true);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
