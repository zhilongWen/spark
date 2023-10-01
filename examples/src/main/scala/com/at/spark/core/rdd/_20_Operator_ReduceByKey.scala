// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _20_Operator_ReduceByKey {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    //  算子 - (Key - Value类型) - reduceByKey

    val rdd = sc.makeRDD(List(
      ("a", 1), ("a", 2), ("a", 3), ("b", 4)
    ))

    // reduceByKey : 相同的key的数据进行value数据的聚合操作
    // scala语言中一般的聚合操作都是两两聚合，spark基于scala开发的，所以它的聚合也是两两聚合
    // 【1，2，3】
    // 【3，3】
    // 【6】
    // reduceByKey中如果key的数据只有一个，是不会参与运算的。
    val reduceRDD: RDD[(String, Int)] = rdd.reduceByKey((x: Int, y: Int) => {
      println(s"x = ${x}, y = ${y}")
      x + y
    })

    reduceRDD.collect().foreach(println)


  }

}
// scalastyle:on