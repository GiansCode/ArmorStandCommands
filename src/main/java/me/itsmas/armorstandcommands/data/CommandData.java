package me.itsmas.armorstandcommands.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandData
{
    private final String id;
    private final Map<CommandType, String> commands;

    CommandData(String id, Map<CommandType, String> commands)
    {
        this.id = id;
        this.commands = commands;
    }

    public String getId()
    {
        return id;
    }

    public void execute(Player player)
    {
        commands.forEach((type, command) ->
        {
            if (type == CommandType.CONSOLE_COMMAND)
            {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }
            else if (type == CommandType.PLAYER_COMMAND)
            {
                player.performCommand(command);
            }
            else if (type == CommandType.PLAYER_MESSAGE)
            {
                player.sendMessage(command);
            }
        });
    }

    public enum CommandType
    {
        CONSOLE_COMMAND, PLAYER_COMMAND, PLAYER_MESSAGE;

        public static CommandType fromString(String input)
        {
            if (input.equalsIgnoreCase("[console]"))
            {
                return CONSOLE_COMMAND;
            }

            if (input.equalsIgnoreCase("[player]"))
            {
                return PLAYER_COMMAND;
            }

            if (input.equalsIgnoreCase("[message]"))
            {
                return PLAYER_MESSAGE;
            }

            return null;
        }
    }
}
