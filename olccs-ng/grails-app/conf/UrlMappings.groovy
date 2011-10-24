import org.bouchot.*

class UrlMappings {
  static mappings = {
    "/$controller/$action?/$id?"{
      constraints {
        // apply constraints here
      }
    }
    
    "/$board/$action" {
      controller = "API"
      constraints {
        board(validator: {
          Board.list()*.name.contains(it)
        })
      }
    }

    "/"(view:"/index")
    "500"(view:'/error')
  }
}
