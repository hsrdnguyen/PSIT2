.PHONY: clean all
all: clean
	pdflatex pflichtenheft
	makeglossaries pflichtenheft
	biber pflichtenheft
	pdflatex pflichtenheft
	pdflatex pflichtenheft



clean:
	rm -f *.aux *.toc *.xdy *.ilg *.log *.gls *.glsdefs *.blg *.bbl *.out *.ist *.glo *.run.xml *.bcf *.glg

