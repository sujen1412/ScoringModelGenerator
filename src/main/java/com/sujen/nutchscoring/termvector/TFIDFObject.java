package com.sujen.nutchscoring.termvector;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Writable;

public class TFIDFObject implements Writable{

  private int documentCount; 
  private int termCount;
  private String term;
  
  public TFIDFObject(){
    
  }
  
  public TFIDFObject(String term){
    documentCount = 0;
    termCount = 0;
    this.term = term;
  }
  
  public void incrementDocumentCount(){
    documentCount++;
  }
  
  public void setTermCount(int termCount){
    this.termCount = termCount;
  }
  
  public void updateAddTermCount(int count){
    termCount += count;
  }
  
  public int getTermCount(){
    return termCount;
  }
  
  public int getDocumentCount(){
    return documentCount;
  }
  
  public String getTerm(){
    return term;
  }
  
  public String toString(){
    return "TermCount : " + termCount + ", DocCount : " + documentCount;
  }

  public void readFields(DataInput in) throws IOException {
    // TODO Auto-generated method stub
    termCount = in.readInt();
    documentCount = in.readInt();
  }

  public void write(DataOutput out) throws IOException {
    // TODO Auto-generated method stub
    out.writeInt(termCount);
    out.writeInt(documentCount);
  }
}
