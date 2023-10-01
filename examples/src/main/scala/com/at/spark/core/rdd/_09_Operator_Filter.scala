// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _09_Operator_Filter {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)

    //  算子 - filter
    val rdd = sc.makeRDD(List(1, 2, 3, 4))

    val filterRDD: RDD[Int] = rdd.filter(num => num % 2 != 0)

    filterRDD.collect().foreach(println)


    sc.stop()


  }

}
// scalastyle:on