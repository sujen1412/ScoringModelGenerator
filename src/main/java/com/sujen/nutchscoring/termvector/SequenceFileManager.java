package com.sujen.nutchscoring.termvector;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;

public class SequenceFileManager {

  private static Configuration conf = new Configuration();
  
  public static void writeToSequenceFile(Map<String, TFIDFObject> map, String sequenceFileName) {
    // TODO Auto-generated method stub
    Path path = new Path(sequenceFileName);
    try {
      SequenceFile.Writer writer = SequenceFile.createWriter(FileSystem.get(conf), conf, path, Text.class, TFIDFObject.class);
      for(Entry<String, TFIDFObject> pair:map.entrySet()){
        writer.append(new Text(pair.getKey()), pair.getValue());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static void writeToSequenceFile(Map<String, TFIDFObject> map, String sequenceFileName, boolean termFreqVect){
    Path path = new Path(sequenceFileName);
    try {
      SequenceFile.Writer writer = SequenceFile.createWriter(FileSystem.get(conf), conf, path, Text.class, IntWritable.class);
      for(Entry<String, TFIDFObject> pair:map.entrySet()){
        IntWritable count = new IntWritable(pair.getValue().getTermCount());
        writer.append(new Text(pair.getKey()), count);
      }
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static void readSequenceFile(String sequenceFileName, Configuration conf){
    Path path = new Path(sequenceFileName);
    try {
      SequenceFile.Reader reader = new SequenceFile.Reader(FileSystem.get(conf), path, conf);
      Text key = (Text) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
      TFIDFObject value = (TFIDFObject) ReflectionUtils.newInstance(reader.getValueClass(), conf);
      while(reader.next(key, value)){
        System.out.println("key : " + key.toString() + " - value : " + value.toString());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  public static void readSequenceFile(String sequenceFileName, Configuration conf, boolean termFreqVect){
    Path path = new Path(sequenceFileName);
    try {
      SequenceFile.Reader reader = new SequenceFile.Reader(FileSystem.get(conf), path, conf);
      Text key = (Text) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
      IntWritable value = (IntWritable) ReflectionUtils.newInstance(reader.getValueClass(), conf);
      while(reader.next(key, value)){
        System.out.println("key : " + key.toString() + " - value : " + value.toString());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
