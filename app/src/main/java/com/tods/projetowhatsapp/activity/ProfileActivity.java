package com.tods.projetowhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.helper.Permission;
import com.tods.projetowhatsapp.model.User;
import com.tods.projetowhatsapp.model.UserFirebase;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String[] permission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private ImageButton buttonCamera, buttonGallery;
    private static final int SELECTION_CAMERA = 100;
    private static final int SELECTION_GALLERY = 200;
    private CircleImageView imageProfile;
    private StorageReference storageReference;
    private String userId;
    private EditText editName;
    private ImageView updateName;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //CONFIGURAÇÕES INICIAIS
        storageReference = FirebaseSettings.getStorageReference();
        userId = UserFirebase.recoverUserId();
        loggedUser = UserFirebase.getDataLoggedUser();

        //VALIDAR PERMISSÕES
        Permission.validatePermission(permission, this, 1);

        buttonCamera = findViewById(R.id.imageButtonCamera);
        buttonGallery = findViewById(R.id.imageButtonGallery);
        imageProfile = findViewById(R.id.profile_image);
        editName = findViewById(R.id.editProfileName);
        updateName = findViewById(R.id.imageUpdateName);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //RECUPERAR DADOS DO USUÁRIO
        FirebaseUser user = UserFirebase.getCurrentUser();
        Uri url = user.getPhotoUrl();
        if (url != null){
            Glide.with(this).load(url).timeout(6000).into(imageProfile);
            Log.i("PHOTO", "URL: " + url);
        } else {
            imageProfile.setImageResource(R.drawable.padrao);
        }

        editName.setText(user.getDisplayName());

        updateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editName.getText().toString();
                boolean thereturn = UserFirebase.updateUserName(name);
                if (thereturn){

                    loggedUser.setName(name);
                    loggedUser.update();

                    Toast.makeText(ProfileActivity.this, "Nome alterado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void openGallery(View view){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityIfNeeded(i, SELECTION_GALLERY);

    }

    public void openCamera(View view){

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityIfNeeded(i, SELECTION_CAMERA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            Bitmap image = null;

            try {
                switch (requestCode){
                    case SELECTION_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECTION_GALLERY:
                        Uri localSelectedImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localSelectedImage);
                        break;
                }

                if (image != null){

                    imageProfile.setImageBitmap(image);

                    //RECUPERAR DADOS PARA O FIREBASE
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                    byte[] dataImage = baos.toByteArray();

                    //SALVAR IMAGEM NO FIREBASE
                    StorageReference imageRef = storageReference
                            .child("images")
                            .child("profile")
                            .child(userId + ".jpeg");

                    UploadTask upTask = imageRef.putBytes(dataImage);
                    upTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, "Sucesso ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    updateUserPhoto(url);
                                }
                            });
                        }
                    });
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "ERRO!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUserPhoto(Uri url){

        boolean thereturn = UserFirebase.updateUserPhoto(url);
        if (thereturn){
            UserFirebase.updateUserPhoto(url);
            loggedUser.setPhoto(url.toString());
            loggedUser.update();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissonResults: grantResults){
            if (permissonResults == PackageManager.PERMISSION_DENIED){
                alertValidatePermission();
            }
        }
    }

    private void alertValidatePermission(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o aplicativo é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}