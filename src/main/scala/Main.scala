import org.mongodb.scala.bson.collection.immutable.Document
import services.LocationService.findIndonesianLocations
import services.RestaurantService.findByLocationId
import services.TemporalService.findByRestaurantId
import services.PredictiveService.{getPredictiveResult, savePredictionResult}

object Main extends App {
  val indonesiaLocations = findIndonesianLocations()

  indonesiaLocations.foreach {location =>
    println(location("name").asString().getValue)

    val restaurants = findByLocationId(location("_id").asObjectId().getValue)

    restaurants.foreach {restaurant =>
      println(restaurant("name").asString().getValue)

      val temporal = findByRestaurantId(restaurant("_id").asObjectId().getValue)

      var months = Seq[Int]()
      var years = Seq[Int]()
      var services = Seq[Double]()
      var values = Seq[Double]()
      var foods = Seq[Double]()
      var vaders = Seq[Double]()
      var wordnets = Seq[Double]()

      temporal.foreach {t =>
        val month = t("month").asInt32().getValue
        val year = t("year").asInt32().getValue
        val service = t("service").asDouble().getValue
        val value = t("value").asDouble().getValue
        val food = t("food").asDouble().getValue
        val vader = t("vader").asDouble().getValue
        val wordnet = t("wordnet").asDouble().getValue

        months = months :+ month
        years = years :+ year
        services = services :+ service
        values = values :+ value
        foods = foods :+ food
        vaders = vaders :+ vader
        wordnets = wordnets :+ wordnet

        println(s"service (${month}/${year}) => ${service}")
      }

      if(temporal.nonEmpty) {
        var nextMonth = months.last + 1
        var nextYear = years.last
        if(months.last == 12) {
          nextMonth = 1
          nextYear = years.last
        }

        val servicePrediction = getPredictiveResult(services, 0.8)
        val valuePrediction = getPredictiveResult(values, 0.8)
        val foodPrediction = getPredictiveResult(foods, 0.8)
        val vaderPrediction = getPredictiveResult(vaders, 0.8)
        val wordnetPrediction = getPredictiveResult(wordnets, 0.8)

        val document = Document(
          "location_id" -> location("location_id").asString().getValue,
          "location" -> location,
          "restaurant_id" -> restaurant("location_id").asString().getValue,
          "restaurant" -> restaurant,
          "month" -> nextMonth,
          "year" -> nextYear,
          "service" -> servicePrediction,
          "value" -> valuePrediction,
          "food" -> foodPrediction,
          "vader" -> vaderPrediction,
          "wordnet" -> wordnetPrediction
        )

        savePredictionResult(restaurant("_id").asObjectId().getValue, document)
      }
    }
  }
}
