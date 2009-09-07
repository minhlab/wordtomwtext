package com.thuvienkhoahoc.wordtomwtext.logic;

import java.util.regex.Pattern;

public class Highlighter {

	public void execute(String html, boolean singleLine, boolean noTimeOut) {
		 
		// start timer to cancel after wikEdMaxHighlightTime
		long start = System.currentTimeMillis();
	 
		// &lt; &gt; &amp; to \x00 \x01 \x02
		html = html.replaceAll("&lt;", "\u0000");
		html = html.replaceAll("&gt;", "\u0001");
		html = html.replaceAll("&amp;", "\u0002");

		// #REDIRECT
		html = Pattern.compile("(^|\n)(#)(redirect\b)", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("$1<span class=\"wikEdRedir\">$3</span><!--wikEdRedir-->");
	 
		/*
	// nowiki (no html highlighting)
		html = html.replaceAll("(\x00nowiki\b.*?\x01)(.*?)(\x00\/nowiki\b.*?\x01)/gi,
			function (p, p1, p2, p3) {
				p2 = p2.replaceAll("\x00/g, '&lt;');
				p2 = p2.replaceAll("\x01/g, '&gt;');
				return(p1 + p2 + p3);
			}
		);
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime / 10) {
				return;
			}
		}
	 
	// blocks
	 
	// lists * # : ;
		html = html.replaceAll("^((\x00!--.*?--\x01)*)([\*\#\:\;]+)(.*?)$/gm, '<span class="wikEdListLine">$1<span class="wikEdListTag">$3</span><!--wikEdListTag-->$4</span><!--wikEdListLine-->');
	 
	//// interferes with other block tags and hiding
		if (singleLine != true) {
////			html = html.replaceAll("((<span class=\"wikEdListLine\">[^\n]*\n)+)/g, '<span class="wikEdList">$1');
////			html = html.replaceAll("(<span class=\"wikEdListLine\">[^\n]*)(\n)(?!<span class=\"wikEdListLine\">)/g, '$1</span><!--wikEdList-->$2');
		}
	 
	// #REDIRECT (finish)
		html = html.replaceAll("(<span class=\"wikEdRedir\">)(.*?<\/span><!--wikEdRedir-->)/g, '$1#$2');
	 
	// various blocks
		if (singleLine != true) {
			html = html.replaceAll("(\x00(blockquote|center|div|pre|gallery|source|poem|categorytree|hiero|imagemap|inputbox|timeline)\b[^\x01]*\x01(.|\n)*?\x00\/\2\x01)/gi, '<span class="wikEdBlock">$1</span><!--wikEdBlock-->');
		}
	 
	// space-pre
		if (singleLine != true) {
			html = html.replaceAll("^((\x00!--.*?--\x01)*)[\xa0 ]([\xa0 ]*)(.*?)$/gm, '<span class="wikEdSpaceLine">$1<span class="wikEdSpaceTag">&nbsp;$3</span><!--wikEdSpaceTag-->$4</span><!--wikEdSpaceLine-->');
	 
	//// interferes with other block tags and hiding
////			html = html.replaceAll("((<span class=\"wikEdSpaceLine\">[^\n]*\n)+)/g, '<span class="wikEdSpace">$1');
////			html = html.replaceAll("(<span class=\"wikEdSpaceLine\">[^\n]*)(\n)(?!<span class="wikEdSpaceLine">)/g, '$1</span><!--wikEdSpace-->$2');
		}
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime / 10) {
				return;
			}
		}
	 
	// ---- <hr> horizontal rule
		html = html.replaceAll("(^|\n)((\x00!--.*?--\x01|<[^>]*>)*)(----)((\x00!--.*?--\x01|<[^>]*>)*)(\n|$)/g, '$1<span class="wikEdHR">$2$4</span><!--wikEdHR-->$5$7');
		html = html.replaceAll("(\x00hr\x01)/g, '<span class="wikEdHRInline">$1</span><!--wikEdHRInline-->');
	 
	// == headings
		html = html.replaceAll("(^|\n)((\x00!--.*?--\x01|<[^>]*>)*)(=+[\xa0 ]*)([^\n]*?)([\xa0 ]*=+)(?=([\xa0 ]|<[^>]*>|\x00!--.*?--\x01)*(\n|$))/g,
			function (p, p1, p2, p3, p4, p5, p6) {
				p4 = p4.replaceAll("(=+)/g, '<span class="wikEdWiki">$1</span><!--wikEdWiki-->');
				p6 = p6.replaceAll("(=+)/g, '<span class="wikEdWiki">$1</span><!--wikEdWiki-->');
				var regExp = new RegExp('^' + wikEdText['External links'] + '?|' + wikEdText['External links'] + '|' + wikEdText['See also'] + '|'  + wikEdText['References'] + '$', 'i');
				if (regExp.test(p5) == true) {
					p1 = p1 + '<span class="wikEdHeadingWp">';
					p6 = p6 + '</span><!--wikEdHeadingWp-->';
				}
				else {
					p1 = p1 + '<span class="wikEdHeading">';
					p6 = p6 + '</span><!--wikEdHeading-->';
				}
				return(p1 + p2 + p4 + p5 + p6);
			}
		);
	 
	// tables |}
		html = html.replaceAll("^((\x00!--.*?--\x01)*)(\|\})(.*)$/gm, '<span class="wikEdTableLine">$1<span class="wikEdTableTag">$3</span><!--wikEdTableTag--></span><!--wikEdTableLine-->$4');
	 
	// tables {| |+ |- !
		html = html.replaceAll("^((\x00!--.*?--\x01)*)(\{\||\|\+|\|\-|\!|\|)(.*)$/gm, '<span class="wikEdTableLine">$1<span class="wikEdTableTag">$3</span><!--wikEdTableTag-->$4</span><!--wikEdTableLine-->');
		if (singleLine != true) {
			html = html.replaceAll("(^|\n)((<[^>]*>|\x00!--.*?--\x01)*\{\|)/g, '$1<span class="wikEdTable">$2');
			html = html.replaceAll("(^|\n)((<[^>]*>|\x00!--.*?--\x01)*\|\}(<[^>]*>)*)/g, '$1$2</span><!--wikEdTable-->');
			html = html.replaceAll("(\x00table\b[^\x01]*\x01)/gi, '<span class="wikEdTable">$1');
			html = html.replaceAll("(\x00\/table\x01)/gi, '$1</span><!--wikEdTable-->');
		}
	 
	// <gallery> wiki markup
		if (singleLine != true) {
			html = html.replaceAll("(\x00(gallery)\b[^\x01]*\x01)/gi, '<span class="wikEdWiki">$1');
			html = html.replaceAll("(\x00\/(gallery)\x01)/gi, '$1</span><!--wikEdWiki-->');
		}
	 
	// various block tags
		html = html.replaceAll("(\x00\/?(blockquote|center|div|pre|gallery|source|poem|categorytree|hiero|imagemap|inputbox|timeline)\b[^\x01]*\x01)/gi, '<span class="wikEdBlockTag">$1</span><!--wikEdBlockTag-->');
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime / 5) {
				return;
			}
		}
	 
	// <p> ... </p> pairs with (wikEdBlockTag) and withhout attributes (wikEdUnknown)
		var isRemove = [];
		html = html.replaceAll("(\x00(\/?)p\b([^\x01]*?)\x01)/g,
			function (p, p1, p2, p3) {
				if (p2 == '') {
					if (p3 == '') {
						isRemove.push(true);
						return('<span class="wikEdUnknown">' + p1 + '</span><!--wikEdUnknown-->');
					}
					if (/\/$/.test(p3)) {
						return('<span class="wikEdUnknown">' + p1 + '</span><!--wikEdUnknown-->');
					}
					isRemove.push(false);
					return('<span class="wikEdBlockTag">' + p1 + '</span><!--wikEdBlockTag-->');
				}
				if (isRemove.pop() == true) {
					return('<span class="wikEdUnknown">' + p1 + '</span><!--wikEdUnknown-->');
				}
				return('<span class="wikEdBlockTag">' + p1 + '</span><!--wikEdBlockTag-->');
			}
		);
	 
	// inline elements
	 
	// <sup> </sub> <ins> <del>
		html = html.replaceAll("(\x00sup\b[^\x01]*\x01((.|\n)*?)\x00\/sup\x01)/gi, '<span class="wikEdSuperscript">$1</span><!--wikEdSuperscript-->');
		html = html.replaceAll("(\x00sub\b[^\x01]*\x01((.|\n)*?)\x00\/sub\x01)/gi, '<span class="wikEdSubscript">$1</span><!--wikEdSubscript-->');
		html = html.replaceAll("(\x00(ins|u)\b[^\x01]*\x01((.|\n)*?)\x00\/(ins|u)\x01)/gi, '<span class="wikEdIns">$1</span><!--wikEdIns-->');
		html = html.replaceAll("(\x00(del|s|strike)\b[^\x01]*\x01((.|\n)*?)\x00\/(del|s|strike)\x01)/gi, '<span class="wikEdDel">$1</span><!--wikEdDel-->');
	 
	// <ref /> wiki markup
			html = html.replaceAll("\x00(ref\b[^\x01]*?\/)\x01/gi, '<span class="wikEdRefHide" title="' + wikEdText['wikEdRefHideTooltip'] + '"></span><!--wikEdRefHide--><span class="wikEdRef">&lt;$1&gt;</span><!--wikEdRef-->');
	 
	// check for and highlight only correctly nested <ref>...</ref>
		var level = 0;
		var regExp = /(\x00(\/?)ref\b([^\x01]*)\x01)(?!<\/span><!--wikEdRef-->)/gi;
		var regExpMatch;
		while ( (regExpMatch = regExp.exec(html)) != null) {
			if (regExpMatch[2] == '/') {
				level --;
				if (level < 0) {
					break;
				}
			}
			else {
				level ++;
			}
		}
		if (level == 0) {
			html = html.replaceAll("(\x00ref\b[^\x01]*\x01)/gi, '<span class="wikEdRefHide" title="' + wikEdText['wikEdRefHideTooltip'] + '"></span><!--wikEdRefHide--><span class="wikEdRef">$1');
			html = html.replaceAll("(\x00\/ref\b[^\x01]*\x01)(?!<\/span><!--wikEdRef-->)/gi, '$1</span><!--wikEdRef-->');
		}
	 
	// various inline tags
		html = html.replaceAll("(\x00\/?(sub|sup|ins|u|del|s|strike|big|br|colgroup|code|font|small|span|tt|rb|rp|rt|ruby)\b[^\x01]*\x01)/gi, '<span class="wikEdInlineTag">$1</span><!--wikEdInlineTag-->');
	 
	// <references/> wiki markup
		html = html.replaceAll("\x00((references)\b[^\x01]*?\/)\x01/gi, '<span class="wikEdWiki">&lt;$1&gt;</span><!--wikEdWiki-->');
	 
	// wiki markup
		html = html.replaceAll("(\x00(math|noinclude|includeonly|charinsert|fundraising|fundraisinglogo|gallery|source|poem|categorytree|hiero|imagemap|inputbox|timeline|references)\b[^\x01]*\x01((.|\n)*?)(\x00)\/\2\x01)/gi, '<span class="wikEdWiki">$1</span><!--wikEdWiki-->');
	 
	// unsupported or not needed <> tags
		html = html.replaceAll("(\x00\/?)(\w+)(.*?\/?\x01)/g,
			function (p, p1, p2, p3) {
				if ( ! /^(col|thead|tfoot|tbody|big|br|blockquote|colgroup|center|code|del|div|font|ins|p|pre|s|small|span|strike|sub|sup|tt|u|rb|rp|rt|ruby|nowiki|math|noinclude|includeonly|ref|charinsert|fundraising|fundraisinglogo|gallery|source|poem|categorytree|hiero|imagemap|inputbox|timeline|references)$/i.test(p2) ) {
					p1 = '<span class="wikEdUnknown">' + p1;
					p3 = p3 + '</span><!--wikEdUnknown-->';
				}
				return(p1 + p2 + p3);
			}
		);
	 
	// comments
		html = html.replaceAll("(\x00!--(.|\n)*?--\x01)/g, '<span class="wikEdComment">$1</span><!--wikEdComment-->');
	 
	// named html colors in quotation marks
		html = html.replaceAll("(\'|\")(aliceblue|antiquewhite|aqua|aquamarine|azure|beige|bisque|blanchedalmond|burlywood|chartreuse|coral|cornsilk|cyan|darkgray|darkgrey|darkkhaki|darkorange|darksalmon|darkseagreen|floralwhite|fuchsia|gainsboro|ghostwhite|gold|goldenrod|greenyellow|honeydew|hotpink|ivory|khaki|lavender|lavenderblush|lawngreen|lemonchiffon|lightblue|lightcoral|lightcyan|lightgoldenrodyellow|lightgray|lightgreen|lightgrey|lightpink|lightsalmon|lightskyblue|lightsteelblue|lightyellow|lime|linen|magenta|mediumaquamarine|mediumspringgreen|mediumturquoise|mintcream|mistyrose|moccasin|navajowhite|oldlace|orange|palegoldenrod|palegreen|paleturquoise|papayawhip|peachpuff|peru|pink|plum|powderblue|salmon|sandybrown|seashell|silver|skyblue|snow|springgreen|tan|thistle|turquoise|violet|wheat|white|whitesmoke|yellow|yellowgreen)(\1)/g, '$1<span style="background-color: $2;" class="wikEdColorsLight">$2</span><!--wikEdColorsLight-->$3');
		html = html.replaceAll("(\'|\")(black|blue|blueviolet|brown|cadetblue|chocolate|cornflowerblue|crimson|darkblue|darkcyan|darkgoldenrod|darkgreen|darkmagenta|darkolivegreen|darkorchid|darkred|darkslateblue|darkslategray|darkslategrey|darkturquoise|darkviolet|deeppink|deepskyblue|dimgray|dimgrey|dodgerblue|firebrick|forestgreen|gray|green|grey|indianred|indigo|lightseagreen|lightslategray|lightslategrey|limegreen|maroon|mediumblue|mediumorchid|mediumpurple|mediumseagreen|mediumslateblue|mediumvioletred|midnightblue|navy|olive|olivedrab|orangered|orchid|palevioletred|purple|red|rosybrown|royalblue|saddlebrown|seagreen|sienna|slateblue|slategray|slategrey|steelblue|teal|tomato)(\1)/g, '$1<span style="background-color: $2;" class="wikEdColorsDark">$2</span><!--wikEdColorsDark-->$3');
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime / 2) {
				return;
			}
		}
	 
	// RGB hex colors #d4d0cc, exclude links and character entities starting with &
		html = html.replaceAll("(^|[^\/\w\x02])(#([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2}))(?=(\W|$))/g,
			function (p, p1, p2, p3, p4, p5) {
				var luminance = parseInt(p3, 16) * 0.299 + parseInt(p4, 16) * 0.587 + parseInt(p5, 16) * 0.114;
				if (luminance > 128) {
					return(p1 + '<span style="background-color: ' + p2 + '" class="wikEdColorsLight">' + p2 + '</span><!--wikEdColorsLight-->');
				}
				else {
					return(p1 + '<span style="background-color: ' + p2 + '" class="wikEdColorsDark">' + p2 + '</span><!--wikEdColorsDark-->');
				}
			}
		);
	 
	// RGB hex colors #ddc, exclude links and character entities starting with &
		html = html.replaceAll("(^|[^\/\w\x02])(#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F]))(?=(\W|$))/g,
			function (p, p1, p2, p3, p4, p5) {
				var luminance = parseInt(p3, 16) * 16 * 0.299 + parseInt(p4, 16) * 16 * 0.587 + parseInt(p5, 16) * 16  * 0.114;
				if (luminance > 128) {
					return(p1 + '<span style="background-color: ' + p2 + '" class="wikEdColorsLight">' + p2 + '</span><!--wikEdColorsLight-->');
				}
				else {
					return(p1 + '<span style="background-color: ' + p2 + '" class="wikEdColorsDark">' + p2 + '</span><!--wikEdColorsDark-->');
				}
			}
		);
	 
	// RGB decimal colors rgb(128,64,265)
		html = html.replaceAll("(rgb\(\s*(\d+),\s*(\d+),\s*(\d+)\s*\))/gi,
			function (p, p1, p2, p3, p4) {
				var luminance = p2 * 0.299 + p3 * 0.587 + p4  * 0.114;
				if (luminance > 128) {
					return('<span style="background-color: ' + p1 + '" class="wikEdColorsLight">' + p1 + '</span><!--wikEdColorsLight-->');
				}
				else {
					return('<span style="background-color: ' + p1 + '" class="wikEdColorsDark">' + p1 + '</span><!--wikEdColorsDark-->');
				}
			}
		);
	 
	// clear array of link addresses
		if (obj.whole == true) {
			wikEdFollowLinkIdNo = 0;
			wikEdFollowLinkHash = {};
		}
		obj.whole = false;
	 
	// URLs
		html = html.replaceAll("(^|.)((http:\/\/|https:\/\/|ftp:\/\/|irc:\/\/|gopher:\/\/|news:|mailto:|file:\/\/)[\!\#\%\x02\(\)\+-\/\:\;\=\?\@\w\~ŠŒŽœžŸŠŒŽšœžŸÀ-ÖØ-öø-\u0220\u0222-\u0233ΆΈΉΊΌΎΏΑ-ΡΣ-ώ\u0400-\u0481\u048a-\u04ce\u04d0-\u04f5\u04f8\u04f9]*)/gm,
			function (p, p1, p2, p3) {
				var trailingChar = '';
	 
	// do not include trailing punctuation for in-text links
	 
				var linkMatch = p2.match(/^(.*?)([\,\.\!\?\:\;])$/);
				if (linkMatch != null) {
					p2 = linkMatch[1];
					trailingChar = linkMatch[2];
				}
	 
	// wikEdURLName
				if (p1 != '[') {
					return(p1 + '<span class="wikEdURL"' + WikEdFollowLinkUrl(null, null, p2) + '><span class="wikEdURLName">' + p2 + '</span><!--wikEdURLName--></span><!--wikEdURL-->' + trailingChar);
				}
	 
	// [wikEdURLText wikEdURLTarget]
				else {
					return(p1 + '<span class="wikEdURLTarget">' + p2 + '</span><!--wikEdURLTarget-->' + trailingChar);
				}
			}
		);
	 
	// [wikEdURLText wikEdURLTarget]
//	                     1[ 12                                 3url3                                24 text     5 ]  5
		html = html.replaceAll("(\[)( *<span class=\"wikEdURLTarget\">(.*?)<\/span><\!--wikEdURLTarget--> *)([^\]\n]*?)( *\])/gi,
			function (p, p1, p2, p3, p4, p5) {
	 
	// link text
				p4 = p4.replaceAll("(.*)/, '<span class="wikEdURLText">$1</span><!--wikEdURLText-->');
	 
	// link tags
				p1 = p1.replaceAll("(\[)/, '<span class="wikEdURL"' + WikEdFollowLinkUrl(null, null, p3) + '><span class="wikEdLinkTag">$1</span><!--wikEdLinkTag-->');
				p5 = p5.replaceAll("(\])/, '<span class="wikEdLinkTag">$1</span><!--wikEdLinkTag--></span><!--wikEdURL-->');
	 
				return(p1 + p2 + p4 + p5);
			}
		);
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime / 2) {
				return;
			}
		}
	 
	// [[ ]] links, categories
		html = html.replaceAll("(\[\[)([^\[\]]*)(\]\])/g,
			function (p, p1, p2, p3) {
	 
	// omit image tags
				var regExpImg = new RegExp('^(<[^>]*>)*(Image|File|' + wikEdText['wikicode Image'] + '|' + wikEdText['wikicode File'] + ')\\s*:', 'i');
				if (regExpImg.test(p2) == true) {
					return(p1 + p2 + p3);
				}
	 
	// get url
				var linkParam = '';
				var linkInter = '';
				var linkMatch = p2.match(/^\s*(([\w ŠŒŽšœžŸÀ-ÖØ-öø-\u0220\u0222-\u0233ΆΈΉΊΌΎΏΑ-ΡΣ-ώ\u0400-\u0481\u048a-\u04ce\u04d0-\u04f5\u04f8\u04f9\-]*\s*:)*)\s*([^\|]+)/);
				if (linkMatch != null) {
					linkInter = linkMatch[1];
					linkParam = WikEdFollowLinkUrl(linkInter, linkMatch[3]);
				}
	 
	// category
				var regExpCat = new RegExp('^\\s*(Category|' + wikEdText['wikicode Category'] + ')\\s*:', 'i');
				if (regExpCat.test(p2)) {
					var regExp = new RegExp('\\s*[\\w\\- ŠŒŽšœžŸÀ-ÖØ-öø-\\u0220\\u0222-\\u0233ΆΈΉΊΌΎΏΑ-ΡΣ-ώ\\u0400-\\u0481\\u048a-\\u04ce\\u04d0-\\u04f5\\u04f8\\u04f9]+\\s*:\\s*(Category|' + wikEdText['wikicode Category'] + ')\\s*:', 'i');
					if (p2.match(regExp) != null) {
						p1 = '<span class="wikEdCatInter"' + linkParam + '>' + p1;
						p3 = p3 + '</span><!--wikEdCatInter-->';
					}
					else {
						p1 = '<span class="wikEdCat"' + linkParam + '>' + p1;
						p3 = p3 + '</span><!--wikEdCat-->';
					}
					p2 = p2.replaceAll("^(\s*)(([\w ]*:)+)/, '$1<span class="wikEdInter">$2</span><!--wikEdInter-->');
					p2 = p2.replaceAll("(\s*)([^>:\|]+)(\s*\|\s*|$)/, '$1<span class="wikEdCatName">$2</span><!--wikEdCatName-->$3');
					p2 = p2.replaceAll("(\|\s*)(.*)/,
						function (p, p1, p2) {
							p2 = p2.replaceAll("(.*?)(\s*(\||$))/g, '<span class="wikEdCatText">$1</span><!--wikEdCatText-->$2');
							return(p1 + p2);
						}
					);
				}
	 
	// wikilink
				else {
					if (linkInter != '') {
						p1 = '<span class="wikEdLinkInter"' + linkParam + '>' + p1;
						p3 = p3 + '</span><!--wikEdLinkInter-->';
					}
					else {
						p1 = '<span class="wikEdLink"' + linkParam + '>' + p1;
						p3 = p3 + '</span><!--wikEdLink-->';
					}
	 
	// [[wikEdLinkTarget|wikEdlinkText]]
					if (/\|/.test(p2) == true) {
						p2 = p2.replaceAll("^(\s*)([^<>\|]+)(\s*(<[^>]*>)*\|\s*)/, '$1<span class="wikEdLinkTarget">$2</span><!--wikEdLinkTarget-->$3');
						p2 = p2.replaceAll("(\|\s*(<[^>]*>)*)(.*)/,
							function (p, p1, p2, p3) {
								p3 = p3.replaceAll("(.*?)(\s*(\||$))/, '<span class="wikEdLinkText">$1</span><!--wikEdLinkText-->$2');
								return(p1 + p3);
							}
						);
					}
	 
	// [[wikEdLinkName]]
					else {
						p2 = p2.replaceAll("^(\s*)([^<>]+)/, '$1<span class="wikEdLinkName">$2</span><!--wikEdLinkText-->');
					}
					p2 = p2.replaceAll("^(\s*<span class=\"wikEdLink(Target|Name)\">)(([\w ]*:)+)/, '$1<span class="wikEdInter">$3</span><!--wikEdInter-->');
				}
	 
	// link tags
				p1 = p1.replaceAll("(\[+)/, '<span class="wikEdLinkTag">$1</span><!--wikEdLinkTag-->');
				p2 = p2.replaceAll("(\|)/g, '<span class="wikEdLinkTag">$1</span><!--wikEdLinkTag-->');
				p3 = p3.replaceAll("(\]+)/, '<span class="wikEdLinkTag">$1</span><!--wikEdLinkTag-->');
				return(p1 + p2 + p3);
			}
		);
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime) {
				return;
			}
		}
	 
	// signature ~~~~
		html = html.replaceAll("(~{3,5})/g, '<span class="wikEdSignature">$1</span><!--wikEdSignature-->');
	 
	// magic words
		var regExp = new RegExp('(__' + wikEdMagicWords + '__)', 'gi');
		html = html.replace(regExp, '<span class="wikEdMagic">$1</span><!--wikEdMagic-->');
	 
	// template parameter {{{parameter|default}}}
		html = html.replaceAll("(\{\{\{)(\s*)([^\{\}\|]*?)(\s*)(\|.*?)?(\}\}\})/g,	'<span class="wikEdTemplTag">{{</span><!--wikEdTemplTag--><span class="wikEdTemplTag">{</span><!--wikEdTemplTag-->$2<span class="wikEdTemplParam">$3</span><!--wikEdTemplParam-->$4$5<span class="wikEdTemplTag">}</span><!--wikEdTemplTag--><span class="wikEdTemplTag">}}</span><!--wikEdTemplTag-->');
	 
	// parser variables and functions
	 
	// {{VARIABLE}} start
		var regExp = new RegExp('(\\{\\{)(\\s*)((' + wikEdTemplModifier + '):\\s*)?(' + wikEdParserVariables + ')(\\s*)(\}\})', 'g');
		html = html.replace(regExp, '<span class="wikEdTempl"><span class="wikEdTemplTag">$1</span><!--wikEdTemplTag-->$2$3<span class="wikEdParserFunct">$5</span><!--wikEdParserFunct-->$6<span class="wikEdTemplTag">$7</span><!--wikEdTemplTag--></span><!--wikEdTempl-->');
	 
	// parser {{VARIABLE:R}} start
		var regExp = new RegExp('(\\{\\{)(\\s*)((' + wikEdTemplModifier + '):\\s*)?(' + wikEdParserVariablesR + ')(:\\s*R)?(\\s*)(\}\})', 'g');
		html = html.replace(regExp, '<span class="wikEdTempl"><span class="wikEdTemplTag">$1</span><!--wikEdTemplTag-->$2$3<span class="wikEdParserFunct">$5</span><!--wikEdParserFunct-->$6$7<span class="wikEdTemplTag">$8</span><!--wikEdTemplTag--></span><!--wikEdTempl-->');
	 
	// parser {{FUNCTION:param|R}} start
		var regExp = new RegExp('(\\{\\{)(\\s*)((' + wikEdTemplModifier + '):\\s*)?(' + wikEdParserFunctionsR + '):', 'g');
		html = html.replace(regExp, '<span class="wikEdTempl"><span class="wikEdTemplTag">$1</span><!--wikEdTemplTag-->$2$3<span class="wikEdParserFunct">$5</span><!--wikEdParserFunct-->:');
	 
	// parser {{function:param|param}} start
		var regExp = new RegExp('(\\{\\{)(\\s*)((' + wikEdTemplModifier + '):\\s*)?(' + wikEdParserFunctions + '):', 'gi');
		html = html.replace(regExp, '<span class="wikEdTempl"><span class="wikEdTemplTag">$1</span><!--wikEdTemplTag-->$2$3<span class="wikEdParserFunct">$5</span><!--wikEdParserFunct-->:');
	 
	// parser {{#function:param|param}} start
		var regExp = new RegExp('(\\{\\{)(\\s*)((' + wikEdTemplModifier + '):\\s*)?#(' + wikEdParserFunctionsHash + '):', 'gi');
		html = html.replace(regExp, '<span class="wikEdTempl"><span class="wikEdTemplTag">$1</span><!--wikEdTemplTag-->$2$3<span class="wikEdParserFunct">#$5</span><!--wikEdParserFunct-->:');
	 
	// parser function modifier
		var regExp = new RegExp('(<span class="wikEdTemplTag">\\{\\{</span><!--wikEdTemplTag-->)(' + wikEdTemplModifier + '):', 'gi');
		html = html.replace(regExp, '$1<span class="wikEdTemplMod">$2</span><!--wikEdTemplMod-->:');
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime) {
				return;
			}
		}
	 
	// simple non-nested {{templates}}
//	                         1      12    234                          4     3 5                             56      6
		var regExp = new RegExp('(\\{\\{)(\\s*)((' + wikEdTemplModifier + '):\\s*)?([^\\{\\}\\<\\>\\x00\\x01\\n]+)(\\}\\})', 'gi');
		html = html.replace(regExp,
			function (p, p1, p2, p3, p4, p5, p6) {
				p3 = p3 || '';
				p4 = p4 || '';
	 
	// template tags
				p1 = '<span class="wikEdTemplTag">' + p1 + '</span><!--wikEdTemplTag-->';
				p6 = '<span class="wikEdTemplTag">' + p6 + '</span><!--wikEdTemplTag-->';
	 
	// get url
				var linkMatch = p5.match(/^\s*(([\wŠŒŽšœžŸÀ-ÖØ-öø-\u0220\u0222-\u0233ΆΈΉΊΌΎΏΑ-ΡΣ-ώ\u0400-\u0481\u048a-\u04ce\u04d0-\u04f5\u04f8\u04f9]*\s*:)*)\s*([^\|]+)/);
				var linkParam = '';
				var linkInter;
				var templClass = 'wikEdTempl';
				if (linkMatch != null) {
					linkInter = linkMatch[1];
					if (linkInter == '') {
						linkInter = wikEdText['wikicode Template'] + ':';
					}
					else {
						templClass = 'wikEdTemplInter';
					}
					linkParam = WikEdFollowLinkUrl(linkInter, linkMatch[3]);
				}
				p1 = '<span class="' + templClass + '" ' + linkParam + '>' + p1;
				p6 = p6 + '</span><!--' + templClass + '-->';
	 
				p3 = p3.replaceAll("^(.*?)(:\s*)$/, '<span class="wikEdTemplMod">$1</span><!--wikEdTemplMod-->$2');
				p5 = p5.replaceAll("^(\s*)((\w*:)+)/, '$1<span class="wikEdInter">$2</span><!--wikEdInter-->');
				p5 = p5.replaceAll("(\s*)([^>:\|]+)(\s*\|\s*|$)/, '$1<span class="wikEdTemplName">$2</span><!--wikEdTemplName-->$3');
				p5 = p5.replaceAll("(\|\s*)(.*)/,
					function (p, p1, p2) {
						p2 = p2.replaceAll("(.*?)(\s*(\||$))/g, '<span class="wikEdTemplText">$1</span><!--wikEdTemplText-->$2');
						return(p1 + p2);
					}
				);
	 
	// template tags
				p5 = p5.replaceAll("(\|)/g, '<span class="wikEdTemplTag">$1</span><!--wikEdTemplTag-->');
	 
				return(p1 + p2 + p3 + p5 + p6);
			}
		);
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime) {
				return;
			}
		}
	 
	// template start
//	                         1      12    234                          4     3 5                                5
		var regExp = new RegExp('(\\{\\{)(\\s*)((' + wikEdTemplModifier + '):\\s*)?([^\\{\\}\\<\\>\\x00\\x01\\n\\|]+)', 'gi');
		html = html.replace(regExp,
			function (p, p1, p2, p3, p4, p5) {
				p3 = p3 || '';
				p4 = p4 || '';
	 
	// template tags
				p1 = '<span class="wikEdTemplTag">' + p1 + '</span><!--wikEdTemplTag-->';
	 
				var linkMatch = p5.match(/^\s*(([\wŠŒŽšœžŸÀ-ÖØ-öø-\u0220\u0222-\u0233ΆΈΉΊΌΎΏΑ-ΡΣ-ώ\u0400-\u0481\u048a-\u04ce\u04d0-\u04f5\u04f8\u04f9]*\s*:)*)\s*([^\|]+)/);
				var linkParam = '';
				var linkInter;
				var templClass = 'wikEdTempl';
				if (linkMatch != null) {
					linkInter = linkMatch[1];
					if (linkInter == '') {
						linkInter = wikEdText['wikicode Template'] + ':';
					}
					else {
						templClass = 'wikEdTemplInter';
					}
					linkParam = WikEdFollowLinkUrl(linkInter, linkMatch[3]);
				}
				p1 = '<span class="wikEdTemplHide" title="' + wikEdText['wikEdTemplHideTooltip'] + '"></span><!--wikEdTemplHide--><span class="' + templClass + '" ' + linkParam + '>' + p1;
	 
				p3 = p3.replaceAll("^(.*?)(:\s*)$/, '<span class="wikEdTemplMod">$1</span><!--wikEdTemplMod-->$2');
				p5 = p5.replaceAll("^(\s*)((\w*:)+)/, '$1<span class="wikEdInter">$2</span><!--wikEdInter-->');
				p5 = p5.replaceAll("(\s*)([^>:\|]+)(\s*\|\s*|$)/, '$1<span class="wikEdTemplName">$2</span><!--wikEdTemplName-->$3');
				p5 = p5.replaceAll("(\|\s*)(.*)/,
					function (p, p1, p2) {
						p2 = p2.replaceAll("(.*?)(\s*(\||$))/g, '<span class="wikEdTemplText">$1</span><!--wikEdTemplText-->$2');
						return(p1 + p2);
					}
				);
				return(p1 + p2 + p3 + p5);
			}
		);
	 
	// highlighting curly template brackets at template end
		html = html.replaceAll("(\}\})(?!<\/span><!--wikEd(Templ|TemplInter|TemplTag)-->)/g, '$1</span><!--wikEdTempl-->');
		html = html.replaceAll("(\}\})(?!<\/span><!--wikEdTemplTag-->)/g, '<span class="wikEdTemplTag">$1</span><!--wikEdTemplTag-->');
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime) {
				return;
			}
		}
	 
	// highlight images
//	                            1      123                                                                                 3     24                45   6     6  57     7
//	                            ( [[   )((Image|File|    Image                          |    File                          )  :  )( name           )(   (     )  )(     ) (                             )
		var regExpImg = new RegExp('(\\[\\[)((Image|File|' + wikEdText['wikicode Image'] + '|' + wikEdText['wikicode File'] + ') *: *)([^\\[\\]\\|\\n]*)(\\|(.|\\n)*?)(\\]\\])(?!<\/span><!--wikEdLinkTag-->)', 'gi');
		html = html.replace(regExpImg,
			function (p, p1, p2, p3, p4, p5, p6, p7) {
				var linkTitle = p4;
				linkTitle = linkTitle.replaceAll("\|.*()/g, '');
				linkTitle = linkTitle.replaceAll("\n.*()/g, '');
				p1 = '<span class="wikEdImage"' + WikEdFollowLinkUrl(p2, linkTitle) + '>' + p1;
				p7 = p7 + '</span><!--wikEdImage-->';
				p2 = '<span class="wikEdImageName">' + p2;
				p4 = p4 + '</span><!--wikEdImageName-->';
	 
	// parameters and capture
				p5 = p5.replaceAll("((<span [^>]*>)?\|(<\/span [^>]*>)?)([^\|]*?)/g,
					function (p, p1, p2, p3, p4) {
						if ( (p2 == '') && (p3 == '') ) {
							if (/^(thumb|thumbnail|frame|right|left|center|none|\d+px|\d+x\d+px)$/.test(p4) == true) {
								p4 = '<span class="wikEdImageParam">' + p4 + '</span><!--wikEdImageParam-->';
							}
							else {
								p4 = '<span class="wikEdImageCaption">' + p4 + '</span><!--wikEdImageCaption-->';
							}
						}
						return(p1 + p4);
					}
				);
	 
	// link tags
				p1 = p1.replaceAll("(\[+)/, '<span class="wikEdLinkTag">$1</span><!--wikEdLinkTag-->');
				p7 = p7.replaceAll("(\]+)/, '<span class="wikEdLinkTag">$1</span><!--wikEdLinkTag-->');
				p5 = p5.replaceAll("(\|)/g, '<span class="wikEdLinkTag">$1</span><!--wikEdLinkTag-->');
				return(p1 + p2 + p4 + p5 + p7);
			}
		);
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime) {
				return;
			}
		}
	 
	// <b> <i>
		html = html.replaceAll("(\'\'\')(\'*)(.*?)(\'*)(\'\'\')/g, '<span class="wikEdBold">$2$3$4</span><!--wikEdBold-->');
		html = html.replaceAll("(\'\')(.*?)(\'\')/g, '<span class="wikEdItalic">$1$2$3</span><!--wikEdItalic-->');
		html = html.replaceAll("(<span class=\"wikEdBold\">)/g, '$1\'\'\'');
		html = html.replaceAll("(<\/span><!--wikEdBold-->)/g, '\'\'\'$1');
		html = html.replaceAll("(\'{2,})/g, '<span class="wikEdWiki">$1</span><!--wikEdWiki-->');
	 
	// nowiki (remove highlighting)
		html = html.replaceAll("(\x00nowiki\b[^\x01]*\x01)((.|\n)*?)(\x00\/nowiki\x01)/gi,
			function (p, p1, p2, p3, p4) {
				p1 = '<span class="wikEdNowiki"><span class="wikEdInlineTag">' + p1 + '</span><!--wikEdInlineTag-->';
				p2 = p2.replaceAll("<[^>]*>/g, '');
				p4 = '<span class="wikEdInlineTag">' + p4 + '</span><!--wikEdInlineTag--></span><!--wikEdNowiki-->';
				return(p1 + p2 + p4);
			}
		);
	 
	// check spent time
		if (noTimeOut != true) {
			if (new Date() - startDate > wikEdMaxHighlightTime) {
				return;
			}
		}
	 
	// suppress hiding if no other content than template in ref
		html = html.replaceAll("(<span class=\"wikEdRefHide\">(\s*|<[^>]*>|\x00ref\b[^\x01]*\x01)*<span class=\"wikEdTemplHide)(\">)/g, '$1Suppr$3');
	 
	// \x00 and \x01 back to &lt; and &gt;
		html = html.replaceAll("\x00/g, '&lt;');
		html = html.replaceAll("\x01/g, '&gt;');
		html = html.replaceAll("\x02/g, '&amp;');
	 
	// control character highlighting
		var regExp = new RegExp('([' + wikEdControlCharHighlightingStr + '])', 'g');
		html = html.replace(regExp,
			function (p, p1) {
				p1 = '<span class="wikEdCtrl" title="' + wikEdControlCharHighlighting[p1.charCodeAt(0).toString()] + '">' + p1 + '</span><!--wikEdCtrl-->';
				return(p1);
			}
		);
	 
	// single character highlighting: spaces, dashes
		var regExp = new RegExp('<[^>]*>|([' + wikEdCharHighlightingStr + '])', 'g');
		html = html.replace(regExp,
			function (p, p1) {
				p1 = p1 || '';
				if (p1 != '') {
					var decimalValue = p1.charCodeAt(0).toString();
					var titleClass = wikEdCharHighlighting[decimalValue];
					p1 = '<span class="' + titleClass + '" title="' + wikEdText[titleClass] + '">' + p1 + '</span><!--' + titleClass + '-->';
					return(p1);
				}
				else {
					return(p);
				}
			}
		);
	 
	// fix single line spans interfering with opening multi-line tags
		html = html.replaceAll("(<span\b[^>]*?\bclass=\"(wikEdBlockTag|wikEdRefHide|wikEdTemplHide)\"[^>]*>)(.*?)(<\/span><!--(wikEdSpaceLine|wikEdListLine|wikEdTableLine)-->)/g, '$4$1$3');
		html = html.replaceAll("(<\/span><!--(wikEdBlockTag|wikEdRefHide|wikEdTemplHide)-->)(.*?)(<\/span><!--(wikEdSpaceLine|wikEdListLine|wikEdTableLine)-->)/g, '$4$1$3');
	 
	// remove comments
		if (wikEdRemoveHighlightComments == true) {
			html = html.replaceAll("<!--wikEd\w+-->/g, '');
		}
	 
		obj.html = html;
		return;
		 */	 
	}
}
