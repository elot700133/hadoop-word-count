package org.myorg;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class WordCount {

   public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
     private final static IntWritable one = new IntWritable(1);
     private Text word = new Text();

     public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
       String line = value.toString();
       StringTokenizer tokenizer = new StringTokenizer(line);
       while (tokenizer.hasMoreTokens()) {
         word.set(tokenizer.nextToken());
         // get the filename associated with this key value
         FileSplit fileSplit = (FileSplit) reporter.getInputSplit();
         String filename = fileSplit.getPath().getName();
         Text filename_text = new Text();
         filename_text.set(filename);
         // output to reducer (word, filename)
         output.collect(word, filename_text);
       }
     }
   }

   public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
     public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
       // push the list of filename to a Set
       Set<Text> s = new HashSet<Text>();
       while(values.hasNext()){
         s.add(values.next());
       }
       // Then simply use size to get the number of unique filename associate
       // with the word
       output.collect(key,new Text(Integer.toString(s.size())));
     }
   }

   public static void main(String[] args) throws Exception {
     JobConf conf = new JobConf(WordCount.class);
     conf.setJobName("wordcount");

     conf.setOutputKeyClass(Text.class);
     conf.setOutputValueClass(Text.class);

     conf.setMapperClass(Map.class);
     conf.setReducerClass(Reduce.class);

     conf.setInputFormat(TextInputFormat.class);
     conf.setOutputFormat(TextOutputFormat.class);

     FileInputFormat.setInputPaths(conf, new Path(args[0]));
     FileOutputFormat.setOutputPath(conf, new Path(args[1]));

     JobClient.runJob(conf);
   }
}

