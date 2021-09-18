package com.pataxsa.mobsspawnwtf;

import org.bukkit.GameMode;

import java.util.UUID;

public class CustomPlayer {
    private String name;
    private UUID UUID;
    private int Deaths = 0;
    private CustomPlayer customplayer;
    private GameMode gamemode = GameMode.SURVIVAL;
    private boolean leaved = false;
    private boolean inrespawn = false;

    public CustomPlayer(String name, UUID UUID) {
        this.name = name;
        this.UUID = UUID;
    }

    public String getname(){
        return name;
    }

    public UUID getUUID(){
        return UUID;
    }

    public int getDeaths(){
        return Deaths;
    }

    public void setDeaths(int deaths){
        this.Deaths = deaths;
    }

    public CustomPlayer getCustomplayer(){
        return customplayer;
    }

    public void setCustomplayer(CustomPlayer customplayer){
        this.customplayer = customplayer;
    }

    public GameMode getgamemode(){
        return gamemode;
    }

    public void setgamemode(GameMode gamemode){
        this.gamemode = gamemode;
    }

    public boolean getinrespawn(){
        return inrespawn;
    }

    public void setinrespawn(Boolean inrespawn){
        this.inrespawn = inrespawn;
    }

    public boolean getleaved(){
        return leaved;
    }

    public void setleaved(boolean leaved) {
        this.leaved = leaved;
    }
}
