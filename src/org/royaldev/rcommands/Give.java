package org.royaldev.rcommands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.royaldev.royalcommands.RUtils;
import org.royaldev.royalcommands.RoyalCommands;

import java.util.HashMap;

public class Give implements CommandExecutor {

    RoyalCommands plugin;

    public Give(RoyalCommands plugin) {
        this.plugin = plugin;
    }

    public static boolean validItem(String itemname) {
        try {
            Integer.parseInt(itemname);
        } catch (Exception e) {
            try {
                Material.getMaterial(itemname.trim().replace(" ", "_").toUpperCase()).getId();
            } catch (Exception e2) {
                return false;
            }
        }
        return true;
    }

    public static boolean giveItemStandalone(Player target, RoyalCommands plugin, String itemname, int amount) {
        if (target == null) {
            return false;
        }
        String called = itemname;
        String data = null;
        if (called.contains(":")) {
            String[] calleds = called.split(":");
            called = calleds[0].trim();
            data = calleds[1].trim();
        }
        Integer iblock;
        try {
            iblock = Integer.parseInt(called);
        } catch (Exception e) {
            try {
                iblock = Material.getMaterial(called.trim().replace(" ", "_").toUpperCase()).getId();
            } catch (Exception e2) {
                target.sendMessage(ChatColor.RED + "That block does not exist!");
                return false;
            }
        }
        if (amount < 1) {
            amount = 1;
        }
        if (iblock == 0) {
            target.sendMessage(ChatColor.RED + "You cannot spawn air!");
            return false;
        }
        ItemStack toInv;
        if (data != null) {
            if (Material.getMaterial(iblock) == null) {
                target.sendMessage(ChatColor.RED + "Invalid item ID!");
                return false;
            }
            int data2;
            try {
                data2 = Integer.parseInt(data);
            } catch (Exception e) {
                target.sendMessage(ChatColor.RED + "The metadata was invalid!");
                return false;
            }
            if (data2 < 0) {
                target.sendMessage(ChatColor.RED + "The metadata was invalid!");
                return false;
            }
            toInv = new ItemStack(Material.getMaterial(iblock).getId(), amount, (short) data2);
        } else {
            toInv = new ItemStack(Material.getMaterial(iblock).getId(), amount);
        }
        target.sendMessage(ChatColor.BLUE
                + "Giving "
                + ChatColor.GRAY
                + amount
                + ChatColor.BLUE
                + " of "
                + ChatColor.GRAY
                + Material.getMaterial(iblock).toString()
                .toLowerCase().replace("_", " ")
                + ChatColor.BLUE + " to " + ChatColor.GRAY
                + target.getName() + ChatColor.BLUE + ".");
        HashMap<Integer, ItemStack> left = target.getInventory().addItem(toInv);
        if (!left.isEmpty() && plugin.dropExtras) {
            for (ItemStack item : left.values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), item);
            }
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label,
                             String[] args) {
        if (cmd.getName().equalsIgnoreCase("give")) {
            if (!plugin.isAuthorized(cs, "rcmds.give")) {
                RUtils.dispNoPerms(cs);
                return true;
            }
            if (args.length < 2) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (args.length < 3) {
                Player target = plugin.getServer().getPlayer(args[0].trim());
                if (target == null) {
                    cs.sendMessage(ChatColor.RED + "That player is not online!");
                    return true;
                }
                if (plugin.isVanished(target)) {
                    cs.sendMessage(ChatColor.RED + "That player does not exist!");
                    return true;
                }
                String called = args[1];
                String data = null;
                if (called.contains(":")) {
                    String[] calleds = called.split(":");
                    called = calleds[0].trim();
                    data = calleds[1].trim();
                }
                Integer iblock;
                try {
                    iblock = Integer.parseInt(called);
                } catch (Exception e) {
                    try {
                        iblock = Material.getMaterial(called.trim().replace(" ", "_").toUpperCase()).getId();
                    } catch (Exception e2) {
                        cs.sendMessage(ChatColor.RED + "That block does not exist!");
                        return true;
                    }
                }
                if (iblock != 0) {
                    if (plugin.blockedItems.contains(iblock.toString()) && !plugin.isAuthorized(cs, "rcmds.allowed.item") && !plugin.isAuthorized(cs, "rcmds.allowed.item." + iblock.toString())) {
                        cs.sendMessage(ChatColor.RED + "You are not allowed to spawn that item!");
                        plugin.log.warning("[RoyalCommands] " + cs.getName() + " was denied access to the command!");
                        return true;
                    }
                    ItemStack toInv;
                    if (data != null) {
                        if (Material.getMaterial(iblock) == null) {
                            cs.sendMessage(ChatColor.RED + "Invalid item ID!");
                            return true;
                        }
                        int data2;
                        try {
                            data2 = Integer.parseInt(data);
                        } catch (Exception e) {
                            cs.sendMessage(ChatColor.RED + "The metadata was invalid!");
                            return true;
                        }
                        if (data2 < 0) {
                            cs.sendMessage(ChatColor.RED + "The metadata was invalid!");
                            return true;
                        }
                        toInv = new ItemStack(Material.getMaterial(iblock).getId(), plugin.defaultStack, (short) data2);
                    } else {
                        toInv = new ItemStack(Material.getMaterial(iblock).getId(), plugin.defaultStack);
                    }
                    HashMap<Integer, ItemStack> left = target.getInventory().addItem(toInv);
                    if (!left.isEmpty() && plugin.dropExtras) {
                        for (ItemStack item : left.values()) {
                            target.getWorld().dropItemNaturally(target.getLocation(), item);
                        }
                    }
                    cs.sendMessage(ChatColor.BLUE
                            + "Giving "
                            + ChatColor.GRAY
                            + plugin.defaultStack
                            + ChatColor.BLUE
                            + " of "
                            + ChatColor.GRAY
                            + Material.getMaterial(iblock).toString()
                            .toLowerCase().replace("_", " ")
                            + ChatColor.BLUE + " to " + ChatColor.GRAY
                            + target.getName() + ChatColor.BLUE + ".");
                    target.sendMessage(ChatColor.BLUE
                            + "You have been given "
                            + ChatColor.GRAY
                            + plugin.defaultStack
                            + ChatColor.BLUE
                            + " of "
                            + ChatColor.GRAY
                            + Material.getMaterial(iblock).toString()
                            .toLowerCase().replace("_", " ")
                            + ChatColor.BLUE + ".");
                    return true;
                } else {
                    cs.sendMessage(ChatColor.RED + "You cannot spawn air!");
                    return true;
                }
            } else if (args.length == 3) {
                Player target = plugin.getServer().getPlayer(args[0]);
                if (target == null) {
                    cs.sendMessage(ChatColor.RED + "That player is not online!");
                    return true;
                }
                String called = args[1];
                Integer amount;
                String data = null;
                if (called.contains(":")) {
                    String[] calleds = called.split(":");
                    called = calleds[0].trim();
                    data = calleds[1].trim();
                }
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (Exception e) {
                    cs.sendMessage(ChatColor.RED
                            + "The amount was not a number!");
                    return true;
                }
                if (amount < 1) {
                    amount = 1;
                }
                Integer iblock;
                try {
                    iblock = Integer.parseInt(called);
                } catch (Exception e) {
                    try {
                        iblock = Material.getMaterial(called.trim().replace(" ", "_").toUpperCase()).getId();
                    } catch (Exception e2) {
                        cs.sendMessage(ChatColor.RED + "That block does not exist!");
                        return true;
                    }
                }
                if (iblock == 0) {
                    cs.sendMessage(ChatColor.RED + "You cannot spawn air!");
                    return true;
                }
                if (plugin.blockedItems.contains(iblock.toString()) && !plugin.isAuthorized(cs, "rcmds.allowed.item") && !plugin.isAuthorized(cs, "rcmds.allowed.item." + iblock.toString())) {
                    cs.sendMessage(ChatColor.RED + "You are not allowed to spawn that item!");
                    plugin.log.warning("[RoyalCommands] " + cs.getName() + " was denied access to the command!");
                    return true;
                }
                ItemStack toInv;
                if (data != null) {
                    if (Material.getMaterial(iblock) == null) {
                        cs.sendMessage(ChatColor.RED + "Invalid item ID!");
                        return true;
                    }
                    int data2;
                    try {
                        data2 = Integer.parseInt(data);
                    } catch (Exception e) {
                        cs.sendMessage(ChatColor.RED + "The metadata was invalid!");
                        return true;
                    }
                    if (data2 < 0) {
                        cs.sendMessage(ChatColor.RED + "The metadata was invalid!");
                        return true;
                    } else {
                        toInv = new ItemStack(Material.getMaterial(iblock).getId(), amount, (short) data2);
                    }
                } else {
                    toInv = new ItemStack(Material.getMaterial(iblock).getId(), amount);
                }
                HashMap<Integer, ItemStack> left = target.getInventory().addItem(toInv);
                if (!left.isEmpty() && plugin.dropExtras) {
                    for (ItemStack item : left.values()) {
                        target.getWorld().dropItemNaturally(target.getLocation(), item);
                    }
                }
                cs.sendMessage(ChatColor.BLUE
                        + "Giving "
                        + ChatColor.GRAY
                        + amount
                        + ChatColor.BLUE
                        + " of "
                        + ChatColor.GRAY
                        + Material.getMaterial(iblock).toString().toLowerCase()
                        .replace("_", " ") + ChatColor.BLUE + " to "
                        + ChatColor.GRAY + target.getName() + ChatColor.BLUE
                        + ".");
                target.sendMessage(ChatColor.BLUE
                        + "You have been given "
                        + ChatColor.GRAY
                        + amount
                        + ChatColor.BLUE
                        + " of "
                        + ChatColor.GRAY
                        + Material.getMaterial(iblock).toString().toLowerCase()
                        .replace("_", " ") + ChatColor.BLUE + ".");
                return true;
            }
        }
        return false;
    }
}
