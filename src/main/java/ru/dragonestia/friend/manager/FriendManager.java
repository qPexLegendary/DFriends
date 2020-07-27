package ru.dragonestia.friend.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import ru.dragonestia.friend.DFriends;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FriendManager {

    private final String playerName;

    public FriendManager(String playerName){
        this.playerName = playerName.toLowerCase();
    }

    public String getPlayerName(){
        return playerName;
    }

    public boolean isRegistered(){
        return DFriends.getInstance().database.exists(playerName);
    }

    public void reset(){
        Config db = DFriends.getInstance().database;

        db.set(playerName + ".maxFriends", 10);
        db.set(playerName + ".avatar", "textures/ui/icon_steve");
        db.set(playerName + ".friends", new ArrayList<String>());
        db.save();
    }

    public int getMaxFriendsCount(){
        return DFriends.getInstance().database.getInt(playerName + ".maxFriends");
    }

    public boolean canAddFriendSlots(int count){
        return getMaxFriendsCount() + count <= 30;
    }

    public boolean canAddFriendSlot(){
        return canAddFriendSlots(1);
    }

    public void addFriendSlots(int count){
        Config db = DFriends.getInstance().database;

        db.set(playerName + ".maxFriends", db.getInt(playerName + ".maxFriends") + count);
        db.save();
    }

    public void addFriendSlot(int count){
        addFriendSlots(1);
    }

    public Friend[] getFriends(){
        Config db = DFriends.getInstance().database;
        List<String> friendsNames = db.getStringList(playerName + ".friends");
        Friend[] list = new Friend[friendsNames.size()];

        for(int i = 0; i < list.length; i++){
            list[i] = new Friend(friendsNames.get(i), db.getString(friendsNames.get(i) + ".avatar"));
        }
        return list;
    }

    public List<String> getFriendList(){
        return DFriends.getInstance().database.getStringList(playerName + ".friends");
    }

    public ArrayList<Player> getOnlineFriendPlayers(){
        List<String> friends = DFriends.getInstance().database.getStringList(playerName + ".friends");
        ArrayList<Player> players = new ArrayList<>(Server.getInstance().getOnlinePlayers().values());

        players.removeIf(player -> !friends.contains(player.getName().toLowerCase()));
        return players;
    }

    public Friend[] getOnlineFriends(){
        Config db = DFriends.getInstance().database;
        List<String> friends = db.getStringList(playerName + ".friends");
        ArrayList<Player> players = new ArrayList<>(Server.getInstance().getOnlinePlayers().values());

        players.removeIf(player -> !friends.contains(player.getName().toLowerCase()));
        Friend[] list = new Friend[players.size()];
        String name;

        for(int i = 0; i< list.length; i++){
            name = players.get(i).getName().toLowerCase();
            list[i] = new Friend(name, db.getString(name + ".avatar"));
        }
        return list;
    }

    public int getFriendsCount(){
        return DFriends.getInstance().database.getStringList(playerName + ".friends").size();
    }

    public boolean canAddFriend(){
        return getFriendsCount() < getMaxFriendsCount();
    }

    public void addFriend(Player player){
        Config db = DFriends.getInstance().database;
        List<String> friends = db.getStringList(playerName + ".friends");
        String name = player.getName().toLowerCase();

        if(friends.contains(name)) return;

        friends.add(name);
        db.set(playerName + ".friends", friends);
        db.save(true);
    }

    public boolean isFriend(Player player){
        return DFriends.getInstance().database.getStringList(playerName + ".friends").contains(player.getName().toLowerCase());
    }

    public void removeFriend(String playerName){
        Config db = DFriends.getInstance().database;
        List<String> friends = db.getStringList(this.playerName + ".friends");
        playerName = playerName.toLowerCase();

        if(!friends.contains(playerName)) return;

        friends.remove(playerName);
        db.set(this.playerName + ".friends", friends);
        db.save(true);
    }

    public boolean isMutualFriends(String playerName){
        Config db = DFriends.getInstance().database;
        return db.getStringList(playerName + ".friends").contains(this.playerName) && db.getStringList(this.playerName + ".friends").contains(playerName);
    }

    public String getAvatar(){
        return DFriends.getInstance().database.getString(playerName + ".avatar");
    }

    public void setAvatar(String avatar){
        Config db = DFriends.getInstance().database;

        db.set(playerName + ".avatar", avatar);
        db.save();
    }

    public static ArrayList<Player> getPlayerListWithThisPlayerIsFriend(String playerName){
        playerName = playerName.toLowerCase();
        Config db = DFriends.getInstance().database;
        ArrayList<Player> players = new ArrayList<>();

        for(Player player: Server.getInstance().getOnlinePlayers().values()){
            if(db.getStringList(player.getName().toLowerCase() + ".friends").contains(playerName)) players.add(player);
        }
        return players;
    }

    public static String getAvatarByPlayer(Player player){
        return DFriends.getInstance().database.getString(player.getName().toLowerCase() + ".avatar");
    }

    public static class Friend {

        public final String playerName, avatar;

        public Friend(String playerName, String avatar){
            this.playerName = playerName;
            this.avatar = avatar;
        }

        public boolean isOnline(){
            return Server.getInstance().getPlayerExact(playerName) != null;
        }

    }

}
