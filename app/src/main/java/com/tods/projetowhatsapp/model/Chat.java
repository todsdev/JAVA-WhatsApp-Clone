package com.tods.projetowhatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.tods.projetowhatsapp.config.FirebaseSettings;

public class Chat {
    private String idSender;
    private String idReceiver;
    private String lastMessage;
    private User userExhibition;
    private String isGroup;
    private Group group;

    public Chat() {
        this.setIsGroup("false");

    }

    public void save(){
        DatabaseReference database = FirebaseSettings.getFirebaseDatabase();
        DatabaseReference chatRef = database.child("chats");
        chatRef.child(this.getIdSender()).child(this.getIdReceiver()).setValue(this);
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getUserExhibition() {
        return userExhibition;
    }

    public void setUserExhibition(User userExhibition) {
        this.userExhibition = userExhibition;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
