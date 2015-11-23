\NeedsTeXFormat{LaTeX2e}
\@obsoletefile{article.cls}{article.sty}
\LoadClass[12pt, a4paper]{article}

\ProvidesClass{avocado}[05/10/2015 Template for Avocado Share Documents]

\RequirePackage{ae}
\RequirePackage{lscape}
\RequirePackage{todonotes}
\RequirePackage{multirow}
\RequirePackage{amsmath}
\RequirePackage{mathrsfs}
\RequirePackage{multicol}
\RequirePackage{lmodern}
\RequirePackage[ngerman]{babel}			% "Table of Contents" --> "Inhaltsverzeichnis"
\RequirePackage[utf8]{inputenc}
\RequirePackage[T1]{fontenc}				% Fontkodierung auf T1-Format
\RequirePackage{graphicx}				% Bilder
\RequirePackage{fancyhdr}				% Kopf- und Fusszeilen
\RequirePackage{lastpage}				% Anzahl gesamte Seiten
\RequirePackage[bookmarks=true]{hyperref}				% f\"ur Hyperlinks
\RequirePackage{array}					% f\"ur Tabellen (tabular Umgebung)
\RequirePackage{listings}				% Quellcodeausgabe
\RequirePackage{supertabular}			% f\"ur Tabellen \"uber mehrere Seiten
\RequirePackage{verbatim}	
\RequirePackage{xcolor}
%\RequirePackage{cite}


\RequirePackage[margin=2cm]{geometry}
\RequirePackage{xcolor}

%\setlength{\oddsidemargin}{0mm} % Adjust margins to center the colored title box
%\setlength{\evensidemargin}{0mm} % Margins on even pages - only necessary if adding more content to this template

\definecolor{avocadogreen}{cmyk}{0.36, 0, 0.52, 0.55}
\definecolor{avocadogreenlight}{HTML}{6B8C21}
\definecolor{avocadoflesh}{RGB}{221, 212, 143}
\definecolor{avocadolightflesh}{RGB}{226, 224, 176}
\definecolor{avocadolightbrown}{cmyk}{0, 0.18, 0.33, 0.20}
\definecolor{avocadobrown}{HTML}{704012}

\renewcommand{\arraystretch}{1.5}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Header/Footer
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
\newcommand{\fancyhdrplain}{
	\fancypagestyle{plain}{%
		\fancyhf{}

% Kopfzeile
		\fancyhead{}
		\renewcommand{\headrulewidth}{0pt}	% Trennlinie 
%		\fancyhead[L]{\nouppercase{\thechapter.\ \leftmark}}			% Kopfzeile links \includegraphics{images/fhnw_logo}
%		\fancyhead[R]{\nouppercase{\shorttitel}}	% Kopfzeile rechts
%		\renewcommand{\headwidth}{17cm} %Breite der Kopf- und Fusszeile (21cm-2*2cm)
%		\renewcommand{\headrulewidth}{0.5pt}	% Trennlinie Kopfzeile

%Fusszeile
		\fancyfoot[L]{\doctype}						% Fusszeile rechts
		\fancyfoot[R]{\shorttitel}		% Fusszeile links
		\cfoot{\thepage{}/\pageref{LastPage}}							% Fusszeile Mitte
%		\renewcommand{\footrulewidth}{0.5pt}	% Trennlinie Fusszeile
%		\footskip40pt									% Abstand Textkörper Fusszeile
	}
}
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Header/Footer
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
\fancypagestyle{plain}{%
	\fancyhf{}
	\fancyhead{}
	\fancyfoot{}
	\renewcommand{\headrulewidth}{0pt}	% Trennlinie 
	\renewcommand{\footrulewidth}{0pt}	% Trennlinie 
	\cfoot{\thepage{}}
	\fancyfoot[L]{\doctype}
	\fancyfoot[R]{\shorttitel}
}

\pagestyle{fancy}

\lhead{Pflichtenheft}
\chead{}
\rhead{\bfseries Avocado Share}
%\lfoot{Sascha Bergmann, Lion Kunz, Dang Thien Nguyen, Cyril Müller}
\lfoot{Gruppe 13: S. Bergmann, L. Kunz, D. T. Ngueyen, C. Müller}
\cfoot{}
\rfoot{}
\renewcommand{\headrulewidth}{0.4pt}
\renewcommand{\footrulewidth}{0.4pt}
