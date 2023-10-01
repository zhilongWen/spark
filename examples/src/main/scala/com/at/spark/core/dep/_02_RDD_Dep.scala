// scalastyle:off

package com.at.spark.core.dep

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * @create 2023-10-01 
 */
object _02_RDD_Dep {

  def main(args: Array[String]): Unit = {

    val sparConf = new SparkConf().setMaster("local").setAppName("Dep")
    val sc = new SparkContext(sparConf)

    val lines: RDD[String] = sc.textFile("./files/1.txt")
    println(lines.dependencies)
    println("*************************")
    val words: RDD[String] = lines.flatMap(_.split(" "))
    println(words.dependencies)
    println("*************************")
    val wordToOne = words.map(word => (word, 1))
    println(wordToOne.dependencies)
    println("*************************")
    val wordToSum: RDD[(String, Int)] = wordToOne.reduceByKey(_ + _)
    println(wordToSum.dependencies)
    println("*************************")


    // collect 触发作业执行
    val array: Array[(String, Int)] = wordToSum.collect()


    array.foreach(println)

    sc.stop()

    /*

List(org.apache.spark.OneToOneDependency@35f3a22c)
*************************
List(org.apache.spark.OneToOneDependency@2aa7399c)
*************************
List(org.apache.spark.OneToOneDependency@5a0bef24)
*************************
List(org.apache.spark.ShuffleDependency@45900b64)
*************************

     */

  }

}

// scalastyle:on