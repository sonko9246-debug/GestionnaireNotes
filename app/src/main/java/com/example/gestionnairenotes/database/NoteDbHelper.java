package com.example.gestionnairenotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NoteDbHelper extends SQLiteOpenHelper {



    // Nom du fichier qui sera créé sur le téléphone
    private static final String NOM_BASE = "notes.db";
    private static final int VERSION = 1;
    public static final String TABLE_NOTES = "notes";

    // Le nom de chaque colonne
    public static final String COL_ID      = "id";
    public static final String COL_TITRE   = "titre";
    public static final String COL_CONTENU = "contenu";
    public static final String COL_COULEUR = "couleur";
    public static final String COL_FAVORI  = "favori";   // 0 = non, 1 = oui
    public static final String COL_DATE    = "date";




    public NoteDbHelper(Context context) {
        super(context, NOM_BASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Cette commande SQL crée la table si elle n'existe pas encore
        String creerTable = "CREATE TABLE " + TABLE_NOTES + " ("
                + COL_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                + COL_TITRE   + " TEXT NOT NULL, "

                + COL_CONTENU + " TEXT NOT NULL, "

                + COL_COULEUR + " TEXT NOT NULL, "

                + COL_FAVORI  + " INTEGER DEFAULT 0, "

                + COL_DATE    + " TEXT NOT NULL" + ")";

        db.execSQL(creerTable); // On exécute la commande SQL
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
}