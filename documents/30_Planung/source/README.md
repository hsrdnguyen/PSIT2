# Erstellen des Dokumentes

## Benötigte Software

**Windows**
* [MiKTeX Installer (32 bzw. 64 bit)](http://www.miktex.org/download)

**Debian (Ubuntu)**
* texlive-latex-extra
* pdflatex

```bash
sudo apt-get install texlive-latex-extra pdflatex
```

## Erzeugen des PDFs
**Windows**

*Bemerke*: Bei erstmaligem Kompilieren wird ein Fenster geöffnet, das um Packete zu installieren.
Es sollte **für Benutzer** ausgewählt werden und **Checkbox enfernen**, dass der Dialog jedes mal angezeigt wird.

  1. TeXworks starten
  2. "Haupt"-Dokument (`document.tex`, `pflichtenheft.tex`, o.Ä.) öffnen
  3. Typeset drücken oder <kbd>Ctr + T</kbd>

**Debian (Ubuntu)**
  
  1. ``make``
  2. ????
  3. PROFIT!!

## LaTeX bearbeiten
### Dokumentation
  * Excellentes Wiki-Buch (Tutorial & Nachschlagen): https://en.wikibooks.org/wiki/LaTeX
  * Für weitere Probleme: http://gidf.de
