.PHONY: clean all
TARGET=planung

all: clean
	$(MAKE) $(TARGET)

$(TARGET):
	pdflatex $@
	makeglossaries $@
	biber $@
	pdflatex $@
	pdflatex $@



clean:
	rm -f *.aux *.toc *.xdy *.ilg *.log *.gls *.glsdefs *.blg *.bbl *.out *.ist *.glo *.run.xml *.bcf *.glg

