package me.stevenkin.http.mineserver.core;

import me.stevenkin.http.mineserver.core.task.MineServer;

/**
 * Created by wjg on 16-4-28.
 */
public class BootStrap {
    public static void main(String[] args){
        MineServer server = new MineServer();
        server.init();
        new Thread(server).start();
    }
}
