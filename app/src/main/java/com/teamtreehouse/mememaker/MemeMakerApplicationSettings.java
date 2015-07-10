package com.teamtreehouse.mememaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.teamtreehouse.mememaker.utils.StorageType;

/**
 * Created by Evan Anger on 8/13/14.
 */
public class MemeMakerApplicationSettings {

    public SharedPreferences mSharedPreferences;

    public MemeMakerApplicationSettings(Context context) {
        // Constructor: Creamos una nueva variable de SharedPreferences pasandole el contexto
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getStoragePreference() {
        // Obtenenmos la sharedPreference "storage", y le pasamos el valor por defecto,
        // en caso de que esté inicializada a INTERNAL
        return mSharedPreferences.getString("Storage", StorageType.PUBLIC_EXTERNAL);
    }
        // Establecemos la shared preference "storage" pasandole como cadena
        // el tipo de almacenamiento
    public void setSharedPreferences (String storageType) {
        mSharedPreferences.edit()
                .putString("Storage", storageType)
                .apply();
    }
}
