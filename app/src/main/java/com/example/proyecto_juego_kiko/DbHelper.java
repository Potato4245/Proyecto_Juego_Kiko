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
        // Al crear la base por primera vez, preparamos las cuentas y el piloto de demostración.
        db.execSQL("CREATE TABLE " + TABLE_USUARIOS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)");
        db.execSQL("INSERT INTO " + TABLE_USUARIOS + " (usuario, password) VALUES ('piloto1', '1234')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Esta migración de la demo recrea la tabla y descarta las cuentas anteriores.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // SQLite devuelve el id de la fila creada o -1 si falla la inserción (p. ej., nombre repetido).
    public long registrarUsuario(String usuario, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuario", usuario);
        values.put("password", password);
        return db.insert(TABLE_USUARIOS, null, values);
    }

    public boolean comprobarLogin(String usuario, String password) {
        SQLiteDatabase db = getReadableDatabase();
        // Los signos de interrogación enlazan los dos datos del formulario a la consulta.
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USUARIOS + " WHERE usuario = ? AND password = ?",
                new String[]{usuario, password}
        );
        boolean existe = cursor.moveToFirst();
        cursor.close();
        return existe;
    }

    // El número de filas actualizadas indica si había una cuenta con ese nombre.
    public boolean cambiarPassword(String usuario, String nuevaPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", nuevaPassword);
        return db.update(TABLE_USUARIOS, values, "usuario = ?", new String[]{usuario}) > 0;
    }
}
