package com.example.gestionnairenotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionnairenotes.model.Note;

import java.util.ArrayList;
import java.util.List;


public class NoteDao {
    private NoteDbHelper dbHelper;

    public NoteDao(Context context) {
        dbHelper = new NoteDbHelper(context);
    }



    // 1. AJOUTER une note

    public long insertNote(Note note) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valeurs = new ContentValues();
        valeurs.put(NoteDbHelper.COL_TITRE,   note.getTitre());
        valeurs.put(NoteDbHelper.COL_CONTENU, note.getContenu());
        valeurs.put(NoteDbHelper.COL_COULEUR, note.getCouleur());
        valeurs.put(NoteDbHelper.COL_FAVORI,  note.isFavori() ? 1 : 0);

        valeurs.put(NoteDbHelper.COL_DATE,    note.getDate());

        long idGenere = db.insert(NoteDbHelper.TABLE_NOTES, null, valeurs);

        db.close();
        return idGenere;
    }


    // 2. LIRE toutes les notes

    public List<Note> getAllNotes() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Note> listNotes = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + NoteDbHelper.TABLE_NOTES, null);


        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_TITRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_CONTENU)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_COULEUR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_FAVORI)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_DATE))
                );
                listNotes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listNotes;
    }


    // 3. LIRE uniquement les notes favorites

    public List<Note> getFavoriteNotes() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Note> listFavoris = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + NoteDbHelper.TABLE_NOTES +
                        " WHERE " + NoteDbHelper.COL_FAVORI + " = 1", null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_TITRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_CONTENU)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_COULEUR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_FAVORI)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_DATE))
                );
                listFavoris.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listFavoris;
    }



    // 4. RECHERCHER des notes par titre


    public List<Note> searchByTitle(String query) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Note> resultats = new ArrayList<>();


        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + NoteDbHelper.TABLE_NOTES +
                        " WHERE " + NoteDbHelper.COL_TITRE + " LIKE ?",
                new String[]{"%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_TITRE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_CONTENU)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_COULEUR)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_FAVORI)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COL_DATE))
                );
                resultats.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return resultats;
    }



    // 5. MODIFIER une note existante

    public void updateNote(Note note) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valeurs = new ContentValues();
        valeurs.put(NoteDbHelper.COL_TITRE,   note.getTitre());
        valeurs.put(NoteDbHelper.COL_CONTENU, note.getContenu());
        valeurs.put(NoteDbHelper.COL_COULEUR, note.getCouleur());
        valeurs.put(NoteDbHelper.COL_FAVORI,  note.isFavori() ? 1 : 0);
        valeurs.put(NoteDbHelper.COL_DATE,    note.getDate());


        db.update(NoteDbHelper.TABLE_NOTES, valeurs,
                NoteDbHelper.COL_ID + " = ?",
                new String[]{String.valueOf(note.getId())});

        db.close();
    }



    // 6. CHANGER le statut favori d'une note


    public void updateFavorite(int id, boolean isFavorite) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valeurs = new ContentValues();
        valeurs.put(NoteDbHelper.COL_FAVORI, isFavorite ? 1 : 0);

        db.update(NoteDbHelper.TABLE_NOTES, valeurs,
                NoteDbHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
    }


    // 7. SUPPRIMER une note (bonus)


    public void deleteNote(int id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // "WHERE id = ?" → on supprime uniquement la note avec cet id
        db.delete(NoteDbHelper.TABLE_NOTES,
                NoteDbHelper.COL_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
    }
}