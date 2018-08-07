package me.itsmas.armorstandcommands.data;

import me.itsmas.armorstandcommands.ArmorStandCommands;
import me.itsmas.armorstandcommands.message.Message;
import me.itsmas.armorstandcommands.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataManager
{
    private final ArmorStandCommands plugin;

    public DataManager(ArmorStandCommands plugin)
    {
        this.plugin = plugin;

        createDataFile();
        parseCommands();
        parseStands();
    }

    public void save()
    {
        dataConfig.set("stands", null);

        standDatas.forEach((id, datas) ->
        {
            List<String> ids = datas.stream().map(CommandData::getId).collect(Collectors.toList());

            dataConfig.set("stands." + id, ids);
        });

        try
        {
            dataConfig.save(file);
        }
        catch (IOException ex)
        {
            Util.logErr("Unable to save data file:");
            ex.printStackTrace();
        }
    }

    public void handleInteract(Player player, ArmorStand stand)
    {
        UUID entityId = stand.getUniqueId();
        Set<CommandData> datas = standDatas.get(entityId);

        if (datas == null)
        {
            // No commands associated with this stand
            return;
        }

        datas.forEach(data -> data.execute(player));
    }

    public void addData(ArmorStand stand, CommandData data)
    {
        UUID uuid = stand.getUniqueId();

        if (!standDatas.containsKey(uuid))
        {
            standDatas.put(uuid, new HashSet<>());
        }

        standDatas.get(uuid).add(data);
    }

    public boolean hasData(ArmorStand stand)
    {
        return standDatas.containsKey(stand.getUniqueId());
    }

    public void clearData(ArmorStand stand)
    {
        standDatas.remove(stand.getUniqueId());
    }

    public void tellDatas(Player player, ArmorStand stand)
    {
        assert hasData(stand);

        String ids = standDatas.get(stand.getUniqueId()).stream().map(CommandData::getId).collect(Collectors.joining(", "));

        player.sendMessage(Message.GET.value().replace("%identifiers%", ids));
    }

    private File file;
    private YamlConfiguration dataConfig;

    private Map<UUID, Set<CommandData>> standDatas = new HashMap<>();

    private Set<CommandData> commandDatas = new HashSet<>();

    public CommandData getData(String id)
    {
        for (CommandData data : commandDatas)
        {
            if (data.getId().equalsIgnoreCase(id))
            {
                return data;
            }
        }

        return null;
    }

    private void createDataFile()
    {
        file = new File(plugin.getDataFolder(), "data.yml");

        if (!file.exists())
        {
            plugin.saveResource("data.yml", false);
        }

        dataConfig = YamlConfiguration.loadConfiguration(file);
    }

    private void parseCommands()
    {
        String path = "commands";

        for (String id : plugin.getConfig().getConfigurationSection(path).getKeys(false))
        {
            List<String> commands = plugin.getConfig(path + "." + id);

            Map<CommandData.CommandType, String> commandData = new HashMap<>();

            commands.forEach(command ->
            {
                String[] split = command.split(" ");

                CommandData.CommandType commandType = CommandData.CommandType.fromString(split[0]);

                if (commandType == null)
                {
                    Util.logErr("Could not recognise command type for ID '" + id + "'");
                    return;
                }

                String cmd = Util.colour(Util.combine(split, 1));

                commandData.put(commandType, cmd);
            });

            commandDatas.add(new CommandData(id, commandData));
        }
    }

    private void parseStands()
    {
        String basePath = "stands";

        for (String entityId : dataConfig.getConfigurationSection(basePath).getKeys(false))
        {
            List<String> datas = dataConfig.getStringList(basePath + "." + entityId);
            Set<CommandData> commandDatas = new HashSet<>();

            for (String id : datas)
            {
                CommandData cmdData = getData(id);

                if (cmdData != null)
                {
                    commandDatas.add(cmdData);
                }
            }

            standDatas.put(UUID.fromString(entityId), commandDatas);
        }
    }
}
