package services

import database.Helpers._
import database.Mongo.db
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.{equal, and}
import org.mongodb.scala.model.Sorts.ascending

object TemporalService {
  def findByRestaurantId(restaurant_id: ObjectId): Seq[Document] ={
    val collection:MongoCollection[Document] = db.getCollection("temporal")

    collection.find(equal("restaurant._id",restaurant_id)).sort(ascending("year", "month")).results()

  }

  def findByRestaurantMonthYear(restaurantId: String, month: Int, year: Int): Document ={
    val collection:MongoCollection[Document] = db.getCollection("temporal")

    collection.find(and(equal("restaurant_id", restaurantId), equal("month", month), equal("year", year))).limit(1).headResult()

  }
}
