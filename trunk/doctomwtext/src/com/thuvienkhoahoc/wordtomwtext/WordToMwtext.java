package com.thuvienkhoahoc.wordtomwtext;

import org.apache.poi.hwpf.HWPFDocument; 
import org.apache.poi.hwpf.usermodel.Paragraph; 
import org.apache.poi.hwpf.usermodel.Table; 
import org.apache.poi.hwpf.usermodel.TableRow; 
import org.apache.poi.hwpf.usermodel.TableCell; 
import org.apache.poi.hwpf.usermodel.Range; 
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.Picture;

//import java.lang.Math;
import java.io.*; //tam thoi dung tat ca cac goi, sau nay se rut gon

/** 
 * 
 * TEST CODE ONLY. 
 * 
 * @author Nguyen The Phuc 
 * @version 1.00 07 09 2009. 
 */ 

public class WordToMwtext { 
	//
	// messages: Hinh, trai, giua, phai, Hinh minh hoa
	//
	public static final String MS_IMAGE = "H\u00ECnh";
	public static final String MS_LEFT = "tr\u00E1i";
	public static final String MS_CENTER = "gi\u1EEFa";
	public static final String MS_RIGHT = "ph\u1EA3i";
	public static final String MS_CAPTION = "H\u00ECnh minh h\u1ECDa";
	//
	// ma xuong hang cua mediawiki (mw), thong thuong no se duoc cong them
	// sau khi ghi mot doan van vao tep
	//
	public static final String SPACE = " ";
	public static final String DIR_NAME = "thuvienkhoahoc.com";
	public static final String TAG_HEADER[] = {"","=","==","===","====","=====","======","=======","========"};
	//
	// cac bien toan cuc trong lop
	//
	Writer _out;
	HWPFDocument doc;
	StyleSheet styleSheet;
	PicturesTable picTable;
	String nameInput = "tablerow";
	String mwUlList ="";
	byte numEnter = 2;
	String Enter[] = {"","\n","\n\n"};
	int maxColTable;
	int maxWidTable;
	//
	// ham them tag mw vao duoi str cho mot character
	// no them tag mw vao dang truoc ki tu xuong hang neu co
	//
	protected String addMore(String str, String more) {
		String reStr = str;
		int length = reStr.length();
		if (reStr.endsWith("\r")) reStr = reStr.substring(0, length-1).trim()+ SPACE + more+"\r";
		else reStr = reStr.trim() + SPACE + more;
		return reStr;
	}
	//
	// ham convert mot doi tuong CharacterRun co chua images thanh wikitext
	// va copy cac file images nay vao thu muc DIR_NAME
	//
	protected String imagesToWiki(CharacterRun run)
		throws IOException, UnsupportedEncodingException 
	{
		String textR="";
			//
			// false de dung writeImageContent,
			// true thi phai dung getContent(pic) va read tung byte
			// neu la images, tuc co phan mo rong thi moi tao ma wiki
			//
			Picture pic = picTable.extractPicture(run,false);
			if (pic.suggestFileExtension().length() > 0){
				String namePic = nameInput+"-"+pic.suggestFullFileName();
				OutputStream outPic = new FileOutputStream(DIR_NAME+"\\"+namePic);
				pic.writeImageContent(outPic);
				textR += "[["+MS_IMAGE+":"+namePic+"|"+MS_CENTER+"|150px|"+MS_CAPTION+"]]";
			}
		return textR;
	}
	//
	// ham convert mot doi tuong CharacterRun thanh wikitext
	//
	protected String charToWiki(CharacterRun run)
		throws IOException, UnsupportedEncodingException 
	{	
		String textR;
		if (picTable.hasPicture(run)) {
			textR = imagesToWiki(run);
			numEnter = 1;
		}
		else
		{
			textR = run.text();
			String mwOpen = "";
			String mwClose = "";
			//
			// khong xu li dinh dang voi khoang trong, ki tu xuong hang, ket thuc bang
			// chu y rang trong MS word co nhieu dinh dang gach chan nen dieu kien
			// "getUnderlineCode()" la lon hon khong.
			// chung ta dung tag html cho cac dinh dang vi cac phien ban MW 1.14+ co ho tro chung
			// chung ta cong them open tag vao sau nhung close tag phai vao truoc
			//
			if (textR.trim().length() > 0){
				if (run.isBold()) { mwOpen = "<b>"; mwClose = "</b>"; }
				if (run.isItalic()) { mwOpen = mwOpen + "<i>"; mwClose =  "</i>" + mwClose; } 
				if (run.getUnderlineCode() > 0) { mwOpen = mwOpen + "<u>"; mwClose = "</u>" + mwClose;}
				if (run.isStrikeThrough()) { mwOpen = mwOpen + "<s>"; mwClose = "</s>" + mwClose; }
				if (run.isDoubleStrikeThrough()) { mwOpen = mwOpen + "<s>"; mwClose = "</s>" + mwClose; }//dong ke kep
				//
				// khi character co mot trong cac dinh dang tren thi convert
				//
				if (mwOpen.length () > 0){
					textR = addMore(textR,mwClose);
					textR = mwOpen + textR;
				}
			}
		}
		return textR.trim();
	}
	//
	// 
	//
	protected String mwLi(int ilfo){
		String symbol = "";
		if (ilfo==1) symbol = "#";
		else if (ilfo > 0) symbol = "*";
		return symbol;
	}
	//
	// ham convert mot doan van dang .doc sang dang ma mw text
	// dau tien ham se convert cac character neu co sang mw text
	// sau do bo sung them dinh dang title hay header neu co
	//
	protected String paraToWiki(Paragraph para)
		throws IOException, UnsupportedEncodingException  
	{
		String mwText ="";
		int headerLevel = 0;
		boolean isTitle = false;
		boolean isCaption = false;
		int ilfo = para.getIlfo();//is list info
		int ilvl = para.getIlvl() + 1;//is list level
		StyleDescription paragraphStyle = styleSheet.getStyleDescription (para.getStyleIndex ());
		String styleName = paragraphStyle.getName();
		//
		// viec can cu vao "Heading", "Title" co the khong chinh xac,
		// khi nguoi dung tu tao styleName cho Header cua ho
		// truoc mat, chung ta chap nhan phuong an nay
		// phuong an toi uu la can cu vao "Level" cua doan van, nhung POI chua ho tro no
		//
		if (styleName.startsWith ("Caption")) isCaption = true;
		if (styleName.startsWith ("Title")) isTitle = true;
		if (styleName.startsWith ("Heading")) 
			headerLevel = Integer.parseInt (styleName.substring (8));
		
		for (int z = 0; z < para.numCharacterRuns(); z++) {
			//
			// lay tung dac trung (dinh dang) trong doan van va convert sang ma mw text
			//
			CharacterRun run = para.getCharacterRun(z);
			mwText += charToWiki(run);
		}//end Character Run
		//
		// sau khi convert cac character, can bo sung tag neu doan van la header hoac title
		// hien nhien, chi convert neu doan van nay co noi dung
		//
		if (mwText.length() > 0){
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
				mwText = "\n" + mwUlList + mwText;
				numEnter = 0;
			}
			else mwUlList = "";
			
			if (isCaption) {
				mwText = "<center>"+mwText+"</center>";
				numEnter = 2;
			}
			if (isTitle) {
				mwText = addMore(mwText,"</title>");
				mwText = "<title>"+ mwText;
				numEnter = 2;
			}
			if ((headerLevel > 0)&&(headerLevel <= 9)) {
				mwText = addMore(mwText,SPACE + TAG_HEADER[headerLevel]);
				mwText = TAG_HEADER[headerLevel] + SPACE + mwText;
				numEnter = 2;
			}
		}
		else 
			numEnter = 0;
		//
		// thuc te la POI xu li tieng viet khong duoc tot,
		// co the mot tu tieng viet "bi coi" la nhieu character
		// va moi character lai cong them mwtag, vi du <b>Th</b><b>u</b>
		// nen can xoa su "trung lap" tag mw. Co the viet tach ra mot ham cho tien
		//
		mwText = mwText.replaceAll("</b><b>","");
		mwText = mwText.replaceAll("</i><i>","");
		mwText = mwText.replaceAll("</u><u>","");
		mwText = mwText.replaceAll("</s><s>","");
		return mwText;
	}
    /** 
    * ham lay so cot lon nhat va chieu dai lon nhat trong bang
    * 
     */ 
	protected void maxColWidth(Table table){
		maxColTable = 1;
		maxWidTable = 0;
		int row=0;
		for(int i = 0; i < table.numRows(); i++){
			if (table.getRow(i).numCells() > maxColTable) {
				maxColTable = table.getRow(i).numCells();
				row = i;
			}
		}
		for (int j = 0; j < maxColTable; j++)
			maxWidTable += table.getRow(row).getCell(j).getWidth();
	}
	//
	//
	//
	protected int sugColSpan(int width){
		float frac = (float) maxColTable/maxWidTable*width;
		return (int) Math.round(frac);
	}
    /** 
    * ham convert mot bang do)n "clone table", tuc la bang khong chua bang con trong no
    * 
     */ 
	protected String tableToWiki(Table tab)
		throws IOException, UnsupportedEncodingException  
	{
		
		Table table = tab;
		maxColWidth(tab);
		
		String mwText = "";
		//
		// chung ta dung mot mang so nguyen de dem so o (cell) duoc tro^.n (merged) trong cung mot row
		// thay vi duyet cac hang/row tu tren xuong (thap den cao), chung ta can duyet nguoc 
		// tu duoi len thi moi dem duoc so luong o da duoc tron trong cung mot cot
		// rowspan[j] la so luong o duoc trong cua cot thu j, mac dinh gia tri cua chung la 1,
		// no se duoc tang len neu o^ do khong phai la o^ dau tien duoc tron va reset (= 1)
		// neu o do la o dau tien duoc tron, tinh tu tren xuong
		//
		int rowspan[] = new int[100];// 100 cot cho moi hang/row, co le hoi nhieu cho mot file MS Word?
		for (int i = 0; i < 100; i++) rowspan[i] = 1;//mac dinh la moi o mot hang/row
		for (int i = table.numRows()-1; i > -1  ; i--){//duyet cac hang tu duoi len
			TableRow row = table.getRow(i);
			String strRow = "\n|- valign=\"top\"";//bat dau mot hang moi
			int numCell = row.numCells();
			for (int j = 0; j < maxColTable; j++){
				String option = "";
				if ( j < numCell){
				TableCell cell = row.getCell(j);
					if (numCell < maxColTable)
						if (sugColSpan(cell.getWidth()) > 1)
							option += "colspan=\""+sugColSpan(cell.getWidth())+"\"";
				int numParas = cell.numParagraphs();
				String strCells ="";
				for (int k=0; k < numParas; k ++){
					Paragraph para = cell.getParagraph(k);
					strCells += paraToWiki(para);
					if (k < numParas-1) strCells += Enter[numEnter];
					numEnter = 2;//tra ve mac dinh
				}
				if (cell.isVerticallyMerged()){//neu o nay duoc tron
					if (cell.isFirstVerticallyMerged()){//va la o dau tien
						option += " rowspan=\""+rowspan[j]+"\"|";
						strRow += "\n|" + option + strCells;
						rowspan[j] = 1;//mac dinh moi cell mot hang
					}
					else
						rowspan[j] += 1;//tang so rowspan cho cot nay
				}
				else
					if (option=="") strRow += "\n|" + strCells;
					else
						strRow += "\n|" + option + "|\n" + strCells;
				}
				else
					if (i < table.numRows()-1) rowspan[j] += 1;
			}//xu li xong mot hang
			mwText = strRow + mwText;
		}//xu li xong ca bang
		mwText = "{|class=\"wikitable\" width=\"100%\"\n" + mwText + "\n|}";
		numEnter = 2;
		return mwText;
	}

    /** 
    * ham thiet lap cac gia tri dau vao va dau ra
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
            // 
            // mo mot file MS Word de convert 
            // 
			doc = new HWPFDocument(new FileInputStream(nameInput+".doc"));
            // 
            // lay toan bo noi dung trong file .doc
            // va khoi tao gia tri style cua tai lieu
            // 
            range = doc.getRange(); 
            styleSheet = doc.getStyleSheet();
            picTable = doc.getPicturesTable();
      	    // lay tong so doan van trong tai lieu
            // 
            numParas = range.numParagraphs(); 
            // 
            // voi moi doan van, chung ta can kiem tra xem no co nam trong mot bang/table
            // nao khong, neu dung thi goi ham convert table 
            // 
            for(int i = 0; i < numParas; i++) { 
                para = range.getParagraph(i); 
                // 
                // doan van co nam trong mot bang nao khong? 
                // 
               if(para.isInTable()) { 
                    // 
                    // vi phuong thuc getTable() chi duoc goi MOT LAN DUY NHAT cho mot table
                    // nen chung ta can danh dau table nay da duoc goi chua
                    // 
                    if(!inTable) { 
                        // 
                        // lay bang ma` doan van nam trong no va convert bang nay
                        // cac phuong thuc xi li ve table nam trong cac file
                        // Table.java, TableRow.java, TableCell.java va chu y rang ta lay moi hang
                        // cua bang roi tu moi hang lai lay moi cot cua dong do
                        // ghi noi dung convert vao file, xuong hang moi va danh dau da xu li bang
                        //
                        table = range.getTable(para);
                        _out.write(tableToWiki(table)+Enter[numEnter]);
                        inTable = true; 
                    } 
                } 
                else { 
                    // 
                    // truong hop bang da duoc xu li hoac la doan van khong nam trong bang
                    // thi ta tien hanh convert binh thuong va chu y cong them ma xuong hang
                    // 
                    inTable = false;
                    String mwText = paraToWiki(para);
                    _out.write(mwText+Enter[numEnter]);
                }
                numEnter = 2;//bat dau mot doan van moi
            } 
            // 
            // dong tep, sau khi xu li xong
            //
            _out.close();
    }
    /** 
     */ 
    public static void main(String[] args) { 
        try { 
        	//
        	// tao thu muc chua file ma nguon wiki va cac images
        	// viec can suy nghi la: nen tao thu muc tam thoi (tmp) hay
        	// tao normal folder roi xoa sau khi xu li xong?
        	//
			File dir = new File(DIR_NAME);
			dir.mkdir();
			OutputStream out = new FileOutputStream(DIR_NAME+"\\mwtext.txt");
			new WordToMwtext(out);
        } 
		catch (Throwable t)	{
			t.printStackTrace();
		}
    } 
} 
