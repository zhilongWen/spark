// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _21_Operator_GroupByKey {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)



    //  算子 - (Key - Value类型) - groupByKey

    val rdd = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("a", 3), ("b", 4)
    ))

    // groupByKey : 将数据源中的数据，相同key的数据分在一个组中，形成一个对偶元组
    //              元组中的第一个元素就是key，
    //              元组中的第二个元素就是相同key的value的集合
    val groupRDD: RDD[(String, Iterable[Int])] = rdd.groupByKey()

    groupRDD.collect().foreach(println)

    val groupRDD1: RDD[(String, Iterable[(String, Int)])] = rdd.groupBy(_._1)



  }

}
// scalastyle:on