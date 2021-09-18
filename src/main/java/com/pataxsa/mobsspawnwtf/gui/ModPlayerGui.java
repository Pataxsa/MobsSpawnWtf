package com.pataxsa.mobsspawnwtf.gui;

import com.pataxsa.mobsspawnwtf.MobsSpawnWtf;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ModPlayerGui implements Listener {


    public final Inventory inv;
    public Player playerinv;
    private final MobsSpawnWtf mobsspawnwtf;
    private Integer number = 0;
    private ScoreboardManager manager = Bukkit.getScoreboardManager();
    private Scoreboard board = manager.getNewScoreboard();
    private Objective objective = board.registerNewObjective("showhealth", "health");
    private Score score = objective.getScore("Kills: ");

    public ModPlayerGui(MobsSpawnWtf mobsspawnwtf) {
        this.mobsspawnwtf = mobsspawnwtf;
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 27, "Player Menu");
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        inv.addItem(createGuiItem(Material.SKULL_ITEM, "§cKill", "Kill " + playerinv.getName() + " !"));
        inv.addItem(createGuiItem(Material.REDSTONE_BLOCK, "§aOp", "Op or Deop " + playerinv.getName() + " !", "Op: " + (playerinv.isOp() ? "Yes" : "No")));
        inv.addItem(createGuiItem(Material.DIAMOND, "§eGamemode", "Change gamemode of " + playerinv.getName() + " !", "Gamemode: " + playerinv.getGameMode().name()));
        inv.addItem(createGuiItem(Material.APPLE, "§bVanish", "Change visibility of " + playerinv.getName() + " !", "Invisible: " + (invisible_list.contains(playerinv.getPlayer()) ? "Yes" : "No")));
        inv.addItem(createGuiItem(Material.GLOWSTONE_DUST, "§9Glow", "Change glow of " + playerinv.getName() + " !", "Glowing: " + (playerinv.isGlowing() ? "Yes" : "No")));
        inv.addItem(createGuiItem(Material.FIREBALL, "§4Ban", "Ban " + playerinv.getName() + " !"));
        inv.addItem(createGuiItem(Material.FIREWORK, "§6Kick", "Kick " + playerinv.getName() + " !"));
        inv.addItem(createGuiItem(Material.FEATHER, "§fFly", "Change fly permission of " + playerinv.getName() + " !", "Fly: " + (playerinv.getAllowFlight() ? "Yes": "No")));
        inv.addItem(createGuiItem(Material.MONSTER_EGG, "§dSpawnMob", "Spawn mob on " + playerinv.getName() + " !"));
        inv.addItem(createGuiItem(Material.TNT, "§2Explode", "Explode " + playerinv.getName() + " !", "Health: " + playerinv.getHealth() + "/" + playerinv.getMaxHealth()));
        inv.addItem(createGuiItem(Material.ENDER_PEARL, "§5Teleport", "Teleport to " + playerinv.getName() + " !"));
        inv.addItem(createGuiItem(Material.EXP_BOTTLE, "§1Invulnerable", "Change " + playerinv.getName() + " invulnerable !", "Invulnerable: " + (playerinv.isInvulnerable() ? "Yes" : "No")));
        inv.addItem(createGuiItem(Material.ANVIL, "§eDisguiseBlock", "Disguise " + playerinv.getName() + " !"));
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        inv.clear();
        initializeItems();
        ent.openInventory(inv);
    }

    ArrayList<Player> invisible_list = new ArrayList<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().getName().equals(inv.getName())) return;

        if (e.getCurrentItem().getType() != Material.AIR) {
            e.setCancelled(true);

            final Player p = (Player) e.getWhoClicked();
            final ItemStack clickedItem = e.getCurrentItem();

            if(Bukkit.getServer().getPlayer(playerinv.getName()) != null){
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);
                if(clickedItem.getItemMeta().getDisplayName().equals("§cKill")){
                    playerinv.setHealth(0);
                    p.sendMessage("§e" + playerinv.getName() + " §cjust been killed");
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§aOp")){
                    if(!playerinv.isOp()){
                        p.sendMessage("§e" + playerinv.getName() + " §ajust been op");
                        playerinv.setOp(true);
                    }else{
                        p.sendMessage("§e" + playerinv.getName() + " §cjust been deop");
                        playerinv.setOp(false);
                    }
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§eGamemode")){
                    if(playerinv.getGameMode().name().equals("CREATIVE")){
                        p.sendMessage("§e" + playerinv.getName() + " §ajust got survival");
                        playerinv.setGameMode(GameMode.SURVIVAL);
                    }else if(playerinv.getGameMode().name().equals("SURVIVAL")){
                        p.sendMessage("§e" + playerinv.getName() + " §ajust got adventure");
                        playerinv.setGameMode(GameMode.ADVENTURE);
                    }else if(playerinv.getGameMode().name().equals("ADVENTURE")){
                        p.sendMessage("§e" + playerinv.getName() + " §ajust got spectator");
                        playerinv.setGameMode(GameMode.SPECTATOR);
                    }else if(playerinv.getGameMode().name().equals("SPECTATOR")){
                        p.sendMessage("§e" + playerinv.getName() + " §ajust got creative");
                        playerinv.setGameMode(GameMode.CREATIVE);
                    }
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§bVanish")){
                    if(invisible_list.contains(playerinv.getPlayer())){
                        invisible_list.remove(playerinv.getPlayer());
                        for ( Player play: Bukkit.getServer().getOnlinePlayers() ) {
                            play.showPlayer(mobsspawnwtf, playerinv.getPlayer());
                        };
                        p.getPlayer().sendMessage("§e" + playerinv.getPlayer().getName() + " §cis no longer in vanish !");
                    }else{
                        invisible_list.add(playerinv.getPlayer());
                        for ( Player play: Bukkit.getServer().getOnlinePlayers() ) {
                            play.hidePlayer(mobsspawnwtf, playerinv.getPlayer());
                        };
                        p.getPlayer().sendMessage("§e" + playerinv.getPlayer().getName() + " §ais now in vanish !");
                    }
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§9Glow")){
                    if(!playerinv.isGlowing()){
                        playerinv.setGlowing(true);
                        p.getPlayer().sendMessage("§e" + playerinv.getPlayer().getName() + " §ais now glowing !");
                    }else{
                        playerinv.setGlowing(false);
                        p.getPlayer().sendMessage("§e" + playerinv.getPlayer().getName() + " §cis no longer glowing !");
                    }
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§4Ban")){
                    Bukkit.getServer().banIP(playerinv.getAddress().getHostName());
                    p.sendMessage("§e" + playerinv.getPlayer().getName() + " §awas banned !");
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§6Kick")){
                    playerinv.kickPlayer("You have been kicked by moderator !");
                    p.sendMessage("§e" + playerinv.getPlayer().getName() + " §awas kicked !");
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§fFly")){
                    if(!playerinv.getAllowFlight()){
                        playerinv.setAllowFlight(true);
                        p.sendMessage("§e" + playerinv.getPlayer().getName() + " §acan now fly !");
                    }else{
                        playerinv.setAllowFlight(false);
                        p.sendMessage("§e" + playerinv.getPlayer().getName() + " §ccan no longer fly !");
                    }
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§dSpawnMob")){
                    World w= Bukkit.getServer().getWorld(playerinv.getWorld().getName());
                    w.spawnEntity(playerinv.getLocation().add(playerinv.getLocation().getDirection().multiply(-2)), EntityType.valueOf("CREEPER"));
                    p.sendMessage("§aYou spawned 1 creepers on §e" + playerinv.getName() + " §a!");
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§2Explode")){
                    World w= Bukkit.getServer().getWorld(playerinv.getWorld().getName());
                    w.createExplosion(playerinv.getLocation(), 25);
                    p.sendMessage("§e" + playerinv.getName() + " §ahas just exploded !");
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§5Teleport")){
                    p.teleport(playerinv.getLocation());
                    p.sendMessage("§aYou have been teleported to §e" + playerinv.getName() + "§a !");
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§eDisguiseBlock")){
                    World w= Bukkit.getServer().getWorld(playerinv.getWorld().getName());
                    w.spawnFallingBlock(playerinv.getLocation(), new MaterialData(18));
                    p.getPlayer().sendMessage("§e" + playerinv.getPlayer().getName() + " §ais now in block !");
                }
                if(clickedItem.getItemMeta().getDisplayName().equals("§1Invulnerable")){
                    if(!playerinv.isInvulnerable()){
                        playerinv.setInvulnerable(true);
                        p.getPlayer().sendMessage("§e" + playerinv.getPlayer().getName() + " §ais now invulnerable !");
                    }else{
                        playerinv.setInvulnerable(false);
                        p.getPlayer().sendMessage("§e" + playerinv.getPlayer().getName() + " §cis no longer invulnerable !");
                    }
                }
                inv.clear();
                initializeItems();
            }else{
                p.sendMessage("§cThis player doesn't exist !");
            }
        }
    }
}
