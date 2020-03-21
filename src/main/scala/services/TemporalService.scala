package services

import database.Mongo.db
import database.Helpers._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Sorts.{orderBy,ascending}
import org.mongodb.scala.model.Filters.and

object TemporalService {
  def findByRestaurantId(restaurant_id: ObjectId): Seq[Document] ={
    val collection:MongoCollection[Document] = db.getCollection("temporal")

//    orderBy(ascending("month"), ascending("year"))
    collection.find(equal("restaurant._id",restaurant_id)).sort(ascending("year", "month")).results()

  }
}
