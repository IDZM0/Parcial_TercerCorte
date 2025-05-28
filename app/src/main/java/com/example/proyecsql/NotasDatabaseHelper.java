package com.example.proyecsql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.proyecsql.api.ApiService;
import com.example.proyecsql.api.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class NotasDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NotasDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NOTAS = "notas";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXTO = "texto";

    private static final String CREATE_TABLE_NOTAS =
            "CREATE TABLE " + TABLE_NOTAS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TEXTO + " TEXT NOT NULL);";

    public NotasDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTAS);
        onCreate(db);
    }

    public long insertarNota(String texto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXTO, texto);
        long id = db.insert(TABLE_NOTAS, null, values);
        db.close();
        // Simular envío a REST
        enviarNotaAlServidor(id, texto);
        return id;
    }

    public List<Nota> getAllNotas() {
        List<Nota> notas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTAS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String texto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXTO));
                notas.add(new Nota(id, texto));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notas;
    }

    private void enviarNotaAlServidor(long id, String texto) {
        Nota nota = new Nota((int) id, texto);
        ApiService api = ApiClient.getApiService();

        api.crearNota(nota).enqueue(new retrofit2.Callback<Nota>() {
            @Override
            public void onResponse(retrofit2.Call<Nota> call, retrofit2.Response<Nota> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Nota enviada al servidor: " + response.body().getTexto());
                } else {
                    Log.e("API", "Error al enviar nota. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Nota> call, Throwable t) {
                Log.e("API", "Fallo al conectar con el servidor: " + t.getMessage());
            }
        });
    }

    // Nuevo método para eliminar una nota por ID
    public int eliminarNotaPorId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filasEliminadas = db.delete(TABLE_NOTAS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return filasEliminadas;
    }

    // Inserta nota en SQLite sin enviar al servidor
    public void insertarNotaSinSincronizar(int id, String texto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_TEXTO, texto);
        db.insert(TABLE_NOTAS, null, values);
        db.close();
    }



}
