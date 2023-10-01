// scalastyle:off

package com.at.spark.core.rdd

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @create 2023-10-01 
 */
object _30_Operator_Action {

  def main(args: Array[String]): Unit = {

    val sparkConf: SparkConf = new SparkConf()
      .setMaster("local[*]")
      .setAppName(this.getClass.getSimpleName)

    val sc: SparkContext = new SparkContext(sparkConf)


    val rdd = sc.makeRDD(List(1,2,3,4))

/*

    // 行动算子
    // 所谓的行动算子，其实就是触发作业(Job)执行的方法
    // 底层代码调用的是环境对象的runJob方法
    // 底层代码中会创建ActiveJob，并提交执行。
    rdd.collect()

*/


/*
    // reduce
    //val i: Int = rdd.reduce(_+_)
    //println(i)

    // collect : 方法会将不同分区的数据按照分区顺序采集到Driver端内存中，形成数组
    //val ints: Array[Int] = rdd.collect()
    //println(ints.mkString(","))

    // count : 数据源中数据的个数
    val cnt = rdd.count()
    println(cnt)

    // first : 获取数据源中数据的第一个
    val first = rdd.first()
    println(first)

    // take : 获取N个数据
    val ints: Array[Int] = rdd.take(3)
    println(ints.mkString(","))

    // takeOrdered : 数据排序后，取N个数据
    val rdd1 = sc.makeRDD(List(4,2,3,1))
    val ints1: Array[Int] = rdd1.takeOrdered(3)
    println(ints1.mkString(","))

    */


/*
    //10 + 13 + 17 = 40
    // aggregateByKey : 初始值只会参与分区内计算
    // aggregate : 初始值会参与分区内计算,并且和参与分区间计算
    //val result = rdd.aggregate(10)(_+_, _+_)
    val result = rdd.fold(10)(_+_)

    println(result)
    */


/*

    //val rdd = sc.makeRDD(List(1,1,1,4),2)
    val rdd = sc.makeRDD(List(
      ("a", 1),("a", 2),("a", 3)
    ))

    //val intToLong: collection.Map[Int, Long] = rdd.countByValue()
    //println(intToLong)
    val stringToLong: collection.Map[String, Long] = rdd.countByKey()
    println(stringToLong)

*/

/*
    //val rdd = sc.makeRDD(List(1,1,1,4),2)
    val rdd = sc.makeRDD(List(
      ("a", 1),("a", 2),("a", 3)
    ))

    rdd.saveAsTextFile("output")
    rdd.saveAsObjectFile("output1")
    // saveAsSequenceFile方法要求数据的格式必须为K-V类型
    rdd.saveAsSequenceFile("output2")
*/

/*

    val rdd = sc.makeRDD(List(1,2,3,4))

    // foreach 其实是Driver端内存集合的循环遍历方法
    rdd.collect().foreach(println)
    println("******************")
    // foreach 其实是Executor端内存数据打印
    rdd.foreach(println)

    // 算子 ： Operator（操作）
    //         RDD的方法和Scala集合对象的方法不一样
    //         集合对象的方法都是在同一个节点的内存中完成的。
    //         RDD的方法可以将计算逻辑发送到Executor端（分布式节点）执行
    //         为了区分不同的处理效果，所以将RDD的方法称之为算子。
    //        RDD的方法外部的操作都是在Driver端执行的，而方法内部的逻辑代码是在Executor端执行。


*/



/*
    val rdd = sc.makeRDD(List[Int]())

    val user = new User()

    // SparkException: Task not serializable
    // NotSerializableException: $User

    // RDD算子中传递的函数是会包含闭包操作，那么就会进行检测功能
    // 闭包检测
    rdd.foreach(
      num => {
        println("age = " + (user.age + num))
      }
    )
*/


  }

  //class User extends Serializable {
  // 样例类在编译时，会自动混入序列化特质（实现可序列化接口）
  //case class User() {
  class User {
    var age : Int = 30
  }

}
// scalastyle:on