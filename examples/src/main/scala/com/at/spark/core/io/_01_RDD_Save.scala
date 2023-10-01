// scalastyle:off

package com.at.spark.core.io

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _01_RDD_Save {

  def main(args: Array[String]): Unit = {

    val sparConf = new SparkConf().setMaster("local").setAppName("Dep")
    val sc = new SparkContext(sparConf)


    val rdd = sc.makeRDD(
      List(
        ("a", 1),
        ("b", 2),
        ("c", 3)
      )
    )

    rdd.saveAsTextFile("output1")
    rdd.saveAsObjectFile("output2")
    rdd.saveAsSequenceFile("output3")

    sc.stop()

  }

}

// scalastyle:on