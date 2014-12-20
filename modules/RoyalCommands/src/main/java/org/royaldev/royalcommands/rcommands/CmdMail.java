package org.royaldev.royalcommands.rcommands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.MessageColor;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;
import org.royaldev.royalcommands.configuration.PlayerConfiguration;
import org.royaldev.royalcommands.configuration.PlayerConfigurationManager;

import java.util.ArrayList;
import java.util.List;

@ReflectCommand
public class CmdMail extends BaseCommand {

    public CmdMail(final RoyalCommands instance, final String name) {
        super(instance, name, true);
    }

    public static String[] splitFirst(String source, String splitter) {
        final List<String> rv = new ArrayList<>();
        int last = 0;
        int next = source.indexOf(splitter, last);
        if (next != -1) {
            rv.add(source.substring(last, next));
            last = next + splitter.length();
        }
        if (last < source.length()) rv.add(source.substring(last, source.length()));
        return rv.toArray(new String[rv.size()]);
    }

    @Override
    public boolean runCommand(final CommandSender cs, final Command cmd, final String label, final String[] args) {
        if (args.length < 1) {
            cs.sendMessage(cmd.getDescription());
            return false;
        }
        if (args[0].equalsIgnoreCase("read")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(MessageColor.NEGATIVE + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            final PlayerConfiguration pcm = PlayerConfigurationManager.getConfiguration(p);
            final List<String> mails = pcm.getStringList("mail");
            if (mails.isEmpty()) {
                cs.sendMessage(MessageColor.NEGATIVE + "You have no mail!");
                return true;
            }
            for (String mail : mails) {
                final String[] splitMail = splitFirst(mail, ": ");
                if (splitMail.length < 2) continue;
                final String user = splitMail[0];
                final String msg = splitMail[1];
                cs.sendMessage(MessageColor.POSITIVE + "[" + MessageColor.NEUTRAL + user + MessageColor.POSITIVE + "] " + MessageColor.NEUTRAL + msg);
            }
            cs.sendMessage(MessageColor.POSITIVE + "Use " + MessageColor.NEUTRAL + "/mail clear" + MessageColor.POSITIVE + " to clear your mailbox.");
        } else if (args[0].equalsIgnoreCase("clear")) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(MessageColor.NEGATIVE + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            final PlayerConfiguration pcm = PlayerConfigurationManager.getConfiguration(p);
            if (!pcm.exists()) pcm.createFile();
            if (pcm.isSet("mail")) pcm.set("mail", null);
            cs.sendMessage(MessageColor.POSITIVE + "Your mailbox has been cleared.");
        } else if (args[0].equalsIgnoreCase("send")) {
            if (!this.ah.isAuthorized(cs, "rcmds.mail.send")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 3) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            OfflinePlayer op = this.plugin.getServer().getOfflinePlayer(args[1]);
            if (!op.hasPlayedBefore()) {
                cs.sendMessage(MessageColor.NEGATIVE + "That player does not exist!");
                return true;
            }
            final String senderName = cs.getName();
            final String newmail = senderName + ": " + RoyalCommands.getFinalArg(args, 2);
            final PlayerConfiguration pcm = PlayerConfigurationManager.getConfiguration(op);
            List<String> mail = pcm.getStringList("mail");
            mail.add(newmail);
            pcm.set("mail", mail);
            cs.sendMessage(MessageColor.POSITIVE + "Mail has been sent.");
        } else {
            cs.sendMessage(cmd.getDescription());
            return false;
        }
        return true;
    }
}
