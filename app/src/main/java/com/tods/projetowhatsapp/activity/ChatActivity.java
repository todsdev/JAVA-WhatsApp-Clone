package com.tods.projetowhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.adapter.MessagesAdapter;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.helper.Base64Custom;
import com.tods.projetowhatsapp.model.Chat;
import com.tods.projetowhatsapp.model.Group;
import com.tods.projetowhatsapp.model.Messages;
import com.tods.projetowhatsapp.model.User;
import com.tods.projetowhatsapp.model.UserFirebase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private CircleImageView imageContactsChat;
    private TextView textContactsNameChat;
    private User receiverUser;
    private User senderUser;
    private EditText editMessage;
    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;
    private List<Messages> messages = new ArrayList<>();
    private DatabaseReference database;
    private DatabaseReference msgRef;
    private StorageReference storage;
    private ChildEventListener celMessages;
    private ImageView buttonCamera;
    private static final int SELECTION_CAMERA = 100;
    private Group group;
    private String idUserSender;
    private String idUserReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        configToolbarSettings();
        configInitialSettings();
        configRecoverUsers();
        configRecoverUserSender();
        configRecyclerView();
        configCameraListener();
    }

    private void configInitialSettings() {
        imageContactsChat = findViewById(R.id.imageContactsChat);
        textContactsNameChat = findViewById(R.id.textContactsNameChat);
        editMessage = findViewById(R.id.editTextMessage);
        recyclerMessages = findViewById(R.id.recyclerMessages);
        buttonCamera = findViewById(R.id.buttonSendPhotoChat);
    }

    private void configRecoverUsers() {
        idUserSender = UserFirebase.recoverUserId();
        senderUser = UserFirebase.getDataLoggedUser();
    }

    private void configToolbarSettings() {
        Toolbar toolbar = findViewById(R.id.toolbar_chats);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void configRecyclerView() {
        adapter = new MessagesAdapter(messages, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setHasFixedSize(true);
        recyclerMessages.setAdapter(adapter);
    }

    private void configCameraListener() {
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messages.clear();
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null){

                } else {
                    recoverMessages();
                    startActivityIfNeeded(i, SELECTION_CAMERA);
                }
            }
        });
        storage = FirebaseSettings.getStorageReference();
        database = FirebaseSettings.getFirebaseDatabase();
        msgRef = database.child("messages").child(idUserSender).child(idUserReceiver);
    }

    private void configRecoverUserSender() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("chatContacts")){
                receiverUser = (User) bundle.getSerializable("chatContacts");
                textContactsNameChat.setText(receiverUser.getName());
                String photo = receiverUser.getPhoto();
                if (photo != null){
                    Uri uri = Uri.parse(receiverUser.getPhoto());
                    Glide.with(ChatActivity.this).load(uri).timeout(6000).into(imageContactsChat);
                } else {
                    imageContactsChat.setImageResource(R.drawable.standard);
                }
                idUserReceiver = Base64Custom.codifyBase64(receiverUser.getEmail());
            } else {
                group = (Group) bundle.getSerializable("chatGroup");
                idUserReceiver = group.getId();
                textContactsNameChat.setText(group.getName());
                String photo = group.getPhoto();
                if (photo != null) {
                    Uri uri = Uri.parse(photo);
                    Glide.with(ChatActivity.this).load(uri).timeout(6000).into(imageContactsChat);
                } else {
                    imageContactsChat.setImageResource(R.drawable.standard);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap image = null;
            try{
                switch (requestCode){
                    case SELECTION_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }
                if (image != null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();
                    String nameImage = UUID.randomUUID().toString();
                    final StorageReference imageRef =
                            storage.child("images").child("photos").child(idUserSender).child(nameImage);
                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Erro ao fazer upload",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                 String url = task.getResult().toString();
                                    if (receiverUser != null){
                                        Messages message = new Messages();
                                        message.setIdUser(idUserSender);
                                        message.setMessage("image.jpeg");
                                        message.setImage(url.toString());
                                        saveMessage(idUserSender, idUserReceiver, message);
                                        saveMessage(idUserReceiver, idUserSender, message);
                                    } else {
                                        for (User member: group.getMembers()){
                                            String idGroupSender = Base64Custom.codifyBase64(member.getEmail());
                                            String idGroupLoggedUser = UserFirebase.recoverUserId();
                                            Messages message = new Messages();
                                            message.setIdUser(idGroupLoggedUser);
                                            message.setMessage("image.jpeg");
                                            message.setImage(url);
                                            message.setName(senderUser.getName());
                                            saveMessage(idGroupSender, idUserReceiver, message);
                                            saveChat(idGroupSender, idUserReceiver, receiverUser, message, true);
                                        }
                                    }
                                    Toast.makeText(ChatActivity.this, "Sucesso ao enviar imagem!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void sendMessage (View view){
        String textMessage = editMessage.getText().toString();
        if (!textMessage.isEmpty()){
            if (receiverUser != null){
                Messages message = new Messages();
                message.setIdUser(idUserSender);
                message.setMessage(textMessage);
                saveMessage(idUserSender, idUserReceiver, message);
                saveMessage(idUserReceiver, idUserSender, message);
                saveChat(idUserSender, idUserReceiver, receiverUser, message, false);
                saveChat(idUserReceiver, idUserSender, senderUser, message, false);
            } else {
                for (User member: group.getMembers()){
                    String idGroupSender = Base64Custom.codifyBase64(member.getEmail());
                    String idGroupLoggedUser = UserFirebase.recoverUserId();
                    Messages message = new Messages();
                    message.setIdUser(idGroupLoggedUser);
                    message.setMessage(textMessage);
                    message.setName(senderUser.getName());
                    saveMessage(idGroupSender, idUserReceiver, message);
                    saveChat(idGroupSender, idUserReceiver, receiverUser, message, true);
                }
            }
        } else {
            Toast.makeText(this, "Preencha uma mensagem!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void saveChat (String idSender, String idReceiver, User userExhibition,
                          Messages msg, boolean isGroup){
        Chat chat = new Chat();
        chat.setIdSender(idSender);
        chat.setIdReceiver(idReceiver);
        chat.setLastMessage(msg.getMessage());
        if (isGroup){
            chat.setIsGroup("true");
            chat.setGroup(group);
        } else {
            chat.setUserExhibition(userExhibition);
            chat.setIsGroup("false");
        }
        chat.save();
    }

    private void saveMessage (String idSender, String idReceiver, Messages msg){
        DatabaseReference database = FirebaseSettings.getFirebaseDatabase();
        msgRef = database.child("messages");
        msgRef.child(idSender).child(idReceiver).push().setValue(msg);
        editMessage.setText("");
    }

    public void recoverMessages(){
        messages.clear();
        celMessages = msgRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages message = snapshot.getValue(Messages.class);
                messages.add(message);
                recyclerMessages.smoothScrollToPosition(messages.size() + 1);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        msgRef.removeEventListener(celMessages);
        messages.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverMessages();
    }
}