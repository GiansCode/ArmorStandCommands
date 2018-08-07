package me.itsmas.armorstandcommands;

import me.itsmas.armorstandcommands.command.MainCommand;
import me.itsmas.armorstandcommands.data.DataManager;
import me.itsmas.armorstandcommands.message.Message;
import org.bukkit.plugin.java.JavaPlugin;

public class ArmorStandCommands extends JavaPlugin
{
    private DataManager dataManager;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        Message.init(this);

        dataManager = new DataManager(this);

        getCommand("asc").setExecutor(new MainCommand(this));
    }

    @Override
    public void onDisable()
    {
        getDataManager().save();
    }

    public DataManager getDataManager()
    {
        return dataManager;
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String path)
    {
        return (T) getConfig().get(path);
    }
}
