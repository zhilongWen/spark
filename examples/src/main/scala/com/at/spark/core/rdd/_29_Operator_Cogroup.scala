// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _29_Operator_Cogroup {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    //  算子 - (Key - Value类型) cogroup

    val rdd1 = sc.makeRDD(List(
      ("a", 1), ("b", 2)//, ("c", 3)
    ))

    val rdd2 = sc.makeRDD(List(
      ("a", 4), ("b", 5),("c", 6),("c", 7)
    ))

    // cogroup : connect + group (分组，连接)
    val cgRDD: RDD[(String, (Iterable[Int], Iterable[Int]))] = rdd1.cogroup(rdd2)

    cgRDD.collect().foreach(println)





  }

}
// scalastyle:on