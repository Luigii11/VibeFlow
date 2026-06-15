/**
 * @file MostPlayedPlaylistCreator.java
 * @brief ConcreteCreator che genera una playlist automatica con le tracce più riprodotte.
 *
 * @details Filtra le tracce con almeno una riproduzione (playCount > 0) e le ordina
 * in ordine decrescente per numero di riproduzioni. Restituisce null se nessuna
 * traccia è ancora stata riprodotta.
 *
 * @pattern Factory Method
 *
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.model.factory;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MostPlayedPlaylistCreator extends PlaylistCreator {

    public static final String PLAYLIST_NAME = "Most Played";

    /**
     * @brief Crea una playlist con le tracce ordinate per numero di riproduzioni decrescente.
     *
     * @param tracks La lista completa delle tracce presenti nella TrackLibrary.
     * @return Una Playlist autogenerata "Most_played", oppure null se nessuna traccia
     *         è stata riprodotta almeno una volta.
     */
    @Override
    public Playlist createPlaylist(List<Track> tracks) {
        List<Track> mostPlayed = tracks.stream()
                .filter(t -> t.getPlayCount() > 0)
                .sorted(Comparator.comparingInt(Track::getPlayCount).reversed())
                .collect(Collectors.toList());

        if (mostPlayed.isEmpty()) {
            return null;
        }

        Playlist playlist = new Playlist(PLAYLIST_NAME, true);
        for (Track t : mostPlayed) {
            playlist.addTrack(t);
        }
        return playlist;
    }
}
