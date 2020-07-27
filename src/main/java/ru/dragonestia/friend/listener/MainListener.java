package ru.dragonestia.friend.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import ru.dragonestia.friend.manager.FriendManager;

public class MainListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event){
        Player player = event.getPlayer();
        FriendManager manager = new FriendManager(player.getName());

        if(!manager.isRegistered()) manager.reset();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        FriendManager manager = new FriendManager(player.getName());

        event.setJoinMessage("");

        for(Player targetPlayer: FriendManager.getPlayerListWithThisPlayerIsFriend(player.getName())){
            targetPlayer.sendMessage("§aИгрок §l" + player.getName() + "§r§a подключился к серверу.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        FriendManager manager = new FriendManager(player.getName());

        event.setQuitMessage("");

        for(Player targetPlayer: FriendManager.getPlayerListWithThisPlayerIsFriend(player.getName())){
            targetPlayer.sendMessage("§aИгрок §l" + player.getName() + "§r§a отключился от сервера.");
        }
    }

}
