package com.revature.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import com.revature.map.ColumnMapper;
import com.revature.reduce.ColumnReducer;


public class GenderStatsTest {

  /*
   * Declare harnesses that let you test a mapper, a reducer, and
   * a mapper and a reducer working together.
   */
  private MapDriver<LongWritable, Text, Text, DoubleWritable> mapDriver;
  private ReduceDriver<Text, DoubleWritable, Text, DoubleWritable> reduceDriver;
  private MapReduceDriver<LongWritable, Text, Text, DoubleWritable, Text, DoubleWritable> mapReduceDriver;

  /*
   * Set up the test. This method will be called before every test.
   */
  @Before
  public void setUp() {

    /*
     * Set up the mapper test harness.
     */
    ColumnMapper mapper = new ColumnMapper();
    mapDriver = new MapDriver<LongWritable, Text, Text, DoubleWritable>();
    mapDriver.setMapper(mapper);

    /*
     * Set up the reducer test harness.
     */
    ColumnReducer reducer = new ColumnReducer();
    reduceDriver = new ReduceDriver<Text, DoubleWritable, Text, DoubleWritable>();
    reduceDriver.setReducer(reducer);

    /*
     * Set up the mapper/reducer test harness.
     */
    mapReduceDriver = new MapReduceDriver<LongWritable, Text, Text, DoubleWritable, Text, DoubleWritable>();
    mapReduceDriver.setMapper(mapper);
    mapReduceDriver.setReducer(reducer);
  }

  /*
   * Test the mapper.
   */
  @Test
  public void testMapper() {

    /*
     * For this test, the mapper's input will be "1 cat cat dog" 
     */
    mapDriver.withInput(new LongWritable(1), new Text("cat cat dog"));

    /*
     * The expected output is "cat 1", "cat 1", and "dog 1".
     */
    mapDriver.withOutput(new Text("cat"), new DoubleWritable(1.0));
    mapDriver.withOutput(new Text("cat"), new DoubleWritable(1.0));
    mapDriver.withOutput(new Text("dog"), new DoubleWritable(1.0));

    /*
     * Run the test.
     */
    mapDriver.runTest();
  }

  /*
   * Test the reducer.
   */
  @Test
  public void testReducer() {

    List<DoubleWritable> values = new ArrayList<DoubleWritable>();
    values.add(new DoubleWritable(1.0));
    values.add(new DoubleWritable(1.0));

    /*
     * For this test, the reducer's input will be "cat 1 1".
     */
    reduceDriver.withInput(new Text("cat"), values);

    /*
     * The expected output is "cat 2"
     */
    reduceDriver.withOutput(new Text("cat"), new DoubleWritable(2.0));

    /*
     * Run the test.
     */
    reduceDriver.runTest();
  }

  /*
   * Test the mapper and reducer working together.
   */
  @Test
  public void testMapReduce() {

    /*
     * For this test, the mapper's input will be "1 cat cat dog" 
     */
    mapReduceDriver.withInput(new LongWritable(1), new Text("cat cat dog"));

    /*
     * The expected output (from the reducer) is "cat 2", "dog 1". 
     */
    mapReduceDriver.addOutput(new Text("cat"), new DoubleWritable(2.0));
    mapReduceDriver.addOutput(new Text("dog"), new DoubleWritable(2.0));

    /*
     * Run the test.
     */
    mapReduceDriver.runTest();
  }
}
