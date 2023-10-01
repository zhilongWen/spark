// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _11_Operator_Distinct {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    //  算子 - distinct
    val rdd = sc.makeRDD(List(1, 2, 3, 4, 1, 2, 3, 4))

    // map(x => (x, null)).reduceByKey((x, _) => x, numPartitions).map(_._1)

    // (1, null),(2, null),(3, null),(4, null),(1, null),(2, null),(3, null),(4, null)
    // (1, null)(1, null)(1, null)
    // (null, null) => null
    // (1, null) => 1
    val rdd1: RDD[Int] = rdd.distinct()

    rdd1.collect().foreach(println)


    sc.stop()


  }

}
// scalastyle:on