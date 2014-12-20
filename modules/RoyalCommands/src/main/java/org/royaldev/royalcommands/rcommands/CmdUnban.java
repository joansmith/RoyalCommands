package org.royaldev.royalcommands.rcommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.royaldev.royalcommands.Config;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.configuration.PlayerConfiguration;
import org.royaldev.royalcommands.configuration.PlayerConfigurationManager;

@ReflectCommand
public class CmdUnban extends BaseCommand {

    public CmdUnban(final RoyalCommands instance, final String name) {
        super(instance, name, true);
    }

    @Override
    public boolean runCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
        if (args.length < 1) {
            cs.sendMessage(cmd.getDescription());
            return false;
        }
        OfflinePlayer t = this.plugin.getServer().getOfflinePlayer(args[0]);
        PlayerConfiguration pcm = PlayerConfigurationManager.getConfiguration(t);
        if (!t.isBanned()) {
            cs.sendMessage(MessageColor.NEGATIVE + "That player isn't banned!");
            return true;
        }
        RUtils.unbanPlayer(t);
        if (pcm.exists()) pcm.set("bantime", null);
        cs.sendMessage(MessageColor.POSITIVE + "You have unbanned " + MessageColor.NEUTRAL + t.getName() + MessageColor.POSITIVE + ".");
        String message = RUtils.getInGameMessage(Config.igUnbanFormat, "", t, cs); // "" because there is no reason for unbans;
        this.plugin.getServer().broadcast(message, "rcmds.see.unban");
        return true;
    }
}
