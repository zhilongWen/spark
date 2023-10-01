// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _14_Operator_SortBy {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)

    //  算子 - sortBy
    val rdd = sc.makeRDD(List(6,2,4,5,3,1), 2)

    val newRDD: RDD[Int] = rdd.sortBy(num=>num)

    newRDD.saveAsTextFile("output")

    sc.stop()


  }

}
// scalastyle:on