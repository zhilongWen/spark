// scalastyle:off

package com.at.spark.core.io

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _01_RDD_Load {

  def main(args: Array[String]): Unit = {

    val sparConf = new SparkConf().setMaster("local").setAppName("Dep")
    val sc = new SparkContext(sparConf)

    val rdd = sc.textFile("output1")
    println(rdd.collect().mkString(","))

    val rdd1 = sc.objectFile[(String, Int)]("output2")
    println(rdd1.collect().mkString(","))

    val rdd2 = sc.sequenceFile[String, Int]("output3")
    println(rdd2.collect().mkString(","))

    sc.stop()

  }

}

// scalastyle:on