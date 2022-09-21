package com.tods.projetowhatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseSettings {

    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //RETORNAR INSTÂNCIA DO FIREBASEDATABASE
    public static DatabaseReference getFirebaseDatabase(){
        if (database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //RETORNAR INSTÃNCIA DO FIREBASEAUTH
    public static FirebaseAuth getFirebaseAuth(){
        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    //RETORNAR INSTÂNCIA DO STORAGE
    public static StorageReference getStorageReference(){
        if (storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
