class BadWordTrie extends Serializable {
  val children = collection.mutable.Map.empty[Char, BadWordTrie]
  var isTerminal = false
  var includeGenres: Seq[String] = Seq.empty
  var excludeGenres: Seq[String] = Seq.empty

  def this(elements: scala.collection.Iterable[(String, Seq[String], Seq[String])]) {
    this()
    elements.foreach(element => this.insert(element._1, element._2, element._3))
  }

  /**
   * Constructs the BadWordTrie with a sequence of strings in one of the given formats:
   * <ul>
   * <li>&lt;badWord&gt; (lineDelimiter) (lineDelimiter) &lt;genre_id&gt; [ (genreDelimiter) &lt;genre_id&gt; ]
   * <ul><li>Example: "bad_word&#44;&#44;111111|222222" represents an bad word "bad_word" with the exclude genres 111111 and 222222</li></ul>
   * </li>
   * <li>&lt;badWord&gt; (lineDelimiter) &lt;genre_id&gt; [ (genreDelimiter) &lt;genre_id&gt; ]
   * <ul><li>Example: "bad_word&#44;111111|222222" represents an bad word "bad_word" with the include genres 111111 and 222222</li></ul>
   * </li>
   * <li>&lt;badWord&gt;
   * <ul><li>Example: "bad_word" represents an bad word "bad_word" that includes all genres</li></ul>
   * </li>
   * </ul>
   *
   * @param elements       a sequence of strings in the above given format
   * @param lineDelimiter  the delimiter that separates the bad word, include genre string and exclude genre string
   * @param genreDelimiter the delimiter that separates the genre ids inside the include genre string or exclude genre string
   */
  def this(elements: scala.collection.Iterable[String], lineDelimiter: String = ",", genreDelimiter: String = "\\|") {
    this(elements.map(line => {
      line.split(lineDelimiter, 3).map(_.trim) match {
        case Array(badWord, includeGenreString, excludeGenreString) =>
          (badWord,
            includeGenreString.split(genreDelimiter).filter(_.nonEmpty).map(_.trim).toSeq,
            excludeGenreString.split(genreDelimiter).filter(_.nonEmpty).map(_.trim).toSeq)
        case Array(badWord, includeGenreString) =>
          (badWord, includeGenreString.split(genreDelimiter).filter(_.nonEmpty).map(_.trim).toSeq, Seq.empty)
        case Array(badWord) =>
          (badWord, Seq.empty, Seq.empty)
      }
    }))
  }

  private def insert(word: String, includeGenres: Seq[String], excludeGenres: Seq[String]): Unit = {
    val terminalTrie = word.foldLeft(this) { case (t, c) => t.children.getOrElseUpdate(c, new BadWordTrie()) }

    terminalTrie.isTerminal = true
    terminalTrie.includeGenres = includeGenres
    terminalTrie.excludeGenres = excludeGenres
  }

  def child(prefix: Char): Option[BadWordTrie] = if (children.contains(prefix)) Some(children(prefix)) else None
}
