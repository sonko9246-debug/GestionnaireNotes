package com.example.gestionnairenotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.gestionnairenotes.database.NoteDao;
import com.example.gestionnairenotes.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activité unique pour la CRÉATION et la MODIFICATION d'une note.
 * - Mode création : lancée depuis le FAB de MainActivity avec une couleur choisie.
 * - Mode modification : lancée au clic sur une note existante, champs pré-remplis.
 */
public class NoteFormActivity extends AppCompatActivity {

    // Clés des extras reçus via Intent
    public static final String EXTRA_COULEUR  = "note_color"; // couleur choisie dans la palette
    public static final String EXTRA_NOTE_ID  = "note_id";   // id de la note à modifier (-1 si création)

    // Vues
    private ConstraintLayout layoutFormulaire;
    private EditText editTitre;
    private EditText editContenu;
    private Button   btnEnregistrer;

    // Données
    private NoteDao noteDao;
    private String  couleurChoisie = "#FFFFFF"; // couleur par défaut
    private int     noteId         = -1;         // -1 = mode création
    private Note    noteExistante  = null;        // note à modifier (si mode modification)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_form);

        // Initialisation des vues
        layoutFormulaire = findViewById(R.id.layoutFormulaire);
        editTitre        = findViewById(R.id.editTitre);
        editContenu      = findViewById(R.id.editContenu);
        btnEnregistrer   = findViewById(R.id.btnEnregistrer);

        // Initialisation du DAO
        noteDao = new NoteDao(this);

        // Récupérer les données passées par Intent
        Intent intent = getIntent();
        if (intent != null) {
            couleurChoisie = intent.getStringExtra(EXTRA_COULEUR);
            if (couleurChoisie == null) couleurChoisie = "#FFFFFF";

            noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1);
        }

        // Appliquer la couleur de fond au formulaire
        appliquerCouleur(couleurChoisie);

        // MODE MODIFICATION : pré-remplir les champs si on modifie une note existante
        if (noteId != -1) {
            chargerNoteExistante(noteId);
            btnEnregistrer.setText("Modifier");
        } else {
            // MODE CRÉATION
            btnEnregistrer.setText("Créer");
        }

        // Clic sur le bouton Créer / Modifier
        btnEnregistrer.setOnClickListener(v -> enregistrerNote());
    }

    /**
     * Applique la couleur choisie comme couleur de fond du formulaire.
     */
    private void appliquerCouleur(String couleur) {
        try {
            layoutFormulaire.setBackgroundColor(Color.parseColor(couleur));
        } catch (IllegalArgumentException e) {
            // Couleur invalide → fond blanc par défaut
            layoutFormulaire.setBackgroundColor(Color.WHITE);
        }
    }

    /**
     * Charge la note depuis la BDD et pré-remplit les champs (mode modification).
     */
    private void chargerNoteExistante(int id) {
        // Recherche de la note dans la liste complète par son id
        for (Note note : noteDao.getAllNotes()) {
            if (note.getId() == id) {
                noteExistante = note;
                break;
            }
        }

        if (noteExistante != null) {
            editTitre.setText(noteExistante.getTitre());
            editContenu.setText(noteExistante.getContenu());
            // Appliquer la couleur de la note existante (peut être différente de l'intent)
            couleurChoisie = noteExistante.getCouleur();
            appliquerCouleur(couleurChoisie);
        }
    }

    /**
     * Valide les champs et enregistre la note (insertion ou mise à jour).
     */
    private void enregistrerNote() {
        String titre   = editTitre.getText().toString().trim();
        String contenu = editContenu.getText().toString().trim();

        // Validation : titre et contenu obligatoires
        if (TextUtils.isEmpty(titre)) {
            editTitre.setError("Le titre est obligatoire");
            editTitre.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contenu)) {
            editContenu.setError("Le contenu est obligatoire");
            editContenu.requestFocus();
            return;
        }

        // Date du jour formatée
        String dateAujourdhui = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                .format(new Date());

        if (noteId == -1) {
            // ── MODE CRÉATION ──
            Note nouvelleNote = new Note(titre, contenu, couleurChoisie, false, dateAujourdhui);
            noteDao.insertNote(nouvelleNote);
            Toast.makeText(this, "Note créée !", Toast.LENGTH_SHORT).show();

        } else {
            // ── MODE MODIFICATION ──
            noteExistante.setTitre(titre);
            noteExistante.setContenu(contenu);
            noteExistante.setCouleur(couleurChoisie);
            noteExistante.setDate(dateAujourdhui);
            noteDao.updateNote(noteExistante);
            Toast.makeText(this, "Note modifiée !", Toast.LENGTH_SHORT).show();
        }

        // Retourner à MainActivity
        finish();
    }
}
