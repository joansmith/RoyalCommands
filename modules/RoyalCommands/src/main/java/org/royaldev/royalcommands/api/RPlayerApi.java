/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.royaldev.royalcommands.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.royaldev.royalcommands.AFKUtils;
import org.royaldev.royalcommands.configuration.PlayerConfiguration;
import org.royaldev.royalcommands.configuration.PlayerConfigurationManager;

public class RPlayerApi {

    /**
     * Gets the player configuration manager of the OfflinePlayer.
     *
     * @param p OfflinePlayer to get config for
     * @return A PConfManager - never null
     */
    public PlayerConfiguration getConfiguration(OfflinePlayer p) {
        return PlayerConfigurationManager.getConfiguration(p);
    }

    /**
     * Returns the display name of a player. This will return a nickname set by /nick,
     * and if one is not set, it will return the player's default name.
     *
     * @param p Player to get name for
     * @return Name - never null
     */
    public String getDisplayName(Player p) {
        PlayerConfiguration pcm = PlayerConfigurationManager.getConfiguration(p);
        String name = pcm.getString("dispname");
        if (name == null) name = p.getName();
        return name;
    }

    /**
     * Checks the AFK (away from keyboard) status of a player.
     *
     * @param p Player to check for
     * @return true if away, false if otherwise
     */
    public boolean isAfk(Player p) {
        return AFKUtils.isAfk(p);
    }

}
