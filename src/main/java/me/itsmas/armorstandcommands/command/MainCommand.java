package me.itsmas.armorstandcommands.command;

import me.itsmas.armorstandcommands.ArmorStandCommands;
import me.itsmas.armorstandcommands.data.CommandData;
import me.itsmas.armorstandcommands.message.Message;
import me.itsmas.armorstandcommands.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class MainCommand implements CommandExecutor, Listener
{
    private final ArmorStandCommands plugin;

    public MainCommand(ArmorStandCommands plugin)
    {
        this.plugin = plugin;

        Util.registerListener(this);
    }

    private Map<Player, String[]> argMap = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            Util.message(sender, Message.CONSOLE_ERROR);
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("asc.commands"))
        {
            Util.message(player, Message.NO_PERMISSION);
            return true;
        }

        argMap.remove(player);

        if (args.length < 1 || args.length > 2)
        {
            Util.message(player, Message.USAGE_ERROR);
            return true;
        }

        if (args[0].equalsIgnoreCase("add"))
        {
            if (checkArgs(player, 2, args))
            {
                String identifier = args[1];

                if (plugin.getDataManager().getData(identifier) == null)
                {
                    Util.message(player, Message.INVALID_IDENTIFIER);
                    return true;
                }

                sendArmorStandMessage(player, args);
            }
        }
        else if (args[0].equalsIgnoreCase("remove"))
        {
            if (checkArgs(player, 1, args))
            {
                sendArmorStandMessage(player, args);
            }
        }
        else if (args[0].equalsIgnoreCase("get"))
        {
            if (checkArgs(player, 1, args))
            {
                sendArmorStandMessage(player, args);
            }
        }

        return true;
    }

    private boolean checkArgs(Player player, int length, String[] args)
    {
        if (args.length != length)
        {
            Util.message(player, Message.USAGE_ERROR);
            return false;
        }

        return true;
    }

    private void sendArmorStandMessage(Player player, String[] args)
    {
        Util.message(player, Message.CLICK_STAND);

        argMap.put(player, args);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        argMap.remove(event.getPlayer());
    }

    @EventHandler
    public void onClickStand(PlayerInteractAtEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType() != EntityType.ARMOR_STAND)
        {
            return;
        }

        ArmorStand stand = (ArmorStand) entity;

        String[] args = argMap.get(player);

        if (args != null)
        {
            event.setCancelled(true);

            if (args[0].equalsIgnoreCase("add"))
            {
                String identifier = args[1];

                CommandData data = plugin.getDataManager().getData(identifier);

                if (data == null)
                {
                    Util.message(player, Message.INVALID_IDENTIFIER);
                    return;
                }

                player.sendMessage(Message.ADDED.value().replace("%identifier%", identifier));
                plugin.getDataManager().addData(stand, data);
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                if (!plugin.getDataManager().hasData(stand))
                {
                    Util.message(player, Message.NO_IDENTIFIER);
                    return;
                }

                Util.message(player, Message.REMOVED);
                plugin.getDataManager().clearData(stand);
            }
            else if (args[0].equalsIgnoreCase("get"))
            {
                if (!plugin.getDataManager().hasData(stand))
                {
                    Util.message(player, Message.NO_IDENTIFIER);
                    return;
                }

                plugin.getDataManager().tellDatas(player, stand);
            }

            argMap.remove(player);
        }
        else
        {
            plugin.getDataManager().handleInteract(player, stand);
        }
    }
}
