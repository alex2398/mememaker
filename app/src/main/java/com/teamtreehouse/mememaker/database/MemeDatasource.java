package com.teamtreehouse.mememaker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Evan Anger on 8/17/14.
 */
public class MemeDatasource {

    private Context mContext;
    private MemeSQLiteHelper mMemeSqlLiteHelper;

    public MemeDatasource(Context context) {
        mContext = context;
        mMemeSqlLiteHelper = new MemeSQLiteHelper(context);

    }

    public SQLiteDatabase open() {
        return mMemeSqlLiteHelper.getWritableDatabase();
    }

    public void close(SQLiteDatabase database) {
        database.close();
    }

    public void create(Meme meme) {

        // Con este metodo creamos los datos del meme en la base de datos usando el objeto Meme (INSERT)

        // Abrimos la base de datos
        SQLiteDatabase database = open();

        // Iniciamos la transaccion
        database.beginTransaction();

        // Implementacion

        // Creamos los valores que vamos a pasar en la consulta
        ContentValues memeValues= new ContentValues();
        // La columna nombre y la columna ubicacion del asset
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_ASSET, meme.getAssetLocation());
        memeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());



        // Insertamos en la tabla pasando el nombre de la tabla, y los valores
        // (segundo parametro a null, se usa para hacer un insert de una fila sin valores)
        long memeID = database.insert(MemeSQLiteHelper.MEMES_TABLE,null,memeValues);

        for (MemeAnnotation annotation : meme.getAnnotations()) {
            ContentValues annotationValues = new ContentValues();
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            annotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            annotationValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeID);

            database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE,null, annotationValues);
        }

        database.setTransactionSuccessful();

        database.endTransaction();

        close(database);

    }

    public void update (Meme meme) {
        SQLiteDatabase database = open();
        database.beginTransaction();

        ContentValues updateMemeValues = new ContentValues();
        updateMemeValues.put(MemeSQLiteHelper.COLUMN_MEME_NAME, meme.getName());
        // Lanzamos el update propiamente dicho
        database.update(MemeSQLiteHelper.MEMES_TABLE,
                updateMemeValues,
                String.format("%s=%d", BaseColumns._ID, meme.getId()), // where en formato String
                null);

        for (MemeAnnotation annotation : meme.getAnnotations()) {
            ContentValues updateAnnotationValues = new ContentValues();
            updateAnnotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE, annotation.getTitle());
            updateAnnotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR, annotation.getColor());
            updateAnnotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_X, annotation.getLocationX());
            updateAnnotationValues.put(MemeSQLiteHelper.COLUMN_ANNOTATION_Y, annotation.getLocationY());
            updateAnnotationValues.put(MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, meme.getId());

            if (annotation.hasBeenSaved()) {
                database.update(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        updateAnnotationValues,
                        String.format("%s=%d", BaseColumns._ID,annotation.getId()),
                        null);
            } else {
                database.insert(MemeSQLiteHelper.ANNOTATIONS_TABLE,
                        null,
                        updateAnnotationValues);
            }

        }
        database.setTransactionSuccessful();
        database.endTransaction();
        close(database);
    }

    public ArrayList<Meme> read() {

        ArrayList<Meme> memes = readMemes();
        addMemeAnnotations(memes);

        return memes;
    }

    public ArrayList<Meme> readMemes() {

        // Método para leer de la base de datos

        // Abrimos una base de datos
        SQLiteDatabase database = open();

        // Declaramos un cursor que hace la consulta a la base de datos instanciada en MemeSQLIteHelper
        // Con esto no obtenemos los datos, solo el cursor para recorrerlos

        Cursor cursor = database.query(
                MemeSQLiteHelper.MEMES_TABLE,           // Nombre de la tabla
                new String[]{MemeSQLiteHelper.COLUMN_MEME_NAME, BaseColumns._ID, MemeSQLiteHelper.COLUMN_MEME_ASSET}, // Array de string con las columnas que vamos a leer
                null,   // selection
                null,   // selection args
                null,   // group by
                null,   // having
                null  // order by
        );


        ArrayList<Meme> memes = new ArrayList<Meme>();
        if (cursor.moveToFirst()) {
            do {
                // Creamos el objeto con el constructor y los datos del cursor
                Meme meme = new Meme(getIntFromColumnName(cursor,BaseColumns._ID),
                        getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_MEME_ASSET),
                        getStringFromColumnName(cursor,MemeSQLiteHelper.COLUMN_MEME_NAME),null);
                // Lo añadimos a la coleccion de memes (arralist)
                memes.add(meme);
                // y pasamos al siguiente registro hasta el ultimo
            } while (cursor.moveToNext());
        }

        // Cerramos el cursor
        cursor.close();
        // Cerramos la base de datos
        close(database);

        // Devolvemos la coleccion de memes consultada
        return memes;
    }

    public void addMemeAnnotations(ArrayList<Meme> memes) {

        SQLiteDatabase database = open();

        for (Meme meme : memes) {
            ArrayList<MemeAnnotation> annotations = new ArrayList<MemeAnnotation>();
            Cursor cursor = database.rawQuery(
                    "SELECT * FROM " + MemeSQLiteHelper.ANNOTATIONS_TABLE + " WHERE MEME_ID = " + meme.getId(),null);
            if (cursor.moveToFirst()) {
                do {
                    MemeAnnotation annotation = new MemeAnnotation(
                            getIntFromColumnName(cursor, BaseColumns._ID),
                            getStringFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_COLOR),
                            getStringFromColumnName(cursor,MemeSQLiteHelper.COLUMN_ANNOTATION_TITLE),
                            getIntFromColumnName(cursor,MemeSQLiteHelper.COLUMN_ANNOTATION_X),
                            getIntFromColumnName(cursor, MemeSQLiteHelper.COLUMN_ANNOTATION_Y)
                    );
                    annotations.add(annotation);
                } while(cursor.moveToNext());
            }
            meme.setAnnotations(annotations);
            cursor.close();
        }
        close(database);
    }

    public void delete(int memeId) {

        SQLiteDatabase database = open();
        database.beginTransaction();

        database.delete(MemeSQLiteHelper.ANNOTATIONS_TABLE
                , String.format("%s=%d", MemeSQLiteHelper.COLUMN_FOREIGN_KEY_MEME, memeId)
                , null);

        database.delete(MemeSQLiteHelper.MEMES_TABLE
                ,String.format("%s=%d",BaseColumns._ID, memeId)
                ,null);



        database.setTransactionSuccessful();
        database.endTransaction();

        close(database);
    }

    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

}
