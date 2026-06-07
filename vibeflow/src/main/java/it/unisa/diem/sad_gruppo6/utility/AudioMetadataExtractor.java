package it.unisa.diem.sad_gruppo6.utility;

import java.io.File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

public class AudioMetadataExtractor {

    public static int extractDuration(String path) {
        try {
            File audio = new File(path);
            AudioFile audioFile = AudioFileIO.read(audio);

            return audioFile.getAudioHeader().getTrackLength();

        } catch (Exception e) {
            System.err.println("Errore durante l'estrazione dei metadati: " + e.getMessage());
            return -1;
        }
    }
}