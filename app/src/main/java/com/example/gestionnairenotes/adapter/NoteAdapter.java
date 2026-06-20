package com.example.gestionnairenotes.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestionnairenotes.NoteFormActivity;
import com.example.gestionnairenotes.R;
import com.example.gestionnairenotes.model.Note;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> notes;
    private OnFavoriteClickListener favoriteListener;

    // Interface pour communiquer avec MainActivity
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Note note, int position);
    }

    public NoteAdapter(Context context, List<Note> notes, OnFavoriteClickListener listener) {
        this.context = context;
        this.notes = notes;
        this.favoriteListener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note, position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    // ========== VIEW HOLDER ==========

    class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleTextView, dateTextView;
        ImageView favoriteIcon;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            titleTextView = itemView.findViewById(R.id.text_note_title);
            dateTextView = itemView.findViewById(R.id.text_note_date);
            favoriteIcon = itemView.findViewById(R.id.image_favorite);
        }

        void bind(Note note, int position) {
            // Titre
            titleTextView.setText(note.getTitre());

            // Date
            dateTextView.setText(note.getDate());

            // Couleur de fond de la carte
            int colorInt = getColorFromHex(note.getCouleur());
            cardView.setCardBackgroundColor(colorInt);

            // Icône favori
            if (note.isFavori()) {
                favoriteIcon.setColorFilter(ContextCompat.getColor(context, R.color.star_favorite));
            } else {
                favoriteIcon.setColorFilter(ContextCompat.getColor(context, R.color.star_not_favorite));
            }

            // ========== GESTION DES CLICS ==========

            // Clic simple → ouvrir modification (Membre 3)
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, NoteFormActivity.class);
                intent.putExtra("note_id", note.getId());
                context.startActivity(intent);
            });

            // Double-clic → basculer favori (Membre 4)
            // Zone gérée par Membre 4 avec GestureDetector
            itemView.setOnTouchListener(new View.OnTouchListener() {
                                            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                                                @Override
                                                public boolean onDown(MotionEvent e) {
                                                    return true;
                                                }

                                                @Override
                                                public boolean onDoubleTap(MotionEvent e) {
                                                    if (favoriteListener != null) {
                                                        favoriteListener.onFavoriteClick(note, position);
                                                    }
                                                    return true;
                                                }
                                            });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });

        }

        /**
         * Convertit un code couleur hexadécimal (#FF219653) en int utilisable par Android.
         */
        private int getColorFromHex(String hexColor) {
            try {
                return android.graphics.Color.parseColor(hexColor);
            } catch (Exception e) {
                return ContextCompat.getColor(context, R.color.green); // Couleur par défaut
            }
        }
    }
}