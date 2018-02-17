package me.stevenkin.mineapp;

import me.stevenkin.http.mineserver.core.MineServer;

public class MineApplication {

    public static void main(String[] args){
        MineServer.run(MineApplication.class, args);
    }

}
