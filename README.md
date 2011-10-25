# OnLine CoinCoin Server - Now in Grails #

OLCCS-ng is a grails port of [OLCCS](https://github.com/zorel/olccs).

At the moment, it doesn't do much.

If ran in a dev environment, it starts a local elasticsearch node and configures a default Board object for you. You may then browse to http://localhost:8080/olccs-ng/shoop/index so that posts from the default board can get indexed. Finally you may browse to http://localhost:8080/olccs-ng/shoop/xml or http://localhost:8080/olccs-ng/shoop/json to get them displayed back. You may also go to http://localhost:8080/olccs-ng/shoop/search?query=queryString to execute a search query (see the elasticsearch documentation on the querystring format).

When ran in a production environment, the application will connect in transport mode to an existing elasticsearch node and will not create proper mappings for board posts when indexing: these need to be set up in the elasticsearch configuration.

## TODO ##
- Fix parsing of posts before indexing: it looks like the XmlSlurper (with or without tagsoup) skips some posts
- Create Quartz jobs to index posts, and remove the "index" action from the API controller
- Use the groovy ES client whenever possible (it should always be)
- Undoubtfully much more stuff