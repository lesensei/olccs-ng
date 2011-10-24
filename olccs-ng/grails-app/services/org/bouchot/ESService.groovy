package org.bouchot

import org.elasticsearch.groovy.node.*
import static org.elasticsearch.groovy.node.GNodeBuilder.*
import org.elasticsearch.groovy.client.GClient
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.search.sort.SortOrder
import static org.elasticsearch.index.query.QueryBuilders.*
import grails.util.Environment
import groovy.json.JsonBuilder
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest
import org.elasticsearch.common.xcontent.XContentFactory

class ESService {
  def grailsApplication
  def client

  def initService() {
    if (!client) {
      if (Environment.current == Environment.PRODUCTION) {
        def esAddress = grailsApplication.config.org.bouchot.elasticsearch.address
        def esPort = grailsApplication.config.org.bouchot.elasticsearch.port?.toInteger()
        client = new GClient(new TransportClient().addTransportAddress(new InetSocketTransportAddress(esAddress, esPort ?: 9300)))
      } else {
        def nb = nodeBuilder()
        nb.settings {
          node {
            local = true
          }
        }
        client = nb.node().client
      }
    }
  }

  def backend(String board, int last = 0, int s = 150) {
    initService()
    def search = client.prepareSearch(board)
    search.with {
      setTypes("post")
      size = s
      from = last
      addSort("id", SortOrder.DESC)
    }
    def res = search.gexecute()

    return res?.response
  }

  def search(String board, String query, int s = 150) {
    initService()
    def search = client.prepareSearch(board)
    search.with {
      setTypes("post")
      size = s
      setQuery(queryString(query).field("message"))
    }
    def res = search.gexecute()

    return res?.response
  }

  def index(Board b) {
    initService()
    //TODO: Make this really work and look less ugly (no groovy client examples online to help me, though)
    if (!client.admin.indices.indicesAdminClient.exists(new IndicesExistsRequest(b.name)).actionGet().exists()) {
      def i = client.admin.indices.indicesAdminClient.prepareCreate(b.name)
          .addMapping("post", XContentFactory.jsonBuilder()
          .startObject()
            .startObject("post")
              .startObject("properties")
                .startObject("board").field("type", "string").field("index", "not_analyzed").endObject()
                .startObject("time").field("type", "date").field("format", "yyyyMMddHHmmss").endObject()
                .startObject("info").field("type", "string").field("index", "not_analyzed").endObject()
                .startObject("login").field("type", "string").field("index", "not_analyzed").endObject()
                .startObject("id").field("type", "long").endObject()
                .startObject("message").field("type", "string").field("analyzer", "default").endObject()
              .endObject()
            .endObject()
          .endObject()).execute().actionGet()
    }
    def remote = new XmlSlurper(new org.ccil.cowan.tagsoup.Parser()).parse(b.getURL)
    def res = remote.'post'.collect { p ->
      def post = new JsonBuilder()
      def out = post.call (
          board: b.name,
          time: p.@time.toString(),
          info: p.info.toString(),
          login: p.login.toString(),
          message: p.message.toString(),
          )
      return client.prepareIndex(b.name, 'post', "${p.@id}")
        .setSource(post.toString())
        .execute()
        .actionGet()
    }
    return res
  }
}
