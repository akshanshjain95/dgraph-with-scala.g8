package com.knoldus.sample

import com.knoldus.sample.dgraph.impl.DgraphCrudImpl
import com.knoldus.sample.dgraph.schema.SchemaSetUp
import com.knoldus.sample.utilities.UtilityObjects
import com.typesafe.config.ConfigFactory

case class Person(uid: String, name: String, id: String, address: Array[Address])
case class Address(uid: String, line1: String, zip: String, city: String, state: String, country: String)

object Boot extends App {

  println("Welcome!")

  //Drop all the data currently in DGraph.
  SchemaSetUp.dropAllData()

  //Load schema from the configuration.
  val schemaConfig = ConfigFactory.load("schema")
  val schema = schemaConfig.getString("schema")

  //Initialize the schema.
  SchemaSetUp.setSchema(schema)

  val dgraphCrud = new DgraphCrudImpl

  //Creating a person object to be mutated in DGraph.
  val address = Address(null, "line1", "201301", "Noida", "UP", "India")
  val person = Person(null, "Angela", "id-1", Array(address))
  val person1 = Person(null, "David", "id-2", Array(address))
  val person2 = Person(null, "Ed", "id-3", Array(address))

  val personList = Array(person, person1, person2)

  //Mutating the person object in DGraph.
  dgraphCrud.upsertNodes(personList)

  //Loading query from configuration.
  val queryConfig = ConfigFactory.load("query")
  val query = queryConfig.getString("query.getPersonById")
  val queryName = "getPersonById"

  //Variable map to assign the query variable a value.
  val variableMap = Map("$id" -> "id-1")

  //Reading data from DGraph. As the data returned by DGraph is always in a list, the response is parsed in a list as
  //well.
  val personResponseList: List[Person] =
    UtilityObjects.GSON.fromJson(dgraphCrud.readNodes(query, queryName, variableMap), classOf[Array[Person]]).toList

  println("Response from DGraph: " + personResponseList)

  //Since we know, that for id-1, there is only one person present in the data, we are going to extract him and then
  //delete him. >:)
  personResponseList.headOption
    .fold(throw new RuntimeException("The person should have been in the database. I don't know what happened. D:"))(
      person => {

        val personToDelete = Person(person.uid, null, null, null)
        dgraphCrud.deleteNodes(personToDelete)
    })

  //Reading the data for id: id-1 again to confirm that it has been deleted.
  val deletedPersonResponseList =
    UtilityObjects.GSON.fromJson(dgraphCrud.readNodes(query, queryName, variableMap), classOf[Array[Person]]).toList

  if (deletedPersonResponseList.isEmpty)
    println("Person with id: id-1 successfully deleted.")
  else
    println("Not deleted!!")

}
