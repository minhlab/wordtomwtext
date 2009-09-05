/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package com.thuvienkhoahoc.wordtomwtext.examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

public final class WordToMwtext
{
  Writer _out;
  HWPFDocument _doc;

  public WordToMwtext(HWPFDocument doc, OutputStream stream)
    throws IOException, UnsupportedEncodingException
  {
	  
	  // bagd
    OutputStreamWriter out = new OutputStreamWriter (stream, "UTF-8");
    _out = out;
    _doc = doc;

    init ();
    openDocument ();
    openBody ();

    Range r = doc.getRange ();
    StyleSheet styleSheet = doc.getStyleSheet ();

    int sectionLevel = 0;
    int lenParagraph = r.numParagraphs ();
    boolean inCode = false;
    for (int x = 0; x < lenParagraph; x++)
    {
      Paragraph p = r.getParagraph (x);
      String text = p.text ();
      if (text.trim ().length () == 0)
      {
        continue;
      }
      StyleDescription paragraphStyle = styleSheet.getStyleDescription (p.
        getStyleIndex ());
      String styleName = paragraphStyle.getName();
      if (styleName.startsWith ("Heading"))
      {
        if (inCode)
        {
          closeSource();
          inCode = false;
        }

        int headerLevel = Integer.parseInt (styleName.substring (8));
        if (headerLevel > sectionLevel)
        {
          openSection ();
        }
        else
        {
          for (int y = 0; y < (sectionLevel - headerLevel) + 1; y++)
          {
            closeSection ();
          }
          openSection ();
        }
        sectionLevel = headerLevel;
        openTitle (sectionLevel);
        writePlainText (text.trim());
        closeTitle (sectionLevel);
      }
      else
      {
        int cruns = p.numCharacterRuns ();
        CharacterRun run = p.getCharacterRun (0);
        String fontName = run.getFontName();
        if (fontName.startsWith ("Courier"))
        {
          if (!inCode)
          {
            openSource ();
            inCode = true;
          }
          writePlainText (p.text());
        }
        else
        {
          if (inCode)
          {
            inCode = false;
            closeSource();
          }
          openParagraph();
          writePlainText(p.text());
          closeParagraph();
        }
      }
    }
    for (int x = 0; x < sectionLevel; x++)
    {
      closeSection();
    }
    closeBody();
    closeDocument();
    _out.flush();

  }

    public void init ()
      throws IOException
    {
    	// DO NOTHING
    }

    public void openDocument ()
      throws IOException
    {
    	// DO NOTHING
    }

    public void closeDocument ()
      throws IOException
    {
    	// DO NOTHING
    }

    public void openBody ()
      throws IOException
    {
    	// DO NOTHING
    }

    public void closeBody ()
      throws IOException
    {
    	// DO NOTHING
    }


    public void openSection ()
      throws IOException
    {
    	// DO NOTHING
    }

    public void closeSection ()
      throws IOException
    {
    	// DO NOTHING
    }

    public void openTitle (int level)
      throws IOException
    {
    	for (int i = 0; i <= level; i++) {
    		_out.write ("=");
    	}
    	_out.write (" ");
    }

    public void closeTitle (int level)
      throws IOException
    {
    	_out.write (" ");
    	for (int i = 0; i <= level; i++) {
    		_out.write ("=");
    	}
    	_out.write ("\n");
    }

    public void writePlainText (String text)
      throws IOException
    {
      _out.write (text);
    }

    public void openParagraph ()
      throws IOException
    {
    	// DO NOTHING
    }

    public void closeParagraph ()
      throws IOException
    {
    	_out.write ("\n\n");
    }

    public void openSource ()
      throws IOException
    {
      _out.write ("<source>");
    }
    public void closeSource ()
      throws IOException
    {
      _out.write ("</source>");
    }


  public static void main(String[] args)
  {
    try
    {
      OutputStream out = new FileOutputStream("c:\\test.wikitext");

      new WordToMwtext(new HWPFDocument(new FileInputStream(args[0])), out);
      out.close();
    }
    catch (Throwable t)
    {
      t.printStackTrace();
    }

  }
}
