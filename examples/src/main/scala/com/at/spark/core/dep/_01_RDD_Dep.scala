// scalastyle:off

package com.at.spark.core.dep

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * @create 2023-10-01 
 */
object _01_RDD_Dep {

  def main(args: Array[String]): Unit = {

    val sparConf = new SparkConf().setMaster("local").setAppName("WordCount")
    val sc = new SparkContext(sparConf)

    val lines: RDD[String] = sc.textFile("./files/1.txt")
    println(lines.toDebugString)
    println("*************************")
    val words: RDD[String] = lines.flatMap(_.split(" "))
    println(words.toDebugString)
    println("*************************")
    val wordToOne = words.map(word => (word, 1))
    println(wordToOne.toDebugString)
    println("*************************")
    val wordToSum: RDD[(String, Int)] = wordToOne.reduceByKey(_ + _)
    println(wordToSum.toDebugString)
    println("*************************")
    val array: Array[(String, Int)] = wordToSum.collect()
    array.foreach(println)

    sc.stop()

    /*

(1) ./files/1.txt MapPartitionsRDD[1] at textFile at _01_RDD_Dep.scala:13 []
 |  ./files/1.txt HadoopRDD[0] at textFile at _01_RDD_Dep.scala:13 []
*************************
(1) MapPartitionsRDD[2] at flatMap at _01_RDD_Dep.scala:16 []
 |  ./files/1.txt MapPartitionsRDD[1] at textFile at _01_RDD_Dep.scala:13 []
 |  ./files/1.txt HadoopRDD[0] at textFile at _01_RDD_Dep.scala:13 []
*************************
(1) MapPartitionsRDD[3] at map at _01_RDD_Dep.scala:19 []
 |  MapPartitionsRDD[2] at flatMap at _01_RDD_Dep.scala:16 []
 |  ./files/1.txt MapPartitionsRDD[1] at textFile at _01_RDD_Dep.scala:13 []
 |  ./files/1.txt HadoopRDD[0] at textFile at _01_RDD_Dep.scala:13 []
*************************
(1) ShuffledRDD[4] at reduceByKey at _01_RDD_Dep.scala:22 []
 +-(1) MapPartitionsRDD[3] at map at _01_RDD_Dep.scala:19 []
    |  MapPartitionsRDD[2] at flatMap at _01_RDD_Dep.scala:16 []
    |  ./files/1.txt MapPartitionsRDD[1] at textFile at _01_RDD_Dep.scala:13 []
    |  ./files/1.txt HadoopRDD[0] at textFile at _01_RDD_Dep.scala:13 []
*************************

     */

  }
}

// scalastyle:on