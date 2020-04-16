package services

import database.Helpers._
import database.Mongo.db
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.equal

object PredictionErrorService {
  def savePredictionError(document: Document): Unit = {
    val collection = db.getCollection("prediction-error")

    val predictionError = collection.find(equal("restaurant_id", document("restaurant_id").asString().getValue)).headResult()

    if(predictionError != null) {
      collection.replaceOne(equal("_id", predictionError("_id").asObjectId().getValue), document).printHeadResult()
    }else {
      collection.insertOne(document).printHeadResult()
    }
  }
}
