package me.itsmas.armorstandcommands.util;

import me.itsmas.armorstandcommands.ArmorStandCommands;
import me.itsmas.armorstandcommands.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Util
{
    private static final ArmorStandCommands plugin = JavaPlugin.getPlugin(ArmorStandCommands.class);

    public static void registerListener(Listener listener)
    {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static String colour(String input)
    {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static void message(CommandSender sender, Message message)
    {
        sender.sendMessage(message.value());
    }

    public static void log(String msg)
    {
        plugin.getLogger().info(msg);
    }

    public static void logErr(String msg)
    {
        plugin.getLogger().log(Level.WARNING, msg);
    }

    public static String combine(String[] args, int beginIndex)
    {
        StringBuilder builder = new StringBuilder();

        for (int i = beginIndex; i < args.length; i++)
        {
            builder.append(args[i]).append(" ");
        }

        return builder.toString().trim();
    }
}