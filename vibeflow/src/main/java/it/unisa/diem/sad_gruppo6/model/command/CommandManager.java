package it.unisa.diem.sad_gruppo6.model.command;
import java.util.Stack;
/**
 * @file AppCommand.java
 * 
 * Classe Singleton responsabile della gestione dei comandi nell'applicazione.  
 * Consente di eseguire comandi e di tenere traccia della loro esecuzione.
 * Utilizza il pattern Command per incapsulare azioni che possono essere eseguite e mantenute in una cronologia. 
 * In quanto Singleton, garantisce che esista un'unica istanza condivisa tra i componenti dell'applicazione. 
 * 
 * @pattern Command, Singleton
 * 
 * @author EmanuelChirico
 */
public class CommandManager 
{
    private static CommandManager instance;
    private Stack<AppCommand> history;

    /**
     * Costruttore privato: impedisce l'instanziazione diretta dall'esterno, garantendo il rispetto del pattern Singleton.
     * 
     * @param command il comando da eseguire.
     */

    private CommandManager() 
    {
        history = new Stack<>();
    }

    /**
     * Restituisce l'unica istanza del CommandManager. Se l'istnza non eiste ancora, viene creata al primo accesso.
     * 
     * @return L'istanza singleton di CommandManager.
     */
    public static CommandManager getInstance(){
        if (instance == null){
            instance = new CommandManager();
        }
        return instance;
    }

    /**
     * Segue un comando e lo aggiunge alla cronologia dei comandi eseguiti.
     * 
     * @param command il comando da eseguire.
     */
    
    public void execute(AppCommand command) 
    {
        command.execute();
        history.push(command);
    }

    /**
     * Annulla l'ultimo comando eseguito estraendolo dallo stack della cronologia. Se la cronologia è vuota, il metodo non compie alcuna azione.
     */
    public void undo(){
        if(!history.isEmpty()){
            AppCommand command = history.pop();
            command.undo();
        }
    }
}
