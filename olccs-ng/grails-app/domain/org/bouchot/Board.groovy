package org.bouchot

class Board {
  static hasMany = [aliases: String]
  String name
  String getURL
  String postURL
  String postParameter
  String lastIdParameter
  
  static constraints = {
    name(nullable: false, blank: false, unique: true)
    getURL(nullable: false, blank: false, url: true)
    postURL(nullable: false, blank: false, url: true)
    postParameter(nullable: false, blank: false)
    lastIdParameter(nullable: true, blank: true)
    aliases(nullable: true)
  }
}
