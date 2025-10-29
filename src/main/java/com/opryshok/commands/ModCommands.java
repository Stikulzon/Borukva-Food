package com.opryshok.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.opryshok.BorukvaFood;
import com.opryshok.config.ModConfig;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ModCommands {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerReloadConfigsCommand(dispatcher);
        });
    }

    private static void registerReloadConfigsCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("borukvafood")
                .requires(Permissions.require("borukvafood.command.reload_configs", 4))
                .then(CommandManager.literal("reloadConfigs")
                        .executes(ModCommands::executeReloadConfigsCommand)
                ));
    }

    private static int executeReloadConfigsCommand(CommandContext<ServerCommandSource> context) {
        BorukvaFood.configInstance.loadValuesFromJson();
        context.getSource().sendFeedback(() -> Text.translatable("commands.borukva-food.config_reload"), false);
        System.out.println(ModConfig.replaceCompostOutputWithFertilizer);
        return 1;
    }
}
