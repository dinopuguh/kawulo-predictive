package services

import database.Helpers._
import database.Mongo.db
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.Sorts.ascending

import scala.math.pow

object PredictiveService {
  def findOldPredictive(restaurantId: String): Seq[Document] = {
    val collection:MongoCollection[Document] = db.getCollection("prediction")

    collection.find(equal("restaurant_id", restaurantId)).sort(ascending("year", "month")).results()
  }

  def getPredictiveResult(data: Seq[Double], alpha: Double):Double = {
    var result: Double = 0
    val n: Int = data.length

    data.zipWithIndex foreach {case(d, i) =>
      val t = n-1-i

      if(i == 0) {
        result += pow((1-alpha),t) * d
      }else {
        result += alpha * pow((1-alpha),t) * d
      }
    }

    result
  }

  def savePredictionResult(restaurantId: ObjectId, document: Document): Unit = {
    val collection = db.getCollection("prediction")

    val prediction = collection.find(and(equal("restaurant._id", restaurantId), equal("month", document("month")), equal("year", document("year")))).headResult()

    if(prediction != null) {
      collection.replaceOne(equal("_id", prediction("_id").asObjectId().getValue), document).printHeadResult()
    }else {
      collection.insertOne(document).printHeadResult()
    }
  }
}
