import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

import scala.collection.mutable

class OldBadWordFilterTest extends FunSuite with Matchers with TableDrivenPropertyChecks {
  ignore("prod test") {
    val oldBadWordFilter = new OldBadWordFilter

    val data =
      """
        <your data here>
      """

    val genreId: String = "123456"
    val badWords: mutable.WrappedArray[String] = Utils.getDataFromFile("bad_words/prod_bad_words.txt")

    oldBadWordFilter.apply(data, genreId, badWords) should equal("1")
  }

  test("apply with bad words that exist should return correctly") {
    val oldBadWordFilter = new OldBadWordFilter

    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (mutable.WrappedArray.make[String](Array("a")), "1"),
      (mutable.WrappedArray.make[String](Array("a", "abc")), "1"),
      (mutable.WrappedArray.make[String](Array("abc", "a")), "1"),
      (mutable.WrappedArray.make[String](Array("z")), "1"),
      (mutable.WrappedArray.make[String](Array("z", "xyz")), "1"),
      (mutable.WrappedArray.make[String](Array("xyz", "z")), "1")
    )

    forAll(table) {
      (input: mutable.WrappedArray[String], expected: String) => {
        oldBadWordFilter.apply(valueToCheck, genreId, input) should equal(expected)
      }
    }
  }

  test("apply with bad words that do not exist should return correctly") {
    val oldBadWordFilter = new OldBadWordFilter

    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (mutable.WrappedArray.make[String](Array("123")), "0"),
      (mutable.WrappedArray.make[String](Array("cba", "zyx")), "0"),
      (mutable.WrappedArray.make[String](Array("abc", "def", "fed", "cba")), "1")
    )

    forAll(table) {
      (input: mutable.WrappedArray[String], expected: String) => {
        oldBadWordFilter.apply(valueToCheck, genreId, input) should equal(expected)
      }
    }
  }

  test("apply with bad words with include genres should return bad words only if genre id is in included genres") {
    val oldBadWordFilter = new OldBadWordFilter

    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (mutable.WrappedArray.make[String](Array("abc,123456")), "1"),
      (mutable.WrappedArray.make[String](Array("abc,654321")), "0"),
      (mutable.WrappedArray.make[String](Array("abc,123456|654321")), "1"),
      (mutable.WrappedArray.make[String](Array("abc,654321|123456")), "1")
    )

    forAll(table) {
      (input: mutable.WrappedArray[String], expected: String) => {
        oldBadWordFilter.apply(valueToCheck, genreId, input) should equal(expected)
      }
    }
  }

  test("apply with bad words with exclude genres should return bad words only if genre id is not in excluded genres") {
    val oldBadWordFilter = new OldBadWordFilter

    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (mutable.WrappedArray.make[String](Array("abc,,123456")), "0"),
      (mutable.WrappedArray.make[String](Array("abc,,654321")), "1"),
      (mutable.WrappedArray.make[String](Array("abc,,123456|654321")), "0"),
      (mutable.WrappedArray.make[String](Array("abc,,654321|123456")), "0")
    )

    forAll(table) {
      (input: mutable.WrappedArray[String], expected: String) => {
        oldBadWordFilter.apply(valueToCheck, genreId, input) should equal(expected)
      }
    }
  }

  test("apply with bad words with both include and exclude genres should ignore exclude genres and return bad words only if genre id is in included genres") {
    val oldBadWordFilter = new OldBadWordFilter

    val valueToCheck = "abcdefghijklmnopqrstuvwxyz"
    val genreId = "123456"

    val table = Table(
      ("input", "expected"),
      (mutable.WrappedArray.make[String](Array("abc,123456,123456")), "1"),
      (mutable.WrappedArray.make[String](Array("abc,123456,654321")), "1"),
      (mutable.WrappedArray.make[String](Array("abc,654321,654321")), "0"),
      (mutable.WrappedArray.make[String](Array("abc,654321,123456")), "0")
    )

    forAll(table) {
      (input: mutable.WrappedArray[String], expected: String) => {
        oldBadWordFilter.apply(valueToCheck, genreId, input) should equal(expected)
      }
    }
  }
}