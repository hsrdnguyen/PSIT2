package ch.avocado.share.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Encoder for servlets.
 */
public class Encoder {

    private static String getHtmlEntityCharacter(char character) {
        String letter = null;
        switch (character) {
            case 34: // "
                letter = "&quot;";
                break;
            case 38: // &
                letter = "&amp;";
                break;
            case 39: // '
                letter = "&apos;";
                break;
            case 60: // <
                letter = "&lt;";
                break;
            case 62: // >
                letter = "&gt;";
                break;
            case 160: // {{bg|blue|&nbsp;}}
                letter = "&nbsp;";
                break;
            case 161: // ¡
                letter = "&iexcl;";
                break;
            case 162: // ¢
                letter = "&cent;";
                break;
            case 163: // £
                letter = "&pound;";
                break;
            case 164: // ¤
                letter = "&curren;";
                break;
            case 165: // ¥
                letter = "&yen;";
                break;
            case 166: // ¦
                letter = "&brvbar;";
                break;
            case 167: // §
                letter = "&sect;";
                break;
            case 168: // ¨
                letter = "&uml;";
                break;
            case 169: // ©
                letter = "&copy;";
                break;
            case 170: // ª
                letter = "&ordf;";
                break;
            case 171: // «
                letter = "&laquo;";
                break;
            case 172: // ¬
                letter = "&not;";
                break;
            case 173: // style="background:#ddd"| &nbsp;
                letter = "&shy;";
                break;
            case 174: // ®
                letter = "&reg;";
                break;
            case 175: // ¯
                letter = "&macr;";
                break;
            case 176: // °
                letter = "&deg;";
                break;
            case 177: // ±
                letter = "&plusmn;";
                break;
            case 178: // ²
                letter = "&sup2;";
                break;
            case 179: // ³
                letter = "&sup3;";
                break;
            case 180: // ´
                letter = "&acute;";
                break;
            case 181: // µ
                letter = "&micro;";
                break;
            case 182: // ¶
                letter = "&para;";
                break;
            case 183: // ·
                letter = "&middot;";
                break;
            case 184: // ¸
                letter = "&cedil;";
                break;
            case 185: // ¹
                letter = "&sup1;";
                break;
            case 186: // º
                letter = "&ordm;";
                break;
            case 187: // &raquo; <!-- Warning: don't use the » character directly or MediaWiki will add U+00A0 NO-BREAK SPACE before it. -->
                letter = "&raquo;";
                break;
            case 188: // ¼
                letter = "&frac14;";
                break;
            case 189: // ½
                letter = "&frac12;";
                break;
            case 190: // ¾
                letter = "&frac34;";
                break;
            case 191: // ¿
                letter = "&iquest;";
                break;
            case 192: // À
                letter = "&Agrave;";
                break;
            case 193: // Á
                letter = "&Aacute;";
                break;
            case 194: // Â
                letter = "&Acirc;";
                break;
            case 195: // Ã
                letter = "&Atilde;";
                break;
            case 196: // Ä
                letter = "&Auml;";
                break;
            case 197: // Å
                letter = "&Aring;";
                break;
            case 198: // Æ
                letter = "&AElig;";
                break;
            case 199: // Ç
                letter = "&Ccedil;";
                break;
            case 200: // È
                letter = "&Egrave;";
                break;
            case 201: // É
                letter = "&Eacute;";
                break;
            case 202: // Ê
                letter = "&Ecirc;";
                break;
            case 203: // Ë
                letter = "&Euml;";
                break;
            case 204: // Ì
                letter = "&Igrave;";
                break;
            case 205: // Í
                letter = "&Iacute;";
                break;
            case 206: // Î
                letter = "&Icirc;";
                break;
            case 207: // Ï
                letter = "&Iuml;";
                break;
            case 208: // Ð
                letter = "&ETH;";
                break;
            case 209: // Ñ
                letter = "&Ntilde;";
                break;
            case 210: // Ò
                letter = "&Ograve;";
                break;
            case 211: // Ó
                letter = "&Oacute;";
                break;
            case 212: // Ô
                letter = "&Ocirc;";
                break;
            case 213: // Õ
                letter = "&Otilde;";
                break;
            case 214: // Ö
                letter = "&Ouml;";
                break;
            case 215: // ×
                letter = "&times;";
                break;
            case 216: // Ø
                letter = "&Oslash;";
                break;
            case 217: // Ù
                letter = "&Ugrave;";
                break;
            case 218: // Ú
                letter = "&Uacute;";
                break;
            case 219: // Û
                letter = "&Ucirc;";
                break;
            case 220: // Ü
                letter = "&Uuml;";
                break;
            case 221: // Ý
                letter = "&Yacute;";
                break;
            case 222: // Þ
                letter = "&THORN;";
                break;
            case 223: // ß
                letter = "&szlig;";
                break;
            case 224: // à
                letter = "&agrave;";
                break;
            case 225: // á
                letter = "&aacute;";
                break;
            case 226: // â
                letter = "&acirc;";
                break;
            case 227: // ã
                letter = "&atilde;";
                break;
            case 228: // ä
                letter = "&auml;";
                break;
            case 229: // å
                letter = "&aring;";
                break;
            case 230: // æ
                letter = "&aelig;";
                break;
            case 231: // ç
                letter = "&ccedil;";
                break;
            case 232: // è
                letter = "&egrave;";
                break;
            case 233: // é
                letter = "&eacute;";
                break;
            case 234: // ê
                letter = "&ecirc;";
                break;
            case 235: // ë
                letter = "&euml;";
                break;
            case 236: // ì
                letter = "&igrave;";
                break;
            case 237: // í
                letter = "&iacute;";
                break;
            case 238: // î
                letter = "&icirc;";
                break;
            case 239: // ï
                letter = "&iuml;";
                break;
            case 240: // ð
                letter = "&eth;";
                break;
            case 241: // ñ
                letter = "&ntilde;";
                break;
            case 242: // ò
                letter = "&ograve;";
                break;
            case 243: // ó
                letter = "&oacute;";
                break;
            case 244: // ô
                letter = "&ocirc;";
                break;
            case 245: // õ
                letter = "&otilde;";
                break;
            case 246: // ö
                letter = "&ouml;";
                break;
            case 247: // ÷
                letter = "&divide;";
                break;
            case 248: // ø
                letter = "&oslash;";
                break;
            case 249: // ù
                letter = "&ugrave;";
                break;
            case 250: // ú
                letter = "&uacute;";
                break;
            case 251: // û
                letter = "&ucirc;";
                break;
            case 252: // ü
                letter = "&uuml;";
                break;
            case 253: // ý
                letter = "&yacute;";
                break;
            case 254: // þ
                letter = "&thorn;";
                break;
            case 255: // ÿ
                letter = "&yuml;";
                break;
            case 338: // Œ
                letter = "&OElig;";
                break;
            case 339: // œ
                letter = "&oelig;";
                break;
            case 352: // Š
                letter = "&Scaron;";
                break;
            case 353: // š
                letter = "&scaron;";
                break;
            case 376: // Ÿ
                letter = "&Yuml;";
                break;
            case 402: // ƒ
                letter = "&fnof;";
                break;
            case 710: // ˆ
                letter = "&circ;";
                break;
            case 732: // ˜
                letter = "&tilde;";
                break;
            case 913: // Α
                letter = "&Alpha;";
                break;
            case 914: // Β
                letter = "&Beta;";
                break;
            case 915: // Γ
                letter = "&Gamma;";
                break;
            case 916: // Δ
                letter = "&Delta;";
                break;
            case 917: // Ε
                letter = "&Epsilon;";
                break;
            case 918: // Ζ
                letter = "&Zeta;";
                break;
            case 919: // Η
                letter = "&Eta;";
                break;
            case 920: // Θ
                letter = "&Theta;";
                break;
            case 921: // Ι
                letter = "&Iota;";
                break;
            case 922: // Κ
                letter = "&Kappa;";
                break;
            case 923: // Λ
                letter = "&Lambda;";
                break;
            case 924: // Μ
                letter = "&Mu;";
                break;
            case 925: // Ν
                letter = "&Nu;";
                break;
            case 926: // Ξ
                letter = "&Xi;";
                break;
            case 927: // Ο
                letter = "&Omicron;";
                break;
            case 928: // Π
                letter = "&Pi;";
                break;
            case 929: // Ρ
                letter = "&Rho;";
                break;
            case 931: // Σ
                letter = "&Sigma;";
                break;
            case 932: // Τ
                letter = "&Tau;";
                break;
            case 933: // Υ
                letter = "&Upsilon;";
                break;
            case 934: // Φ
                letter = "&Phi;";
                break;
            case 935: // Χ
                letter = "&Chi;";
                break;
            case 936: // Ψ
                letter = "&Psi;";
                break;
            case 937: // Ω
                letter = "&Omega;";
                break;
            case 945: // α
                letter = "&alpha;";
                break;
            case 946: // β
                letter = "&beta;";
                break;
            case 947: // γ
                letter = "&gamma;";
                break;
            case 948: // δ
                letter = "&delta;";
                break;
            case 949: // ε
                letter = "&epsilon;";
                break;
            case 950: // ζ
                letter = "&zeta;";
                break;
            case 951: // η
                letter = "&eta;";
                break;
            case 952: // θ
                letter = "&theta;";
                break;
            case 953: // ι
                letter = "&iota;";
                break;
            case 954: // κ
                letter = "&kappa;";
                break;
            case 955: // λ
                letter = "&lambda;";
                break;
            case 956: // μ
                letter = "&mu;";
                break;
            case 957: // ν
                letter = "&nu;";
                break;
            case 958: // ξ
                letter = "&xi;";
                break;
            case 959: // ο
                letter = "&omicron;";
                break;
            case 960: // π
                letter = "&pi;";
                break;
            case 961: // ρ
                letter = "&rho;";
                break;
            case 962: // ς
                letter = "&sigmaf;";
                break;
            case 963: // σ
                letter = "&sigma;";
                break;
            case 964: // τ
                letter = "&tau;";
                break;
            case 965: // υ
                letter = "&upsilon;";
                break;
            case 966: // φ
                letter = "&phi;";
                break;
            case 967: // χ
                letter = "&chi;";
                break;
            case 968: // ψ
                letter = "&psi;";
                break;
            case 969: // ω
                letter = "&omega;";
                break;
            case 977: // ϑ
                letter = "&thetasym;";
                break;
            case 978: // ϒ
                letter = "&upsih;";
                break;
            case 982: // ϖ
                letter = "&piv;";
                break;
            case 8194: // {{bg|blue|&ensp;}}
                letter = "&ensp;";
                break;
            case 8195: // {{bg|blue|&emsp;}}
                letter = "&emsp;";
                break;
            case 8201: // {{bg|blue|&thinsp;}}
                letter = "&thinsp;";
                break;
            case 8204: // style="background:#ddd"| &nbsp;
                letter = "&zwnj;";
                break;
            case 8205: // style="background:#ddd"| &nbsp;
                letter = "&zwj;";
                break;
            case 8206: // style="background:#ddd"| &nbsp;
                letter = "&lrm;";
                break;
            case 8207: // style="background:#ddd"| &nbsp;
                letter = "&rlm;";
                break;
            case 8211: // –
                letter = "&ndash;";
                break;
            case 8212: // —
                letter = "&mdash;";
                break;
            case 8216: // ‘
                letter = "&lsquo;";
                break;
            case 8217: // ’
                letter = "&rsquo;";
                break;
            case 8218: // ‚
                letter = "&sbquo;";
                break;
            case 8220: // “
                letter = "&ldquo;";
                break;
            case 8221: // ”
                letter = "&rdquo;";
                break;
            case 8222: // „
                letter = "&bdquo;";
                break;
            case 8224: // †
                letter = "&dagger;";
                break;
            case 8225: // ‡
                letter = "&Dagger;";
                break;
            case 8226: // •
                letter = "&bull;";
                break;
            case 8230: // …
                letter = "&hellip;";
                break;
            case 8240: // ‰
                letter = "&permil;";
                break;
            case 8242: // ′
                letter = "&prime;";
                break;
            case 8243: // ″
                letter = "&Prime;";
                break;
            case 8249: // ‹
                letter = "&lsaquo;";
                break;
            case 8250: // ›
                letter = "&rsaquo;";
                break;
            case 8254: // ‾
                letter = "&oline;";
                break;
            case 8260: // ⁄
                letter = "&frasl;";
                break;
            case 8364: // €
                letter = "&euro;";
                break;
            case 8465: // {{unicode|ℑ}}
                letter = "&image;";
                break;
            case 8472: // {{unicode|℘}}
                letter = "&weierp;";
                break;
            case 8476: // {{unicode|ℜ}}
                letter = "&real;";
                break;
            case 8482: // ™
                letter = "&trade;";
                break;
            case 8501: // {{unicode|ℵ}}
                letter = "&alefsym;";
                break;
            case 8592: // ←
                letter = "&larr;";
                break;
            case 8593: // ↑
                letter = "&uarr;";
                break;
            case 8594: // →
                letter = "&rarr;";
                break;
            case 8595: // ↓
                letter = "&darr;";
                break;
            case 8596: // ↔
                letter = "&harr;";
                break;
            case 8629: // {{unicode|↵}}
                letter = "&crarr;";
                break;
            case 8656: // {{unicode|⇐}}
                letter = "&lArr;";
                break;
            case 8657: // {{unicode|⇑}}
                letter = "&uArr;";
                break;
            case 8658: // {{unicode|⇒}}
                letter = "&rArr;";
                break;
            case 8659: // {{unicode|⇓}}
                letter = "&dArr;";
                break;
            case 8660: // {{unicode|⇔}}
                letter = "&hArr;";
                break;
            case 8704: // ∀
                letter = "&forall;";
                break;
            case 8706: // ∂
                letter = "&part;";
                break;
            case 8707: // ∃
                letter = "&exist;";
                break;
            case 8709: // {{unicode|∅}}
                letter = "&empty;";
                break;
            case 8711: // ∇
                letter = "&nabla;";
                break;
            case 8712: // ∈
                letter = "&isin;";
                break;
            case 8713: // {{unicode|∉}}
                letter = "&notin;";
                break;
            case 8715: // ∋
                letter = "&ni;";
                break;
            case 8719: // ∏
                letter = "&prod;";
                break;
            case 8721: // ∑
                letter = "&sum;";
                break;
            case 8722: // −
                letter = "&minus;";
                break;
            case 8727: // {{unicode|∗}}
                letter = "&lowast;";
                break;
            case 8730: // √
                letter = "&radic;";
                break;
            case 8733: // ∝
                letter = "&prop;";
                break;
            case 8734: // ∞
                letter = "&infin;";
                break;
            case 8736: // ∠
                letter = "&ang;";
                break;
            case 8743: // ∧
                letter = "&and;";
                break;
            case 8744: // ∨
                letter = "&or;";
                break;
            case 8745: // ∩
                letter = "&cap;";
                break;
            case 8746: // ∪
                letter = "&cup;";
                break;
            case 8747: // ∫
                letter = "&int;";
                break;
            case 8756: // ∴
                letter = "&there4;";
                break;
            case 8764: // ∼
                letter = "&sim;";
                break;
            case 8773: // {{unicode|≅}}
                letter = "&cong;";
                break;
            case 8776: // ≈
                letter = "&asymp;";
                break;
            case 8800: // ≠
                letter = "&ne;";
                break;
            case 8801: // ≡
                letter = "&equiv;";
                break;
            case 8804: // ≤
                letter = "&le;";
                break;
            case 8805: // ≥
                letter = "&ge;";
                break;
            case 8834: // ⊂
                letter = "&sub;";
                break;
            case 8835: // ⊃
                letter = "&sup;";
                break;
            case 8836: // {{unicode|⊄}}
                letter = "&nsub;";
                break;
            case 8838: // ⊆
                letter = "&sube;";
                break;
            case 8839: // ⊇
                letter = "&supe;";
                break;
            case 8853: // ⊕
                letter = "&oplus;";
                break;
            case 8855: // {{unicode|⊗}}
                letter = "&otimes;";
                break;
            case 8869: // ⊥
                letter = "&perp;";
                break;
            case 8901: // ⋅
                letter = "&sdot;";
                break;
            case 8968: // ⌈
                letter = "&lceil;";
                break;
            case 8969: // ⌉
                letter = "&rceil;";
                break;
            case 8970: // ⌊
                letter = "&lfloor;";
                break;
            case 8971: // ⌋
                letter = "&rfloor;";
                break;
            case 9001: //
                letter = "&lang;";
                break;
            case 9002: //
                letter = "&rang;";
                break;
            case 9674: // ◊
                letter = "&loz;";
                break;
            case 9824: // ♠
                letter = "&spades;";
                break;
            case 9827: // ♣
                letter = "&clubs;";
                break;
            case 9829: // ♥
                letter = "&hearts;";
                break;
            case 9830: // ♦
                letter = "&diams;";
                break;
        }
        return letter;
    }

    private static String htmlEntities(String value) {
        if(value == null) throw new IllegalArgumentException("value can't be null");
        String result = "";
        for(char character: value.toCharArray()) {
            String letter = getHtmlEntityCharacter(character);
            if(letter == null) {
                result += character;
            } else {
                result += letter;
            }
        }
        return result;
    }

    /**
     * Encodes a value for inside a html tag.
     * @param value The value to encode
     * @return The encoded value.
     */
    public static String forHtml(String value) {
        if(value == null) value = "";
        value = htmlEntities(value);
        // TODO: is this a good idea?
        value = value.replaceAll("\n", "<br/>");
        return value;
    }

    /**
     * Encodes a value for inside a HTML attribute.
     * @param value The value to encode
     * @return The encoded value
     */
    public static String forHtmlAttribute(String value) {
        if(value == null) value = "";
        return htmlEntities(value);
    }

    /**
     * Encodes a value for inside a url inside a HTML attribute.
     * For example if you need to encode a name for inside a parameter
     * of the href attribute of a link.
     * @param value The value to be encoded.
     * @return The encoded value
     * @throws UnsupportedEncodingException If "UTF-8" is not supported.
     */
    public static String forUrlAttribute(String value) throws UnsupportedEncodingException {
        return htmlEntities(URLEncoder.encode(value, "UTF-8"));
    }

    /**
     * Encodes a value for use inside a url.
     * @param value The value to encode
     * @return The encoded value.
     * @throws UnsupportedEncodingException If "UTF-8" is not supported.
     */
    public static String forUrl(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }
}
