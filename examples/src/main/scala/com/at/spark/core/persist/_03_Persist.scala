// scalastyle:off

package com.at.spark.core.persist

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _03_Persist {


  def main(args: Array[String]): Unit = {

    // cache : 将数据临时存储在内存中进行数据重用
    //         会在血缘关系中添加新的依赖。一旦，出现问题，可以重头读取数据
    // persist : 将数据临时存储在磁盘文件中进行数据重用
    //           涉及到磁盘IO，性能较低，但是数据安全
    //           如果作业执行完毕，临时保存的数据文件就会丢失
    // checkpoint : 将数据长久地保存在磁盘文件中进行数据重用
    //           涉及到磁盘IO，性能较低，但是数据安全
    //           为了保证数据安全，所以一般情况下，会独立执行作业
    //           为了能够提高效率，一般情况下，是需要和cache联合使用
    //           执行过程中，会切断血缘关系。重新建立新的血缘关系
    //           checkpoint等同于改变数据源


    val sparConf = new SparkConf().setMaster("local").setAppName("Persist")

    val sc = new SparkContext(sparConf)

    // 配置 cp 的存储位置
    sc.setCheckpointDir("cp")


    val list = List("Hello Scala", "Hello Spark")

    val rdd = sc.makeRDD(list)

    val flatRDD = rdd.flatMap(_.split(" "))

    val mapRDD = flatRDD.map(word => {
      println("@@@@@@@@@@@@")
      (word, 1)
    })

    mapRDD.cache()

    // checkpoint 需要落盘，需要指定检查点保存路径
    // 检查点路径保存的文件，当作业执行完毕后，不会被删除
    // 一般保存路径都是在分布式存储系统：HDFS
    mapRDD.checkpoint()

    /*

单独 cp

@@@@@@@@@@@@
@@@@@@@@@@@@
@@@@@@@@@@@@
@@@@@@@@@@@@
(Spark,1)
(Hello,2)
(Scala,1)
**************************************
@@@@@@@@@@@@
@@@@@@@@@@@@
@@@@@@@@@@@@
@@@@@@@@@@@@
(Spark,CompactBuffer(1))
(Hello,CompactBuffer(1, 1))
(Scala,CompactBuffer(1))

cp 前加 cache

@@@@@@@@@@@@
@@@@@@@@@@@@
@@@@@@@@@@@@
@@@@@@@@@@@@
(Spark,1)
(Hello,2)
(Scala,1)
**************************************
(Spark,CompactBuffer(1))
(Hello,CompactBuffer(1, 1))
(Scala,CompactBuffer(1))

 */


    val reduceRDD: RDD[(String, Int)] = mapRDD.reduceByKey(_ + _)
    reduceRDD.collect().foreach(println)

    println("**************************************")

    val groupRDD = mapRDD.groupByKey()
    groupRDD.collect().foreach(println)


    sc.stop()

  }
}

// scalastyle:on
