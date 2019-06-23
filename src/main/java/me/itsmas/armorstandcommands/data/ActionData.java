package me.itsmas.armorstandcommands.data;

import io.samdev.actionutil.ActionUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionData
{
    private final String id;
    private final List<String> actions;

    ActionData(String id, List<String> actions)
    {
        this.id = id;
        this.actions = actions;
    }

    String getId()
    {
        return id;
    }

    void execute(Player player)
    {
        ActionUtil.executeActions(player, actions);
    }
}
