// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _15_Operator_Intersection_Union_Subtract_Zip {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)
    //  算子 - intersection  union  subtract zip

    // 交集，并集和差集要求两个数据源数据类型保持一致
    // 拉链操作两个数据源的类型可以不一致

    val rdd1 = sc.makeRDD(List(1,2,3,4))
    val rdd2 = sc.makeRDD(List(3,4,5,6))
    val rdd7 = sc.makeRDD(List("3","4","5","6"))

    // 交集 : 【3，4】
    val rdd3: RDD[Int] = rdd1.intersection(rdd2)
    //val rdd8 = rdd1.intersection(rdd7)
    println(rdd3.collect().mkString(","))

    // 并集 : 【1，2，3，4，3，4，5，6】
    val rdd4: RDD[Int] = rdd1.(rdd2)
    println(rdd4.collect().mkString(","))

    // 差集 : 【1，2】
    val rdd5: RDD[Int] = rdd1.subtract(rdd2)
    println(rdd5.collect().mkString(","))

    // 拉链 : 【1-3，2-4，3-5，4-6】
    val rdd6: RDD[(Int, Int)] = rdd1.zip(rdd2)
    val rdd8 = rdd1.zip(rdd7)
    println(rdd6.collect().mkString(","))


  }

}
// scalastyle:on