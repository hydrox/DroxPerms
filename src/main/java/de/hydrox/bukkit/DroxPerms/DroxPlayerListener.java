package de.hydrox.bukkit.DroxPerms;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Player listener: takes care of registering and unregistering players on join
 */
public class DroxPlayerListener extends PlayerListener {

    private DroxPerms plugin;

    public DroxPlayerListener(DroxPerms plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.registerPlayer(event.getPlayer());
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.unregisterPlayer(event.getPlayer());
    }

    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        plugin.unregisterPlayer(event.getPlayer());
    }

}
