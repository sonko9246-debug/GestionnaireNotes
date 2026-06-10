package com.example.gestionnairenotes;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionnairenotes.database.NoteDao;
import com.example.gestionnairenotes.model.Note;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotes;
    private NoteAdapter noteAdapter;
    private NoteDao noteDao;
    private Button btnFavoris;

    // true = on affiche uniquement les favoris
    private boolean affichageFavoris = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteDao = new NoteDao(this);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        btnFavoris = findViewById(R.id.btnFavoris);

        // Charger toutes les notes au démarrage
        List<Note> notes = noteDao.getAllNotes();
        noteAdapter = new NoteAdapter(this, notes, noteDao);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotes.setAdapter(noteAdapter);

        // Bouton Favoris — toggle
        btnFavoris.setOnClickListener(v -> filterFavorites());
    }

    // Méthode de filtre (ta contribution principale)
    private void filterFavorites() {
        if (affichageFavoris) {
            // Retour à toutes les notes
            noteAdapter.updateListe(noteDao.getAllNotes());
            btnFavoris.setText("☆ Favoris");
            affichageFavoris = false;
        } else {
            // Afficher uniquement les favoris
            noteAdapter.updateListe(noteDao.getFavoriteNotes());
            btnFavoris.setText("★ Favoris");
            affichageFavoris = true;
        }
    }

    // Rechargement quand on revient sur l'écran (après ajout/modif par M3)
    @Override
    protected void onResume() {
        super.onResume();
        if (!affichageFavoris) {
            noteAdapter.updateListe(noteDao.getAllNotes());
        }
    }
}