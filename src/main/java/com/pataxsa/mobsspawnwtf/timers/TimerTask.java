package com.pataxsa.mobsspawnwtf.timers;

import com.pataxsa.mobsspawnwtf.CustomPlayer;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.stream.Stream;

public class TimerTask extends BukkitRunnable {
    private int timer = 10;
    private Player player;
    private ArrayList<CustomPlayer> customplayers;

    public TimerTask(Player player, ArrayList<CustomPlayer> customplayers){
        this.player = player;
        this.customplayers = customplayers;
    }

    @Override
    public void run(){

        player.sendTitle("§cYou respawn in " + timer + "s", "");

        if(timer == 0){
            player.sendTitle("§aYou respawn now !", "");
            player.setHealth(player.getMaxHealth());
            player.setGameMode(GameMode.SURVIVAL);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
            Stream<CustomPlayer> result = customplayers.stream().filter(s -> s.getUUID().equals(player.getUniqueId()));
            result.forEach(s -> {
                s.setinrespawn(false);
            });
            cancel();
        }

        timer--;
    }
}
