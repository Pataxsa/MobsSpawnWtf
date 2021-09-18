package com.pataxsa.mobsspawnwtf.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class ModGui implements Listener {

    public final Inventory inv;
    public ModPlayerGui modplayerinv;

    public ModGui() {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 45, "Players Menu");
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        for (Player players : Bukkit.getServer().getOnlinePlayers()){
            ItemStack playerskull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
            SkullMeta meta = (SkullMeta) playerskull.getItemMeta();

            meta.setOwner(players.getName());
            meta.setDisplayName("§e" + players.getName());

            playerskull.setItemMeta(meta);
            inv.addItem(playerskull);
        }
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

            String playername = clickedItem.getItemMeta().getDisplayName().replace("§e", "");

            if(Bukkit.getServer().getPlayer(playername) != null){
                modplayerinv.playerinv = Bukkit.getServer().getPlayer(playername);
                modplayerinv.openInventory(p);
            }else{
                p.sendMessage("§cThis player doesn't exist !");
            }
        }
    }
}
