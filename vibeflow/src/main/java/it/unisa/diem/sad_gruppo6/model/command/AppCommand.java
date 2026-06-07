/**
 * @file AppCommand.java
 * 
 * Interfaccia per la definizione di un comando nell'applicazione. 
 * Utilizza il pattern Command per incapsulare azioni che possono essere eseguite
 * 
 * @pattern Command
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.model.command;

public interface AppCommand 
{
    void execute(); 
    void undo(); 
}
