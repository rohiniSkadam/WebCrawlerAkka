package controllers

import com.ning.http.client.{AsyncCompletionHandler, AsyncHttpClient, AsyncHttpClientConfig, Response}

import scala.concurrent.{Future, Promise}

/**
  * Created by synerzip on 7/4/17.
  */

object CrawlClient {
  val clientConfig = new AsyncHttpClientConfig.Builder()
  val clientUrl = new AsyncHttpClient(clientConfig.build())

  /**
    * Prepare & Execute HTTP client request.
    * @param url - input url
    * @return
    */
  def geturl(url: String): Future[String] = {
    val promise = Promise[String]()
    val request = clientUrl.prepareGet(url).build()
    clientUrl.executeRequest(request, new AsyncCompletionHandler[Response]() {
      def onCompleted(response: Response): Response = {
        promise.success(response.getResponseBody)
        response
      }
    })
    promise.future
  }
}
