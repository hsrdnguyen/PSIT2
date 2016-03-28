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

*Functions should do one thing. They should do it well. They should do it only.*

Eine Methode hat (oder sollte) immer einen aussagekräftigen Namen. Dies bringt aber nur dann etwas, wenn eine Methode auch macht was ihr Name ankündigt. Wenn sie aber etwas ganz anderes oder mehrere Dinge tut könnte der Name auch abcd sein und wir wüssten genau gleich viel über ihre Tätigkeit.

Wenn wir Methoden schreiben, müssen wir uns immer bewusst sein, was diese Funktion machen soll. Sobald wir uns nicht sicher sind oder einem mehrere Dinge in den Sinn kommen sollte man die Methode nochmals überdenken und gegebenenfalls in mehrere Methoden aufteilen.

Eine hilfreiche Technik ist, zu versuchen aus einer Methode eine weitere Methode zu extrahieren, die nicht das selbe tut, sondern nur einen Bruchteil der Funktionalität der ersten Methode hat.

### Use Intention-Revealing Names

*Choosing good names takes time but saves more than it takes.*

Beim vergeben von Namen für Klassen, Variabeln, Methoden usw. sollte man sich genügend Zeit nehmen und sich etwas dabei überlegen. So ein Name sollte alle grösseren Fragen klären, die beim lesen des Codes allenfalls auftretten. Man sollte intuitiv auf den Zweck des Elements kommen.
Der Name sollte einem sagen wieso diese Klasse, Variable oder Methode existiert, was sie macht und wie sie gebraucht wird.
Wenn ein Kommentar für den Namen benötigt wird, sollte man diesen nochmals überdenken.

Wer sich bei der Namensgebung Mühe gibt, macht es für andere zum lesen oder für sich selbst im Nachhinein um so vieles leichter den Code zu verstehen und man muss sich nicht noch durch Zeilen von Kommentaren lesen bzw. diese schreiben. Wodurch erheblich Zeit eingespaart werden kann.

Wenn einem beim nachträglichen lesen von Code noch bessere Namen einfallen, welche präziser sind, sollte man diese auch umbedingt noch ändern. Oder wenn man beim lesen von Code eines anderen Programmieres schlechte Namensgebung entdeckt, sollte man ihn darauf aufmerksam machen oder es gleich verbessern.
