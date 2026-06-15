/**
 * @file Tag.java
 * Enumerazione dei tag visivi assegnabili a una traccia musicale.
 * Ogni tag dichiara se è gestibile manualmente dall'utente oppure se è
 * riservato al sistema (assegnato automaticamente e non modificabile).
 * Questo incapsula nel dominio l'invariante "i tag di sistema non sono
 * modificabili dall'utente", evitando di delegare il controllo a classi esterne.
 *
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.domain;

public enum Tag
{
    /** Traccia preferita, assegnabile/rimovibile manualmente dall'utente. */
    Favourite(false),

    /** Traccia con contenuto esplicito, assegnata automaticamente dal sistema. */
    Explicit(true),

    /** Traccia pubblicata nell'anno corrente, assegnata automaticamente dal sistema. */
    NewRelease(true);

    private final boolean systemAssigned;

    Tag(boolean systemAssigned)
    {
        this.systemAssigned = systemAssigned;
    }

    /**
     * Indica se il tag è riservato al sistema e quindi non modificabile dall'utente.
     *
     * @return true se il tag è assegnato automaticamente dal sistema, false se è gestibile dall'utente.
     */
    public boolean isSystemAssigned()
    {
        return systemAssigned;
    }
}