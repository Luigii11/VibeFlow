/**
 * @file PlaylistLibrary.java
 * Classe che gestisce la libreria delle playlist dell'utente.
 * Fungere da 'Subject' nel pattern Observer per notificare le viste dei cambiamenti.
 * * @author LuigiAutorino
 */
package it.unisa.diem.sad_gruppo6.models;

import java.util.List;
import java.util.ArrayList;

public class PlaylistLibrary {

    // Attributi
    private static PlaylistLibrary instance;
    private List<Playlist> playlists;
    private List<PlaylistLibraryObserver> observers;

    /**
     * Costruttore classe 'PlaylistLibrary'.
     * Inizializza la lista vuota delle playlist e la lista degli observer.
     */
    private PlaylistLibrary() {
        this.playlists = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    /**
     * @pattern Singleton
     * @return L'istanza unica e globale di PlaylistLibrary.
     */
    public static PlaylistLibrary getInstance() {
        if (instance == null) {
            instance = new PlaylistLibrary();
        }
        return instance;
    }

    // Metodi CRUD

    /**
     * Aggiunge una nuova playlist alla libreria e notifica gli observer del cambiamento.
     * * @param p La playlist da aggiungere.
     */
    public void addPlaylist(Playlist p) {
        if (p != null) {
            playlists.add(p);
            notifyObservers();
        }
    }

    /**
     * Rimuove una playlist dalla libreria e notifica gli observer del cambiamento.
     * * @param p La playlist da rimuovere.
     */
    public void removePlaylist(Playlist p) {
        if (p != null) {
            playlists.remove(p);
            notifyObservers();
        }
    }

    /**
     * Aggiorna lo stato di una playlist esistente all'interno della libreria
     * e notifica gli observer.
     * * @param p La playlist da aggiornare.
     */
    public void updatePlaylist(Playlist p) {
        // Da implementare
        notifyObservers();
    }

    /**
     * Getter della collezione di playlist.
     * * @return La lista contenente tutte le playlist memorizzate.
     */
    public List<Playlist> getPlaylists() {
        return playlists;
    }
    
    /**
     * Controlla se esiste già una playlist con il nome specificato, 
     * ignorando le differenze tra maiuscole/minuscole e gli spazi vuoti.
     * * @param name Il nome della playlist da cercare.
     * @return true se esiste già una playlist con questo nome, false altrimenti.
     */
    public boolean containsPlaylistName(String name) {
        if (name == null) return false;
        
        for (Playlist p : playlists) {
            if (p.getName().equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Svuota la libreria per resettare lo stato tra un test e l'altro.
     */
    public void clear() {
        playlists.clear(); 
    }

    // Gestione Observer

    /**
     * Registra un nuovo observer che rimarrà in ascolto dei cambiamenti della libreria.
     * * @param o L'observer (es. una Vista) da aggiungere.
     */
    public void registerObserver(PlaylistLibraryObserver o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    /**
     * Rimuove un observer precedentemente registrato.
     * * @param o L'observer da rimuovere.
     */
    public void removeObserver(PlaylistLibraryObserver o) {
        observers.remove(o);
    }

    /**
     * Notifica tutti gli observer registrati che è avvenuto un cambiamento 
     * (aggiunta, rimozione o aggiornamento di una playlist), scatenando l'aggiornamento della UI.
     */
    private void notifyObservers() {
        for (PlaylistLibraryObserver observer : observers) {
            observer.onPlaylistLibraryChanged();
        }
    }

}

