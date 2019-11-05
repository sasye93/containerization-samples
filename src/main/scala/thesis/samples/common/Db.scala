package thesis.samples.common

import rescala.default._

class Db(dbConString : String, dbName : String){

  import org.mongodb.scala._
  import bson._

  // Use a Connection String
  val client: MongoClient = MongoClient(dbConString)
  val database: MongoDatabase = client.getDatabase(dbName)

  def insertDocument(col : String, doc : Seq[(String, BsonValue)]) : Unit = {
    val insertDoc: Document = bson.Document.fromSeq(doc)
    database.getCollection(col).insertOne(insertDoc).subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("Message inserted into database");
      override def onError(e: Throwable): Unit = println(s"DB Failure while trying to insert/read message: $e");
      override def onComplete(): Unit = println("DB action successful.")
    })
  }
  def insertDocumentObserved(col : String, doc : Seq[(String, BsonValue)], onComplete : Observer[Completed]) : Unit = {
    val insertDoc: Document = bson.Document.fromSeq(doc)
    database.getCollection(col).insertOne(insertDoc).subscribe(onComplete)
  }
  def getDocuments(col : String, onComplete : Evt[String]) : Unit = getDocuments(col, null, onComplete)
  def getDocuments(col : String, doc : org.mongodb.scala.bson.conversions.Bson, onComplete : Evt[String]) : Unit = database.getCollection(col).find(doc).subscribe(
    (doc: Document) => onComplete.fire(doc.get(col).get.asString().getValue),
    (e: Throwable) => println(s"DB Failure while trying to insert/read message: $e")
  )
}