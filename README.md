<h1 align="center">
  &nbsp;VibeFlow   <img src="./vibeflow/src/main/resources/images/VibeFlow.png" height="60"/>
</h1>

<p align="center"><em>Your favourites vibes</em></p>

<p align="center">
  <a href="https://openjdk.org/"><img src="https://img.shields.io/badge/Java-23.0.2-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java"/></a>
  <a href="https://openjfx.io/"><img src="https://img.shields.io/badge/JavaFX-23.0.2-2B6CB0?style=flat-square" alt="JavaFX"/></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.9.16-C71A36?style=flat-square&logo=apachemaven&logoColor=white" alt="Maven"/></a>
  <a href="https://junit.org/junit5/"><img src="https://img.shields.io/badge/JUnit-5-25A162?style=flat-square&logo=junit5&logoColor=white" alt="JUnit"/></a>
</p>

<p align="center">
  <a href="https://trello.com/invite/b/6a0c847752c06f9f38a7bff3/ATTI8da34fdd02d2f4e0d96f9113b6d94c2bD9D6596B/sadgruppo6">📋 Trello Board</a>
  &nbsp;·&nbsp;
  <a href="https://unisalerno-my.sharepoint.com/:x:/r/personal/l_autorino4_studenti_unisa_it/Documents/Product%20Backlog.xlsx?d=wbe106b33c7664a70b537ede3951f2907&csf=1&web=1&e=DdNkd3">📊 Artefatti</a>
  &nbsp;·&nbsp;
  <a href="docs/">📁 Documentazione</a>
</p>

<p align="center">Applicazione desktop per la gestione e la riproduzione di tracce musicali, sviluppata in <strong>Java 23</strong> con <strong>JavaFX</strong>.</p>

---

## Funzionalità

- **Libreria Musicale** — Gestione completa delle tracce con metadati (titolo, artista, genere, anno, tag)
- **Playlist Manuali** — Creazione e personalizzazione con riordinamento delle tracce
- **Playlist Automatiche** — Generazione per genere, anno, tag o tracce più ascoltate
- **Player Avanzato** — Riproduzione in modalità **Sequenziale**, **Shuffle** e **Loop**
- **Undo** — Cronologia completa delle azioni tramite Command Pattern

---

## Design Patterns

| Pattern  | Utilizzo nel progetto                               |
| -------- | --------------------------------------------------- |
| Command  | Undo/Redo per operazioni su libreria e playlist     |
| Observer | Aggiornamento reattivo di UI al cambio di stato     |
| State    | Gestione degli stati del player (Playing, Paused)   |
| Strategy | Modalità intercambiabili: Sequential, Shuffle, Loop |
| Iterator | Navigazione delle playlist nelle varie modalità     |
| Factory  | Generazione automatica di playlist per criterio     |

---

## Requisiti

- Java **23.0.2**
- Maven **3.9.16**
- JavaFX SDK *(gestito automaticamente tramite Maven)*

---

## Installazione e avvio

```bash
# Clona la repository
git clone https://github.com/Luigii11/SAD_Gruppo6.git
cd SAD_Gruppo6/vibeflow

# Compila il progetto
mvn clean install

# Avvia l'applicazione
mvn javafx:run
```

---

## Team — Gruppo 6

Università degli Studi di Salerno — DIEM

**Autori:** Autorino Luigi · Chirico Emanuel · Crisci Chiara · Graziuso Emanuela

**Docenti:** Andrea Apicella · Pierluigi Ritrovato

---

## Riferimenti

Nella cartella [`references/`](references/) è presente il file [`links.md`](references/links.md) con accesso a:

1. 📋 [Trello Board](https://trello.com/invite/b/6a0c847752c06f9f38a7bff3/ATTI8da34fdd02d2f4e0d96f9113b6d94c2bD9D6596B/sadgruppo6) — Gestione degli sprint e dei task
2. 📊 [Excel Artefatti](https://unisalerno-my.sharepoint.com/:x:/r/personal/l_autorino4_studenti_unisa_it/Documents/Product%20Backlog.xlsx?d=wbe106b33c7664a70b537ede3951f2907&csf=1&web=1&e=DdNkd3) — Product Backlog
3. 📁 [Documentazione PDF](docs/) — Sprint Review & Retrospective, Documento di Progetto
