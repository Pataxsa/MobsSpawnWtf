package com.pataxsa.mobsspawnwtf;

import com.google.common.base.Predicates;
import com.pataxsa.mobsspawnwtf.commands.CommandMobsSpawnWtf;
import com.pataxsa.mobsspawnwtf.events.OnPlayerChatEvent;
import com.pataxsa.mobsspawnwtf.gui.ModGui;
import com.pataxsa.mobsspawnwtf.gui.ModPlayerGui;
import com.pataxsa.mobsspawnwtf.gui.ModServerGui;
import com.pataxsa.mobsspawnwtf.timers.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MobsSpawnWtf extends JavaPlugin implements Listener {

    private static ModGui modgui;
    private static ModPlayerGui modplayergui;
    private static ModServerGui modservergui;
    public Scoreboard board;
    public Objective objective;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Bukkit.getConsoleSender().sendMessage("§8[§eMobsSpawnWtf§8] §aThe plugin has just launched");
        modgui = new ModGui();
        modplayergui = new ModPlayerGui(this);
        modservergui = new ModServerGui();
        modgui.modplayerinv = modplayergui;
        board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        objective = board.registerNewObjective("Belowname", "dummy");
        getCommand("mobsspawnwtf").setExecutor(new CommandMobsSpawnWtf(this));
        getServer().getPluginManager().registerEvents(modgui, this);
        getServer().getPluginManager().registerEvents(modplayergui, this);
        getServer().getPluginManager().registerEvents(modservergui, this);
        getServer().getPluginManager().registerEvents(new OnPlayerChatEvent(this, modgui, modservergui), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§8[§eMobsSpawnWtf§8] §cThe plugin just stopped !");
    }

    int taskID;
    boolean cancelEvent = true;
    CustomPlayer customplayer;
    ArrayList<CustomPlayer> allPlayers = new ArrayList<CustomPlayer>();
    ArrayList<EntityType> mobs = new ArrayList<EntityType>();
    Random rdm = new Random();
    Integer minutes = 1;
    int deaths;
    UUID uuid;
    String name;

    public void startMobs(final CommandSender sender, final Integer minmobs, final Integer maxmobs, final Integer mintime, final Integer maxtime) {
        allPlayers.clear();
        for (String path : this.getConfig().getConfigurationSection("MobsSpawnWtf").getKeys(false)) {
            String MobsToSpawn = this.getConfig().getString("MobsSpawnWtf." + path);
            if(!mobs.contains(EntityType.fromName(MobsToSpawn))){
                mobs.add(EntityType.fromName(MobsToSpawn));
            }
        }
        for (final Player players : Bukkit.getOnlinePlayers()) {
            customplayer = new CustomPlayer(players.getName(), players.getUniqueId());
            customplayer.setCustomplayer(customplayer);
            allPlayers.add(customplayer);
            customplayer.setgamemode(players.getGameMode());
        }
        cancelEvent = false;
        deaths = 0;
        name = null;
        uuid = null;
        taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                int randomentity = new Random().nextInt(mobs.size());
                EntityType toSpawn = mobs.get(randomentity);
                CustomPlayer picked = allPlayers.get(new Random().nextInt(allPlayers.size()));
                if (Bukkit.getServer().getPlayer(picked.getname()) != null) {
                    Player player = Bukkit.getServer().getPlayer(picked.getname());
                    int maxminmobs = rdm.nextInt(maxmobs - minmobs + 1) + minmobs;
                    IntStream.range(0, maxminmobs).forEach(i -> {
                        player.getWorld().spawnEntity(player.getLocation(), toSpawn);
                    });
                    minutes = rdm.nextInt(maxtime - mintime + 1) + mintime;
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1f, 1f);
                    Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + maxminmobs + "§d " + toSpawn.getName() + "§c has spawned on " + picked.getname() + " !");
                }
            }
        }, 60L, minutes*1200);
    }

    public void stopMobs() {
        Bukkit.getServer().getScheduler().cancelTask(taskID);
        cancelEvent = true;
    }

    @EventHandler
    public void damage(EntityDamageEvent ev) {
        if(!cancelEvent){
            if(Bukkit.getServer().getPlayer(ev.getEntity().getName()) != null){
                if (((Bukkit.getServer().getPlayer(ev.getEntity().getName()).getHealth() - ev.getFinalDamage()) <= 0) && ev.getEntity() instanceof Player)
                {
                    Stream<CustomPlayer> result = allPlayers.stream().filter(s -> s.getUUID().equals(ev.getEntity().getUniqueId()));
                    result.forEach(s -> {
                        s.setDeaths(s.getDeaths() + 1);
                        deaths = s.getDeaths();
                        uuid = s.getUUID();
                        name = s.getname();
                        customplayer = s.getCustomplayer();
                    });
                    if(deaths >= this.getConfig().getInt("Lifes")){
                        ev.setCancelled(true);
                        Player player = Bukkit.getServer().getPlayer(ev.getEntity().getName());
                        World world = player.getWorld();
                        Score score = objective.getScore(Bukkit.getServer().getOfflinePlayer("Players:"));
                        player.setGameMode(GameMode.SPECTATOR);
                        score.setScore(Integer.parseInt(Long.toString(allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes")).count())));
                        world.strikeLightning(player.getLocation());
                        player.sendTitle("§cYou are dead !", "§cBy §b" + ev.getCause().name());
                        player.setHealth(player.getMaxHealth());
                        Bukkit.getServer().broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + "§c is dead by §b" + ev.getCause().name() + " §c! (Eleminated)");
                        if(allPlayers.stream().filter(s -> s.getDeaths() < getConfig().getInt("Lifes")).count() == 0){
                            Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §cThere is not alive players in game, then i have stopped the plugin !");
                            Bukkit.getServer().getScheduler().cancelTask(taskID);
                            cancelEvent = true;
                            for (final Player players : Bukkit.getOnlinePlayers()) {
                                players.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                            }
                        }
                    }else{
                        ev.setCancelled(true);
                        Player player = Bukkit.getServer().getPlayer(ev.getEntity().getName());
                        World world = player.getWorld();
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendTitle("§cYou are dead !", "§cBy §b" + ev.getCause().name());
                        player.setHealth(player.getMaxHealth());
                        world.strikeLightning(player.getLocation());
                        int lifes = this.getConfig().getInt("Lifes") - deaths;
                        Bukkit.getServer().broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + "§c is dead by §b" + ev.getCause().name() + " §c! (" + lifes + " life(s))");
                        TimerTask task = new TimerTask(player);
                        task.runTaskTimer(this, 0, 20);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onjoin(PlayerJoinEvent ev) {
        if(!cancelEvent){
            Player player = ev.getPlayer();
            player.setScoreboard(board);
            allPlayers.stream().filter(s -> s.getUUID() == player.getUniqueId()).forEach(s -> {
                customplayer = s.getCustomplayer();
            });
            if(allPlayers.contains(customplayer)){
                allPlayers.stream().filter(s -> s.getUUID() == player.getUniqueId()).forEach(s -> {
                    s.setleaved(false);
                });
            }else{
                customplayer = new CustomPlayer(player.getName(), player.getUniqueId());
                customplayer.setCustomplayer(customplayer);
                allPlayers.add(customplayer);
                customplayer.setgamemode(player.getGameMode());
                Score score = objective.getScore(Bukkit.getServer().getOfflinePlayer("Players:"));
                score.setScore(Integer.parseInt(Long.toString(allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes")).count())));
            }
            Bukkit.getServer().broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + " §ahave joined the game !");
        }
    }

    @EventHandler
    public void onleave(PlayerQuitEvent ev) {
        if(!cancelEvent){
            Player player = ev.getPlayer();
            Bukkit.getServer().broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + " §chave leaved the game !");
            allPlayers.stream().filter(s -> s.getUUID() == player.getUniqueId()).forEach(s -> {
                s.setleaved(true);
            });
            if(allPlayers.stream().filter(s -> !s.getleaved()).count() == 0){
                Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §cThere is not alive players in game, then i have stopped the plugin !");
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                cancelEvent = true;
                for (final Player players : Bukkit.getOnlinePlayers()) {
                    players.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                }
            }
        }
    }

    @EventHandler
    public void onchangegamemode(PlayerGameModeChangeEvent ev){
        if(!cancelEvent){
            GameMode newgamemode = ev.getNewGameMode();
            Player player = ev.getPlayer();
            Stream<CustomPlayer> myplayer = allPlayers.stream().filter(s -> s.getUUID() == player.getUniqueId());
            myplayer.forEach(s -> {
                s.setgamemode(newgamemode);
            });
        }
    }
}
