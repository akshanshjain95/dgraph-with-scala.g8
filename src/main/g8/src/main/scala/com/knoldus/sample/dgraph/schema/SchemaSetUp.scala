package com.knoldus.sample.dgraph.schema

import com.knoldus.sample.dgraph.connection.DgraphConnectionHandler
import io.dgraph.DgraphProto

object SchemaSetUp {

  private val DGRAPH_CLIENT = DgraphConnectionHandler.getClient

  /**
    * Sets the schema of DGraph using the provided schema in a string format.
    *
    * @param schema Schema to be set for DGraph.
    */
  def setSchema(schema: String): Unit =
    DGRAPH_CLIENT.alter(DgraphProto.Operation.newBuilder.setSchema(schema).build)

  /**
    * Drops all of the data and the schema currently present in DGraph.
    */
  def dropAllData(): Unit = DGRAPH_CLIENT.alter(DgraphProto.Operation.newBuilder.setDropAll(true).build)
}
