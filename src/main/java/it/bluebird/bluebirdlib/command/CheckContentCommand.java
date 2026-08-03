package it.bluebird.bluebirdlib.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.animations.util.AnimationsLoader;
import it.bluebird.bluebirdlib.simplecora.geometry.util.GeometryLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = BluebirdLib.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CheckContentCommand {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher()
                .register(Commands.literal("reload_models")
                        .executes(CheckContentCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> command){
        if(command.getSource().getEntity() instanceof Player) {
            GeometryLoader.loadModels();
            AnimationsLoader.loadAnimations();
        }
        return Command.SINGLE_SUCCESS;
    }
}
