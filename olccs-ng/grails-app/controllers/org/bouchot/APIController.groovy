package org.bouchot

import grails.converters.*

class APIController {
  def ESService
  
  def xml = {
    def hits = ESService.backend(params.board)?.hits
    render(contentType: "text/xml") {
      board(site: params.board) {
        for (h in hits) {
          post(time: h.source.time, id: h.id) {
            info(h.source.info)
            message(h.source.message)
            login(h.source.login)
          }
        }
      }
    }
  }
  
  def json = {
    def res = ESService.backend(params.board)
    render res
  }
  
  def search = {
    def hits = ESService.search(params.board, params.query)?.hits
    render(contentType: "text/xml") {
      board(site: params.board) {
        for (h in hits) {
          post(time: h.source.time, id: h.id) {
            info(h.source.info)
            message(h.source.message)
            login(h.source.login)
          }
        }
      }
    }
  }
  
  def index = {
    def b = Board.findByName(params.board)
    if (!b)
      render 'Non existent board'
    def res = ESService.index(b)
    render res
  }
}
