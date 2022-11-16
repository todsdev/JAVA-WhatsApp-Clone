package com.tods.projetowhatsapp.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.helper.Base64Custom;

public class UserFirebase {

    public static String recoverUserId(){
        FirebaseAuth user = FirebaseSettings.getFirebaseAuth();
        String email = user.getCurrentUser().getEmail();
        String idUser = Base64Custom.codifyBase64(email);
        return idUser;
    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth user = FirebaseSettings.getFirebaseAuth();
        return user.getCurrentUser();
    }

    public static boolean updateUserPhoto(Uri url){
        try {
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(url).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("PROFILE", "Erro ao atualizar foto");
                    }
                }
            });
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUserName(String name){
        try{
            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("PROFILE", "Erro ao atualizar nome");
                    }
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User getDataLoggedUser(){
        FirebaseUser firebaseUser = getCurrentUser();
        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        if (firebaseUser.getPhotoUrl() == null){
            user.setPhoto("");
        } else {
            user.setPhoto(firebaseUser.getPhotoUrl().toString());
        }
        return user;
    }
}
