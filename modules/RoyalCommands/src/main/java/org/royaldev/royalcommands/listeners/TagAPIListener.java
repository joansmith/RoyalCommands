package org.royaldev.royalcommands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
import org.royaldev.royalcommands.Config;
import org.royaldev.royalcommands.configuration.PConfManager;

public class TagAPIListener implements Listener {

    @EventHandler
    public void onTag(AsyncPlayerReceiveNameTagEvent e) {
        if (e.isTagModified()) return;
        if (e.getNamedPlayer() == null) return;
        final PConfManager pcm = PConfManager.getPConfManager(e.getNamedPlayer());
        String dispname = pcm.getString("dispname");
        if (dispname == null) return;
        if (dispname.equalsIgnoreCase(e.getNamedPlayer().getName())) {
            e.setTag(dispname);
            return;
        }
        if (!Config.nickPrefix.equals("") && dispname.startsWith(Config.nickPrefix))
            dispname = dispname.substring(Config.nickPrefix.length());
        e.setTag(dispname);
    }

}
