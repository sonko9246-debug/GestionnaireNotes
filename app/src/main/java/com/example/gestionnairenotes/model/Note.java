package com.example.gestionnairenotes.model;

public class Note {

    // --- Attributs ---

    private int id;
    private String titre;
    private String contenu;
    private String couleur;
    private boolean favori;
    private String date;

    // --- Constructeur complet (utilisé quand on lit une note depuis la BDD) ---

    public Note(int id, String titre, String contenu, String couleur, boolean favori, String date) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.couleur = couleur;
        this.favori = favori;
        this.date = date;
    }


    // --- Constructeur sans id (utilisé quand on CRÉE une nouvelle note) ---
    // L'id sera attribué automatiquement

    public Note(String titre, String contenu, String couleur, boolean favori, String date) {
        this.titre = titre;
        this.contenu = contenu;
        this.couleur = couleur;
        this.favori = favori;
        this.date = date;
    }


    // --- Getters (lire les valeurs) ---

    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getContenu() { return contenu; }
    public String getCouleur() { return couleur; }
    public boolean isFavori() { return favori; }
    public String getDate() { return date; }


    // --- Setters (modifier les valeurs) ---

    public void setId(int id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public void setFavori(boolean favori) { this.favori = favori; }
    public void setDate(String date) { this.date = date; }


    // --- toString (utile pour déboguer dans les logs) ---

    @Override
    public String toString() {
        return "Note{id=" + id + ", titre='" + titre + "', favori=" + favori + ", date=" + date + "}";
    }
}
