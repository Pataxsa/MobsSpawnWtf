package com.pataxsa.mobsspawnwtf.commands;

import com.pataxsa.mobsspawnwtf.MobsSpawnWtf;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;

public class CommandMobsSpawnWtf implements CommandExecutor {
    private final MobsSpawnWtf main;
    private int errors = 0;

    public CommandMobsSpawnWtf(MobsSpawnWtf mobsspawnwtf){
        this.main = mobsspawnwtf;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args){
        if(args.length == 1){
            if(args[0].equals("stop")){
                for (final Player players : Bukkit.getOnlinePlayers()) {
                    players.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
                Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §cThe plugin has just been stopped !");
                main.stopMobs();
            }else{
                sender.sendMessage("§8[§eMobsSpawnWtf§8] §cUsage: /mobsspawnwtf start [minmobs] [maxmobs] [mintime (minutes)] [maxtime (minutes)]\n§cUsage: /mobsspawnwtf stop");
            }
        }else if(args.length == 5){
            if(args[0].equals("start")){
                if(Bukkit.getOnlinePlayers().size() != 0){
                    try{
                        Integer.parseInt(args[1]);
                        Integer.parseInt(args[2]);
                        Integer.parseInt(args[3]);
                        Integer.parseInt(args[4]);
                    } catch (final NumberFormatException nfe) {
                        sender.sendMessage("§8[§eMobsSpawnWtf§8] §cError: Enter a valid number !");
                        return false;
                    }
                    if (Integer.parseInt(args[1]) >= 1 && Integer.parseInt(args[3]) >= 1) {
                        if(Integer.parseInt(args[2]) >= 2 && Integer.parseInt(args[4]) >= 2){
                            if(Integer.parseInt(args[1]) < Integer.parseInt(args[2])){
                                if(Integer.parseInt(args[3]) < Integer.parseInt(args[4])){
                                    if(main.getConfig().getConfigurationSection("MobsSpawnWtf.").getKeys(true).size() > 0){
                                        main.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                                        main.objective.setDisplayName("MobsSpawnWtf");
                                        Score score = main.objective.getScore(Bukkit.getServer().getOfflinePlayer("Players:"));
                                        score.setScore(Bukkit.getOnlinePlayers().size());
                                        for (final Player players : Bukkit.getOnlinePlayers()) {
                                            players.setScoreboard(main.board);
                                        }
                                        for (String path : main.getConfig().getConfigurationSection("MobsSpawnWtf").getKeys(false)) {
                                            String MobsToSpawn = main.getConfig().getString("MobsSpawnWtf." + path);
                                            try{
                                                EntityType.fromName(MobsToSpawn).isSpawnable();
                                            }catch (Exception e){
                                                errors = errors+1;
                                                sender.sendMessage("§8[§eMobsSpawnWtf§8] §cThe mob §b" + MobsToSpawn + "§c in line the §e" + path + "§c doesn't exist !");
                                            }
                                        }
                                        if(errors == 0){
                                            Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §aThe plugin has just been started with minmobs: §e" + Integer.parseInt(args[1]) + "§a maxmobs: §e" + Integer.parseInt(args[2]) + "§a mintime: §e" + Integer.parseInt(args[3]) + "§a maxtime: §e" + Integer.parseInt(args[4]) + "§a Mobs: §e" + main.getConfig().getConfigurationSection("MobsSpawnWtf").getKeys(true).size());
                                            main.stopMobs();
                                            main.startMobs(sender, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                                        }
                                    }else{
                                        sender.sendMessage("§8[§eMobsSpawnWtf§8] §cThere is §e0 §cmobs in the config file !");
                                    }
                                }else{
                                    sender.sendMessage("§8[§eMobsSpawnWtf§8] §cError: Your mintime §e" + Integer.parseInt(args[3]) + "§c number is taller or equal than maxtime §e" + Integer.parseInt(args[4]) + "§c number !");
                                }
                            }else{
                                sender.sendMessage("§8[§eMobsSpawnWtf§8] §cError: Your minmobs §e" + Integer.parseInt(args[1]) + "§c number is taller or equal than maxmobs §e" + Integer.parseInt(args[2]) + "§c number !");
                            }
                        }else{
                            sender.sendMessage("§8[§eMobsSpawnWtf§8] §cError: Your maxmobs number or your maxtime number is less than §e2 §c!");
                        }
                    }else{
                        sender.sendMessage("§8[§eMobsSpawnWtf§8] §cError: Your minmobs number or your mintime is less than §e1 §c!");
                    }
                }else{
                    sender.sendMessage("§8[§eMobsSpawnWtf§8] §cError: There is not players in game !");
                }
            }else{
                sender.sendMessage("§8[§eMobsSpawnWtf§8] §cUsage: /mobsspawnwtf start [minmobs] [maxmobs] [mintime (minutes)] [maxtime (minutes)]\n§cUsage: /mobsspawnwtf stop");
            }
        }else{
            sender.sendMessage("§8[§eMobsSpawnWtf§8] §cUsage: /mobsspawnwtf start [minmobs] [maxmobs] [mintime (minutes)] [maxtime (minutes)]\n§cUsage: /mobsspawnwtf stop");
        }
        return false;
    }
}
