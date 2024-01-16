import scala.collection.mutable

class OldBadWordFilter {

  def apply(valueToCheck: String, genreId: String, badWordGenres: mutable.WrappedArray[String]): String = {
    for (badWordGenre <- badWordGenres) {
      val arr = badWordGenre.split(",", -1)
      val badWord = arr(0)

      if (valueToCheck.contains(badWord)) {
        if (arr.length > 1 && arr(1).trim.nonEmpty) {
          val includeGenres = arr(1).split("\\|").map(_.trim)

          if (genreId != null && genreId.trim != "" && includeGenres.contains(genreId.trim)) {
            return "1"
          }
        }
        else if (arr.length > 2 && arr(2).trim.nonEmpty) {
          val excludeGenres = arr(2).split("\\|").map(_.trim)

          if (!excludeGenres.contains(genreId.trim)) {
            return "1"
          }
        }
        else {
          return "1"
        }
      }
    }
    "0"
  }

}