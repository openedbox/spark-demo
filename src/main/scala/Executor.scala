import com.github.nscala_time.time.Imports._
import com.google.common.base.Stopwatch
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
object Executor {

  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.load()
    val conf = new SparkConf()
      .setIfMissing("spark.master", config.getString("spark.master"))
      .setIfMissing("spark.app.name", config.getString("spark.app.name"))

    val sc = new SparkContext(conf)
    val sqlc = new SQLContext(sc)
    val id = sc.applicationId

    sc
      .textFile("file:/Users/wxy/Desktop/testdata.txt")
      .flatMap(str => {
        str.split(" ").map(word => {
          (word, 1)
        })
      })
      .reduceByKey((left, right) => left + right)
      .foreach(println)

    val prop = new java.util.Properties
    prop.setProperty("user",config.getString("testdb.name"))
    prop.setProperty("password",config.getString("testdb.password"))
    val dburl = config.getString("testdb.url")
    val dbtable="account"

    val testDf = sqlc.read.jdbc(dburl,dbtable,prop)

    testDf.where("name='xxx'").select("id").show


    val stopwatch = new Stopwatch
    stopwatch.start()
    stopwatch.stop()
    sc.stop()

  }
}
