package com.thuvienkhoahoc.wordtomwtext;

import org.apache.poi.hwpf.HWPFDocument; 
import org.apache.poi.hwpf.usermodel.Paragraph; 
import org.apache.poi.hwpf.usermodel.Table; 
import org.apache.poi.hwpf.usermodel.TableRow; 
import org.apache.poi.hwpf.usermodel.TableCell; 
import org.apache.poi.hwpf.usermodel.Range; 
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.model.PicturesTable;

import java.io.*; //tạm thời nhập tất cả các gói, sau này sẽ rút gọn
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
* TEST CODE ONLY. 
* @author Nguyễn Thế Phúc 
* @version 1.00 07 09 2009. 
*/ 

public class WordToMwtext { 
/*
* Hệ thống các messages
*/
public static final String MS_IMAGE = "Hình";
public static final String MS_LEFT = "trái";
public static final String MS_CENTER = "giữa";
public static final String MS_RIGHT = "phải";
public static final String MS_CAPTION = "Hình minh họa";
public static final String SPACE = " ";
public static final String DIR_NAME = "thuvienkhoahoc.com";
public static final String TAG_HEADER[] = {"","=","==","===","====","=====","======","=======","========","========="};
/*
*  Các biến toàn cục trong lớp
*/
Writer _out;
HWPFDocument doc;
StyleSheet styleSheet;
PicturesTable picTable;
/*
* Tên tệp MS Word đầu vào, với phiên bản full biến này sẽ nhận từ giao diện người dùng
* Chỉ viết phần main name, còn extension name thì đã cộng ở dưới
* Chúng ta làm vậy, vì biến này còn dùng để đặt tên cho file ảnh nếu có
*/
String nameInput = "Bai_giang_thao_giang_11.2007";
/*
* Dùng một chuỗi kí tự để tạo mã list {#, *} cho kiểu danh sách
*/
String mwUlList = "";
/*
* Chúng ta dùng một biến đếm số lần xuống hàng/Enter cho mỗi đoạn văn
* Giá trị của nó tùy vào từng loại/type đoạn văn, hai biến này đi đôi với nhau,
* sau khi Enter[numEnter] được gọi thì numEnter sẽ được gán lại giá trị mặc định 
* 2 - đoạn văn thông thường
* 0 - đoạn văn là danh sách
* 1 - đoạn văn cuối cùng trong một cột của bảng 
*/
byte numEnter = 2;//mặc định là đoạn văn thông thường
String Enter[] = {"","\n","\n\n"};
/*
* Số ô tối đa của bảng cùng với chiều rộng của hàng đó
* nhằm tạo giá trị colspan cho mỗi cell
*/
int maxColTable;
int maxWidTable;
/*
* Biến tạo tag html
*/
String tagOpen;
String tagClose;
/*
* Các kí tự điều khiển, chúng dùng để nhận dạng các phần tử đặc biệt trong word
* chẳng hạn như: HYPERLINK, EQUATION, PICTURE, etc
*/
String u13 = String.valueOf('\u0013');//bắt đầu một phần tử
String u01 = String.valueOf('\u0001');//phân cách trong hyperlink
String u07 = String.valueOf('\u0007');//kết thúc một hàng trong bảng
String u0C = String.valueOf('\u000C');//chưa biết kí tự này có chức năng gì?
String u14 = String.valueOf('\u0014');//dính liền đằng sau u01
String u15 = String.valueOf('\u0015');//kết thúc một phần tử
String u22 = String.valueOf('\u0022');//kí tự nháy kép (")
/*
* Các hàm nhận dạng bắt đầu và kết thúc một phần tử đặc biệt
*/
protected boolean startObject(String text){
	return (text.indexOf(u13) > -1);
}

protected boolean endObject(String text){
	return (text.indexOf(u15) > -1);
}
/*
* Hàm convert hyperlink sang wiki link, trong word có 4 dạng hyperlink chủ yếu:
* 	1) Bookmark
* 		liên kết tới một đoạn text đã được bookmark hoặc tới một header trong cùng
* 		1 file
* 	2) Website hoặc email
* 		liên kết tới một trang mạng qua url của nó hoặc email
* 	3) Folder
* 		liên kết tới một thư mục
* 	4) File
* 		liên kết đến một tệp tin
* Các biểu thức chính quy dưới đây khá phức tạp, đừng sửa đổi chúng nếu bạn không
* hiểu gì về nó.
*/
protected String linkToWiki(String text){
	String mwText ="";
	Pattern pattern;
	Matcher matcher;
	// HYPERLINK  \l "book mark name" real name 
	String interLink = "("+u13+" HYPERLINK  )(.*) ("+u22+")(.*)("+u22+" "+u01+u14+")(.*)"+"("+u15+")";
	// HYPERLINK "url website" real name
	String extraLink = "("+u13+" HYPERLINK "+u22+")(.*)("+u22+" "+u01+u14+")(.*)"+"("+u15+")";
	// HYPERLINK "folder name" real name
	String folderLink = "("+u13+" HYPERLINK "+u22+")(.*)("+u22+" "+u01+u14+")(.*)"+"("+u15+")";
	//HYPERLINK "file name.ext"real name
	String fileLink = "("+u13+"HYPERLINK "+u22+")(.*)("+u22+u01+u14+")(.*)"+"("+u15+")";
	
	//liên kết đến bookmark hoặc header
	if (text.indexOf(" HYPERLINK  ") >-1 ){
		pattern = Pattern.compile(interLink);
		matcher = pattern.matcher(text);
		mwText = matcher.replaceAll("[[#$4|$6]]");
		mwText = mwText.replaceAll("#_","#");
	}
	else 
	//liên kết đến website hoặc email
	if ((text.indexOf("http:") >-1)||(text.indexOf("mailto:") >-1)) {
		pattern = Pattern.compile(extraLink);
		matcher = pattern.matcher(text);
		mwText = matcher.replaceAll("[$2 $4]");
	}
	else
	//liên kết đến thư mục
	if (text.indexOf(" HYPERLINK") > -1){
		pattern = Pattern.compile(folderLink);
		matcher = pattern.matcher(text);
		mwText = matcher.replaceAll("[[$2|$4]]");
	}
	else
	//liên kết đến tệp tin
	{
		pattern = Pattern.compile(fileLink);
		matcher = pattern.matcher(text);
		mwText = matcher.replaceAll("[[$2|$4]]");
	}
	return mwText;
}

/*
* Lấy màu của font chữ 
*/
protected String getColor(int color){
	switch(color){
		case -1: // mặc định
		return "";
		case 0x000000:
		return "black";
		case 0xFF0000:
		return "blue";
		case 0xFFFF00: 
		return "cyan";
		case 0x00FF00:
		return "green";
		case 0xFF00FF: 
		return "magenta";
		case 0x0000FF: 
		return "red";
		case 0x00FFFF: 
		return "yellow";
		case 0x0FFFFFF: 
		return "white";
		case 0x800000: 
		return "darkblue";
		case 0x808000: 
		return "darkcyan";
		case 0x008000: 
		return "darkgreen";
		case 0x800080: 
		return "darkmagenta";
		case 0x000080: 
		return "darkred";
		case 0x008080: 
		return "darkyellow";
		case 0x808080: 
		return "darkgrey";
		case 0xC0C0C0: 
		return "lightgrey";
	}
	return "";
}
/*
*  Chuyển đổi size font MS Word sang size font html, có tính chất tương đối
*   pt size, 2*pt size html size 
*      6       12          1
*      8       16          1
*      10      20          2
*      12      24          3
*      14      28          4
*      16      32          4
*      18      36          5
*      20      40          5
*      24      48          6
*      28      56          6
*      32      64          7
*      36      72          7
*/ 
protected int getHtmlFontSize(int size){
	if      (size < 10) return 1; 
	else if (size < 24) return 2; 
	else if (size < 28) return 3; 
	else if (size < 36) return 4; 
	else if (size < 48) return 5; 
	else if (size < 64) return 6;
	return 7;
}  
/*
* Thêm tag wikitext vào cuối đoạn text của CharacterRun
* Cộng vào đằng trước kí tự xuống hàng (nếu có)
*/
protected String addMore(String str, String more) {
	String reStr = str;
	int length = reStr.length();
	if (reStr.endsWith("\r")) reStr = reStr.substring(0, length-1) + more+"\r";
	else reStr = reStr + more;
	return reStr;
}
/*
* Convert CharacterRun chứa image thành wikitext và copy file image đó vào thư mục DIR_NAME
* Chú ý rằng, một đối tượng Picture có thể là một trong các đối tượng
* {image, shape, wmf,...} và hiện tại chương trình chỉ convert image
*/
protected String imagesToWiki(CharacterRun run)
throws IOException, UnsupportedEncodingException {
	String textR="";
	Picture pic = picTable.extractPicture(run,false);
	if (pic.suggestFileExtension().length() > 0){
		String namePic = nameInput+"-"+pic.suggestFullFileName();
		OutputStream outPic = new FileOutputStream(DIR_NAME+"\\"+namePic);
		pic.writeImageContent(outPic);
		textR += "[["+MS_IMAGE+":"+namePic+"|"+MS_CENTER+"|150px|"+MS_CAPTION+"]]";
	}
	return textR;
}
/*
* Thiết lập các tùy chọn cho font
*/ 
protected String getOptionFont(CharacterRun run) {
	String optionFont="";
	boolean hasContent = run.text().trim().length()>0;
	if (hasContent){
		String font = run.getFontName();
		if (!font.equals("Times New Roman")) optionFont = " face=\""+font+"\"";
		int size = getHtmlFontSize(run.getFontSize());
		if (size != 3) optionFont = optionFont + " size="+size;
		int color = run.getIco24();
		if (color != -1) optionFont = optionFont + " color=\""+getColor(color)+"\"";
	}
	return optionFont;
}
/*
* Tạo các tag html
*/
protected void getTagHtml(CharacterRun run) {
	String textR;
	short ssIndex = run.getSubSuperScriptIndex();
	boolean isDeleted = run.isMarkedDeleted() || run.isFldVanished() || run.isStrikeThrough() || run.isDoubleStrikeThrough();    
	textR = run.text();
	tagOpen = "";
	tagClose = "";
	String optionFont = getOptionFont(run);
	if (optionFont.length() > 0) {
		tagOpen = "<font"+optionFont+">";
		tagClose = "</font>";
	}
	if (textR.trim().length() > 0){
		if (run.isBold()) {					tagOpen  = tagOpen +  "<b>"; 		tagClose = "</b>" 	+ tagClose; }
		if (run.isItalic()) {				tagOpen  = tagOpen +  "<i>"; 		tagClose = "</i>" 	+ tagClose; }
		if (run.getUnderlineCode() > 0) {	tagOpen  = tagOpen +  "<u>"; 		tagClose = "</u>" 	+ tagClose;}
		if (isDeleted) {					tagOpen  = tagOpen +  "<del>"; 		tagClose = "</del>" 	+ tagClose; }
		if (ssIndex == 1) {					tagOpen  = tagOpen +  "<sup>"; 		tagClose = "</sup>" 	+ tagClose; }
		if (ssIndex == 2) {					tagOpen  = tagOpen +  "<sub>"; 		tagClose = "</sub>" 	+ tagClose; }
		if (run.isHighlighted()) {			tagOpen  = tagOpen +  "<em>"; 		tagClose = "</em>" 	+ tagClose; }
	}
}
/*
* Hàm trả về mã tag wikitext cho danh sách list (ul) là ol (*) hay li (#)
* Bullet thành *, còn Numbering thành #.
* Nhưng vấn đề là giá trị nào, phương thức nào trong POI cho biết đó là
* Bullet hay Numbering
*/
protected String mwLi(int ilfo){
	String symbol = "";
	if (ilfo==1) symbol = "#";
	else
	if (ilfo > 0) symbol = "*";
	return symbol;
}
/*
* Convert một đoạn văn (Paragraph) thành wikitext
* Đầu tiên chúng ta cần convert các đặc trưng (CharacterRun)
* sang wikitext, sau đó cập nhật mã wikitext cho kiểu của đoạn văn
* có thể là {Title, Header, Caption, ...} 
*/
protected String paraToWiki(Paragraph para)
throws IOException, UnsupportedEncodingException {
	String mwText ="";
	String tagOpenOld = "";
	String tagCloseOld = "";
	int headerLevel = 0;
	boolean isCaption = false;
	int ilfo = para.getIlfo();//kiểu list nào {Bullet, Numbering}
	int ilvl = para.getIlvl() + 1;//cấp độ, hay mức lùi vào đầu dòng
	StyleDescription paragraphStyle = styleSheet.getStyleDescription (para.getStyleIndex ());
	String styleName = paragraphStyle.getName();
	/*
	* Nhận dạng kiểu Paragraph = {Heading, Caption,...}
	* Việc căn cứ vào getName() có thể không chính xác vì người dùng có thể tự tạo Heading
	* và Title cá nhân. Nhưng chúng ta chấp nhận phương án này, vì hiện tại POI chưa hỗ trợ
	* "Level" của đoạn văn
	*/
	if (styleName.startsWith ("Caption")) isCaption = true;
	if (styleName.startsWith ("Heading")) 
		headerLevel = Integer.parseInt (styleName.substring (8));
	/*
	* Convert các CharacterRun của đoạn văn
	*/
	String strObject="";
	boolean object = false;
	boolean hasContent;
	boolean hasTagOpen;
	int numRun = para.numCharacterRuns();
	for (int z = 0; z < numRun; z++) {
		CharacterRun run = para.getCharacterRun(z);
		if (picTable.hasPicture(run)) mwText += imagesToWiki(run);
		else
		if (startObject(run.text())) {object = true; strObject = run.text();}
		else
		if (object){
			strObject = strObject + run.text();
			if (endObject(run.text())) {
				if (strObject.indexOf("HYPERLINK") > -1) mwText += linkToWiki(strObject);
				object = false;
				strObject = "";
			}
		}
		else {
			String runText = run.text();
			runText = runText.replace(u07,"");
			runText = runText.replace(u0C,"");
			runText = runText.replace(u01,"");
			hasContent = run.text().trim().length()>0;
			if (hasContent){
				getTagHtml(run);
				hasTagOpen = tagOpen.length()>0;
				if (hasTagOpen){
					if (!tagOpen.equals(tagOpenOld)) {
						if (tagOpenOld.length()>0) mwText = addMore(mwText,tagCloseOld);
						mwText += tagOpen;
						tagOpenOld = tagOpen;
						tagCloseOld = tagClose;
					}
					if (z == numRun-1) runText = addMore(runText,tagClose);
				}
				else 
				if (tagOpenOld.length()>0){
					mwText = addMore(mwText,tagCloseOld);
					tagOpenOld = ""; tagCloseOld = ""; 
				}
			}
			else 
			if ((z == numRun-1)&&(tagOpenOld.length()>0)) mwText = addMore(mwText,tagCloseOld);
			mwText += runText;
		}
	}
	/*
	* Bổ sung định dạng cho các loại paragraph
	* Hiển nhiên, chúng ta chỉ làm việc này nếu đoạn văn có nội dung
	*/
	if (mwText.length() > 0){
		numEnter = 2;
		/*
		* Kiểm tra xem đoạn có phải là danh sách hay không
		*/
		if (ilfo > 0){
			String symbol_info = mwLi(ilfo);
			int length = mwUlList.length();
			if (length < ilvl) mwUlList += symbol_info;
			else
			if (length > ilvl) mwUlList = mwUlList.substring(0, ilvl-1) + symbol_info;
			else 
			if (!mwUlList.endsWith(symbol_info)){
				if (length > 1) mwUlList = mwUlList.substring(0, length-2) + symbol_info;
				else mwUlList = symbol_info;
			}
			mwText = mwUlList + mwText;
			numEnter = 1;
		}
		else 
		mwUlList = "";

		if (isCaption) mwText = "<center>"+mwText+"</center>";
		if ((headerLevel > 0)&&(headerLevel <= 9)) {
			mwText = addMore(mwText,SPACE + TAG_HEADER[headerLevel]);
			mwText = TAG_HEADER[headerLevel] + SPACE + mwText;
		}
	}
	return mwText;
}
/*
* Xóa các kí tự trắng ở đầu chuỗi
*/
protected String trimLeft(String line) {
	int start = 0;
	String whiteSpaceChars = " \t\f";
	while (start < line.length()){
		if (whiteSpaceChars.indexOf(line.charAt(start)) == -1) {
			break;
		}
		start++;
	}
	return line.substring(start);
}
/*
* Hàm trả về số cột tối đa trong bảng cùng với chiều rộng của hàng tương ứng
*/ 
protected void maxColWidth(Table table){
	maxColTable = 1;
	maxWidTable = 0;
	int row=0;
	for(int i = 0; i < table.numRows(); i++)
		if (table.getRow(i).numCells() > maxColTable) {
			maxColTable = table.getRow(i).numCells();
			row = i;
		}
	for (int j = 0; j < maxColTable; j++) maxWidTable += table.getRow(row).getCell(j).getWidth();
}
/*
* Hàm lấy colspan cho một cell từ chiều rộng của nó
*/
protected int sugColSpan(int width){
	float frac = (float) maxColTable/maxWidTable*width;
	return (int) Math.round(frac);
}
/*
* Convert một bảng đơn thành bảng wikitext. Bảng đơn, là bảng không chứa bảng con trong nó
* Có 2 loại bảng đơn:
*  Bảng chuẩn NxM, có N hàng và mỗi hàng có M ô
*  Bảng merge, số ô của mỗi hàng không bằng nhau, do có sự trộn các ô cùng cột hoặc cùng hàng
* Vì thế, để sinh mã wikitext cho bảng chúng ta phải biết rowspan và colspan của mỗi ô là bao nhiêu?
* Ví dụ, với bảng chuẩn thì rowspan và colspan của mỗi ô là 1-1. 
* POI chỉ hỗ trợ phương thức nhận dạng các ô được trộn theo cột (chiều dọc)
* và không có phương thức nhận dạng ô được trộn theo hàng (chiều ngang)
* 
* Tuy nhiên, "ý tưởng thông minh" dưới đây sẽ cho ta kết quả mong muốn.
*          Số colspan được tính bởi công thức: colspan = (width/maxWidTable)*maxColTable
*          Số rowpan được tính bằng cách đếm số ô được trộn theo chiều dọc từ dưới lên
* Trong đó:    maxColTable là số ô tối đa theo hàng ngang
*              maxWidTable là chiều rộng của hàng có số cột tối đa.
*              width là chiều rộng của ô cần tính colspan
* 
*/ 
protected String tableToWiki(Table table)
throws IOException, UnsupportedEncodingException {
	maxColWidth(table);
	String mwText = "";
	int rowspan[] = new int[maxColTable];
	for (int i = 0; i < maxColTable; i++) rowspan[i] = 1;
	for (int i = table.numRows()-1; i > -1  ; i--){
		TableRow row = table.getRow(i);
		String strRow = "\n|- valign=\"top\"";
		int numCell = row.numCells();
		int cellAdded = 0;
		for (int j = 0; j < numCell; j++){
			String optionSpan = "";
			int colspan = 1;
			TableCell cell = row.getCell(j);
			if (numCell < maxColTable){
				colspan = sugColSpan(cell.getWidth());
				if (colspan > 1) optionSpan += "colspan=\""+colspan+"\" ";
			}
			int numP = cell.numParagraphs();
			String strCells ="";
			for (int k=0; k < numP; k ++){
				Paragraph para = cell.getParagraph(k);
				strCells += paraToWiki(para);
				if (k < numP-1) strCells += Enter[numEnter];
				numEnter = 2;
			}
			strCells = trimLeft(strCells);
			if (strCells.startsWith("-")||strCells.startsWith("*")
			||strCells.startsWith("#")||strCells.startsWith(":")) strCells = "\n" + strCells;
			if (cell.isVerticallyMerged()){
				if (cell.isFirstVerticallyMerged()){
					optionSpan += "rowspan=\""+rowspan[cellAdded]+"\"|";
					strRow += "\n|" + optionSpan + strCells;
					for (int k=cellAdded;k < cellAdded+colspan;k++) rowspan[k] = 1;
				}
				else
					for (int k=cellAdded;k < cellAdded+colspan;k++) rowspan[k] += 1;
			}
			else
			if (optionSpan.trim().length()==0) strRow += "\n|" + strCells;
			else
				strRow += "\n|" + optionSpan.trim() + "|" + strCells;
			cellAdded += colspan;
		}
		mwText = strRow + mwText;
	}
	mwText = "{|class=\"wikitable\" width=\"100%\"" + mwText + "\n|}"+Enter[1];
	return mwText;
}
/*
* Thiết lập các thông số đầu vào, đầu ra
*/ 
public WordToMwtext(OutputStream stream)
throws IOException, UnsupportedEncodingException  {
	OutputStreamWriter out = new OutputStreamWriter (stream, "UTF-8");
	_out = out;
	Range range = null; 
	Table table = null; 
	Paragraph para = null; 
	boolean inTable = false; 
	int numParas = 0; 
	doc = new HWPFDocument(new FileInputStream(nameInput+".doc"));
	range = doc.getRange(); 
	styleSheet = doc.getStyleSheet();
	picTable = doc.getPicturesTable();
	/*
	* Tổng số đoạn văn trong tệp
	*/
	numParas = range.numParagraphs(); 
	/* 
	* Với mỗi đoạn văn, kiểm tra xem nó có nằm trong bảng không,
	* nếu đúng thì convert bảng
	*/ 
	for(int i = 0; i < numParas; i++) { 
		para = range.getParagraph(i); 
		if(para.isInTable()) { 
			/*
			* Vì phương thức getTable() chỉ được gọi MỘT LẦN DUY NHẤT cho mỗi bảng
			* nên chúng ta cần đánh dấu bảng này đã được xử lí chưa
			*/ 
			if(!inTable) { 
				/*
				* Convert bảng, ghi vào file
				*/ 
				table = range.getTable(para);
				_out.write(tableToWiki(table));
				inTable = true; 
			} 
		} 
		else { 
			/*
			* Nếu bảng đã được convert hoặc là đoạn văn không nằm trong bảng thì
			* tiến hành convert bình thường và cộng mã xuống hàng
			*/ 
			inTable = false;
			String mwText = paraToWiki(para);
			_out.write(mwText+Enter[numEnter]);
		}
		numEnter = 2;//bắt đầu một đoạn văn mới
	} 
	/*
	* Đóng tệp sau khi xử lí xong
	*/
	_out.close();
}//END wordtomwtext
/*
*/ 
public static void main(String[] args) { 
	try { 
		/*
		* Tạo thư mục chứa file mã nguồn wikitext và các image
		* Việc cần lựa chọn là nên tạo thư mục tạm thời (tmp) hay tạo normal folder rồi xóa
		*/
		File dir = new File(DIR_NAME);
		dir.mkdir();
		OutputStream out = new FileOutputStream(DIR_NAME+"\\mwtext.txt");
		new WordToMwtext(out);
	} 
	catch (Throwable t) {
		t.printStackTrace();
	}
}//END MAIN
}//END CLASS