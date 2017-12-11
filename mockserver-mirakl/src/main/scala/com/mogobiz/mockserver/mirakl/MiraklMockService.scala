package com.mogobiz.mockserver.mirakl

/**
  *
  * Created by smanciot on 10/09/16.
  */
class MiraklMockService extends AbstractMockService {

  override def rootPath: String = RootPath

  override def active: Boolean = Active

  override def remote: Option[Remote] = Mirakl.remote

  val frontApiKey = Mirakl.frontApikey

  override def handle(httpRequest: HttpRequest): HttpResponse = {
    // mock service implementation goes here
    super.handle(httpRequest.withHeader("Authorization", frontApiKey).withHeader("Accept", "application/json"))
  }
}
