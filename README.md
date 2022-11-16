<!-- # Title -->
# WhatsApp Clone

<!-- # Short Description -->

>- In this application, the user is able to exchange **messages** and **photos**, which can be from gallery or your own camera, between the registered members
>- Create **groups** with your selected contacts and interact with all of them at she same time
>- Customize your **profile** editing your name and your photo, which will be displayed for your contacts

This application was written in JAVA language using Android Studio. It uses Google Firebase as database, saving messages, photos and user information in there. 

<!-- # Badges -->
<div style="display: inline_block"><br>
    <img height="30" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/androidstudio/androidstudio-original.svg">
    <img height="30" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/firebase/firebase-plain.svg">
    <img height="30" width="40" src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg">
</div>

---

# Tags

`Android Studio` `Firebase` `JAVA` `WhatsApp`

---


# Demo

![Demo](https://media.discordapp.net/attachments/655489748885831713/1042303074234028044/ezgif.com-gif-maker-2.gif)


>- Registration and Login using email and password authenticated by Google Firebase!


![Demo](https://media.discordapp.net/attachments/655489748885831713/1042303073919434873/ezgif.com-gif-maker.png)


>- Update your profile with your name and photo!
>- Choose from your internal gallery or just take a photo!
  
![Demo](https://media.discordapp.net/attachments/655489748885831713/1042303073604878386/ezgif.com-gif-maker-2.png)


>- Chat with your friends and send them photos using your camera!

![Demo](https://media.discordapp.net/attachments/655489748885831713/1042303704654692413/ezgif.com-gif-maker.gif)


>- Also chat with your group friends and send them photos using your camera!
>- Create and edit your group in your way!

---

# Code Example
```java
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
```

Example of the code needed to register the user in the **Google Firebase** platform and avoid the exceptions of the registration, such as *Week Password*, *Invalid Email*, *Already Registered Email* and any other possibilities.

---

# Contributors

- [Thiago Rodrigues](https://www.linkedin.com/in/tods/)
