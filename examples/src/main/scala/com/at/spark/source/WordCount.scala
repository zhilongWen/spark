
// scalastyle:off println
package com.at.spark.source


import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.math.random
import org.apache.spark.sql.SparkSession


object WordCount {

  def main(args: Array[String]): Unit = {


    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[1]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)

    val sourceRDD: RDD[String] = sc.makeRDD(
      Seq("hello spark spark hadoop count")
    )

    val flatMapRDD: RDD[String] = sourceRDD.flatMap(_.split(" "))

    val mapRDD: RDD[(String, Long)] = flatMapRDD.map(r => (r, 1L))

    val reduceRDD: RDD[(String, Long)] = mapRDD.reduceByKey(_ + _)


    reduceRDD.collect().foreach(println)

    sc.stop()

  }
}
// scalastyle:on println
