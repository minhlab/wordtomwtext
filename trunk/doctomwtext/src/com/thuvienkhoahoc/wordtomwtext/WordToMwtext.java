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
        public static final String TAG_HEADER[] = {"","=","==","===","====","=====","======","=======","========"};
        /*
         *  Các biến toàn cục trong lớp
         */
        Writer _out;
        HWPFDocument doc;
        StyleSheet styleSheet;
        PicturesTable picTable;
        /*
         * Tên tệp MS Word đầu vào, với phiên bản full biến này sẽ nhận từ giao diện người dùng
         */
        String nameInput = "tablerow";
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
         * Lấy màu của font chữ 
         */
        protected String getColor(int color)
        {
            switch(color)
            {
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
         * Thiết lập các tùy chọn cho font
         */
       protected String getOptionFont(CharacterRun run){
    	   String optionFont=""; 
    	   String font = run.getFontName();
    	   if (!font.equals("Times New Roman")) optionFont = optionFont + " face=\""+font+"\"";
    	   int size = getHtmlFontSize(run.getFontSize());
    	   if (size != 3) optionFont = optionFont + " size="+size;
    	   int color = run.getIco24();
    	   String strColor=getColor(color);
    	   if (strColor.length() >0 ) optionFont = optionFont + " color=\""+strColor+"\"";
    	   return optionFont;
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
                if (reStr.endsWith("\r")) reStr = reStr.substring(0, length-1).trim()+ SPACE + more+"\r";
                else reStr = reStr.trim() + SPACE + more;
                return reStr;
        }
        /*
        * Convert CharacterRun chứa image thành wikitext và copy file image đó vào thư mục DIR_NAME
        * Chú ý rằng, một đối tượng Picture có thể là một trong các đối tượng
        * {image, shape, wmf,...} và hiện tại chương trình chỉ convert image
        */
        protected String imagesToWiki(CharacterRun run)
                throws IOException, UnsupportedEncodingException 
        {
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
         * Convert một đối tượng CharacterRun thành wikitext
         */
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
            	short ssIndex = run.getSubSuperScriptIndex();
        		boolean isDeleted = run.isMarkedDeleted() || run.isFldVanished() || run.isStrikeThrough() || run.isDoubleStrikeThrough();        			/*
    			/*
    			 * Điều khó hiểu là, giá trị text của CharacterRun đôi khi lại không trùng
    			 * với text của Paragraph. Do đó có thể chúng ta cần phải encode text của Paragraph
    			 * trước khi convert CharacterRun
    			 */
    			textR = run.text();
    			String mwOpen = "";
    			String mwClose = "";
    			String optionFont = getOptionFont(run);
    			if (optionFont.length()>0) {
    				mwOpen = "<font"+optionFont+">";
    				mwClose = "</font>";
    			}
    			/*
    			 * Không xử lí định dạng với khoảng trống, kí tự xuống hàng, kết thúc bảng
    			 * Chú ý rằng trong MS Word có nhiều định dạng gạch chân nhưng Mediawiki chỉ có một.
    			 * Chúng ta dùng các tag HTML cho các định dạng vì các phiên bản MW 1.14+ co ho tro chung
    			 * Việc cộng các tag phải theo kiểu "Last in first out", tag nào được mở trước thì sẽ đóng sau.
    			 * Ví dụ để tạo ra mã wikitext dạng: <b><i><u>ABCD></u></i></b>, thì thuật toán:
    			 * mwOpen=								mwClose=
    			 * <b>										</b>
    			 * <b><i>								</i></b>
    			 * <b><i><u>						</u></i></b>
    			 */
    			if (textR.trim().length() > 0){
	    			if (run.isBold()) {					mwOpen  = mwOpen +  "<b>"; 		mwClose = "</b>" 	+ mwClose; }
	    			if (run.isItalic()) {				mwOpen  = mwOpen +  "<i>"; 		mwClose = "</i>" 	+ mwClose; }
	    			if (run.getUnderlineCode() > 0) {	mwOpen  = mwOpen +  "<u>"; 		mwClose = "</u>" 	+ mwClose; }
	    			if (isDeleted) {					mwOpen  = mwOpen +  "<del>"; 	mwClose = "</del>" 	+ mwClose; }
	    			if (ssIndex == 1) {					mwOpen  = mwOpen +  "<sup>"; 	mwClose = "</sup>" 	+ mwClose; }
	    			if (ssIndex == 2) {					mwOpen  = mwOpen +  "<sub>"; 	mwClose = "</sub>" 	+ mwClose; }
	    			if (run.isHighlighted()) {			mwOpen  = mwOpen +  "<em>"; 	mwClose = "</em>" 	+ mwClose; }
	    			/*
	    			 * Khi CharacterRun có 1 trong các định dạng trên thì sẽ convert
	    			 */
	    			if (mwOpen.length () > 0){
	    				textR = addMore(textR,mwClose);
	    				textR = mwOpen + textR;
	    			}
    			}
    		}
    		return textR.trim();
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
                throws IOException, UnsupportedEncodingException  
        {
                String mwText ="";
                int headerLevel = 0;
                boolean isTitle = false;
                boolean isCaption = false;
                int ilfo = para.getIlfo();//kiểu list nào {Bullet, Numbering}
                int ilvl = para.getIlvl() + 1;//cấp độ, hay mức lùi vào đầu dòng
                StyleDescription paragraphStyle = styleSheet.getStyleDescription (para.getStyleIndex ());
                String styleName = paragraphStyle.getName();
                /*
                 * Nhận dạng kiểu Paragraph = {Heading, Title, Caption,...}
                 * Việc căn cứ vào getName() có thể không chính xác vì người dùng có thể tự tạo Heading
                 * và Title cá nhân. Nhưng chúng ta chấp nhận phương án này, vì hiện tại POI chưa hỗ trợ
                 * "Level" của đoạn văn
                 */
                if (styleName.startsWith ("Caption")) isCaption = true;
                if (styleName.startsWith ("Title")) isTitle = true;
                if (styleName.startsWith ("Heading")) 
                        headerLevel = Integer.parseInt (styleName.substring (8));
                /*
                 * Convert các CharacterRun của đoạn văn
                 */
                for (int z = 0; z < para.numCharacterRuns(); z++) {
                        CharacterRun run = para.getCharacterRun(z);
                        mwText += charToWiki(run);
                }
                /*
                 * Bổ sung định dạng cho các loại paragraph
                 * Hiển nhiên, chúng ta chỉ làm việc này nếu đoạn văn có nội dung
                 */
                if (mwText.length() > 0){
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
                                mwText = "\n" + mwUlList + mwText;
                                numEnter = 0;
                        }
                        else 
                                mwUlList = "";
                        
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
                /*
                 * Thực tế là POI không "giỏi tiếng Việt", thỉnh thoảng nó tách một từ tiếng việt
                 * thành nhiều CharacterRun. Điều này dẫn đến việc lặp các tag. Chẳng hạn, thay vì
                 * <b>Cộng</b> thì CharacterRun lại trả về <b>C</b><b>ộ</b><b>ng</b>
                 * Vì thế, chúng ta có thể xóa sự thừa các tag này
                 */
                mwText = mwText.replaceAll("</b><b>","");
                mwText = mwText.replaceAll("</i><i>","");
                mwText = mwText.replaceAll("</u><u>","");
                mwText = mwText.replaceAll("</s><s>","");
                return mwText;
        }
    /*
     * Hàm trả về số cột tối đa trong bảng cùng với chiều rộng của hàng tương ứng
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
         *                              maxWidTable là chiều rộng của hàng có số cột tối đa.
         *                              width là chiều rộng của ô cần tính colspan
         * 
         * Thuật toán:
         * Duyệt các hàng theo thứ tự từ dưới lên
     * Với mỗi hàng, chúng ta "giả vờ" số ô của mỗi hàng đều bằng maxColTable. Như vậy, sẽ có
     * những ô "thực" và những ô "ảo". Với những ô "thực" ta tính số colspan và rowspan cho nó. 
     * Ta dùng một mảng nguyên rowspan[] để đếm số ô được trộn theo chiều dọc.
     */ 
        protected String tableToWiki(Table tab)
                throws IOException, UnsupportedEncodingException  
        {
                Table table = tab;
                maxColWidth(tab);
                String mwText = "";
                /*
                 * Chúng ta dùng một mảng số nguyên để đếm "số ô liên tiếp được trộn" trong cùng một cột
                 * rowspan[j] là "số ô liên tiếp được trộn" của cột j, giá trị mặc định là 1.
                 */
                int rowspan[] = new int[maxColTable];
                for (int i = 0; i < maxColTable; i++) rowspan[i] = 1;//mặc định, mỗi ô một hàng
                /*
                 * Chúng ta sẽ duyệt các hàng theo thứ tự từ dưới lên
                 *  Với mỗi hàng, ta sẽ duyệt maxColTable cell của nó, 
                 *    nếu phát hiện cell thứ j của hàng i được trộn thì rowspan[j] sẽ tăng lên 1
                 *    nếu phát hiện cell thứ j là ô đầu tiên được trộn từ trên xuống thì
                 *    sinh mã rowspan cho ô này và gán lại giá trị mặc định cho rowspan[j] của cột j
                 */
                for (int i = table.numRows()-1; i > -1  ; i--){//duyệt các hàng theo thứ tự từ dưới lên
                        TableRow row = table.getRow(i);
                        String strRow = "\n|- valign=\"top\"";//bắt đầu một hàng mới
                        int numCell = row.numCells();//số ô "thực"
                        for (int j = 0; j < maxColTable; j++){//"giả vờ" rằng, tất cả các hàng đều có maxColTable ô
                                String optionSpan = "";
                                if (j < numCell){//với mỗi ô thực
                                TableCell cell = row.getCell(j);
                                /*
                                 * Tính colspan cho mỗi ô, nếu hàng này có ô ảo
                                 */
                                if (numCell < maxColTable)
                                                if (sugColSpan(cell.getWidth()) > 1)
                                                        optionSpan += "colspan=\""+sugColSpan(cell.getWidth())+"\" ";
                                /*
                                 * Convert các đoạn văn trong ô này và sinh mã xuống hàng tương ứng
                                 */
                                int numP = cell.numParagraphs();
                                String strCells ="";
                                for (int k=0; k < numP; k ++){
                                        Paragraph para = cell.getParagraph(k);
                                        strCells += paraToWiki(para);
                                        if (k < numP-1) strCells += Enter[numEnter];
                                        numEnter = 2;//gán lại giá trị xuống hàng mặc định
                                }
                                /*
                                 * Sinh mã rowspan nếu là ô đầu tiên được trộn và gán lại giá trị mặc định
                                 * cho rowspan[j]. Nếu không phải là ô đầu tiên được trộn thì tăng rowspan[j]
                                 */
                                if (cell.isVerticallyMerged()){
                                        if (cell.isFirstVerticallyMerged()){
                                                optionSpan += "rowspan=\""+rowspan[j]+"\"|";
                                                strRow += "\n|" + optionSpan + "\n" + strCells;
                                                rowspan[j] = 1;
                                        }
                                        else
                                                rowspan[j] += 1;
                                }
                                else
                                        /*
                                         * Nếu ô này không được trộn thì cập nhật nó vào hàng
                                         */
                                        if (optionSpan.trim().length()==0) strRow += "\n|\n" + strCells;
                                        else
                                                strRow += "\n|" + optionSpan + "|\n" + strCells;
                                }
                                /*
                                 * Nếu là ô ảo và không nằm ở hàng cuối cùng của bảng thì tăng rowspan[j]
                                 */
                                else
                                        if (i < table.numRows()-1) rowspan[j] += 1;
                        }//xử lí xong một hàng
                        mwText = strRow + mwText;
                }//xử lí xong cả bảng
                mwText = "{|class=\"wikitable\" width=\"100%\"" + mwText + "\n|}"+Enter[2];
                numEnter = 2;
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
                     * nên chúng ta cần đánh dáu bảng này đã được xử lí chưa
                     */ 
                    if(!inTable) { 
                        /*
                         * Convert bảng, ghi vào file cùng với xuống hàng
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
    }
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
                catch (Throwable t)     {
                        t.printStackTrace();
                }
    } 
} 