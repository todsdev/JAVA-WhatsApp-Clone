package com.tods.projetowhatsapp.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codifyBase64(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT)
                .replaceAll("(\\n|\\r)", "");
    }

    public static String decodifyBase64(String codifiedText){
        return new String(Base64.decode(codifiedText, Base64.DEFAULT));
    }
}
