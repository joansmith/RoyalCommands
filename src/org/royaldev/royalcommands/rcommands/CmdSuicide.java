package org.royaldev.royalcommands.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

public class CmdSuicide implements CommandExecutor {

    private RoyalCommands plugin;

    public CmdSuicide(RoyalCommands instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("suicide")) {
            if (!plugin.isAuthorized(cs, "rcmds.suicide")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            p.setLastDamageCause(new EntityDamageByEntityEvent(p, p, EntityDamageEvent.DamageCause.SUICIDE, 0));
            p.setHealth(0);
            plugin.getServer().broadcastMessage(ChatColor.RED + "The player " + ChatColor.GRAY + p.getDisplayName() + ChatColor.RED + " committed suicide.");
            return true;
        }
        return false;
    }
}
