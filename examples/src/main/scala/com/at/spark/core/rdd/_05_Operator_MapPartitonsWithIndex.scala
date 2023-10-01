// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _05_Operator_MapPartitonsWithIndex {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)

    //  算子 - mapPartitions
    val rdd = sc.makeRDD(List(1, 2, 3, 4), 2)
    // 【1，2】，【3，4】
    val mpiRDD = rdd.mapPartitionsWithIndex(
      (index, iter) => {
        if (index == 1) {
          iter
        } else {
          Nil.iterator
        }
      }
    )
    mpiRDD.collect().foreach(println)


    sc.stop()
  }

}
// scalastyle:on