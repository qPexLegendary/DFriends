package ru.dragonestia.friend.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import ru.dragonestia.friend.DFriends;
import ru.dragonestia.friend.action.FriendAction;
import ru.dragonestia.friend.event.PlayerAddFriendEvent;
import ru.dragonestia.friend.manager.FriendManager;
import ru.nukkitx.forms.elements.CustomForm;
import ru.nukkitx.forms.elements.ImageType;
import ru.nukkitx.forms.elements.SimpleForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendCommand extends Command {

    public final static HashMap<String, String> avatars = new HashMap<>();

    public FriendCommand() {
        super("friend", "Система друзей", "/friend", new String[]{
                "friends"
        });
    }

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if(commandSender instanceof Player){
            sendMainForm((Player) commandSender);
            return true;
        }

        commandSender.sendMessage("Данную команду можно использовать только в игре!");
        return false;
    }

    public void sendMainForm(Player player){
        sendMainForm(player, new FriendManager(player.getName()));
    }

    public void sendMainForm(Player player, FriendManager manager){
        new SimpleForm("Друзья")
                .addButton("Мой профиль", ImageType.PATH, manager.getAvatar())
                .addButton("Список друзей", ImageType.PATH, "textures/ui/icon_multiplayer")
                .addButton("Добавить друга", ImageType.PATH, "textures/ui/color_plus")
                .send(player, (target, form ,data) -> {
                    switch (data){
                        case 0: //Мой профиль
                            sendProfileForm(target, manager);
                            break;

                        case 1: //Список друзей
                            sendFriendsForm(target, manager);
                            break;

                        case 2: //Добавить друга
                            sendAddFriendForm(target, manager);
                            break;
                    }
                });
    }

    public void sendProfileForm(Player player, FriendManager manager){
        new SimpleForm("Мой профиль")
                .setContent(
                        "Количество друзей: §e" + manager.getFriendsCount() + " §f(§a" + manager.getOnlineFriendPlayers().size() + " Онлайн§f)\n" +
                                "Количество мест в списке друзей: §e" + manager.getMaxFriendsCount() + " §f(§2" + (manager.getMaxFriendsCount() - manager.getFriendsCount()) + " доступно§f)"
                )
                .addButton("Сменить аватарку", ImageType.PATH, manager.getAvatar())
                .addButton("Назад")
                .send(player, (target, form, data) -> {
                    switch (data){
                        case 0: //Сменить аватарку
                            sendChangeAvatarForm(target, manager);
                            break;

                        case 1: //Назад
                            sendMainForm(target, manager);
                            break;
                    }
                });
    }

    public void sendChangeAvatarForm(Player player, FriendManager manager){
        SimpleForm form = new SimpleForm("Выбор аватарки", "Выберите аватарку, которая вам больше всего нравится. Она будет отображаться в списке друзей.")
                .addButton("Отмена");

        for(String name: avatars.keySet()){
            form.addButton(name, ImageType.PATH, avatars.get(name));
        }

        form.send(player, (target, targetPlayer, data) -> {
            if(data == -1) return;

            if(data == 0){
                sendProfileForm(target, manager);
                return;
            }

            String avatar = new ArrayList<>(avatars.keySet()).get(data - 1);
            manager.setAvatar(avatars.get(avatar));
            target.sendMessage("§eВы успешно изменили вашу аватарку на §l" + avatar + "§r§e!");
        });
    }

    public void sendFriendsForm(Player player, FriendManager manager){
        sendFriendsForm(player, manager, false);
    }

    public void sendFriendsForm(Player player, FriendManager manager, boolean online){
        SimpleForm form = new SimpleForm("Список друзей" + (online? " онлайн" : ""))
                .addButton("Назад");
        FriendManager.Friend[] friends = manager.getFriends();

        if(friends.length == 0) form.setContent(online? "Все ваши друзья сейчас оффлайн :C" : "У вас пока-что нет друзей :C");

        for(FriendManager.Friend friend: friends){
            form.addButton(friend.playerName + (friend.isOnline() && !online? "\n§l(§2Онлайн§0)" : ""), ImageType.PATH, friend.avatar);
        }

        form.send(player, (target, targetForm, data) -> {
            if(data == -1) return;

            if(data == 0){
                sendMainForm(target, manager);
                return;
            }

            sendFriendSelectedForm(target, manager, friends[data - 1], online);
        });
    }

    public void sendFriendSelectedForm(Player player, FriendManager manager, FriendManager.Friend friend, boolean online){
        SimpleForm form = new SimpleForm(friend.playerName, "Выберите действие, которое хотите применить к §e" + friend.playerName + "§f.");

        for(FriendAction action: DFriends.getInstance().getFriendActionManager().getActions()){
            form.addButton(action.getName(), ImageType.PATH, action.getIcon());
        }

        form.addButton("Назад")
                .send(player, (target, targetForm, data) -> {
                    if(data == -1) return;

                    List<FriendAction> actions = DFriends.getInstance().getFriendActionManager().getActions();
                    if(data == actions.size()){
                        sendFriendsForm(target, manager, online);
                        return;
                    }

                    actions.get(data).handle(target, manager, friend);
                });
    }

    public void sendAddFriendForm(Player player, FriendManager manager){
        if(!manager.canAddFriend()){
            player.sendMessage("§cВы не можете добавлять игроков в свой список друзей, так как в списке нет свободного места.");
            return;
        }

        new CustomForm("Добавление игрока в друзья")
                .addLabel("Добавить можно только играющего на сервере игрока.")
                .addInput("НикНейм игрока")
                .addLabel("Параметр '§3Точный НикНейм§f' ищет игрока с таким же НикНеймом, какой вы ввели в поле выше. При отключенном параметре вы можете ввести часть НикНейма и вам будут предложены игроки, соответствующие запросу.")
                .addToggle("Точный НикНейм", false)
                .send(player, (target, form, data) -> {
                    if(data == null) return;

                    String name = data.get(1).toString();
                    boolean precise = (boolean) data.get(3);

                    if(name.length() == 0){
                        target.sendMessage("§cФорма неверно заполнена: отсутствует знаениче 'НикНейм'.");
                        return;
                    }

                    if(precise){
                        Player targetPlayer;
                        targetPlayer = Server.getInstance().getPlayerExact(name);

                        if(targetPlayer == null){
                            player.sendMessage("§cИгрок не найден.");
                            return;
                        }

                        if(targetPlayer.equals(target)){
                            player.sendMessage("§cК сожалению добавить самого себя в друзья нельзя. Попробуйте как-нибудь в другой раз :D");
                            return;
                        }

                        addPlayerToFriendList(target, targetPlayer, manager);
                    }else{
                        ArrayList<Player> players = new ArrayList<>(Server.getInstance().getOnlinePlayers().values());
                        players.removeIf(p -> {
                            if(p.equals(target)) return true;
                            return !p.getName().matches(name);
                        });

                        if(players.size() > 25){
                            target.sendMessage("§cБыло найдено более 25 игроков с данным поисковым запросом. Попробуйте изменить запрос.");
                            return;
                        }

                        sendSearchResultForm(target, manager, players);
                    }
                });
    }

    private void sendSearchResultForm(Player player, FriendManager manager, List<Player> players){
        SimpleForm form = new SimpleForm("Добавление игрока в друзья")
                .setContent("Выберите игрока, которого вы хотите добавить в свой список друзей.");

        for(Player target: players){
            form.addButton(target.getName(), ImageType.PATH, FriendManager.getAvatarByPlayer(target));
        }

        form.addButton("Назад")
                .send(player, (target, targetForm, data) -> {
                    if(data == -1) return;

                    if(data == players.size()){
                        sendMainForm(target, manager);
                        return;
                    }

                    addPlayerToFriendList(target, players.get(data), manager);
                });
    }

    private void addPlayerToFriendList(Player player, Player target, FriendManager manager){
        PlayerAddFriendEvent event = new PlayerAddFriendEvent(player, target, manager);
        Server.getInstance().getPluginManager().callEvent(event);

        if(event.isCancelled()){
            return;
        }

        manager.addFriend(target);
        player.sendMessage("§eВы успешно добавили игрока §l" + target.getName() + "§r§e в свой список друзей!");
        target.sendMessage("§eИгрок §l" + player.getName() + "§r§e добавил вас в свой список друзей.");
    }

    static {
        avatars.put("Нагрудник", "textures/ui/armor_full");
        avatars.put("Плохой знак", "textures/ui/bad_omen_effect");
        avatars.put("Палитра", "textures/ui/color_picker");
        avatars.put("Корона", "textures/ui/op");
        avatars.put("Звезда", "textures/ui/filledStar");
        avatars.put("Алекс", "textures/ui/Friend1");
        avatars.put("Стив", "textures/ui/Friend2");
        avatars.put("Кирка", "textures/ui/haste_effect");
        avatars.put("Яблоко", "textures/ui/icon_apple");
        avatars.put("Шарик", "textures/ui/icon_balloon");
        avatars.put("Алмаз", "textures/ui/icon_best3");
        avatars.put("Торт", "textures/ui/icon_cake");
        avatars.put("Верстак", "textures/ui/icon_crafting");
        avatars.put("Житель", "textures/ui/icon_deals");
        avatars.put("Листик", "textures/ui/icon_fall");
        avatars.put("Панда", "textures/ui/icon_panda");
        avatars.put("Цветочки", "textures/ui/icon_spring");
        avatars.put("Солнце", "textures/ui/icon_summer");
        avatars.put("Огонь", "textures/ui/icon_trending");
        avatars.put("Снежинка", "textures/ui/icon_winter");
        avatars.put("Черепаха", "textures/ui/purtle");
        avatars.put("Игральная кость", "textures/ui/random_dice");
        avatars.put("Сердечко", "textures/ui/regeneration_effect");
        avatars.put("Бошмак", "textures/ui/speed_effect");
        avatars.put("Меч", "textures/ui/strength_effect");
        avatars.put("Утро", "textures/ui/time_1sunrise");
        avatars.put("День", "textures/ui/time_2day");
        avatars.put("Вечер", "textures/ui/time_4sunset");
        avatars.put("Ночь", "textures/ui/time_5night");
        avatars.put("Фугу", "textures/items/fish_pufferfish_raw");
        avatars.put("Говно", "textures/items/dye_powder_brown_new");
        avatars.put("Лесные ягоды", "textures/items/sweet_berries");
        avatars.put("Тотем", "textures/items/totem");
        avatars.put("Печка", "textures/blocks/furnace_front_off");
        avatars.put("Наблюдатель", "textures/blocks/observer_front");
        avatars.put("Тыква", "textures/blocks/pumpkin_face_off");
        avatars.put("Динамит", "textures/blocks/tnt_side");
        avatars.put("Сундук", "textures/blocks/chest_front");
    }

}
