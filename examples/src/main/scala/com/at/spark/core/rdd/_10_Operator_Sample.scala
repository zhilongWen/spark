// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _10_Operator_Sample {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    //  算子 - sample
    val rdd = sc.makeRDD(List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

    // sample算子需要传递三个参数
    // 1. 第一个参数表示，抽取数据后是否将数据返回 true（放回），false（丢弃）
    // 2. 第二个参数表示，
    //         如果抽取不放回的场合：数据源中每条数据被抽取的概率，基准值的概念
    //         如果抽取放回的场合：表示数据源中的每条数据被抽取的可能次数
    // 3. 第三个参数表示，抽取数据时随机算法的种子
    //                    如果不传递第三个参数，那么使用的是当前系统时间
    //        println(rdd.sample(
    //            false,
    //            0.4
    //            //1
    //        ).collect().mkString(","))

    println(rdd.sample(
      true,
      2
      //1
    ).collect().mkString(","))


    sc.stop()


  }

}
// scalastyle:on