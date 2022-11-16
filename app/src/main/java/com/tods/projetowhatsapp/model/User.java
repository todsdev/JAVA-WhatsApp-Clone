package com.tods.projetowhatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.tods.projetowhatsapp.config.FirebaseSettings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String name;
    private String email;
    private String password;
    private String id;
    private String photo;

    public User() {
    }

    public void save(){
        DatabaseReference database = FirebaseSettings.getFirebaseDatabase();
        DatabaseReference user = database.child("user").child(getId());
        user.setValue(this);
    }

    public void update(){
        String userId = UserFirebase.recoverUserId();
        DatabaseReference database = FirebaseSettings.getFirebaseDatabase();
        DatabaseReference userRef = database.child("user").child(userId);
        Map<String, Object> userValue = configMap();
        userRef.updateChildren(userValue);
    }

    @Exclude
    public Map<String, Object> configMap(){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("name", getName());
        userMap.put("photo", getPhoto());
        return userMap;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Exclude
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
