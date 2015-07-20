package com.sujen.nutchscoring.termvector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class constructs a Term Frequency Vector in a Sequence file format. 
 * It is used to generate the model file for the Nutch Similarity based Scoring plugin. 
 * 
 *  @author Sujen Shah
 *
 */
public class TermVectorGenerator 
{
  private static Map<String, TFIDFObject> tfidfVector = new HashMap<String, TFIDFObject>();
  private static final Logger LOG = LoggerFactory.getLogger(TermVectorGenerator.class);
  private static int totalDocs = 0;
  private static CharArraySet stopSet;

  public static void main( String[] args ){
    //    String corpusPath = args[0];
    //    String sequenceFileName = args[1];
    String corpusPath = "/Users/shah/Documents/memex/autonomy/modelParseText.txt";
    String sequenceFileName = "/Users/shah/Documents/memex/autonomy/model";
    if(args.length>2){
      String stopWordFilePath = "/Users/shah/Documents/nutch-testing/stopwords.txt";
      stopSet = populateStopWords(stopWordFilePath);
    }
    String stopWordFilePath = "/Users/shah/Documents/nutch-testing/stopwords.txt";
    stopSet = populateStopWords(stopWordFilePath);
    LOG.info("Path of corpus documents : {}",corpusPath);
    LOG.info("Path of sequence file : {}", sequenceFileName);
    generateTermVectorModel(corpusPath, sequenceFileName);
    System.out.println(tfidfVector.size());
    SequenceFileManager.writeToSequenceFile(tfidfVector, sequenceFileName, true);
    //    SequenceFileManager.readSequenceFile(sequenceFileName, new Configuration(), true);
  }

  private static void generateTermVectorModel(String corpusPath, String sequenceFileName) {
    File file = new File(corpusPath);
    if(file.exists()){
      if(file.isDirectory()){
        LOG.info("User provided directory : {}", file.toString());
        for(File fileEntry: file.listFiles()){
          LOG.info("Processing file : {}",fileEntry.toString());
          updateTFIDF(createDocVect(fileEntry));
          totalDocs++;
        }
      }
      else{
        LOG.info("User provided file : {}", file.toString());
        updateTFIDF(createDocVect(file));
        totalDocs++;
      }
    }
    else{
      LOG.error("{} does not exists",corpusPath);
    }
  }

  private static DocumentVector createDocVect(File file) {
    ParseFile parseFile = new ParseFile(file);
    DocumentTokenizer documentTokenizer = new DocumentTokenizer();
    TokenStream tokenStream = documentTokenizer.generateTokenStream(parseFile.getParsedContent());
    tokenStream = documentTokenizer.applyStopFilter(tokenStream, stopSet);
    tokenStream = documentTokenizer.performPorterStemming(tokenStream);
    DocumentVector docVect = new DocumentVector(tokenStream);
    //    docVect.printTerms();
    return docVect;
  }

  private static void updateTFIDF(DocumentVector docVect){
    Map<String, Integer> termFreqVect = docVect.getTermVect();
    for(Entry<String, Integer> pair: termFreqVect.entrySet()){
      String term = pair.getKey();
      int docTermCount = pair.getValue();
      if(tfidfVector.containsKey(term)){
        TFIDFObject counts = tfidfVector.get(term);
        counts.updateAddTermCount(docTermCount);
        counts.incrementDocumentCount();
        tfidfVector.put(term, counts);
      }
      else{
        TFIDFObject counts = new TFIDFObject(term);
        counts.setTermCount(docTermCount);
        counts.incrementDocumentCount();
        tfidfVector.put(term, counts);
      }
    }
  }

  private static void calculateTFIDF(){
    Map<String, Integer> outputVector = new HashMap<String, Integer>();
    for(Entry<String, TFIDFObject> pair: tfidfVector.entrySet()){

    }
  }

  private static CharArraySet populateStopWords(String stopWordFilePath){
    CharArraySet stopSet = new CharArraySet(1, true);
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File(stopWordFilePath)));
      String s;
      while((s = br.readLine())!=null){
        stopSet.add(s.trim());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block

      e.printStackTrace();
    } 
    return stopSet;
  }

  public DocumentVector createGoldStandardDocVect(String corpusPath, String stopWordFilePath){
    File model = new File("cosine.model");
    DocumentVector docVect = new DocumentVector();
    Map<String, Integer> termVect = null;
    if(model.exists()){
      FileInputStream fileIn;
      try {
        fileIn = new FileInputStream(model);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        termVect = (Map<String, Integer>) in.readObject();
        LOG.info("Found a model file at {}",model.getAbsolutePath());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else {
      stopSet = populateStopWords(stopWordFilePath);
      File file = new File(corpusPath);
      if(file.exists()){
        if(file.isDirectory()){
          LOG.info("User provided directory : {}", file.toString());
          for(File fileEntry: file.listFiles()){
            LOG.info("Processing file : {}",fileEntry.toString());
            updateTFIDF(createDocVect(fileEntry));
            totalDocs++;
          }
        }
      }
      termVect = createTermFreqVectFromTFIDF(tfidfVector, totalDocs);

      FileOutputStream fileOut;
      try {
        fileOut = new FileOutputStream(model);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(termVect);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }     
    }
    docVect.setTermVect(termVect);
    return docVect;
  }

  private int getTFIDFValue(int termCount, int documentCount, int totalDocs){
    double idf = Math.log(totalDocs/documentCount);
    System.out.println("log : " + idf);
    return (int) ((int)termCount*idf);
  }
  
  private Map<String, Integer> createTermFreqVectFromTFIDF(Map<String, TFIDFObject> tfidfVector, int totalDocs){
    Map<String, Integer> termFreq = new HashMap<String, Integer>();
    System.out.println(tfidfVector);
    System.out.println(tfidfVector.size());
    for(Entry<String, TFIDFObject> pair: tfidfVector.entrySet()){
      int documentCount = pair.getValue().getDocumentCount();
      if(documentCount != 1 && pair.getKey().matches("[a-zA-Z]+")){
        termFreq.put(pair.getKey(), pair.getValue().getTermCount());
      }
      
    }
    System.out.println(termFreq.size());
    System.out.println(termFreq);
    return termFreq;
  }
}