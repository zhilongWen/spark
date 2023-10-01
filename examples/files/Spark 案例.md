---
typora-root-url: images
---



## 数据

![](/Snipaste_2023-10-01_22-54-38.jpg)

上面的数据图是从数据文件中截取的一部分内容，表示为电商网站的用户行为数据，主
要包含用户的 4 种行为： 搜索，点击，下单，支付。 数据规则如下：
➢ 数据文件中每行数据采用下划线分隔数据
➢ 每一行数据表示用户的一次行为，这个行为只能是 4 种行为的一种
➢ 如果搜索关键字为 null,表示数据不是搜索数据
➢ 如果点击的品类 ID 和产品 ID 为-1，表示数据不是点击数据
➢ 针对于下单行为，一次可以下单多个商品，所以品类 ID 和产品 ID 可以是多个， id 之间采用逗号分隔，如果本次不是下单行为，则数据采用 null 表示
➢ 支付行为和下单行为类似 



详细字段说明

| 编号 | 字段名称           | 字段类型 | 字段含义                     |
| ---- | ------------------ | -------- | ---------------------------- |
| 1    | date               | String   | 用户点击行为的日期           |
| 2    | user_id            | Long     | 用户的 ID                    |
| 3    | session_id         | String   | Session 的 ID                |
| 4    | page_id            | Long     | 某个页面的 ID                |
| 5    | action_time        | String   | 动作的时间点                 |
| 6    | search_keyword     | String   | 用户搜索的关键词             |
| 7    | click_category_id  | Long     | 某一个商品品类的 ID          |
| 8    | click_product_id   | Long     | 某一个商品的 ID              |
| 9    | order_category_ids | String   | 一次订单中所有品类的 ID 集合 |
| 10   | order_product_ids  | String   | 一次订单中所有商品的 ID 集合 |
| 11   | pay_category_ids   | String   | 一次支付中所有品类的 ID 集合 |
| 12   | pay_product_ids    | String   | 一次支付中所有商品的 ID 集合 |
| 13   | city_id            | Long     | 城市 id                      |

样例类

```scala
//用户访问动作表
case class UserVisitAction(
    date: String,//用户点击行为的日期
    user_id: Long,//用户的 ID
    session_id: String,//Session 的 ID
    page_id: Long,//某个页面的 ID
    action_time: String,//动作的时间点
    search_keyword: String,//用户搜索的关键词
    click_category_id: Long,//某一个商品品类的 ID
    click_product_id: Long,//某一个商品的 ID
    order_category_ids: String,//一次订单中所有品类的 ID 集合
    order_product_ids: String,//一次订单中所有商品的 ID 集合
    pay_category_ids: String,//一次支付中所有品类的 ID 集合
    pay_product_ids: String,//一次支付中所有商品的 ID 集合
    city_id: Long //城市 id
)

```





## 需求 1： Top10 热门品类 

### 需求说明 

品类是指产品的分类，大型电商网站品类分多级，咱们的项目中品类只有一级，不同的
公司可能对热门的定义不一样。我们按照每个品类的点击、下单、支付的量来统计热门品类。 

鞋        点击数        下单数        支付数

衣服    点击数        下单数        支付数 

电脑    点击数        下单数        支付数 

例如，综合排名 = 点击数*20%+下单数*30%+支付数*50%
本项目需求优化为： 先按照点击数排名，靠前的就排名高；如果点击数相同，再比较下
单数；下单数再相同，就比较支付数 



### 实现方案一 

分别统计每个品类点击的次数，下单的次数和支付的次数：
（品类，点击总数）（品类，下单总数）（品类，支付总数） 

```scala

  def main(args: Array[String]): Unit = {

    //  : Top10热门品类
    val sparConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
    val sc = new SparkContext(sparConf)

    // 1. 读取原始日志数据
    val actionRDD = sc.textFile("datas/user_visit_action.txt")

    // 2. 统计品类的点击数量：（品类ID，点击数量）
    val clickActionRDD = actionRDD.filter(
      action => {
        val datas = action.split("_")
        datas(6) != "-1"
      }
    )

    val clickCountRDD: RDD[(String, Int)] = clickActionRDD.map(
      action => {
        val datas = action.split("_")
        (datas(6), 1)
      }
    ).reduceByKey(_ + _)

    // 3. 统计品类的下单数量：（品类ID，下单数量）
    val orderActionRDD = actionRDD.filter(
      action => {
        val datas = action.split("_")
        datas(8) != "null"
      }
    )

    // orderid => 1,2,3
    // 【(1,1)，(2,1)，(3,1)】
    val orderCountRDD = orderActionRDD.flatMap(
      action => {
        val datas = action.split("_")
        val cid = datas(8)
        val cids = cid.split(",")
        cids.map(id => (id, 1))
      }
    ).reduceByKey(_ + _)

    // 4. 统计品类的支付数量：（品类ID，支付数量）
    val payActionRDD = actionRDD.filter(
      action => {
        val datas = action.split("_")
        datas(10) != "null"
      }
    )

    // orderid => 1,2,3
    // 【(1,1)，(2,1)，(3,1)】
    val payCountRDD = payActionRDD.flatMap(
      action => {
        val datas = action.split("_")
        val cid = datas(10)
        val cids = cid.split(",")
        cids.map(id => (id, 1))
      }
    ).reduceByKey(_ + _)

    // 5. 将品类进行排序，并且取前10名
    //    点击数量排序，下单数量排序，支付数量排序
    //    元组排序：先比较第一个，再比较第二个，再比较第三个，依此类推
    //    ( 品类ID, ( 点击数量, 下单数量, 支付数量 ) )
    //
    //  cogroup = connect + group
    val cogroupRDD: RDD[(String, (Iterable[Int], Iterable[Int], Iterable[Int]))] =
    clickCountRDD.cogroup(orderCountRDD, payCountRDD)
    val analysisRDD = cogroupRDD.mapValues {
      case (clickIter, orderIter, payIter) => {

        var clickCnt = 0
        val iter1 = clickIter.iterator
        if (iter1.hasNext) {
          clickCnt = iter1.next()
        }
        var orderCnt = 0
        val iter2 = orderIter.iterator
        if (iter2.hasNext) {
          orderCnt = iter2.next()
        }
        var payCnt = 0
        val iter3 = payIter.iterator
        if (iter3.hasNext) {
          payCnt = iter3.next()
        }

        (clickCnt, orderCnt, payCnt)
      }
    }

    val resultRDD = analysisRDD.sortBy(_._2, false).take(10)

    // 6. 将结果采集到控制台打印出来
    resultRDD.foreach(println)

    sc.stop()
  }

```



### 实现方案二 

一次性统计每个品类点击的次数，下单的次数和支付的次数：
（品类， （点击总数，下单总数，支付总数） ） 

```scala

    def main(args: Array[String]): Unit = {

        //  : Top10热门品类
        val sparConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
        val sc = new SparkContext(sparConf)

        // Q : actionRDD重复使用
        // Q : cogroup性能可能较低

        // 1. 读取原始日志数据
        val actionRDD = sc.textFile("datas/user_visit_action.txt")
        actionRDD.cache()

        // 2. 统计品类的点击数量：（品类ID，点击数量）
        val clickActionRDD = actionRDD.filter(
            action => {
                val datas = action.split("_")
                datas(6) != "-1"
            }
        )

        val clickCountRDD: RDD[(String, Int)] = clickActionRDD.map(
            action => {
                val datas = action.split("_")
                (datas(6), 1)
            }
        ).reduceByKey(_ + _)

        // 3. 统计品类的下单数量：（品类ID，下单数量）
        val orderActionRDD = actionRDD.filter(
            action => {
                val datas = action.split("_")
                datas(8) != "null"
            }
        )

        // orderid => 1,2,3
        // 【(1,1)，(2,1)，(3,1)】
        val orderCountRDD = orderActionRDD.flatMap(
            action => {
                val datas = action.split("_")
                val cid = datas(8)
                val cids = cid.split(",")
                cids.map(id=>(id, 1))
            }
        ).reduceByKey(_+_)

        // 4. 统计品类的支付数量：（品类ID，支付数量）
        val payActionRDD = actionRDD.filter(
            action => {
                val datas = action.split("_")
                datas(10) != "null"
            }
        )

        // orderid => 1,2,3
        // 【(1,1)，(2,1)，(3,1)】
        val payCountRDD = payActionRDD.flatMap(
            action => {
                val datas = action.split("_")
                val cid = datas(10)
                val cids = cid.split(",")
                cids.map(id=>(id, 1))
            }
        ).reduceByKey(_+_)

        // (品类ID, 点击数量) => (品类ID, (点击数量, 0, 0))
        // (品类ID, 下单数量) => (品类ID, (0, 下单数量, 0))
        //                    => (品类ID, (点击数量, 下单数量, 0))
        // (品类ID, 支付数量) => (品类ID, (0, 0, 支付数量))
        //                    => (品类ID, (点击数量, 下单数量, 支付数量))
        // ( 品类ID, ( 点击数量, 下单数量, 支付数量 ) )

        // 5. 将品类进行排序，并且取前10名
        //    点击数量排序，下单数量排序，支付数量排序
        //    元组排序：先比较第一个，再比较第二个，再比较第三个，依此类推
        //    ( 品类ID, ( 点击数量, 下单数量, 支付数量 ) )
        //
        val rdd1 = clickCountRDD.map{
            case ( cid, cnt ) => {
                (cid, (cnt, 0, 0))
            }
        }
        val rdd2 = orderCountRDD.map{
            case ( cid, cnt ) => {
                (cid, (0, cnt, 0))
            }
        }
        val rdd3 = payCountRDD.map{
            case ( cid, cnt ) => {
                (cid, (0, 0, cnt))
            }
        }

        // 将三个数据源合并在一起，统一进行聚合计算
        val soruceRDD: RDD[(String, (Int, Int, Int))] = rdd1.union(rdd2).union(rdd3)

        val analysisRDD = soruceRDD.reduceByKey(
            ( t1, t2 ) => {
                ( t1._1+t2._1, t1._2 + t2._2, t1._3 + t2._3 )
            }
        )

        val resultRDD = analysisRDD.sortBy(_._2, false).take(10)

        // 6. 将结果采集到控制台打印出来
        resultRDD.foreach(println)

        sc.stop()
    }

```

```scala

    def main(args: Array[String]): Unit = {

        //  : Top10热门品类
        val sparConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
        val sc = new SparkContext(sparConf)

        // Q : 存在大量的shuffle操作（reduceByKey）
        // reduceByKey 聚合算子，spark会提供优化，缓存

        // 1. 读取原始日志数据
        val actionRDD = sc.textFile("datas/user_visit_action.txt")

        // 2. 将数据转换结构
        //    点击的场合 : ( 品类ID，( 1, 0, 0 ) )
        //    下单的场合 : ( 品类ID，( 0, 1, 0 ) )
        //    支付的场合 : ( 品类ID，( 0, 0, 1 ) )
        val flatRDD: RDD[(String, (Int, Int, Int))] = actionRDD.flatMap(
            action => {
                val datas = action.split("_")
                if (datas(6) != "-1") {
                    // 点击的场合
                    List((datas(6), (1, 0, 0)))
                } else if (datas(8) != "null") {
                    // 下单的场合
                    val ids = datas(8).split(",")
                    ids.map(id => (id, (0, 1, 0)))
                } else if (datas(10) != "null") {
                    // 支付的场合
                    val ids = datas(10).split(",")
                    ids.map(id => (id, (0, 0, 1)))
                } else {
                    Nil
                }
            }
        )

        // 3. 将相同的品类ID的数据进行分组聚合
        //    ( 品类ID，( 点击数量, 下单数量, 支付数量 ) )
        val analysisRDD = flatRDD.reduceByKey(
            (t1, t2) => {
                ( t1._1+t2._1, t1._2 + t2._2, t1._3 + t2._3 )
            }
        )

        // 4. 将统计结果根据数量进行降序处理，取前10名
        val resultRDD = analysisRDD.sortBy(_._2, false).take(10)

        // 5. 将结果采集到控制台打印出来
        resultRDD.foreach(println)

        sc.stop()
    }

```



### 实现方案三 

使用累加器的方式聚合数据 

```scala

    def main(args: Array[String]): Unit = {

        //  : Top10热门品类
        val sparConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
        val sc = new SparkContext(sparConf)

        // 1. 读取原始日志数据
        val actionRDD = sc.textFile("datas/user_visit_action.txt")

        val acc = new HotCategoryAccumulator
        sc.register(acc, "hotCategory")

        // 2. 将数据转换结构
        actionRDD.foreach(
            action => {
                val datas = action.split("_")
                if (datas(6) != "-1") {
                    // 点击的场合
                    acc.add((datas(6), "click"))
                } else if (datas(8) != "null") {
                    // 下单的场合
                    val ids = datas(8).split(",")
                    ids.foreach(
                        id => {
                            acc.add( (id, "order") )
                        }
                    )
                } else if (datas(10) != "null") {
                    // 支付的场合
                    val ids = datas(10).split(",")
                    ids.foreach(
                        id => {
                            acc.add( (id, "pay") )
                        }
                    )
                }
            }
        )

        val accVal: mutable.Map[String, HotCategory] = acc.value
        val categories: mutable.Iterable[HotCategory] = accVal.map(_._2)

        val sort = categories.toList.sortWith(
            (left, right) => {
                if ( left.clickCnt > right.clickCnt ) {
                    true
                } else if (left.clickCnt == right.clickCnt) {
                    if ( left.orderCnt > right.orderCnt ) {
                        true
                    } else if (left.orderCnt == right.orderCnt) {
                        left.payCnt > right.payCnt
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
        )

        // 5. 将结果采集到控制台打印出来
        sort.take(10).foreach(println)

        sc.stop()
    }
    case class HotCategory( cid:String, var clickCnt : Int, var orderCnt : Int, var payCnt : Int )
    /**
      * 自定义累加器
      * 1. 继承AccumulatorV2，定义泛型
      *    IN : ( 品类ID, 行为类型 )
      *    OUT : mutable.Map[String, HotCategory]
      * 2. 重写方法（6）
      */
    class HotCategoryAccumulator extends AccumulatorV2[(String, String), mutable.Map[String, HotCategory]]{

        private val hcMap = mutable.Map[String, HotCategory]()

        override def isZero: Boolean = {
            hcMap.isEmpty
        }

        override def copy(): AccumulatorV2[(String, String), mutable.Map[String, HotCategory]] = {
            new HotCategoryAccumulator()
        }

        override def reset(): Unit = {
            hcMap.clear()
        }

        override def add(v: (String, String)): Unit = {
            val cid = v._1
            val actionType = v._2
            val category: HotCategory = hcMap.getOrElse(cid, HotCategory(cid, 0,0,0))
            if ( actionType == "click" ) {
                category.clickCnt += 1
            } else if (actionType == "order") {
                category.orderCnt += 1
            } else if (actionType == "pay") {
                category.payCnt += 1
            }
            hcMap.update(cid, category)
        }

        override def merge(other: AccumulatorV2[(String, String), mutable.Map[String, HotCategory]]): Unit = {
            val map1 = this.hcMap
            val map2 = other.value

            map2.foreach{
                case ( cid, hc ) => {
                    val category: HotCategory = map1.getOrElse(cid, HotCategory(cid, 0,0,0))
                    category.clickCnt += hc.clickCnt
                    category.orderCnt += hc.orderCnt
                    category.payCnt += hc.payCnt
                    map1.update(cid, category)
                }
            }
        }

        override def value: mutable.Map[String, HotCategory] = hcMap
    }

```





## 需求 2： Top10 热门品类中每个品类的 Top10 活跃 Session 统计 



### 需求说明 

在需求一的基础上，增加每个品类用户 session 的点击统计 

```scala

    def main(args: Array[String]): Unit = {

        //  : Top10热门品类
        val sparConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
        val sc = new SparkContext(sparConf)

        val actionRDD = sc.textFile("datas/user_visit_action.txt")
        actionRDD.cache()
        val top10Ids: Array[String] = top10Category(actionRDD)

        // 1. 过滤原始数据,保留点击和前10品类ID
        val filterActionRDD = actionRDD.filter(
            action => {
                val datas = action.split("_")
                if ( datas(6) != "-1" ) {
                    top10Ids.contains(datas(6))
                } else {
                    false
                }
            }
        )

        // 2. 根据品类ID和sessionid进行点击量的统计
        val reduceRDD: RDD[((String, String), Int)] = filterActionRDD.map(
            action => {
                val datas = action.split("_")
                ((datas(6), datas(2)), 1)
            }
        ).reduceByKey(_ + _)

        // 3. 将统计的结果进行结构的转换
        //  (（ 品类ID，sessionId ）,sum) => ( 品类ID，（sessionId, sum） )
        val mapRDD = reduceRDD.map{
            case ( (cid, sid), sum ) => {
                ( cid, (sid, sum) )
            }
        }

        // 4. 相同的品类进行分组
        val groupRDD: RDD[(String, Iterable[(String, Int)])] = mapRDD.groupByKey()

        // 5. 将分组后的数据进行点击量的排序，取前10名
        val resultRDD = groupRDD.mapValues(
            iter => {
                iter.toList.sortBy(_._2)(Ordering.Int.reverse).take(10)
            }
        )

        resultRDD.collect().foreach(println)


        sc.stop()
    }
    def top10Category(actionRDD:RDD[String]) = {
        val flatRDD: RDD[(String, (Int, Int, Int))] = actionRDD.flatMap(
            action => {
                val datas = action.split("_")
                if (datas(6) != "-1") {
                    // 点击的场合
                    List((datas(6), (1, 0, 0)))
                } else if (datas(8) != "null") {
                    // 下单的场合
                    val ids = datas(8).split(",")
                    ids.map(id => (id, (0, 1, 0)))
                } else if (datas(10) != "null") {
                    // 支付的场合
                    val ids = datas(10).split(",")
                    ids.map(id => (id, (0, 0, 1)))
                } else {
                    Nil
                }
            }
        )

        val analysisRDD = flatRDD.reduceByKey(
            (t1, t2) => {
                ( t1._1+t2._1, t1._2 + t2._2, t1._3 + t2._3 )
            }
        )

        analysisRDD.sortBy(_._2, false).take(10).map(_._1)
    }

```



## 需求 3：页面单跳转换率统计 

### 需求说明 

页面单跳转化率
计算页面单跳转化率，什么是页面单跳转换率，比如一个用户在一次 Session 过程中访问的页面路径 3,5,7,9,10,21，那么页面 3 跳到页面 5 叫一次单跳， 7-9 也叫一次单跳，那么单跳转化率就是要统计页面点击的概率。
比如：计算 3-5 的单跳转化率，先获取符合条件的 Session 对于页面 3 的访问次数（PV）为 A，然后获取符合条件的 Session 中访问了页面 3 又紧接着访问了页面 5 的次数为 B，那么 B/A 就是 3-5 的页面单跳转化率 

![](/Snipaste_2023-10-01_23-14-28.jpg)

统计页面单跳转化率意义
产品经理和运营总监，可以根据这个指标，去尝试分析，整个网站，产品，各个页面的
表现怎么样，是不是需要去优化产品的布局；吸引用户最终可以进入最后的支付页面。
数据分析师，可以此数据做更深一步的计算和分析。
企业管理层，可以看到整个公司的网站，各个页面的之间的跳转的表现如何，可以适当
调整公司的经营战略或策略。 

![](/Snipaste_2023-10-01_23-18-31.jpg)

```scala

    def main(args: Array[String]): Unit = {

        //  : Top10热门品类
        val sparConf = new SparkConf().setMaster("local[*]").setAppName("HotCategoryTop10Analysis")
        val sc = new SparkContext(sparConf)

        val actionRDD = sc.textFile("datas/user_visit_action.txt")

        val actionDataRDD = actionRDD.map(
            action => {
                val datas = action.split("_")
                UserVisitAction(
                    datas(0),
                    datas(1).toLong,
                    datas(2),
                    datas(3).toLong,
                    datas(4),
                    datas(5),
                    datas(6).toLong,
                    datas(7).toLong,
                    datas(8),
                    datas(9),
                    datas(10),
                    datas(11),
                    datas(12).toLong
                )
            }
        )
        actionDataRDD.cache()

        //  对指定的页面连续跳转进行统计
        // 1-2,2-3,3-4,4-5,5-6,6-7
        val ids = List[Long](1,2,3,4,5,6,7)
        val okflowIds: List[(Long, Long)] = ids.zip(ids.tail)

        //  计算分母
        val pageidToCountMap: Map[Long, Long] = actionDataRDD.filter(
            action => {
                ids.init.contains(action.page_id)
            }
        ).map(
            action => {
                (action.page_id, 1L)
            }
        ).reduceByKey(_ + _).collect().toMap

        //  计算分子

        // 根据session进行分组
        val sessionRDD: RDD[(String, Iterable[UserVisitAction])] = actionDataRDD.groupBy(_.session_id)

        // 分组后，根据访问时间进行排序（升序）
        val mvRDD: RDD[(String, List[((Long, Long), Int)])] = sessionRDD.mapValues(
            iter => {
                val sortList: List[UserVisitAction] = iter.toList.sortBy(_.action_time)

                // 【1，2，3，4】
                // 【1，2】，【2，3】，【3，4】
                // 【1-2，2-3，3-4】
                // Sliding : 滑窗
                // 【1，2，3，4】
                // 【2，3，4】
                // zip : 拉链
                val flowIds: List[Long] = sortList.map(_.page_id)
                val pageflowIds: List[(Long, Long)] = flowIds.zip(flowIds.tail)

                // 将不合法的页面跳转进行过滤
                pageflowIds.filter(
                    t => {
                        okflowIds.contains(t)
                    }
                ).map(
                    t => {
                        (t, 1)
                    }
                )
            }
        )
        // ((1,2),1)
        val flatRDD: RDD[((Long, Long), Int)] = mvRDD.map(_._2).flatMap(list=>list)
        // ((1,2),1) => ((1,2),sum)
        val dataRDD = flatRDD.reduceByKey(_+_)

        //  计算单跳转换率
        // 分子除以分母
        dataRDD.foreach{
            case ( (pageid1, pageid2), sum ) => {
                val lon: Long = pageidToCountMap.getOrElse(pageid1, 0L)

                println(s"页面${pageid1}跳转到页面${pageid2}单跳转换率为:" + ( sum.uble/lon ))
            }
        }


        sc.stop()
    }

    //用户访问动作表
    case class UserVisitAction(
              date: String,//用户点击行为的日期
              user_id: Long,//用户的ID
              session_id: String,//Session的ID
              page_id: Long,//某个页面的ID
              action_time: String,//动作的时间点
              search_keyword: String,//用户搜索的关键词
              click_category_id: Long,//某一个商品品类的ID
              click_product_id: Long,//某一个商品的ID
              order_category_ids: String,//一次订单中所有品类的ID集合
              order_product_ids: String,//一次订单中所有商品的ID集合
              pay_category_ids: String,//一次支付中所有品类的ID集合
              pay_product_ids: String,//一次支付中所有商品的ID集合
              city_id: Long
      )//城市 id

```















