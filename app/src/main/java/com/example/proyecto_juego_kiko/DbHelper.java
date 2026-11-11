package com.example.proyecto_juego_kiko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "juego_robots.db";
    private static final String TABLE_USUARIOS = "t_usuarios";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USUARIOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)");
        db.execSQL("INSERT INTO " + TABLE_USUARIOS + " (usuario, password) VALUES ('piloto1', '1234')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    public long registrarUsuario(String usuario, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario", usuario);
        values.put("password", password);
        return db.insert(TABLE_USUARIOS, null, values);
    }

    public boolean comprobarLogin(String usuario, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USUARIOS + " WHERE usuario = ? AND password = ?",
                new String[]{usuario, password}
        );
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }
}
