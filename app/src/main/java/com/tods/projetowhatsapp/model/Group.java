package com.tods.projetowhatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.helper.Base64Custom;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {

    private String id, name, photo;
    private List<User> members;

    public Group() {
        DatabaseReference database = FirebaseSettings.getFirebaseDatabase();
        DatabaseReference groupRef = database.child("groups");

        //GETKEY PARA RECUPERAR CHAVE CRIADA PELO PUSH
        String idFirebase = groupRef.push().getKey();
        setId(idFirebase);
    }

    public void save (){
        DatabaseReference database = FirebaseSettings.getFirebaseDatabase();
        DatabaseReference groupRef = database.child("groups");
        groupRef.child(getId()).setValue(this);

        //SALVAR CONVERSAS PARA MEMBRO DO GRUPO
        for (User member: getMembers()){
            String idSender = Base64Custom.codifyBase64(member.getEmail());
            String idReceiver = getId();

            Chat chat = new Chat();
            chat.setIdSender(idSender);
            chat.setIdReceiver(idReceiver);
            chat.setLastMessage("");
            chat.setIsGroup("true");
            chat.setGroup(this);
            chat.save();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
