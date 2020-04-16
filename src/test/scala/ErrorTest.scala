import org.mongodb.scala.bson.collection.immutable.Document
import services.LocationService.findIndonesianLocations
import services.MAPEService.calculateMAPE
import services.PredictiveService.findOldPredictive
import services.PredictionErrorService.savePredictionError
import services.RestaurantService.findByLocationId
import services.TemporalService.findByRestaurantMonthYear

object ErrorTest extends App{
  val indonesiaLocations = findIndonesianLocations()

  indonesiaLocations.foreach {location =>
    println(location("name").asString().getValue)

    val restaurants = findByLocationId(location("_id").asObjectId().getValue)

    restaurants.foreach {restaurant =>
      var predictions = findOldPredictive(restaurant("location_id").asString().getValue)

      if (predictions.nonEmpty) {
        predictions = predictions.init
      }

      var actualServices = Seq[Double]()
      var actualValues = Seq[Double]()
      var actualFoods = Seq[Double]()
      var actualVaders = Seq[Double]()
      var actualWordnets = Seq[Double]()

      var predictionServices = Seq[Double]()
      var predictionValues = Seq[Double]()
      var predictionFoods = Seq[Double]()
      var predictionVaders = Seq[Double]()
      var predictionWordnets = Seq[Double]()

      predictions.foreach {prediction =>
        println(s"[${restaurant("location_id").asString().getValue}] - (${prediction("month").asInt32().getValue}/${prediction("year").asInt32().getValue})")

        val actual =  findByRestaurantMonthYear(prediction("restaurant_id").asString().getValue, prediction("month").asInt32().getValue, prediction("year").asInt32().getValue)

        actualServices = actualServices :+ actual("service").asDouble().getValue
        actualValues = actualValues :+ actual("value").asDouble().getValue
        actualFoods = actualFoods :+ actual("food").asDouble().getValue
        actualVaders = actualVaders :+ actual("vader").asDouble().getValue
        actualWordnets = actualWordnets :+ actual("wordnet").asDouble().getValue

        predictionServices = predictionServices :+ prediction("service").asDouble().getValue
        predictionValues = predictionValues :+ prediction("value").asDouble().getValue
        predictionFoods = predictionFoods :+ prediction("food").asDouble().getValue
        predictionVaders = predictionVaders :+ prediction("vader").asDouble().getValue
        predictionWordnets = predictionWordnets :+ prediction("wordnet").asDouble().getValue
      }

      val serviceError = calculateMAPE(predictionServices, actualServices)
      val valueError = calculateMAPE(predictionValues, actualValues)
      val foodError = calculateMAPE(predictionFoods, actualFoods)
      val vaderError = calculateMAPE(predictionVaders, actualVaders)
      val wordnetError = calculateMAPE(predictionWordnets, actualWordnets)
      val overallError = (serviceError + valueError + foodError + vaderError + wordnetError) / 5

      val document = Document(
        "restaurant_id" -> restaurant("location_id").asString().getValue,
        "service_error" -> serviceError,
        "value_error" -> valueError,
        "food_error" -> foodError,
        "vader_error" -> vaderError,
        "wordnet_error" -> wordnetError,
        "overall_error" -> overallError
      )

      savePredictionError(document)
    }
  }
}
