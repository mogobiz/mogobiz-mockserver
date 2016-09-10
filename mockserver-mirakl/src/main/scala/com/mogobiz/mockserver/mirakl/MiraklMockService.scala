package com.mogobiz.mockserver.mirakl

import com.mogobiz.mockserver.mirakl.conf.Settings._
import com.mogobiz.mockserver.spi.{AbstractMockService, Remote}
import org.mockserver.model.{HttpResponse, HttpRequest}

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
    super.handle(httpRequest.withHeader("Authorization", frontApiKey))
  }
}