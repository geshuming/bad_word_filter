import scala.annotation.tailrec

object NewBadWordFilter {
  /**
   * @param valueToCheck  the string to check for any bad words
   * @param genreId       the bottom level genre id belonging to the item
   * @param badWordGenres the Trie containing bad words.
   * @return "1" if bad word is found, "0" otherwise
   */
  def apply(valueToCheck: String, genreId: String, badWordGenres: BadWordTrie): String = {
    var isFound = false

    /**
     * A tail recursive function that recursively searches the Trie for bad Words. Terminates early once found.
     */
    @tailrec
    def checkNext(trie: BadWordTrie, index: Int): Boolean = {
      if (index >= valueToCheck.length) return false

      trie.child(valueToCheck(index)) match {
        case Some(nextTrie) => (
          if (nextTrie.isTerminal) {
            if (nextTrie.includeGenres.nonEmpty) {
              if (nextTrie.includeGenres.contains(genreId)) return true else return false
            }

            if (nextTrie.excludeGenres.nonEmpty) {
              if (nextTrie.excludeGenres.contains(genreId)) return false
            }

            true
          } else false) || checkNext(nextTrie, index + 1)
        case None => false
      }
    }

    for (i <- 0 until valueToCheck.length) {
      isFound = isFound || checkNext(badWordGenres, i)
    }

    if (isFound) "1" else "0"
  }

  /**
   *
   * @param valueToCheck  the string to check for any bad words
   * @param genreId       the bottom level genre id belonging to the item
   * @param badWordGenres the Trie containing bad words.
   * @return the list of bad words
   */
  def applyAndGetBadWords(valueToCheck: String, genreId: String, badWordGenres: BadWordTrie): Seq[String] = {
    var foundBadWords: Seq[String] = Seq.empty

    /**
     * A recursive function that recursively searches the Trie for bad Words.
     */
    def checkNext(nextTrie: BadWordTrie, index: Int, prefix: String): Seq[String] = {
      if (index >= valueToCheck.length) return Seq.empty

      nextTrie
        .child(valueToCheck(index))
        .fold(Seq.empty[String]) {
          next =>
            (if (next.isTerminal) {
              if (next.includeGenres.nonEmpty) {
                if (next.includeGenres.contains(genreId)) {
                  return Seq(prefix + valueToCheck(index))
                }
                return Seq.empty
              }

              if (next.excludeGenres.nonEmpty) {
                if (next.excludeGenres.contains(genreId)) {
                  return Seq.empty
                }
              }

              Seq(prefix + valueToCheck(index))
            } else Seq.empty) ++
              checkNext(next, index + 1, prefix + valueToCheck(index))
        }
    }

    for (i <- 0 until valueToCheck.length) {
      foundBadWords = foundBadWords ++ checkNext(badWordGenres, i, "")
    }

    foundBadWords
  }
}