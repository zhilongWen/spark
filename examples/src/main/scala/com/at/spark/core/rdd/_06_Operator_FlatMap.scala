// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _06_Operator_FlatMap {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)

    //  算子 - flatMap
    val rdd = sc.makeRDD(List(List(1, 2), 3, List(4, 5)))

    val flatRDD = rdd.flatMap(
      data => {
        data match {
          case list: List[_] => list
          case dat => List(dat)
        }
      }
    )

    flatRDD.collect().foreach(println)

    sc.stop()
  }

}
// scalastyle:on