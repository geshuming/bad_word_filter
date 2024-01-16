import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, udf}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

import scala.collection.mutable

class NewBadWordFilterSparkTest extends FunSuite with Matchers with TableDrivenPropertyChecks {
  lazy implicit val spark: SparkSession = SparkSession
    .builder()
    .config("spark.master", "local")
    .getOrCreate()

  import spark.sqlContext.implicits._

  test("prod test") {
    NewBadWordFilter
    val data =
      """
        <your data here>
      """
      
    val genreId: String = "123456"
    val badWords: mutable.WrappedArray[String] = Utils.getDataFromFile("bad_words/prod_bad_words.txt")
    val badWordsTrie = new BadWordTrie(badWords)

    val df = Seq((data, genreId)).toDF("valueToCheck", "genreId")
    val badWordsTrieBroadcast = spark.sparkContext.broadcast[BadWordTrie](badWordsTrie)
    val badWordFilterUDF = udf(
      (valueToCheck: String, genreId: String) =>
        NewBadWordFilter(valueToCheck, genreId, badWordsTrieBroadcast.value))

    val resultsDf = df.select(
      badWordFilterUDF(col("valueToCheck"), col("genreId"))
        .as("hasBadWord"))

    resultsDf.head().getAs[String]("hasBadWord") should equal("1")
  }

  test("prod test 2") {
    NewBadWordFilter
    val data: mutable.WrappedArray[String] = Utils.getDataFromFile("data/has_bad_word/200_lines_5000_chars_each.txt")
    val genreId: String = "123456"
    val badWords: mutable.WrappedArray[String] = Utils.getDataFromFile("bad_words/prod_bad_words.txt")
    val badWordsTrie = new BadWordTrie(badWords)

    val df = data.map((_, genreId)).toDF("valueToCheck", "genreId")
    val badWordsTrieBroadcast = spark.sparkContext.broadcast[BadWordTrie](badWordsTrie)
    val badWordFilterUDF = udf(
      (valueToCheck: String, genreId: String) =>
        NewBadWordFilter(valueToCheck, genreId, badWordsTrieBroadcast.value))

    val resultsDf = df.select(
      badWordFilterUDF(col("valueToCheck"), col("genreId"))
        .as("hasBadWord"))

    resultsDf.collect()
      .foreach(row => row.getAs[String]("hasBadWord") should equal("1"))
  }
}