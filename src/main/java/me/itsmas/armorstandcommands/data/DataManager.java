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

import static java.util.stream.Collectors.joining;

public class DataManager
{
    private final ArmorStandCommands plugin;

    public DataManager(ArmorStandCommands plugin)
    {
        this.plugin = plugin;

        createDataFile();
        parseActions();
        parseStands();
    }

    public void save()
    {
        dataConfig.set("stands", null);

        standActions.forEach((id, datas) ->
        {
            List<String> ids = datas.stream().map(ActionData::getId).collect(Collectors.toList());

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
        Set<ActionData> datas = standActions.get(entityId);

        if (datas == null)
        {
            // No identifiers associated with this stand
            return;
        }

        datas.forEach(data -> data.execute(player));
    }

    public void addData(ArmorStand stand, ActionData data)
    {
        UUID uuid = stand.getUniqueId();

        if (!standActions.containsKey(uuid))
        {
            standActions.put(uuid, new HashSet<>());
        }

        standActions.get(uuid).add(data);
    }

    public boolean hasData(ArmorStand stand)
    {
        return standActions.containsKey(stand.getUniqueId());
    }

    public void clearData(ArmorStand stand)
    {
        standActions.remove(stand.getUniqueId());
    }

    public void tellDatas(Player player, ArmorStand stand)
    {
        assert hasData(stand);

        String ids = standActions.get(stand.getUniqueId())
            .stream()
            .map(ActionData::getId)
            .collect(joining(", "));

        player.sendMessage(Message.GET.value().replace("%identifiers%", ids));
    }

    private File file;
    private YamlConfiguration dataConfig;

    private final Map<UUID, Set<ActionData>> standActions = new HashMap<>();

    private final Set<ActionData> actionDatas = new HashSet<>();

    public ActionData getData(String id)
    {
        for (ActionData data : actionDatas)
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

    private final String actionsPath = "actions";

    private void parseActions()
    {
        for (String identifier : plugin.getConfig().getConfigurationSection(actionsPath).getKeys(false))
        {
            List<String> actions = plugin.getConfig(actionsPath + "." + identifier);
            actionDatas.add(new ActionData(identifier, actions));
        }
    }

    private final String basePath = "stands";

    private void parseStands()
    {
        if (!dataConfig.contains(basePath))
        {
            return;
        }

        for (String entityId : dataConfig.getConfigurationSection(basePath).getKeys(false))
        {
            List<String> datas = dataConfig.getStringList(basePath + "." + entityId);
            Set<ActionData> commandDatas = new HashSet<>();

            for (String id : datas)
            {
                ActionData cmdData = getData(id);

                if (cmdData != null)
                {
                    commandDatas.add(cmdData);
                }
            }

            standActions.put(UUID.fromString(entityId), commandDatas);
        }
    }
}
