package services

import database.Mongo._
import database.Helpers._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document

object RestaurantService {
  def findAll(): Seq[Document] ={
    val collection:MongoCollection[Document] = db.getCollection("restaurant")

    collection.find().results()
  }

  def findByLocationId(location_id: ObjectId): Seq[Document] ={
    val collection:MongoCollection[Document] = db.getCollection("restaurant")

    collection.find(equal("location.$id",location_id)).results()
  }
}
