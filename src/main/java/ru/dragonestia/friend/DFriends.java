package ru.dragonestia.friend;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import ru.dragonestia.friend.action.FriendActionManager;
import ru.dragonestia.friend.command.FriendCommand;
import ru.dragonestia.friend.listener.MainListener;

public class DFriends extends PluginBase {

    private static DFriends instance;

    private final FriendActionManager actionManager = new FriendActionManager();

    public Config database;

    @Override
    public void onLoad() {
        instance = this;

        database = new Config("plugins/DFriends/database.yml", Config.YAML);

        getFriendActionManager().registerDefaultActions();
    }

    @Override
    public void onEnable() {
        getServer().getCommandMap().register("", new FriendCommand());

        getServer().getPluginManager().registerEvents(new MainListener(), this);
    }

    @Override
    public void onDisable() {

    }

    public FriendActionManager getFriendActionManager(){
        return actionManager;
    }

    public static DFriends getInstance(){
        return instance;
    }

}
