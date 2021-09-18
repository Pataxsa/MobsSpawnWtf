package com.pataxsa.mobsspawnwtf.timers;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimerTask extends BukkitRunnable {
    private int timer = 10;
    private Player player;

    public TimerTask(Player player){
        this.player = player;
    }

    @Override
    public void run(){

        player.sendTitle("§cYou respawn in " + timer + "s", "");

        if(timer == 0){
            player.sendTitle("§aYou respawn now !", "");
            player.setHealth(player.getMaxHealth());
            player.setGameMode(GameMode.SURVIVAL);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
            cancel();
        }

        timer--;
    }
}
