package com.tods.projetowhatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    public static boolean validatePermission(String[] permissions, Activity activity, int requestCode){

        if (Build.VERSION.SDK_INT >= 23){

            List<String> permissionList = new ArrayList<>();

            //PERCORRE AS PERMISSÕES PASSADAS VERIFICANDO UMA A UMA SE JÁ ESTÁ LIBERADA
            for(String permission: permissions){
                Boolean havePermission =
                        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
                if (!havePermission) permissionList.add(permission);
            }

            //CASO A LISTA ESTEJA VAZIA NÃO É NECESSÁRIO SOLICITAR PERMISSÃO
            if(permissionList.isEmpty()) return true;

            //CONVERTENDO PERMISSIONS PARA ARRAY (UTILIZAR NA SOLICITAÇÃO)
            String[] newPermissions = new String[permissionList.size()];
            permissionList.toArray(newPermissions);

            //SOLICITAR PERMISSÃO
            ActivityCompat.requestPermissions(activity, newPermissions, requestCode);
        }
        return true;
    }

}
