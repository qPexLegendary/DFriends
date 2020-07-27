package ru.dragonestia.friend.action;

import ru.dragonestia.friend.action.defaults.RemoveFriendAction;

import java.util.ArrayList;
import java.util.List;

public class FriendActionManager {

    private final List<FriendAction> actions = new ArrayList<>();

    public void register(FriendAction action){
        actions.add(action);
    }

    public List<FriendAction> getActions(){
        return actions;
    }

    public void registerDefaultActions(){
        actions.add(new RemoveFriendAction());
    }

}
