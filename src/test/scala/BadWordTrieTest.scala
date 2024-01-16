import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class BadWordTrieTest extends FunSuite with Matchers with TableDrivenPropertyChecks {
  test("constructor with sequence of elements should insert correctly") {
    val elements = Seq(("abc", Seq("123456"), Seq("654321")), ("ade", Seq("234567", "345678"), Seq("765432", "876543")))
    val trie = new BadWordTrie(elements)

    val trie_a = trie.child('a')
    trie_a.nonEmpty should equal(true)
    trie_a.get.isTerminal should equal(false)
    trie_a.get.includeGenres should equal(Seq.empty)
    trie_a.get.excludeGenres should equal(Seq.empty)

    val trie_ab = trie_a.get.child('b')
    trie_ab.nonEmpty should equal(true)
    trie_ab.get.isTerminal should equal(false)
    trie_ab.get.includeGenres should equal(Seq.empty)
    trie_ab.get.excludeGenres should equal(Seq.empty)

    val trie_abc = trie_ab.get.child('c')
    trie_abc.nonEmpty should equal(true)
    trie_abc.get.isTerminal should equal(true)
    trie_abc.get.includeGenres should equal(Seq("123456"))
    trie_abc.get.excludeGenres should equal(Seq("654321"))

    val trie_b = trie.child('b')
    trie_b.isEmpty should equal(true)

    val trie_c = trie.child('c')
    trie_c.isEmpty should equal(true)

    val trie_ad = trie_a.get.child('d')
    trie_ad.nonEmpty should equal(true)
    trie_ad.get.isTerminal should equal(false)
    trie_ad.get.includeGenres should equal(Seq.empty)
    trie_ad.get.excludeGenres should equal(Seq.empty)

    val trie_ade = trie_ad.get.child('e')
    trie_ade.nonEmpty should equal(true)
    trie_ade.get.isTerminal should equal(true)
    trie_ade.get.includeGenres should equal(Seq("234567", "345678"))
    trie_ade.get.excludeGenres should equal(Seq("765432", "876543"))
  }

  test("constructor with sequence of delimited strings should insert correctly") {
    val elements = List("abc,123456,654321", "ade,234567|345678,765432|876543")
    val trie = new BadWordTrie(elements)

    val trie_a = trie.child('a')
    trie_a.nonEmpty should equal(true)
    trie_a.get.isTerminal should equal(false)
    trie_a.get.includeGenres should equal(Seq.empty)
    trie_a.get.excludeGenres should equal(Seq.empty)

    val trie_ab = trie_a.get.child('b')
    trie_ab.nonEmpty should equal(true)
    trie_ab.get.isTerminal should equal(false)
    trie_ab.get.includeGenres should equal(Seq.empty)
    trie_ab.get.excludeGenres should equal(Seq.empty)

    val trie_abc = trie_ab.get.child('c')
    trie_abc.nonEmpty should equal(true)
    trie_abc.get.isTerminal should equal(true)
    trie_abc.get.includeGenres should equal(Seq("123456"))
    trie_abc.get.excludeGenres should equal(Seq("654321"))

    val trie_b = trie.child('b')
    trie_b.isEmpty should equal(true)

    val trie_c = trie.child('c')
    trie_c.isEmpty should equal(true)

    val trie_ad = trie_a.get.child('d')
    trie_ad.nonEmpty should equal(true)
    trie_ad.get.isTerminal should equal(false)
    trie_ad.get.includeGenres should equal(Seq.empty)
    trie_ad.get.excludeGenres should equal(Seq.empty)

    val trie_ade = trie_ad.get.child('e')
    trie_ade.nonEmpty should equal(true)
    trie_ade.get.isTerminal should equal(true)
    trie_ade.get.includeGenres should equal(Seq("234567", "345678"))
    trie_ade.get.excludeGenres should equal(Seq("765432", "876543"))
  }

  test("constructor with sequence of delimited strings and without genre ids should insert correctly") {
    val elements = List("abc", "ade,", "afg,,", "ahi,123456", "ajk,,654321", "alm,123456,654321")
    val trie = new BadWordTrie(elements)

    val trie_abc = trie.child('a').get.child('b').get.child('c').get
    trie_abc.isTerminal should equal(true)
    trie_abc.includeGenres should equal(Seq.empty)
    trie_abc.excludeGenres should equal(Seq.empty)

    val trie_ade = trie.child('a').get.child('d').get.child('e').get
    trie_ade.isTerminal should equal(true)
    trie_ade.includeGenres should equal(Seq.empty)
    trie_ade.excludeGenres should equal(Seq.empty)

    val trie_afg = trie.child('a').get.child('f').get.child('g').get
    trie_afg.isTerminal should equal(true)
    trie_afg.includeGenres should equal(Seq.empty)
    trie_afg.excludeGenres should equal(Seq.empty)

    val trie_ahi = trie.child('a').get.child('h').get.child('i').get
    trie_ahi.isTerminal should equal(true)
    trie_ahi.includeGenres should equal(Seq("123456"))
    trie_ahi.excludeGenres should equal(Seq.empty)

    val trie_ajk = trie.child('a').get.child('j').get.child('k').get
    trie_ajk.isTerminal should equal(true)
    trie_ajk.includeGenres should equal(Seq.empty)
    trie_ajk.excludeGenres should equal(Seq("654321"))

    val trie_alm = trie.child('a').get.child('l').get.child('m').get
    trie_alm.isTerminal should equal(true)
    trie_alm.includeGenres should equal(Seq("123456"))
    trie_alm.excludeGenres should equal(Seq("654321"))
  }

  test("constructor with sequence of malformed delimited strings should try inserting without errors") {
    val elements = List("abc,,,123456", "ade,", "afg,,", "ahi,123456", "ajk,,654321", "alm,123456,654321")

    val trie_1 = new BadWordTrie(List("abc,,,123456"))
    val trie_1_abc = trie_1.child('a').get.child('b').get.child('c').get
    trie_1_abc.isTerminal should equal(true)
    trie_1_abc.includeGenres should equal(Seq.empty)
    trie_1_abc.excludeGenres should equal(Seq(",123456"))

    val trie_2 = new BadWordTrie(List("abc,,|123456|"))
    val trie_2_abc = trie_2.child('a').get.child('b').get.child('c').get
    trie_2_abc.isTerminal should equal(true)
    trie_2_abc.includeGenres should equal(Seq.empty)
    trie_2_abc.excludeGenres should equal(Seq("123456"))

    val trie_3 = new BadWordTrie(List("abc,|123456|,,,"))
    val trie_3_abc = trie_3.child('a').get.child('b').get.child('c').get
    trie_3_abc.isTerminal should equal(true)
    trie_3_abc.includeGenres should equal(Seq("123456"))
    trie_3_abc.excludeGenres should equal(Seq(",,"))

    val trie_4 = new BadWordTrie(List("abc,|123456|,"))
    val trie_4_abc = trie_4.child('a').get.child('b').get.child('c').get
    trie_4_abc.isTerminal should equal(true)
    trie_4_abc.includeGenres should equal(Seq("123456"))
    trie_4_abc.excludeGenres should equal(Seq.empty)
  }
}