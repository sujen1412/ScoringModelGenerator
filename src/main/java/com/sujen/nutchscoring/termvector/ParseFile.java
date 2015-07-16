package com.sujen.nutchscoring.termvector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ParseFile {

  private String parseContent;

  public ParseFile(File file){
    try {
      parseContent = parse(file).toLowerCase();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TikaException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private String parse(File file) throws IOException, SAXException, TikaException{
    InputStream input = new FileInputStream(file);
    ContentHandler handler = new BodyContentHandler(-1);
    Parser parser = new AutoDetectParser();
    Metadata metadata = new Metadata();
    ParseContext context = new ParseContext();
    parser.parse(input, handler, metadata, context);
    return handler.toString();
  }

  public String getParsedContent(){
    return parseContent;
  }
}
