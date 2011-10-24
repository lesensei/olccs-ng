import org.bouchot.*
import grails.util.Environment

class BootStrap {
  def init = { servletContext ->
    if(!Board.findByName('shoop')) {
      def b = new Board()
      b.name = 'shoop'
      b.getURL = 'http://dax.sveetch.net/tribune/remote.xml'
      b.postURL = 'http://dax.sveetch.net/tribune/post.xml'
      b.lastIdParameter = 'last'
      b.postParameter = 'content'
      b.addToAliases('dax')
      b.addToAliases('sveetch')
      b.save()
    }
    //ESService.initService()
  }
  def destroy = {
  }
}
