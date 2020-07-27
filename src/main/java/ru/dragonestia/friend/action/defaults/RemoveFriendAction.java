package ru.dragonestia.friend.action.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import ru.dragonestia.friend.action.FriendAction;
import ru.dragonestia.friend.manager.FriendManager;
import ru.nukkitx.forms.elements.ModalForm;

public class RemoveFriendAction extends FriendAction {

    @Override
    public String getName() {
        return "Удалить из друзей";
    }

    @Override
    public String getIcon() {
        return "textures/blocks/barrier";
    }

    @Override
    public void handle(Player player, FriendManager manager, FriendManager.Friend friend) {
        sendMainForm(player, manager, friend.playerName);
    }

    public void sendMainForm(Player player, FriendManager manager, String playerName){
        new ModalForm("Подтверждение", "Вы точно хотите удалить игрока §e" + playerName + "§f из своего списка друзей?", "Да", "Нет")
                .send(player, (target, form, data) -> {
                    if(data != 0) return;

                    manager.removeFriend(playerName);
                    player.sendMessage("§eВы успешно удалили игрока §l" + playerName + "§r§e из своего списка друзей!");

                    Player targetPlayer = Server.getInstance().getPlayerExact(playerName);
                    if(targetPlayer != null){
                        targetPlayer.sendMessage("§eИгрок §l" + target.getName() + "§r§e удалил вас из своего списка друзей.");
                    }
                });
    }

}
