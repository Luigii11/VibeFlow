/**
 * @file TrackLibrary.java
 * Classe di definizione di un oggetto di tipo 'TrackLibrary', collezione di oggetti di tipo 'Track'.
 * 
 * @author EmanuelChirico
 * @author EmanuelaGraziuso
 */

package it.unisa.diem.sad_gruppo6.model.library;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import it.unisa.diem.sad_gruppo6.model.domain.Track;

public class TrackLibrary 
{

    private static TrackLibrary instance;
    private LinkedHashSet<Track> tracks;
    private List<TrackLibraryObserver> observers;

    private TrackLibrary() 
    {
        tracks = new LinkedHashSet<>();
        observers = new ArrayList<>();
    }

    /**
      * Metodo per ottenere l'istanza singleton di TrackLibrary.
      * Se l'istanza non esiste, viene creata una nuova istanza.
      * 
      * @pattern Singleton
      * @return L'istanza singleton di TrackLibrary.
      * 
     */

    public static TrackLibrary getInstance() 
    {
        if (instance == null) 
        {
            instance = new TrackLibrary();
        }
        return instance;
    }

    /**
      * Metodo per aggiungere una traccia alla libreria.
      * 
      * @param track La traccia da aggiungere alla libreria.
     */
    public void addTrack(Track track)

    {
        if (tracks.contains(track))
        {
            throw new IllegalArgumentException("Impossibile aggiungere la traccia: una traccia con lo stesso titolo e autore è già presente in libreria!");
        }
        else
        {
            tracks.add(track);
            notifyTrackAdded(track);
            notifyObserver();
        }
        return;

    }

     /**
      * Metodo per registrare un osservatore alla libreria.
      * 
      * @param Track TrackLibraryObserver da registrare alla libreria.
     */
    private void notifyTrackAdded(Track track) 
    {
        for (TrackLibraryObserver observer : observers) 
        {
            observer.onTrackAdded(track);
        }
    }

    /**
     * Restituisce la lista di tutte le tracce presenti nella libreria.

    * @return Lista tracce presenti nella libreria.
     */

    public List<Track> getTracks() 
    {
        return new ArrayList<>(tracks);
    }

    /**
     * Registra un nuovo observer che verrà notificato ad ogni modifica della libreria
     * 
     * @param o: L'observer da registrare.
     */
    public void registerObserver(TrackLibraryObserver o)
    {
        if(o!= null && !observers.contains(o)){
            observers.add(o);
        }
    }

    /**
     * Rimuove un observer precedentemente registrato.
     * 
     * @param o: L'observer da rimuovere.
     */
    public void removeObserver(TrackLibraryObserver o)
    {
        observers.remove(o);
    }

    /**
     * Notifica a tutti gli observer registrati che la libreria è stata modificata, in modo che possano 
     * aggiornare le loro viste di conseguenza.
     * 
     */
    private void notifyObserver()
    {
    for (TrackLibraryObserver observer : observers) 
            {
                observer.onLibraryChanged();
            }
    }
    /**
     * Aggiorna i metadati di una traccia esistente sostituendo {@code oldTrack}
     * con {@code updatedTrack}, preservando l'ordine di inserimento.
     * Poiché {@link Track#equals(Object)} si basa su titolo e autore, la
     * sostituzione ricostruisce il LinkedHashSet per mantenere la posizione originale.
     *
     * @param oldTrack     la traccia originale già presente in libreria.
     * @param updatedTrack la traccia con i nuovi metadati da sostituire.
     * @throws IllegalArgumentException se {@code oldTrack} non è presente in libreria.
     */
    public void updateTrack(Track oldTrack, Track updatedTrack)
    {
        if (oldTrack == null || !tracks.contains(oldTrack))
        {
            throw new IllegalArgumentException("La traccia da aggiornare non è presente in libreria.");
        }
        LinkedHashSet<Track> rebuilt = new LinkedHashSet<>();
        for (Track t : tracks)
        {
            rebuilt.add(t.equals(oldTrack) ? updatedTrack : t);
        }
        tracks = rebuilt;
        notifyTrackAdded(updatedTrack); 
    }
    /**
     * Rimuove una traccia dalla libreria e notifica tutti gli observer registrati.
     *
     * @param track la traccia da rimuovere.
     * @throws IllegalArgumentException se {@code track} è null o non presente in libreria.
     */
    public void removeTrack(Track track)
    {
        if (track == null || !tracks.contains(track))
        {
            throw new IllegalArgumentException("La traccia da rimuovere non è presente in libreria.");
        }
        tracks.remove(track);
        notifyObserver();
    }


    /**
     * Reinserisce una traccia nella posizione originale specificata (usato da undo).
     * Poiché la struttura interna è un LinkedHashSet, ricostruisce l'intero set
     * inserendo la traccia all'indice desiderato e mantenendo l'ordine degli altri elementi.
     *
     * @param track la traccia da reinserire.
     * @param index la posizione originale in cui reinserire la traccia.
     */
    public void addTrackAtIndex(Track track, int index)
    {
        List<Track> list = new ArrayList<>(tracks);
        int safeIndex = Math.max(0, Math.min(index, list.size()));
        list.add(safeIndex, track);
        tracks = new LinkedHashSet<>(list);
        notifyTrackAdded(track);
        notifyObserver();
    }

    /**
     * @brief Notifica tutti gli observer che una traccia ha subito una modifica "leggera"
     * (es. cambio dei tag) senza alterare l'ordine o l'identità della traccia in libreria.
     * @details Usato da TrackController.addTag()/removeTag() per propagare
     * l'aggiornamento dello stato del TagSet alla UI tramite notifyObserver(),
     * in linea con il requisito "lo stato dell'icona si aggiorna istantaneamente".
     *
     * @param track La traccia il cui stato è stato aggiornato.
     */
    public void notifyTrackUpdated(Track track)
    {
        notifyObserver();
    }
  
}
