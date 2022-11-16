package com.tods.projetowhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.adapter.SelectedGroupAdapter;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.model.Group;
import com.tods.projetowhatsapp.model.User;
import com.tods.projetowhatsapp.model.UserFirebase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterGroupActivity extends AppCompatActivity {
    private List<User> listSelectedMembers = new ArrayList<>();
    private TextView textTotal;
    private Toolbar toolbar;
    private SelectedGroupAdapter adapter_selected;
    private RecyclerView recyclerGroupMembers;
    private ImageView imageGroup;
    private static final int SELECTION_GALLERY = 200;
    private StorageReference storage;
    private Group group;
    private FloatingActionButton fab;
    private EditText editGroupName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);
        configToolbar();
        configInitialSettings();
        configRecoverLists();
        configRecyclerView();
        configImageGroup();
        configFloatingActionButtonSaveGroup();
    }

    private void configInitialSettings() {
        editGroupName = findViewById(R.id.editGroupName);
        fab = findViewById(R.id.fab_confirm_group);
        imageGroup = findViewById(R.id.imageGroup);
        recyclerGroupMembers = findViewById(R.id.recyclerGroupMembers);
        textTotal = findViewById(R.id.textTotalGroupMembers);
        storage = FirebaseSettings.getStorageReference();
        group = new Group();
    }

    private void configFloatingActionButtonSaveGroup() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameGroup = editGroupName.getText().toString();
                listSelectedMembers.add(UserFirebase.getDataLoggedUser());
                group.setMembers(listSelectedMembers);
                group.setName(nameGroup);
                group.save();
                Intent i = new Intent(RegisterGroupActivity.this, ChatActivity.class);
                i.putExtra("chatGroup", group);
                startActivity(i);
                Toast.makeText(RegisterGroupActivity.this, "Grupo salvo com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configImageGroup() {
        imageGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityIfNeeded(i, SELECTION_GALLERY);
                }
            }
        });
    }

    private void configRecyclerView() {
        adapter_selected = new SelectedGroupAdapter(listSelectedMembers, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerGroupMembers.setLayoutManager(layoutManager);
        recyclerGroupMembers.setHasFixedSize(true);
        recyclerGroupMembers.setAdapter(adapter_selected);
    }

    private void configRecoverLists() {
        if (getIntent().getExtras() != null) {
            List<User> members = (List<User>) getIntent().getExtras().getSerializable("members");
            listSelectedMembers.addAll(members);
            textTotal.setText("Participantes: " + listSelectedMembers.size());
        }
    }

    private void configToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("Defina o nome");
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap image = null;
            try {
                Uri localImageSelected = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImageSelected);
                if (image != null){
                    imageGroup.setImageBitmap(image);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dataImage = baos.toByteArray();
                    StorageReference imageRef = storage.child("images").child("groups").child(group.getId());
                    UploadTask uploadTask = imageRef.putBytes(dataImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterGroupActivity.this, "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(RegisterGroupActivity.this, "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String url = task.getResult().toString();
                                    group.setPhoto(url);
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
}