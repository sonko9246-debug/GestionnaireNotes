package com.example.gestionnairenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestionnairenotes.adapter.NoteAdapter;
import com.example.gestionnairenotes.database.NoteDao;
import com.example.gestionnairenotes.model.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnFavoriteClickListener {

    // --- Vues ---
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private SearchView searchView;
    private ImageButton buttonFavoritesFilter;
    private LinearLayout emptyStateView;
    private LinearLayout colorPalette;
    private FloatingActionButton fabAddNote;

    // --- Données ---
    private NoteDao noteDao;
    private List<Note> allNotes = new ArrayList<>();
    private List<Note> displayedNotes = new ArrayList<>();
    private boolean isFilteringFavorites = false;
    private String selectedColor = "#FF219653"; // Vert par défaut

    // ========== CYCLE DE VIE ==========

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation BDD (Membre 1)
        noteDao = new NoteDao(this);

        // Liaison des vues
        recyclerView = findViewById(R.id.recyclerViewNotes);
        searchView = findViewById(R.id.searchView);
        buttonFavoritesFilter = findViewById(R.id.button_favorites_filter);
        emptyStateView = findViewById(R.id.empty_state_view);
        fabAddNote = findViewById(R.id.fab_add_note);
        colorPalette = findViewById(R.id.color_palette);

        // Configuration
        setupRecyclerView();
        setupColorPalette();
        setupSearchView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    // ========== CONFIGURATION ==========

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this, displayedNotes, this);
        recyclerView.setAdapter(noteAdapter);
    }

    private void setupListeners() {
        // FAB : ouvrir/fermer la palette
        fabAddNote.setOnClickListener(v -> toggleColorPalette());

        // Bouton Favoris : filtrer
        buttonFavoritesFilter.setOnClickListener(v -> toggleFavoritesFilter());
    }

    // ========== CHARGEMENT DES NOTES (Membre 1) ==========

    private void loadNotes() {
        allNotes = noteDao.getAllNotes();
        applyFilters();
    }

    // ========== PALETTE DE COULEURS ==========

    private void toggleColorPalette() {
        if (colorPalette.getVisibility() == View.VISIBLE) {
            colorPalette.setVisibility(View.INVISIBLE);
        } else {
            colorPalette.setVisibility(View.VISIBLE);
        }
    }

    private void setupColorPalette() {
        // Tableaux parallèles : ressource de couleur + code hexadécimal
        int[] colorResources = {
                R.color.green, R.color.red, R.color.blue,
                R.color.yellow, R.color.orange, R.color.gray, R.color.black
        };
        String[] hexColors = {
                "#FF219653", "#FFEB5757", "#FF2F80ED",
                "#FFF2C94C", "#FFF2994A", "#FF828282", "#FF000000"
        };

        for (int i = 0; i < colorResources.length; i++) {
            final String hexColor = hexColors[i];
            View colorCircle = new View(this);
            int size = (int) (48 * getResources().getDisplayMetrics().density); // 48dp en pixels
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 8, 8, 8);
            colorCircle.setLayoutParams(params);
            colorCircle.setBackgroundColor(ContextCompat.getColor(this, colorResources[i]));
            colorCircle.setClickable(true);
            colorCircle.setFocusable(true);

            // Clic sur une couleur → ouvrir l'écran de création (Membre 3)
            colorCircle.setOnClickListener(v -> {
                selectedColor = hexColor;
                Intent intent = new Intent(MainActivity.this, NoteFormActivity.class);
                intent.putExtra("note_color", selectedColor);
                startActivity(intent);
                colorPalette.setVisibility(View.INVISIBLE);
            });

            colorPalette.addView(colorCircle);
        }
    }

    // ========== RECHERCHE (Zone Membre 5 pour optimisation) ==========

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return false;
            }
        });
    }

    // ========== FILTRE FAVORIS (Membre 4) ==========

    private void toggleFavoritesFilter() {
        isFilteringFavorites = !isFilteringFavorites;

        // Changement visuel du bouton
        if (isFilteringFavorites) {
            buttonFavoritesFilter.setColorFilter(ContextCompat.getColor(this, R.color.yellow));
        } else {
            buttonFavoritesFilter.setColorFilter(ContextCompat.getColor(this, R.color.gray));
        }

        applyFilters();
    }

    // ========== APPLICATION DES FILTRES (Recherche + Favoris) ==========

    private void applyFilters() {
        String query = searchView.getQuery().toString().toLowerCase().trim();
        List<Note> filteredList = new ArrayList<>();

        for (Note note : allNotes) {
            // Filtre recherche : le titre contient la query (ou query vide = tout)
            boolean matchesQuery = query.isEmpty() || note.getTitre().toLowerCase().contains(query);

            // Filtre favoris : si actif, seules les favorites
            boolean matchesFavFilter = !isFilteringFavorites || note.isFavori();

            if (matchesQuery && matchesFavFilter) {
                filteredList.add(note);
            }
        }

        // Mise à jour de l'adapter
        displayedNotes.clear();
        displayedNotes.addAll(filteredList);
        noteAdapter.notifyDataSetChanged();

        // Gestion état vide / liste
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (displayedNotes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);
        }
    }

    // ========== GESTION DOUBLE-CLIC FAVORI (Interface pour Membre 4) ==========

    /**
     * Appelée par NoteAdapter lors d'un double-clic sur une note.
     * Membre 4 : le GestureDetector est dans NoteAdapter.java
     */
    @Override
    public void onFavoriteClick(Note note, int position) {
        // Inverser le statut favori
        note.setFavori(!note.isFavori());

        // Mettre à jour en base (Membre 1)
        noteDao.updateFavorite(note.getId(), note.isFavori());

        // Rafraîchir l'affichage
        applyFilters();
    }
}