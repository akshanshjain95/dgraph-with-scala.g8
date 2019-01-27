package com.knoldus.sample.dgraph.impl

import com.google.protobuf.ByteString
import com.knoldus.sample.dgraph.api.DgraphCrud
import com.knoldus.sample.dgraph.connection.DgraphConnectionHandler
import com.knoldus.sample.utilities.UtilityObjects
import com.typesafe.scalalogging.{LazyLogging, Logger}
import io.dgraph.DgraphProto.Mutation

import scala.collection.JavaConverters._

class DgraphCrudImpl extends DgraphCrud with LazyLogging {

  private val LOGGER = Logger("DgraphCrudImpl")
  private val dgraphClient = DgraphConnectionHandler.getClient

  override def upsertNodes[T](element: T): List[String] = {
    LOGGER.info("Received request for mutation for the element: {}", element)
    val mutation = getMutation(element)

    LOGGER.info("Performing mutation with the mutation object: {}", mutation)

    //Creating a transaction to perform mutation in DGraph.
    val transaction = dgraphClient.newTransaction()
    val assignedResponse = transaction.mutate(mutation)

    LOGGER.info("Got response for mutation by creating data with latency parameters: {}",
      assignedResponse.getLatency.toString)

    //Committing the created transaction.
    transaction.commit()

    //Returns all the uids of the new nodes created in DGraph. Converting them to a scala list.`
    assignedResponse.getUidsMap.values().asScala.toList
  }

  override def readNodes(query: String, queryName: String, variableMap: Map[String, String]): String = {
    LOGGER.info("Received request to read data with query: {}, queryName: {} and variableMap: {}",
      query, queryName, variableMap)

    //Reading the data from dgraph using the query and the variable map. Variable Map contains the variables that
    //needs to be replaced in the provided query.
    val response = dgraphClient.newReadOnlyTransaction().queryWithVars(query, variableMap.asJava)

    val latency = response.getLatency
    LOGGER.info("Got response for query with queryName: {} and query parameters: {} with latency "
      + "parameters: {}", queryName, variableMap, latency.toString)

    //Parsing the response from DGraph. The response is wrapped in the name of the query, so the response is being
    //extracted out from that and the final result is being returned as a string.
    UtilityObjects.JSON_PARSER.parse(response.getJson.toStringUtf8).getAsJsonObject.get(queryName).toString
  }

  override def deleteNodes[T](element: T): List[String] = {
    LOGGER.info("Received request to delete data corresponding to element: {}", element)
    val deleteMutation = getDeleteMutation(element)

    LOGGER.info("Performing delete mutation with the delete mutation object: {}", deleteMutation)

    //Creating a transaction to perform mutation in DGraph.
    val transaction = dgraphClient.newTransaction()
    val assignedResponse = transaction.mutate(deleteMutation)

    LOGGER.info("Got response for mutation by deleting data with latency parameters: {}",
      assignedResponse.getLatency.toString)

    //Committing the created transaction.
    transaction.commit()

    //Returns all the uids of the new nodes created in DGraph. Converting them to a scala list.`
    assignedResponse.getUidsMap.values().asScala.toList
  }

  /**
    * Converts the passed element to JSON and builds a mutation object.
    *
    * @param element element to be converted to JSON.
    * @tparam T Type of the element.
    * @return Mutation object.
    */
  private def getMutation[T](element: T): Mutation = {
    LOGGER.info("Converting element: {} to JSON", element)

    //Converting the passed element to JSON
    val jsonString = UtilityObjects.GSON.toJson(element)

    //Creating a mutation object which is needed by DGraph to perform mutation.
    Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(jsonString)).build()
  }

  /**
    * Converts the passed element to JSON and builds a mutation object for deleting the data.
    *
    * @param element element to be converted to JSON.
    * @tparam T Type of the element.
    * @return Mutation object for deleting data.
    */
  private def getDeleteMutation[T](element: T): Mutation = {
    LOGGER.info("Converting element: {} to JSON", element)

    //Converting the passed element to JSON
    val jsonString = UtilityObjects.GSON.toJson(element)

    //Creating a mutation object which is needed by DGraph to perform mutation.
    Mutation.newBuilder().setDeleteJson(ByteString.copyFromUtf8(jsonString)).build()
  }
}
