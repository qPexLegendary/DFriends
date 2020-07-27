package ru.dragonestia.friend.event;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import ru.dragonestia.friend.manager.FriendManager;

public class PlayerAddFriendEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player target;

    private final FriendManager manager;

    public PlayerAddFriendEvent(Player player, Player target, FriendManager manager){
        this.player = player;
        this.target = target;
        this.manager = manager;
    }

    public Player getTarget() {
        return target;
    }

    public FriendManager getManager() {
        return manager;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
