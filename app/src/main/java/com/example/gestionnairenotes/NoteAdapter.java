package com.example.gestionnairenotes;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionnairenotes.database.NoteDao;
import com.example.gestionnairenotes.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> listNotes;
    private NoteDao noteDao;
    private Context context;

    public NoteAdapter(Context context, List<Note> listNotes, NoteDao noteDao) {
        this.context = context;
        this.listNotes = listNotes;
        this.noteDao = noteDao;
    }

    // Mettre à jour la liste affichée (utilisé pour le filtre favoris)
    public void updateListe(List<Note> nouvelleListe) {
        this.listNotes = nouvelleListe;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = listNotes.get(position);

        // Afficher titre et date
        holder.tvTitre.setText(note.getTitre());
        holder.tvDate.setText(note.getDate());

        // Couleur de fond de la carte
        try {
            holder.itemView.setBackgroundColor(Color.parseColor(note.getCouleur()));
        } catch (Exception e) {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // ⭐ Icône étoile selon l'état favori
        if (note.isFavori()) {
            holder.tvEtoile.setText("★"); // étoile pleine
            holder.tvEtoile.setTextColor(Color.parseColor("#F2C94C")); // jaune
        } else {
            holder.tvEtoile.setText("☆"); // étoile vide
            holder.tvEtoile.setTextColor(Color.GRAY);
        }

        // Double-clic → basculer favori
        GestureDetector gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        int pos = holder.getAdapterPosition();
                        if (pos == RecyclerView.NO_ID) return false;

                        Note noteCliquee = listNotes.get(pos);
                        boolean nouveauStatut = !noteCliquee.isFavori();

                        // Mise à jour en BDD
                        noteDao.updateFavorite(noteCliquee.getId(), nouveauStatut);

                        // Mise à jour en mémoire
                        noteCliquee.setFavori(nouveauStatut);

                        // Rafraîchir uniquement cette carte
                        notifyItemChanged(pos);
                        return true;
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        // M3 branchera la navigation ici
                        return true;
                    }
                });

        holder.itemView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    // ViewHolder
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvDate, tvEtoile;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre  = itemView.findViewById(R.id.tvTitreNote);
            tvDate   = itemView.findViewById(R.id.tvDateNote);
            tvEtoile = itemView.findViewById(R.id.tvEtoile);
        }
    }
}