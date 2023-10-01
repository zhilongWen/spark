// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _19_Operator_PartitionBy {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    //  算子 - (Key - Value类型) - partitionBy
    val rdd = sc.makeRDD(List(1,2,3,4),2)

    val mapRDD:RDD[(Int, Int)] = rdd.map((_,1))
    // RDD => PairRDDFunctions
    // 隐式转换（二次编译）

    // partitionBy根据指定的分区规则对数据进行重分区
    val newRDD = mapRDD.partitionBy(new HashPartitioner(2))
    newRDD.partitionBy(new HashPartitioner(2))

    newRDD.saveAsTextFile("output")




  }

}
// scalastyle:on