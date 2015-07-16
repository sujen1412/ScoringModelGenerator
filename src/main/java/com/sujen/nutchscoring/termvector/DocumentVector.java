package com.sujen.nutchscoring.termvector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class DocumentVector {
  
  private Map<String, Integer> termFreqVect;
  
  public DocumentVector(TokenStream tokenStream){
    termFreqVect = new HashMap<String, Integer>();
    createDocVect(tokenStream);
  }

  public DocumentVector(){
    
  }
  
  private void createDocVect(TokenStream tokenStream) {
    int count;
    try {
      CharTermAttribute charTerm = tokenStream.addAttribute(CharTermAttribute.class);
      tokenStream.reset();
      while(tokenStream.incrementToken()){
        String term = charTerm.toString();
        if(termFreqVect.containsKey(term)){
          count = termFreqVect.get(term);
          termFreqVect.put(term, ++count);
        }
        else{
          termFreqVect.put(term, 1);
        }
      }
      tokenStream.end();
      tokenStream.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  public Map<String, Integer> getTermVect(){
    return termFreqVect;
  }
  
  public void setTermVect(Map<String, Integer> termFreqVect){
    this.termFreqVect = termFreqVect;
  }
  public void printTerms(){
    for(Entry<String, Integer> pair: termFreqVect.entrySet())
    System.out.print(pair.getKey()+ " ");
  }
}