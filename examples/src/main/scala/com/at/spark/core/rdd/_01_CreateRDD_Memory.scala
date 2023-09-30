
package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object _01_CreateRDD_Memory {

  def main(args: Array[String]): Unit = {

    //  准备环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")

    //    sparkConf.set("spark.default.parallelism", "5")

    val sc = new SparkContext(sparkConf)


    /*
        // 创建RDD
        // 从内存中创建RDD，将内存中集合的数据作为处理的数据源
        val seq = Seq[Int](1, 2, 3, 4)

        // parallelize : 并行
        //val rdd: RDD[Int] = sc.parallelize(seq)
        // makeRDD方法在底层实现时其实就是调用了rdd对象的parallelize方法。
        val rdd: RDD[Int] = sc.makeRDD(seq)

        rdd.collect().foreach(println)

        */

    /*
        // 创建RDD
        // RDD的并行度 & 分区
        // makeRDD方法可以传递第二个参数，这个参数表示分区的数量
        // 第二个参数可以不传递的，那么makeRDD方法会使用默认值 ： defaultParallelism（默认并行度）
        //     scheduler.conf.getInt("spark.default.parallelism", totalCores)
        //    spark在默认情况下，从配置对象中获取配置参数：spark.default.parallelism
        //    如果获取不到，那么使用totalCores属性，这个属性取值为当前运行环境的最大可用核数
        val rdd: RDD[Int] = sc.makeRDD(List(1, 2, 3, 4), 2)
        // val rdd = sc.makeRDD(List(1,2,3,4))

        // 将处理的数据保存成分区文件
        rdd.saveAsTextFile("output")
    */


    // 【1，2】，【3，4】
    // val rdd = sc.makeRDD(List(1,2,3,4), 2)
    // 【1】，【2】，【3，4】
    // val rdd = sc.makeRDD(List(1,2,3,4), 3)
    // 【1】，【2,3】，【4,5】
    // org.apache.spark.rdd.ParallelCollectionRDD.slice 在该方法中判断数据到底方到按个分片中
    val rdd = sc.makeRDD(List(1, 2, 3, 4, 5), 3)



    // 关闭环境
    sc.stop()

  }
}
