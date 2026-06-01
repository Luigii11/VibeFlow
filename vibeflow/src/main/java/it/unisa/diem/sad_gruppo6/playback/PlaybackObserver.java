/**
 * La classe 'PlaybackObserver' definisce l'interfaccia per gli osservatori che vogliono monitorare i cambiamenti di stato 
 * del player. Gli osservatori implementano il metodo 'update', che viene chiamato ogni volta che lo stato del player cambia,
 * ricevendo una copia dello stato attuale del player come parametro.
 * 
 * @pattern Observer
 * 
 * @author EmanuelChirico
 */


package it.unisa.diem.sad_gruppo6.playback;


public interface PlaybackObserver 
{
    void update(PlaybackState state);
}
