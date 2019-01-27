package com.knoldus.sample.dgraph.api

trait DgraphCrud {

  /**
    * Creates or updates the node corresponding to element T in DGraph. It creates multiple nodes depending on the
    * provided element. If it has object type parameters, then edges will be created with the object parameters on the
    * new node.
    *
    * For ex. If the element is like
    *
    * case class Person(uid: String, name: String, address: Address)
    * case class Address(uid: String, line1: String, zip: String, city: String, state: String, country: String)
    *
    * Then, one node will be created for person, with name as a predicate and an edge, address, will be created
    * pointing to a node with the predicates as defined in the Address case class(line1, zip, etc.). So, in this case,
    * two nodes will be created.
    *
    * If the method is provided with an element that already has uid value assigned to it, then instead of creating a
    * new node, it will simply update the node present in DGraph with that uid value.
    *
    * @param element The element to be mutated in DGraph.
    * @tparam T The type of the element.
    * @return List of all the uids of the NEWLY created nodes.
    */
  def upsertNodes[T](element: T): List[String]

  /**
    * Reads data from DGraph. Uses the provided query to get the data. The provided query is parameterized and the
    * values of those parameters are passed in a map, which is the variableMap. The keys of the map represent the
    * placeholder in the query and the values represent their corresponding values. The name of the query is the name
    * given to the root function of the query.
    *
    * For ex. If the query is as below -
    * query sampleQuery($id: string) {
    * thisIsASampleQuery(func: uid($id)) {
    * expand(_all_)
    * }
    * }
    *
    * Then, the name of the query is `thisIsASampleQuery`. The variables in the query is `$id`. The variable map would
    * look something like -
    *
    * Map("$id" -> "0x123")
    *
    * And the query parameter would be the whole query as written above.
    *
    * @param query       The query to be used to retrieve the data from DGraph.
    * @param queryName   The name of the provided query.
    * @param variableMap The map that contains the query variables and their values.
    * @return The response from DGraph, extracted from the query name, as a String.
    */
  def readNodes(query: String, queryName: String, variableMap: Map[String, String]): String

  /**
    * Deletes the given element from the database. If only given a uid of a node, then it deletes that node and every
    * predicate on that node. If a predicate is also given with the uid, then only that predicate is deleted from that
    * node. Providing a uid is necessary for deletion to happen, else a `StatusRuntimeException` is thrown. If you want
    * to delete all the nodes associated with the node that is to be deleted, then you will have to provide the value
    * for each of the predicate of the root node, along with the uids of the associated nodes.
    *
    * For ex. If you want to delete only a person -
    *
    * Person("0x123", null, null, null) - will delete that person, but NOT the address node associated with that person.
    *
    * Person("0x123", null, null, address)
    * Address("0x234", null, null, null..) - will delete ONLY the address node and NOT the person node.
    *
    * Person("0x123", "Name", "ID", address)
    * Address("0x234", [all other values are null]) - will delete the person AND the address node.
    *
    * @param element element that needs to be deleted.
    * @tparam T type of the element.
    * @return a list of all the uids that were created, although in this case no uids will be created, so will always
    *         be an empty list.
    */
  def deleteNodes[T](element: T): List[String]
}
