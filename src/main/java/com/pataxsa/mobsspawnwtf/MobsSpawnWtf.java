package com.pataxsa.mobsspawnwtf;

import com.pataxsa.mobsspawnwtf.commands.CommandMobsSpawnWtf;
import com.pataxsa.mobsspawnwtf.events.OnPlayerChatEvent;
import com.pataxsa.mobsspawnwtf.gui.ModGui;
import com.pataxsa.mobsspawnwtf.gui.ModPlayerGui;
import com.pataxsa.mobsspawnwtf.gui.ModServerGui;
import com.pataxsa.mobsspawnwtf.timers.TimerTask;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
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
        objective = board.registerNewObjective("Players", "dummy");
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
            customplayer.setgamemode(players.getGameMode());
            allPlayers.add(customplayer);
            ItemStack item = new ItemStack(Material.COOKED_BEEF, 64);
            ItemMeta metadata = item.getItemMeta();
            metadata.setDisplayName("§eEat to survive");
            item.setItemMeta(metadata);
            players.getInventory().addItem(item);
        }
        cancelEvent = false;
        deaths = 0;
        name = null;
        uuid = null;
        taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                ArrayList<UUID> uuids = new ArrayList<UUID>();
                allPlayers.stream().filter(s -> s.getDeaths() < getConfig().getInt("Lifes") && !s.getleaved() && s.getgamemode() != GameMode.CREATIVE).forEach(s -> {
                    if(!s.getinrespawn()){
                        if(s.getgamemode() != GameMode.SPECTATOR){
                            uuids.add(s.getUUID());
                        }
                    }else{
                        uuids.add(s.getUUID());
                    }
                });
                if(uuids.size() == 0){
                    Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §cAll the players are in creative or spectator !");
                }else{
                    UUID playeruuid = uuids.get(new Random().nextInt(uuids.size()));
                    int randomentity = new Random().nextInt(mobs.size());
                    EntityType toSpawn = mobs.get(randomentity);
                    allPlayers.stream().filter(s -> s.getUUID().equals(playeruuid)).forEach(s -> {
                        if (Bukkit.getServer().getPlayer(s.getname()) != null) {
                            Player player = Bukkit.getServer().getPlayer(s.getname());
                            int maxminmobs = rdm.nextInt(maxmobs - minmobs + 1) + minmobs;
                            IntStream.range(0, maxminmobs).forEach(i -> {
                                player.getWorld().spawnEntity(player.getLocation(), toSpawn);
                            });
                            minutes = rdm.nextInt(maxtime - mintime + 1) + mintime;
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1f, 1f);
                            Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + maxminmobs + "§d " + toSpawn.getName() + "§c has spawned on " + s.getname() + " !");
                        }
                    });
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
                        Score score = objective.getScore(Bukkit.getServer().getOfflinePlayer("§7Players:"));
                        player.getInventory().clear();
                        player.setGameMode(GameMode.SPECTATOR);
                        world.strikeLightning(player.getLocation());
                        player.sendTitle("§cYou are dead !", "§cBy §b" + ev.getCause().name());
                        player.setHealth(player.getMaxHealth());
                        Bukkit.getServer().broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + "§c is dead by §b" + ev.getCause().name() + " §c! (Eleminated)");
                        if(allPlayers.stream().filter(s -> s.getDeaths() < getConfig().getInt("Lifes")).count() == 0){
                            Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + "§a has win the game !");
                            Bukkit.getServer().getScheduler().cancelTask(taskID);
                            cancelEvent = true;
                            for (final Player players : Bukkit.getOnlinePlayers()) {
                                players.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                            }
                        }else{
                            if(allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes")).count() == 1){
                                allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes")).forEach(s -> {
                                    Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + s.getname() + "§a has win the game !");
                                    Player player2 =  Bukkit.getServer().getPlayer(s.getname());
                                    player2.sendTitle("§aYou win the game !", "§2GG you win !");
                                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                                    cancelEvent = true;
                                    for (final Player players : Bukkit.getOnlinePlayers()) {
                                        players.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                                    }
                                });
                            }else{
                                score.setScore(Integer.parseInt(Long.toString(allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes") && !s.getleaved()).count())));
                            }
                        }
                    }else{
                        ev.setCancelled(true);
                        Stream<CustomPlayer> result2 = allPlayers.stream().filter(s -> s.getUUID().equals(ev.getEntity().getUniqueId()));
                        result2.forEach(s -> {
                            s.setinrespawn(true);
                        });
                        Player player = Bukkit.getServer().getPlayer(ev.getEntity().getName());
                        World world = player.getWorld();
                        player.getInventory().clear();
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendTitle("§cYou are dead !", "§cBy §b" + ev.getCause().name());
                        player.setHealth(player.getMaxHealth());
                        world.strikeLightning(player.getLocation());
                        int lifes = this.getConfig().getInt("Lifes") - deaths;
                        Bukkit.getServer().broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + "§c is dead by §b" + ev.getCause().name() + " §c! (" + lifes + " life(s))");
                        TimerTask task = new TimerTask(player, allPlayers);
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
            Score score = objective.getScore(Bukkit.getServer().getOfflinePlayer("§7Players:"));
            if(allPlayers.stream().filter(s -> s.getUUID().equals(player.getUniqueId())).count() == 0){
                customplayer = new CustomPlayer(player.getName(), player.getUniqueId());
                customplayer.setCustomplayer(customplayer);
                customplayer.setgamemode(player.getGameMode());
                allPlayers.add(customplayer);
            }else{
                allPlayers.stream().filter(s -> s.getUUID().equals(player.getUniqueId())).forEach(s -> {
                    s.setleaved(false);
                });
            }
            score.setScore(Integer.parseInt(Long.toString(allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes") && !s.getleaved()).count())));
            player.setScoreboard(board);
            ev.setJoinMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + " §ahas joined the game ! §7(§b" + allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes") && !s.getleaved()).count() + "§7/§b" + Bukkit.getServer().getMaxPlayers() + "§7)");
        }
    }

    @EventHandler
    public void onleave(PlayerQuitEvent ev) {
        if(!cancelEvent){
            Player player = ev.getPlayer();
            Score score = objective.getScore(Bukkit.getServer().getOfflinePlayer("§7Players:"));
            allPlayers.stream().filter(s -> s.getUUID().equals(player.getUniqueId())).forEach(s -> {
                s.setleaved(true);
            });
            score.setScore(Integer.parseInt(Long.toString(allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes") && !s.getleaved()).count())));
            if(allPlayers.stream().filter(s -> !s.getleaved()).count() == 1){
                allPlayers.stream().filter(s -> !s.getleaved()).forEach(s -> {
                    Player player2 =  Bukkit.getServer().getPlayer(s.getname());
                    player2.sendTitle("§aYou win the game !", "§2GG you win !");
                    Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §e" + s.getname() + "§a has win the game !");
                    Bukkit.getServer().getScheduler().cancelTask(taskID);
                    cancelEvent = true;
                    for (final Player players : Bukkit.getOnlinePlayers()) {
                        players.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    }
                });
            }else if(allPlayers.stream().filter(s -> !s.getleaved()).count() == 0){
                Bukkit.broadcastMessage("§8[§eMobsSpawnWtf§8] §cThere is not players in the game, then i have stopped the plugin !");
                Bukkit.getServer().getScheduler().cancelTask(taskID);
                cancelEvent = true;
            }
            ev.setQuitMessage("§8[§eMobsSpawnWtf§8] §e" + player.getName() + " §chas leaved the game ! §7(§b" + allPlayers.stream().filter(s -> s.getDeaths() < this.getConfig().getInt("Lifes") && !s.getleaved()).count() + "§7/§b" + Bukkit.getServer().getMaxPlayers() + "§7)");
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
