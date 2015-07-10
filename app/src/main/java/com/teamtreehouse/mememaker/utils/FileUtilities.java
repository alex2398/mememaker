package com.teamtreehouse.mememaker.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.teamtreehouse.mememaker.MemeMakerApplicationSettings;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Evan Anger on 7/28/14.
 */
public class FileUtilities {

    public static void saveAssetImage(Context context, String assetName) {

        // Obtenemos el directorio y el nombre de archivo de la imagen
        File fileDirectory = getFileDirectory(context);
        File fileToWrite = new File(fileDirectory,assetName);

        // Creamos un assetmanager
        AssetManager assetManager = context.getAssets();

        try {
            // Abrimos dos streams, uno de entrada y otro de salida
            InputStream in = assetManager.open(assetName);
            FileOutputStream out = new FileOutputStream(fileToWrite);

            // Copiamos el archivo de la entrada a la salida
            //if (!fileToWrite.exists()) {

                copyFile(in, out);
            //}

            // Cerramos los archivos
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


    public static File [] listFiles(Context context) {
        File fileDirectory = getFileDirectory(context);
        File [] filteredFiles = fileDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(file.getAbsolutePath().contains(".jpg")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        return filteredFiles;
    }


/*
    // Creamos un metodo para devolver una lista de archivos
    public static File[] listFiles(Context context) {

        // Obtenemos el directorio
        File fileDirectory = getFileDirectory(context);

        // Obtenemos la lista de archivos, en este caso hemos filtrado
        // los archivos con extension jpg.
        File [] filteredFiles = fileDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getAbsolutePath().contains(".jpg")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        return filteredFiles;
    }
*/
    public static Uri saveImageForSharing(Context context, Bitmap bitmap,  String assetName) {
        File fileToWrite = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), assetName);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return Uri.fromFile(fileToWrite);
        }
    }


    public static void saveImage(Context context, Bitmap bitmap, String name) {
        File fileDirectory = getFileDirectory(context);
        File fileToWrite = new File(fileDirectory, name);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFileDirectory(Context context) {

        // Establecemos el tipo de almacenamiento
        MemeMakerApplicationSettings settings = new MemeMakerApplicationSettings(context);
        String storageType = settings.getStoragePreference();

        File filr;
        // Si el almacenamiento es interno, obtenemos el directorio
        if (storageType.equals(StorageType.INTERNAL)) {
            filr = context.getFilesDir();
            return context.getFilesDir();
        } else {
            // Si no, comprobamos si el almacenamiento externo esta disponible
            if (isExternalStorageAvailable()) {
                // Si esta disponible, comprobamos si es privado o publico
                if (storageType.equals(StorageType.PRIVATE_EXTERNAL)) {
                    filr = context.getExternalFilesDir(null);
                    return context.getExternalFilesDir(null);
                } else {
                    // Si es publico, seleccionamos el directorio publico de las fotos
                    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                }
            } else {
                // Si el almacenamiento externo no esta disponible, cogemos el interno
                filr = context.getFilesDir();
                return context.getFilesDir();
            }
        }
    }

    public static boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            } else {
                return false;
            }
    }

}