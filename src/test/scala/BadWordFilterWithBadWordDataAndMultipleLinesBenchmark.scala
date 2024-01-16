import org.scalameter.api._

import scala.collection.mutable

object BadWordFilterWithBadWordDataAndMultipleLinesBenchmark extends Bench.OfflineReport {
  val oldBadWordFilter = new OldBadWordFilter

  // Setup test data
  val data: mutable.WrappedArray[String] = Utils.getDataFromFile("data/has_bad_word/200_lines_5000_chars_each.txt")
  val genreId: String = "123456"
  val badWords: mutable.WrappedArray[String] = Utils.getDataFromFile("bad_words/prod_bad_words.txt")
  val badWordsTrie = new BadWordTrie(badWords)

  // Setup test values
  val values: Gen[mutable.WrappedArray[String]] = Gen
    .range("number_of_lines")(5, 50, 5)
    .map(number_of_lines => data.slice(0, number_of_lines))

  // Execute test
  performance of "NewBadWordFilter" in {
    performance of "withPositiveTestcase" in {
      performance of "withDifferentNumberOfLines" in {
        using(values) in {
          lines => lines.foreach(NewBadWordFilter(_, genreId, badWordsTrie))
        }
      }
    }
  }

  performance of "OldBadWordFilter" in {
    performance of "withPositiveTestcase" in {
      performance of "withDifferentNumberOfLines" in {
        measure method "apply" in {
          using(values) in {
            lines => lines.foreach(oldBadWordFilter.apply(_, genreId, badWords))
          }
        }
      }
    }
  }
}