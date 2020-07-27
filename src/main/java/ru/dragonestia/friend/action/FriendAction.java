package ru.dragonestia.friend.action;

import cn.nukkit.Player;
import ru.dragonestia.friend.manager.FriendManager;

public abstract class FriendAction {

    public abstract String getName();

    public String getIcon(){
        return "textures/blocks/command_block";
    }

    public abstract void handle(Player player, FriendManager manager, FriendManager.Friend friend);

}
