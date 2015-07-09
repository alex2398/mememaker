package com.teamtreehouse.mememaker;

import android.preference.PreferenceManager;

import com.teamtreehouse.mememaker.utils.FileUtilities;

/**
 * Created by Evan Anger on 7/28/14.
 */
public class MemeMakerApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FileUtilities.saveAssetImage(this, "dogmess.jpg");
        FileUtilities.saveAssetImage(this, "excitedcat.jpg");
        FileUtilities.saveAssetImage(this, "guiltypup.jpg");

        // El tercer parametro se pone a false para asegurar que solo se toma
        // el valor por defecto en aquellas preferencias que no esten inicializadas
        // se pondria a true por ejemplo para restablecer todas las preferencias, sobreescribiendo
        // las que haya establecido el usuario

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
    }
}
