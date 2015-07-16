package com.sujen.nutchscoring.cosine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.sujen.nutchscoring.termvector.DocumentTokenizer;
import com.sujen.nutchscoring.termvector.DocumentVector;
import com.sujen.nutchscoring.termvector.ParseFile;
import com.sujen.nutchscoring.termvector.TFIDFObject;
import com.sujen.nutchscoring.termvector.TermVectorGenerator;

public class CosineSimilarity {

  private static Map<String, TFIDFObject> tfidfVector = new HashMap<String, TFIDFObject>();
  private static final Logger LOG = LoggerFactory.getLogger(CosineSimilarity.class);
  private static int totalDocs = 0;
  private static CharArraySet stopSet;
  private static DocumentVector goldStandardDocVect;

  private static DocumentVector createDocVect(File file) {
    ParseFile parseFile = new ParseFile(file);
    DocumentTokenizer documentTokenizer = new DocumentTokenizer();
    TokenStream tokenStream = documentTokenizer.generateTokenStream(parseFile.getParsedContent());
    tokenStream = documentTokenizer.applyStopFilter(tokenStream, stopSet);
    tokenStream = documentTokenizer.performPorterStemming(tokenStream);
    DocumentVector docVect = new DocumentVector(tokenStream);
    return docVect;
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
  
  private static void generateSimilarityScore(File[] files){
    StringBuffer sb = new StringBuffer();
    sb.append("filename, score\n");
    System.out.println("FileName, Score");
    for(File f : files){
      LOG.info("Processing file {}",f.getAbsolutePath());
      DocumentVector docVect = createDocVect(f);
      double score = calculateCosineSimilarity(goldStandardDocVect, docVect);
      sb.append(""+f.getAbsolutePath()+","+ score+ "\n");
      LOG.info("{} score: {}", f.getAbsolutePath(), score+"");
      System.out.println(f.getAbsolutePath() + "," + score+"");
    }
//    System.out.println(sb.toString());
    File output = new File("scores.csv");
    try {
      FileWriter writer = new FileWriter(output);
      BufferedWriter bw = new BufferedWriter(writer);
      bw.write(sb.toString());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println(output.getAbsolutePath());
  }
  
  private static double calculateCosineSimilarity(DocumentVector docVect1, DocumentVector docVect2){

    double doc1Dist = getEuclideanDist(docVect1);
    double doc2Dist = getEuclideanDist(docVect2);

    double dotProduct = getDotProduct(docVect1, docVect2);
    if(doc1Dist*doc2Dist == 0){
      return 0.0;
    }
    return dotProduct/(doc1Dist*doc2Dist);
  }

  private static double getDotProduct(DocumentVector docVect1, DocumentVector docVect2) {
    double dotProduct = 0.0;
    Map<String, Integer> doc2TermFreqVect = docVect2.getTermVect();
    for(Map.Entry<String, Integer> pair : docVect1.getTermVect().entrySet()){
      double doc1value = pair.getValue();
      double doc2value = 0;

      if(doc2TermFreqVect.containsKey(pair.getKey()))
        doc2value = doc2TermFreqVect.get(pair.getKey());

      dotProduct += doc1value*doc2value;
    }

    return dotProduct;
  }

  private static double getEuclideanDist(DocumentVector docVect) {
    float sum = 0f;
    for(Map.Entry<String, Integer> pair : docVect.getTermVect().entrySet()){
      sum += pair.getValue() * pair.getValue();
    }    
    return Math.sqrt(sum);
  }

  public static void main(String[] args) { 
    
    if(args.length<3){
      System.out.println("Usage : corpusPAth stopWordPAth documentsPath");
      System.exit(0);
    }
    String modelFile = args[0];
    String stopWordFile = args[1];
    stopSet = populateStopWords(stopWordFile);
    String documentsPath = args[2];
    
    TermVectorGenerator tvg = new TermVectorGenerator();
    goldStandardDocVect = tvg.createGoldStandardDocVect(modelFile, stopWordFile);
    
//    goldStandardDocVect = createDocVect(new File(modelFile));
//    System.out.println(goldStandardDocVect.getTermVect());
//    for(Entry<String, Integer> pair : goldStandardDocVect.getTermVect().entrySet()){
//      System.out.println(pair.getKey() + " : " + pair.getValue());
//    }
    File folder = new File(documentsPath);
    File files[] = folder.listFiles();
    generateSimilarityScore(files);
  }
}
