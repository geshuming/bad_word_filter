import org.scalameter.api._

import scala.collection.mutable

object BadWordFilterWithBadWordDataBenchmark extends Bench.OfflineReport {
  val oldBadWordFilter = new OldBadWordFilter

  // Setup test data
  val data: mutable.WrappedArray[String] = Utils.getDataFromFile("data/has_bad_word/1_lines_5000_chars_each.txt")
  val genreId: String = "123456"
  val badWords: mutable.WrappedArray[String] = Utils.getDataFromFile("bad_words/prod_bad_words.txt")
  val badWordsTrie = new BadWordTrie(badWords)

  // Setup test values
  val values: Gen[String] = Gen
    .range("substring_length")(500, 5000, 500)
    .map(substring_length => data(0).padTo(5001, " ").toString().substring(0, substring_length))

  // Execute test
  println("Found NG Words: " + NewBadWordFilter(data(0), genreId, badWordsTrie))

  performance of "NewBadWordFilter" in {
    performance of "withPositiveTestcase" in {
      performance of "withDifferentSubstringLengths" in {
        measure method "apply" in {
          using(values) in {
            value => NewBadWordFilter(value, genreId, badWordsTrie)
          }
        }
      }
    }
  }

  performance of "OldBadWordFilter" in {
    performance of "withPositiveTestcase" in {
      performance of "withDifferentSubstringLengths" in {
        measure method "apply" in {
          using(values) in {
            value => oldBadWordFilter.apply(value, genreId, badWords)
          }
        }
      }
    }
  }
}