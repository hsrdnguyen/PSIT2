\NeedsTeXFormat{LaTeX2e}
\@obsoletefile{article.cls}{article.sty}
\LoadClass[12pt, a4paper]{article}
\ProvidesClass{avocado}[05/10/2015 Template for Avocado Share Documents]

\RequirePackage[margin=2.5cm]{geometry}
\RequirePackage{xcolor}
\RequirePackage{tikz}
\RequirePackage[some]{background}
\RequirePackage{setspace}

\RequirePackage{sectsty}
\sectionfont{\\selectfont}

%\newcommand{\addauthor}[1]{
%    \let\oldauthor\author
%    \renewcommand{\author}[1]{\oldauthor \hfill #1 \\}
%}

\setlength{\oddsidemargin}{0mm} % Adjust margins to center the colored title box
\setlength{\evensidemargin}{0mm} % Margins on even pages - only necessary if adding more content to this template

\thispagestyle{empty} % Remove page numbering on this page

\definecolor{avocadogreen}{cmyk}{0.36, 0, 0.52, 0.55}
\definecolor{avocadogreenlight}{HTML}{6B8C21}
%\definecolor{avocadoflesh}{cmyk}{0,  0.4, 0.35, 0.13}
\definecolor{avocadoflesh}{RGB}{221, 212, 143}
\definecolor{avocadolightflesh}{RGB}{226, 224, 176}
\definecolor{avocadolightbrown}{cmyk}{0, 0.18, 0.33, 0.20}
\definecolor{avocadobrown}{HTML}{704012}
\backgroundsetup{
scale=1,
angle=0,
opacit=1,
contents={\begin{tikzpicture}[remember picture,overlay]
 \path [fill=avocadogreen] (-0.5\paperwidth,0.5\paperheight) rectangle (0.5\paperwidth,0.11\paperheight);
 \path [fill=avocadolightflesh] (-0.5\paperwidth,0.11\paperheight) rectangle (0.5\paperwidth,-0.5\paperheight);  
\end{tikzpicture}}
}


\renewcommand{\arraystretch}{1.5}

\RequirePackage[utf8]{inputenc}
\RequirePackage[T1]{fontenc}
\RequirePackage{lmodern}
\RequirePackage[ngerman]{babel}


%\makeatletter     
%\def\printauthor{%                  
%    {\large \@author}}
%\def\printtitle{%                  
%    {\@title}}              
%\makeatother
%
%\newcommand*{\project}[1]{\gdef\@project{#1}%
%}
%
%\renewcommand*{\maketitle}{%
%\begin{titlepage}
%\BgThispage
%\vspace*{4cm}
%\noindent
%{\color{avocadolightflesh}
%    \textsf{\fontsize{2cm}{1em}\selectfont \@project}
%    \vspace*{8cm}\par
%    \noindent
%}
%{
%    \color{avocadobrown}
%    \textsf{\fontsize{1cm}{1em}\selectfont \printtitle}
%}
%\vspace*{2.5cm}\par
%\noindent
%%\begin{minipage}{0.35\linewidth}
%%    \begin{flushright}
%%        \printauthor
%%    \end{flushright}
%%\end{minipage} \hspace{15pt}
%%
%%\begin{minipage}{0.02\linewidth}
%%    \rule{1pt}{175pt}
%%\end{minipage} \hspace{-10pt}
%%
%%\null
%%----------------------------------------------------------------------------------------
%
%\vfill % Space between the title box and author information
%\begin{flushright}
%    \textcolor{\textsf{avocadobrown}{\printauthor}}
%\end{flushright}
%%----------------------------------------------------------------------------------------
%%   AUTHOR NAME AND INFORMATION SECTION
%%----------------------------------------------------------------------------------------
%\clearpage
%
%%\end{titlepage}
%%\restoregeometry
%}
%\endinput