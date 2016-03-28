# Projektschiene 15 - Gruppe 13
Gruppe 13 PSIT 2015/16 Repository (Avocado Share)

## Clean Code Regeln
Die folgenden Regeln sind beim Implementieren zu beachten und gelten
projektweit.

### The Boy Scout Rule

*Try and leave this world a little better than you found it [...].*

Angewandt auf das Schreiben von Code besagt diese Regel, man sollte immer
versuchen etwas zu verbessern.

Nicht nur ist es wichtig schönen Code zu schreiben, sondern es ist  auch
essentiell Diesen über längere Zeit sauber zu halten. Wenn jeder Entwickler
sich diese Regel zu Herzen nimmt, verbessert sich die Qualität des Quelltextes
kontinuerlich.

Jeder, der den Code bearbeitet sieht einen kleinen Fehler, eine komisch
benannte Variabel oder merkt, dass ein Test fehlt. Diese können meist einfach
behoben werden und somit wird der Quelltext immer besser und die Wartbarkeit und
Fehlerresistenz des Produktes nehmen zu.

### Do one thing

*Functions should do one thing. They should do it well. They should do it only. [...].*

Eine Methode hat (oder sollte) immer einen aussagekräftigen Namen. Dies bringt aber nur dann etwas, wenn eine Methode auch macht was ihr Name ankündigt. Wenn sie aber etwas ganz anderes oder mehrere Dinge tut könnte der Name auch abcd sein und wir wüssten genau gleich viel über ihre Tätigkeit.

Wenn wir Methoden schreiben, müssen wir uns immer bewusst sein, was diese Funktion machen soll. Sobald wir uns nicht sicher sind oder einem mehrere Dinge in den Sinn kommen sollte man die Methode nochmals überdenken und gegebenenfalls in mehrere Methoden aufteilen.

Eine hilfreiche Technik ist, zu versuchen aus einer Methode eine weitere Methode zu extrahieren, die nicht das selbe tut, sondern nur einen Bruchteil der Funktionalität der ersten Methode hat.
