package com.tods.projetowhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.helper.Base64Custom;
import com.tods.projetowhatsapp.model.User;
import com.tods.projetowhatsapp.model.UserFirebase;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText name, email, password1, password2;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        configInitialSettings();
    }

    private void configInitialSettings() {
        name = findViewById(R.id.editRegisterName);
        email = findViewById(R.id.editRegisterEmail);
        password1 = findViewById(R.id.editRegisterPassword1);
        password2 = findViewById(R.id.editRegisterPassword2);
    }

    public void registerUser(User user){
        auth = FirebaseSettings.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            String idUser = Base64Custom.codifyBase64(user.getEmail());
                            user.setId(idUser);
                            user.save();
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Sucesso ao cadastrar usuário!",
                                    Toast.LENGTH_SHORT).show();
                            UserFirebase.updateUserName(user.getName());
                            finish();
                        } else {
                            String exception = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                exception = "Digite uma senha mais forte";
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                exception = "Digite um email válido";
                            } catch (FirebaseAuthUserCollisionException e){
                                exception = "Esse e-mail já foi utilizado";
                            } catch (Exception e) {
                                exception = "Erro ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(RegisterActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void validateUser(View view){
        String textName = name.getText().toString();
        String textEmail = email.getText().toString();
        String textPassword1 = password1.getText().toString();
        String textPassword2 = password2.getText().toString();
        if(!textName.isEmpty()){
            if(!textEmail.isEmpty()){
                if (!textPassword1.isEmpty()){
                    if (!textPassword2.isEmpty()){
                        if (textPassword1.equals(textPassword2)){
                            User user = new User();
                            user.setName(textName);
                            user.setEmail(textEmail);
                            user.setPassword(textPassword1);
                            registerUser(user);
                        } else {
                            Toast.makeText(this, "Senhas não correspondem!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Preencha a confirmação de senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha o email!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}