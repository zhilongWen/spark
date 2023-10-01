// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _12_Operator_Coalesce {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    //  算子 - coalesce
    val rdd = sc.makeRDD(List(1, 2, 3, 4, 5, 6), 3)

    // coalesce方法默认情况下不会将分区的数据打乱重新组合
    // 这种情况下的缩减分区可能会导致数据不均衡，出现数据倾斜
    // 如果想要让数据均衡，可以进行shuffle处理
    //val newRDD: RDD[Int] = rdd.coalesce(2)
    val newRDD: RDD[Int] = rdd.coalesce(2, true)

    newRDD.saveAsTextFile("output")

    sc.stop()


  }

}
// scalastyle:on