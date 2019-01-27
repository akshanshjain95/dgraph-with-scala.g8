package com.knoldus.sample.dgraph.connection

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import io.dgraph.{DgraphClient, DgraphGrpc}
import io.grpc.ManagedChannelBuilder

/**
  * DgraphConnectionHandler is an object that creates the connection with Dgraph.
  */
object DgraphConnectionHandler {

  private val LOGGER  = Logger("DgraphConnectionHandler")

  val getClient: DgraphClient = {
    LOGGER.info("Creating Connection to DGraph.")

    //Loading configuration to get the host and port to connect to access dgraph.
    val config = ConfigFactory.load()
    val host = config.getString("dgraph.host")
    val port = config.getInt("dgraph.port")

    //Creating DGraph client using the extracted host and port.
    new DgraphClient(DgraphGrpc.newStub(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()))
  }
}
