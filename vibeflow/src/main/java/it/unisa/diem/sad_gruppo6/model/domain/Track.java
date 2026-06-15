/**
 * @file Track.java
 * Classe di definizione di un oggetto di tipo 'Track'.
 * 
 * @author EmanuelChirico, ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.model.domain;
import java.time.LocalDate;
import java.util.Objects;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Track 
{
    // Attributi
    private String title;
    private String author;
    private int duration;
    private String genre;
    private int year; 
    private int playCount;
    private String path;
    private final TagSet tagSet;
    /**
     * Costruttore classe 'Track'.
     * 
     * @param title Il titolo della traccia.
     * @param author L'autore della traccia.
     * @param duration La durata della traccia in secondi.
     * @param genre Il genere musicale della traccia.
     * @param year L'anno di pubblicazione della traccia.  
     * @param playCount Il numero di volte che la traccia è stata riprodotta.
     * @param path Il percorso del file audio della traccia.
     */

    public Track(String title, String author, int duration, String genre, int year, String path) 
    {
        setTitle(title);
        setAuthor(author);
        setGenre(genre);
        setYear(year);
        setPath(path);
        setDuration(duration);
        this.playCount = 0;
        this.tagSet = new TagSet();
    }

    // Getter e Setter
    public String getTitle() 
    {
        return title;
    }

    /**
     * Setter del titolo della traccia, con controllo di validità sull'input.
     * 
     * @param title Il titolo da assegnare alla traccia.
     * @throws IllegalArgumentException Se il titolo è null o vuoto.
     */

    public void setTitle(String title) 
    {
        if (title == null || title.isBlank())
        {
            throw new IllegalArgumentException("Il titolo della traccia non può essere vuoto.");
        }
        this.title = title;
    }

    public String getAuthor() 
    {
        return author;
    }
    
    /**
     * Setter dell'autore della traccia, con controllo di validità sull'input.
     * 
     * @param author L'artista che ha composto la traccia.
     * @throws IllegalArgumentException Se la stringa dell'autore è null o vuoto.
     */

    public void setAuthor(String author) 
    {
        if (author == null || author.isBlank())
        {
            throw new IllegalArgumentException("L'autore della traccia non può essere vuoto.");
        }
        this.author = author;
    }

    public String getGenre() 
    {
        return genre;
    }

    /**
     * Setter del genere musicale della traccia, con controllo di validità sull'input.
     * 
     * @param genre Genere musicale della traccia.
     * @throws IllegalArgumentException Se la stringa del genere è null o vuoto.
     */

    public void setGenre(String genre)
    {
        if (genre == null || genre.isBlank())
        {
            throw new IllegalArgumentException("Il genere della traccia non può essere vuoto.");
        }
        this.genre = genre;
    }

    public int getDuration() 
    {
        return duration;
    }

    private void setDuration(int duration) 
    {
        if (duration <= 0) 
        {
            throw new IllegalArgumentException("La durata della traccia deve essere positiva.");
        }
        this.duration = duration;
    }

    public int getYear() 
    {
        return year;
    }

    /**
     * Setter dell'anno di pubblicazione della traccia, con controllo di validità sull'input.
     * 
     * @param year Anno di uscita della traccia.
     * @throws IllegalArgumentException Se l'anno di pubblicazione è inferiore al 1900 o superiore all'anno corrente.
     */

    public void setYear(int year) 
    {
        int currentYear = LocalDate.now().getYear();
        if (year < 1900 || year > currentYear) 
        {
            throw new IllegalArgumentException("L'anno di pubblicazione deve essere compreso tra 1900 e " + currentYear + ".");
        }
        this.year = year;
    }

    /**
     * Getter del numero di riproduzioni della traccia
     *
     * @return Il numero di volte che la traccia è stata riprodotta.
     */
    public int getPlayCount()
    {
        return playCount;
    }

    /**
     * Incrementa di uno il contatore di riproduzioni della traccia.
     */
    public void incrementPlayCount()
    {
        this.playCount++;
    }

    /**
     * Setter del percorso del file audio della traccia, con controllo di validità sull'input.
     * 
     */
    public void setPath (String path)
    {
        if (path == null || path.isBlank())
        {
            throw new IllegalArgumentException("Il percorso del file audio non può essere vuoto.");
        }
        if (!path.toLowerCase().endsWith(".mp3"))
        {
            throw new IllegalArgumentException("Il percorso del file audio deve terminare con .mp3");
        }

        Path filePath = Paths.get(path);

        if (Files.exists(filePath) == false)
        {
            throw new IllegalArgumentException("Il percorso del file audio non esiste.");
        }

        this.path = path;
    }

    /**
     * Getter del percorso del file audio della traccia, con controllo di validità sull'input.
     * 
     * @return Il percorso del file audio della traccia.
     */
    public String getPath(){
        return this.path;
    }

    /**
     * @brief Restituisce l'insieme dei tag visivi associati alla traccia.
     * @details Espone l'oggetto TagSet incapsulato, usato da TrackController
     * per assegnare/rimuovere tag manuali e tag di sistema.
     *
     * @return Il TagSet associato a questa traccia.
     */
    public TagSet getTagSet()
    {
        return tagSet;
    }

     /**
     * Metodo equals sovrascritto per confrontare due oggetti di tipo 'Track' basandosi su titolo e autore,
     * ignorando gli altri attributi come durata, genere, anno e playCount, in quanto due tracce con lo stesso 
     * titolo e autore possono essere considerate la stessa traccia anche se differiscono per altri attributi.
     *  
     * @return true se i titoli e gli autori delle tracce sono uguali, false altrimenti.
     */
    
    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Track other = (Track) obj;
        return Objects.equals(title, other.title) && Objects.equals(author, other.author);
    }

     /**
     * Metodo hashCode sovrascritto per generare un codice hash basato su titolo e autore, in coerenza con il metodo equals
     * 
     * @return L'hash generato a partire da titolo e autore della traccia.
     */
    @Override
    public int hashCode() 
    {
        return Objects.hash(title, author);
    }

    @Override
    public String toString() {
    return this.getTitle() + " - " + this.getAuthor();
    }
}
