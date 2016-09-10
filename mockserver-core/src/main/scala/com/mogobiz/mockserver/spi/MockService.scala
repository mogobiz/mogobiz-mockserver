package com.mogobiz.mockserver.spi

import org.mockserver.client.server.MockServerClient
import org.mockserver.initialize.ExpectationInitializer
import org.mockserver.mock.action.{HttpForwardActionHandler, ExpectationCallback}
import org.mockserver.model.HttpCallback._
import org.mockserver.model.HttpForward._
import org.mockserver.model.HttpResponse._
import org.mockserver.model.{HttpResponse, HttpRequest}
import org.mockserver.model.HttpRequest._

/**
  *
  * Created by smanciot on 04/05/16.
  */
trait MockHandler extends ExpectationCallback {

  def remote: Option[Remote] = None

  final def redirect(remote: Remote, httpRequest: HttpRequest): HttpResponse = {
    new HttpForwardActionHandler() handle (forward withPort remote.port withHost remote.host withScheme remote.scheme, httpRequest)
  }

  override def handle(httpRequest: HttpRequest): HttpResponse = {
    remote match {
      case Some(r) =>
        redirect(r, httpRequest)
      case _ => response withStatusCode 404
    }
  }
}

trait MockService extends ExpectationInitializer with MockHandler {

  def active: Boolean = true

  def rootPath: String = ".*"

  final val callBack: Class[_ <: MockHandler] = getClass

  override final def initializeExpectations(mockServerClient: MockServerClient) = {
    if(active){
      mockServerClient when (request withPath rootPath) callback (callback withCallbackClass s"${callBack.getPackage.getName}.${callBack.getSimpleName}")
    }
    else{
      remote match {
        case Some(r) =>
          mockServerClient when (request withPath rootPath) forward (forward withPort r.port withHost r.host withScheme r.scheme)
        case _ => response withStatusCode 404
      }
    }
  }

}

case class Remote(port: Int, host: String, secure: Boolean){
  lazy val scheme = secure match {
    case true => Scheme.HTTPS
    case _ => Scheme.HTTP
  }
}

abstract class AbstractMockService extends MockService{
  val rootUri: String = rootPath.takeWhile(_ != '.')
}

