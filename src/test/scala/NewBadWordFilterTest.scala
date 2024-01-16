import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

import scala.collection.mutable

class NewBadWordFilterTest extends FunSuite with Matchers with TableDrivenPropertyChecks {
  ignore("prod test") {
    val data =
      """
        <your data here>
      """

    val genreId: String = "123456"
    val badWords: mutable.WrappedArray[String] = Utils.getDataFromFile("bad_words/prod_bad_words.txt")
      .map(_.trim.toLowerCase)

    val badWordsTrie = new BadWordTrie(badWords)

    val results = NewBadWordFilter.applyAndGetBadWords(data.toLowerCase, genreId, badWordsTrie)

    println(s"Words: ${results}")
    //    NewBadWordFilter(data, genreId, badWordsTrie) should equal("1")
    //    NewBadWordFilter.applyAndGetBadWords(data, genreId, badWordsTrie) should equal(Seq("手榴弾"))
  }

  test("apply with bad words that exist should return correctly") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("a", Seq.empty, Seq.empty)
        ), "1"
      ),
      (
        Seq(
          ("a", Seq.empty, Seq.empty),
          ("abc", Seq.empty, Seq.empty)
        ), "1"
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq.empty),
          ("a", Seq.empty, Seq.empty)
        ), "1"
      ),
      (
        Seq(
          ("z", Seq.empty, Seq.empty)
        ), "1"
      ),
      (
        Seq(
          ("z", Seq.empty, Seq.empty),
          ("xyz", Seq.empty, Seq.empty)
        ), "1"
      ),
      (
        Seq(
          ("xyz", Seq.empty, Seq.empty),
          ("z", Seq.empty, Seq.empty)
        ), "1"
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: String) => {
        NewBadWordFilter(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("apply with bad words that do not exist should return correctly") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("123", Seq.empty, Seq.empty)
        ), "0"
      ),
      (
        Seq(
          ("cba", Seq.empty, Seq.empty),
          ("zyx", Seq.empty, Seq.empty)
        ), "0"
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq.empty),
          ("def", Seq.empty, Seq.empty),
          ("fed", Seq.empty, Seq.empty),
          ("cba", Seq.empty, Seq.empty)
        ), "1"
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: String) => {
        NewBadWordFilter(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("apply with bad words with include genres should return bad words only if genre id is in included genres") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("abc", Seq("123456"), Seq.empty)
        ), "1"
      ),
      (
        Seq(
          ("abc", Seq("654321"), Seq.empty)
        ), "0"
      ),
      (
        Seq(
          ("abc", Seq("123456", "654321"), Seq.empty)
        ), "1"
      ),
      (
        Seq(
          ("abc", Seq("654321", "123456"), Seq.empty)
        ), "1"
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: String) => {
        NewBadWordFilter(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("apply with bad words with exclude genres should return bad words only if genre id is not in excluded genres") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("abc", Seq.empty, Seq("123456"))
        ), "0"
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq("654321"))
        ), "1"
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq("123456", "654321"))
        ), "0"
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq("654321", "123456"))
        ), "0"
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: String) => {
        NewBadWordFilter(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("apply with bad words with both include and exclude genres should ignore exclude genres and return bad words only if genre id is in included genres") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("abc", Seq("123456"), Seq("123456"))
        ), "1"
      ),
      (
        Seq(
          ("abc", Seq("123456"), Seq("654321"))
        ), "1"
      ),
      (
        Seq(
          ("abc", Seq("654321"), Seq("654321"))
        ), "0"
      ),
      (
        Seq(
          ("abc", Seq("654321"), Seq("123456"))
        ), "0"
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: String) => {
        NewBadWordFilter(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("applyAndGetBadWords with bad words that exist should return correctly") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("a", Seq.empty, Seq.empty)
        ), Seq("a")
      ),
      (
        Seq(
          ("a", Seq.empty, Seq.empty),
          ("abc", Seq.empty, Seq.empty)
        ), Seq("a", "abc")
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq.empty),
          ("a", Seq.empty, Seq.empty)
        ), Seq("a", "abc")
      ),
      (
        Seq(
          ("z", Seq.empty, Seq.empty)
        ), Seq("z")
      ),
      (
        Seq(
          ("z", Seq.empty, Seq.empty),
          ("xyz", Seq.empty, Seq.empty)
        ), Seq("xyz", "z")
      ),
      (
        Seq(
          ("xyz", Seq.empty, Seq.empty),
          ("z", Seq.empty, Seq.empty)
        ), Seq("xyz", "z")
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: Seq[String]) => {
        NewBadWordFilter.applyAndGetBadWords(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("applyAndGetBadWords with bad words that do not exist should return correctly") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("123", Seq.empty, Seq.empty)
        ), Seq.empty
      ),
      (
        Seq(
          ("cba", Seq.empty, Seq.empty),
          ("zyx", Seq.empty, Seq.empty)
        ), Seq.empty
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq.empty),
          ("def", Seq.empty, Seq.empty),
          ("fed", Seq.empty, Seq.empty),
          ("cba", Seq.empty, Seq.empty)
        ), Seq("abc", "def")
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: Seq[String]) => {
        NewBadWordFilter.applyAndGetBadWords(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("applyAndGetBadWords with bad words with include genres should return bad words only if genre id is in included genres") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("abc", Seq("123456"), Seq.empty)
        ), Seq("abc")
      ),
      (
        Seq(
          ("abc", Seq("654321"), Seq.empty)
        ), Seq.empty
      ),
      (
        Seq(
          ("abc", Seq("123456", "654321"), Seq.empty)
        ), Seq("abc")
      ),
      (
        Seq(
          ("abc", Seq("654321", "123456"), Seq.empty)
        ), Seq("abc")
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: Seq[String]) => {
        NewBadWordFilter.applyAndGetBadWords(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("applyAndGetBadWords with bad words with exclude genres should return bad words only if genre id is not in excluded genres") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("abc", Seq.empty, Seq("123456"))
        ), Seq.empty
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq("654321"))
        ), Seq("abc")
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq("123456", "654321"))
        ), Seq.empty
      ),
      (
        Seq(
          ("abc", Seq.empty, Seq("654321", "123456"))
        ), Seq.empty
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: Seq[String]) => {
        NewBadWordFilter.applyAndGetBadWords(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }

  test("applyAndGetBadWords with bad words with both include and exclude genres should ignore exclude genres and return bad words only if genre id is in included genres") {
    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (
        Seq(
          ("abc", Seq("123456"), Seq("123456"))
        ), Seq("abc")
      ),
      (
        Seq(
          ("abc", Seq("123456"), Seq("654321"))
        ), Seq("abc")
      ),
      (
        Seq(
          ("abc", Seq("654321"), Seq("654321"))
        ), Seq.empty
      ),
      (
        Seq(
          ("abc", Seq("654321"), Seq("123456"))
        ), Seq.empty
      )
    )

    forAll(table) {
      (input: Seq[(String, Seq[String], Seq[String])], expected: Seq[String]) => {
        NewBadWordFilter.applyAndGetBadWords(valueToCheck, genreId, new BadWordTrie(input)) should equal(expected)
      }
    }
  }
}