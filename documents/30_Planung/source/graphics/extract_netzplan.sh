#!/bin/bash
set -e
pdfcrop -m "0" netzplan.pdf netzplan_cropped.pdf
pdftk A=netzplan_cropped.pdf cat A1 output netzplan_s1.pdf
pdftk A=netzplan_cropped.pdf cat A2 output netzplan_s2.pdf
rm -f netzplan_cropped.pdf
