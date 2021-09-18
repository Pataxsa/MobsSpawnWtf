package com.pataxsa.mobsspawnwtf.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ModServerGui implements Listener {

    public final Inventory inv;

    public ModServerGui() {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 27, "Server Menu");
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        inv.addItem(createGuiItem(Material.REDSTONE, "§cStop", "Stop the server !"));
        inv.addItem(createGuiItem(Material.WATER_BUCKET, "§bWeather", "Change the weather of the world !"));
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
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().getName().equals(inv.getName())) return;

        if (e.getCurrentItem().getType() != Material.AIR) {
            e.setCancelled(true);

            final ItemStack clickedItem = e.getCurrentItem();
            final Player p = (Player) e.getWhoClicked();

            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1f);

            if(clickedItem.getItemMeta().getDisplayName().equals("§cStop")){
                p.sendMessage("§cThe server will soon be closed !");
                Bukkit.getServer().shutdown();
            }
            if(clickedItem.getItemMeta().getDisplayName().equals("§bWeather")){
                if(!Bukkit.getServer().getWorld(p.getWorld().getName()).isThundering()){
                    Bukkit.getServer().getWorld(p.getWorld().getName()).setThunderDuration(100);
                    p.sendMessage("§aIs now thundering !");
                }else if(Bukkit.getServer().getWorld(p.getWorld().getName()).isThundering()){
                    Bukkit.getServer().getWorld(p.getWorld().getName()).setStorm(true);
                    p.sendMessage("§aIs now storming !");
                }else if(Bukkit.getServer().getWorld(p.getWorld().getName()).hasStorm()){
                    Bukkit.getServer().getWorld(p.getWorld().getName()).setWeatherDuration(100);
                    p.sendMessage("§aIs now raining !");
                }else if(Bukkit.getServer().getWorld(p.getWorld().getName()).getWeatherDuration() > 1){
                    Bukkit.getServer().getWorld(p.getWorld().getName()).setWeatherDuration(0);
                    p.sendMessage("§aIs now clear !");
                }
            }
            inv.clear();
            initializeItems();
        }
    }
}
