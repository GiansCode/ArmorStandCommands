package me.itsmas.armorstandcommands.message;

import me.itsmas.armorstandcommands.ArmorStandCommands;
import me.itsmas.armorstandcommands.util.Util;

import java.util.Arrays;

public enum Message
{
    CONSOLE_ERROR,
    NO_PERMISSION,
    USAGE_ERROR,
    CLICK_STAND,
    INVALID_IDENTIFIER,
    ADDED,
    REMOVED,
    NO_IDENTIFIER,
    GET;

    private String msg;

    public String value()
    {
        return msg;
    }

    private void setValue(String msg)
    {
        this.msg = msg;
    }

    /**
     * Initialise the plugin messages
     * @param plugin The plugin instance
     */
    public static void init(ArmorStandCommands plugin)
    {
        Arrays.stream(values()).forEach(message ->
        {
            String raw = plugin.getConfig("messages." + message.name().toLowerCase());

            if (raw == null)
            {
                Util.logErr("Unable to find message value for message '" + message.name() + "'");

                raw = message.name();
            }

            message.setValue(Util.colour(raw));
        });
    }
}
