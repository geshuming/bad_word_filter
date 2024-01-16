import java.nio.charset.StandardCharsets
import scala.collection.mutable
import scala.io.{BufferedSource, Source}

object Utils {
  def getDataFromFile(path: String): mutable.WrappedArray[String] = {
    val file: BufferedSource = Source.fromFile(this.getClass.getResource(path).getPath, "UTF-8")
    val data: mutable.WrappedArray[String] = mutable.WrappedArray.make[String](file.getLines.toArray)
    file.close()

    data
  }
}
