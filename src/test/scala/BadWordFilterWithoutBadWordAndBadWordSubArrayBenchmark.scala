import org.scalameter.api._

import scala.collection.mutable

object BadWordFilterWithoutBadWordAndBadWordSubArrayBenchmark extends Bench.OfflineReport {
  val oldBadWordFilter = new OldBadWordFilter

  // Setup test data
  val data: mutable.WrappedArray[String] = Utils.getDataFromFile("data/not_has_bad_word/1_lines_5000_chars_each.txt")
  val genreId: String = "123456"
  val badWords: mutable.WrappedArray[String] = Utils.getDataFromFile("bad_words/prod_bad_words.txt")
  val badWordsTrie = new BadWordTrie(badWords)

  // Setup test values
  val badWordValues: Gen[mutable.WrappedArray[String]] = Gen
    .range("subarray_length")(500, 5000, 500)
    .map(subarray_length => badWords.slice(0, subarray_length))
  val badWordTrieValues: Gen[BadWordTrie] = Gen
    .range("subarray_length")(500, 5000, 500)
    .map(subarray_length => new BadWordTrie(badWords.slice(0, subarray_length)))

  // Execute test
  println("Found NG Words: " + NewBadWordFilter(data(0), genreId, badWordsTrie))

  performance of "NewBadWordFilter" in {
    performance of "withNegativeTestcase" in {
      performance of "withDifferentBadWordSubarrayLengths" in {
        measure method "apply" in {
          using(badWordTrieValues) in {
            value => NewBadWordFilter(data(0), genreId, value)
          }
        }
      }
    }
  }

  performance of "OldBadWordFilter" in {
    performance of "withPositiveTestcase" in {
      performance of "withDifferentSubstringLengths" in {
        measure method "apply" in {
          using(badWordValues) in {
            value => oldBadWordFilter.apply(data(0), genreId, value)
          }
        }
      }
    }
  }
}