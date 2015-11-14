\NeedsTeXFormat{LaTeX2e}
\@obsoletefile{article.cls}{article.sty}
\LoadClass[12pt, a4paper]{article}
\ProvidesClass{avocado}[05/10/2015 Template for Avocado Share Documents]
\RequirePackage[margin=2cm]{geometry}
\RequirePackage{nameref}

\RequirePackage{xcolor}

%\setlength{\oddsidemargin}{0mm} % Adjust margins to center the colored title box
%\setlength{\evensidemargin}{0mm} % Margins on even pages - only necessary if adding more content to this template

\thispagestyle{empty} % Remove page numbering on this page

\definecolor{avocadogreen}{cmyk}{0.36, 0, 0.52, 0.55}
\definecolor{avocadogreenlight}{HTML}{6B8C21}
\definecolor{avocadoflesh}{RGB}{221, 212, 143}
\definecolor{avocadolightflesh}{RGB}{226, 224, 176}
\definecolor{avocadolightbrown}{cmyk}{0, 0.18, 0.33, 0.20}
\definecolor{avocadobrown}{HTML}{704012}

\renewcommand{\arraystretch}{1.5}

\RequirePackage[utf8]{inputenc}
\RequirePackage[T1]{fontenc}
\RequirePackage{lmodern}
\RequirePackage[ngerman]{babel}

\RequirePackage{fancyhdr}
\pagestyle{fancy}

\lhead{Pflichtenheft}
\chead{}
\rhead{\bfseries Avocado Share}
%\lfoot{Sascha Bergmann, Lion Kunz, Dang Thien Nguyen, Cyril Müller}
\lfoot{Gruppe 13}
\cfoot{}
\rfoot{\thepage}
\renewcommand{\headrulewidth}{0.4pt}
\renewcommand{\footrulewidth}{0.4pt}
