// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _03_Operator_Map {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    //  算子 - map

    val rdd = sc.makeRDD(List(1, 2, 3, 4))
    // 1,2,3,4
    // 2,4,6,8

    // 转换函数
    def mapFunction(num: Int): Int = {
      num * 2
    }

    //val mapRDD: RDD[Int] = rdd.map(mapFunction)
    //val mapRDD: RDD[Int] = rdd.map((num:Int)=>{num*2})
    //val mapRDD: RDD[Int] = rdd.map((num:Int)=>num*2)
    //val mapRDD: RDD[Int] = rdd.map((num)=>num*2)
    //val mapRDD: RDD[Int] = rdd.map(num=>num*2)

    val mapRDD: RDD[Int] = rdd.map(_ * 2)

    mapRDD.collect().foreach(println)


    sc.stop()
  }

}
// scalastyle:on