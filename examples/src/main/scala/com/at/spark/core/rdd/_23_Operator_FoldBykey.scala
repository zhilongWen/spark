// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _23_Operator_FoldBykey {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    //  算子 - (Key - Value类型) - foldByKey

    val rdd = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("b", 3),
      ("b", 4), ("b", 5), ("a", 6)
    ), 2)

    //rdd.aggregateByKey(0)(_+_, _+_).collect.foreach(println)

    // 如果聚合计算时，分区内和分区间计算规则相同，spark提供了简化的方法
    rdd.foldByKey(0)(_ + _).collect.foreach(println)


  }

}
// scalastyle:on