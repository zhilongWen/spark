
package com.at.spark.core.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-09-30 
 */
object _02_CreateRDD_File {

  def main(args: Array[String]): Unit = {

    // 1.txt
    // 1
    // 2
    // 3

    //  准备环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("RDD")


    val sc = new SparkContext(sparkConf)

    /*

        // 从文件中创建RDD，将文件中的数据作为处理的数据源
        // path路径默认以当前环境的根路径为基准。可以写绝对路径，也可以写相对路径
    //    val rdd: RDD[String] = sc.textFile("./files/1.txt")

        // path路径可以是文件的具体路径，也可以目录名称
    //    val rdd = sc.textFile("./files")

        // path路径还可以使用通配符 *
    //    val rdd = sc.textFile("./files/1*.txt")

        // path还可以是分布式存储系统路径：HDFS
    //    val rdd = sc.textFile("hdfs://hadoop102:8020/test.txt")

        // textFile : 以行为单位来读取数据，读取的数据都是字符串
        // wholeTextFiles : 以文件为单位读取数据
        //    读取的结果表示为元组，第一个元素表示文件路径，第二个元素表示文件内容
        val rdd = sc.wholeTextFiles("./files")

        rdd.collect().foreach(println)

    */


    // textFile可以将文件作为数据处理的数据源，默认也可以设定分区。
    //     minPartitions : 最小分区数量
    //     math.min(defaultParallelism, 2)
    //    val rdd = sc.textFile("./files/1.txt")

    // 如果不想使用默认的分区数量，可以通过第二个参数指定分区数
    // Spark读取文件，底层其实使用的就是Hadoop的读取方式
    // 分区数量的计算方式：
    //    totalSize（文件大小 -> 字节数） = 7
    //    goalSize =  7 / 2 = 3（byte）

    //    7 / 3 = 2...1 (1.1) + 1 = 3(分区)

    //
//    val rdd = sc.textFile("./files/1.txt", 2)


    // 数据分区的分配
    // 1. 数据以行为单位进行读取
    //    spark读取文件，采用的是hadoop的方式读取，所以一行一行读取，和字节数没有关系
    // 2. 数据读取时以偏移量为单位,偏移量不会被重复读取
    /*
     每行数据    偏移量
       1@@   => 012
       2@@   => 345
       3     => 6

     */
    // 3. 数据分区的偏移量范围的计算
    // 0 => [0, 3]  => 12         读取第一行的 1 时会读取到第二行的 2,所有第二行的所有数据都会被读取
    // 1 => [3, 6]  => 3          由于第一次已经读了第而行的数据，所以不会重复读取
    // 2 => [6, 7]  =>

    // 【1,2】，【3】，【】
    val rdd = sc.textFile("./files/1.txt", 2)



    rdd.saveAsTextFile("output")

    // 如果数据源为多个文件，那么计算分区时以文件为单位进行分区




    //  关闭环境
    sc.stop()

  }

}
