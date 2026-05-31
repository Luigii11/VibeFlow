/**
 * @file PlaylistLibraryObserver.java
 * Interfaccia per l'implementazione del pattern Observer sulla libreria delle playlist.
 * Le classi (solitamente i Controller delle Viste) che implementano questa interfaccia 
 * verranno notificate in caso di variazioni dello stato della 'PlaylistLibrary'.
 * * @author LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.models;

public interface PlaylistLibraryObserver {
    
    /**
     * Metodo di callback invocato dal Subject (PlaylistLibrary) per notificare 
     * un cambiamento (es. aggiunta o rimozione di una playlist).
     * Chi implementa questo metodo deve inserirvi la logica per aggiornare 
     * l'interfaccia grafica in modo da riflettere i nuovi dati.
     */
    void onPlaylistLibraryChanged();
}