package com.sujen.nutchscoring.termvector;

import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.mortbay.log.Log;

public class DocumentTokenizer {

  
  public TokenStream generateTokenStream(String content){
    @SuppressWarnings("deprecation")
    TokenStream tokenStream = new ClassicTokenizer(Version.LUCENE_CURRENT, new StringReader(content));
    return tokenStream;
  }
  
  public TokenStream applyStopFilter(TokenStream tokenStream, CharArraySet stopWords) {
    CharArraySet stopWordSet = StandardAnalyzer.STOP_WORDS_SET;
    if(stopWords != null){
      Log.info("Using user provided stop word set");
      stopWordSet = stopWords;
    }
    tokenStream = new StopFilter(tokenStream, stopWordSet); 
    return tokenStream;
  }
  
  public TokenStream performPorterStemming(TokenStream tokenStream) {
    tokenStream = new PorterStemFilter(tokenStream);
    return tokenStream; 
  }
}
