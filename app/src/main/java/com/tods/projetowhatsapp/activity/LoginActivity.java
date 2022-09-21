package com.tods.projetowhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.tods.projetowhatsapp.R;
import com.tods.projetowhatsapp.config.FirebaseSettings;
import com.tods.projetowhatsapp.model.User;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText userEmail, userPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = findViewById(R.id.editLoginEmail);
        userPassword = findViewById(R.id.editLoginPassword);

        auth = FirebaseSettings.getFirebaseAuth();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            openMain();
        }
    }

    public void loginUser(User user){

        auth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(LoginActivity.this, "Login feito com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            openMain();

                        } else {

                            String exception = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e){
                                exception = "Usuário não está cadastrado";
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                exception = "E-mail e senha não correspondem";
                            } catch (Exception e){
                                exception = "Erro ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this, exception,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void validateAuthUser(View view){

        //RECUPERAR TEXTOS DOS CAMPOS
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        //VALIDAR SE E-MAIL E SENHA FORAM DIGITADOS
        if (!email.isEmpty()){
            if (!password.isEmpty()){

                User user = new User();
                user.setEmail(email);
                user.setPassword(password);

                loginUser(user);

            } else {
                Toast.makeText(this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Preencha o e-mail!", Toast.LENGTH_SHORT).show();
        }
    }

    public void openRegister(View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void openMain(){
        startActivity(new Intent(this, MainActivity.class));
    }
}