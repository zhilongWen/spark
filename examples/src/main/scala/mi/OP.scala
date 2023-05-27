package mi

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-05-27
 */
object OP {

  def main(args: Array[String]): Unit = {

    println(System.currentTimeMillis())

    val conf: SparkConf = new SparkConf()
      .setAppName("test")
      .setMaster("local[1]")

    val sc: SparkContext = new SparkContext(conf)


    val source: RDD[String] = sc
      .makeRDD(
        Seq("a", "b", "c", "d", "c", "d", "a")
      )

    val mapRDD: RDD[(String, Int)] = source.map(r => (r, 1))

    val reducrRDD: RDD[(String, Int)] = mapRDD.reduceByKey(_ + _)

    val res: Array[(String, Int)] = reducrRDD.collect()

    res.foreach(println)

    sc.stop()

  }

}
