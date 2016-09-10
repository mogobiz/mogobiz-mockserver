package com.mogobiz.mockserver.util

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import com.mogobiz.mockserver.io.SoapHelper.{write => writeSoap}
import com.mogobiz.mockserver.io.XMLHelper.{write => writeXML}
import org.mockserver.model.{HttpRequest, Parameter, Header, HttpResponse}
import org.mockserver.model.HttpResponse._

import java._

/**
  *
  * Created by smanciot on 04/05/16.
  */
object MockServerTools {

  def extractParameters(httpRequest: HttpRequest): util.Map[String, Parameter] = {
    val map = new util.HashMap[String, Parameter]
    val parameters: util.List[Parameter] = httpRequest.getQueryStringParameters
    import scala.collection.JavaConversions._
    for (parameter <- parameters) {
      map.put(parameter.getName.getValue, parameter)
    }
    val body: String = httpRequest.getBodyAsString
    if (body != null) {
      try {
        val params = body.trim.split("&")
        for (param <- params) {
          val kv = param.split("=")
          if (kv.length > 1) {
            val k = URLDecoder.decode(kv(0), "UTF-8")
            val v = URLDecoder.decode(kv(1), "UTF-8")
            val parameter: Parameter = new Parameter(k, v)
            map.put(k, parameter)
          }
        }
      }
      catch {
        case e: UnsupportedEncodingException => e.printStackTrace(System.err)
      }
    }
    map
  }

  def extractParameterValue(parameters: util.Map[String, Parameter], name: String): Option[String] =
    if (parameters != null && name != null && parameters.containsKey(name))
      Some(parameters.get(name).getValues.get(0).getValue)
    else None

  def getDateHeader: Header = new Header("Date", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).format(new Date))

  def getContentLengthHeader(body: String): Header = new Header("Content-Length", "" + body.getBytes.length)

  def generateOsbResponse(statusCode: Int, body: String): HttpResponse =
    response withStatusCode statusCode withHeaders(
      new Header("Content-Language", "en"),
      new Header("Age", "0"),
      getContentLengthHeader(body),
      new Header("Connection", "keep-alive"),
      new Header("Server", "Oracle-Application-Server-11g"),
      new Header("X-Cache", "MISS"),
      new Header("X-Powered-By", "Servlet/2.5 JSP/2.1"),
      new Header("Cache-Control", "no-cache"),
      new Header("Strict-Transport-Security", "max-age=16000000; includeSubDomains; preload;"),
      getDateHeader,
      new Header("Access-Control-Allow-Origin", "*"),
      new Header("Keep-Alive", "timeout=5, max=100"),
      new Header("Via", "1.1 varnish-v4"),
      new Header("Accept-Ranges", "bytes")
      ) withBody body

  def generateOsbResponseAsXML(statusCode: Int, o: AnyRef): HttpResponse =
    generateOsbResponse(statusCode, writeXML(o)) withHeader new Header("Content-Type", "text/xml; charset=UTF-8")

  def generateOsbResponseAsXML(statusCode: Int, body: String): HttpResponse =
    generateOsbResponse(statusCode, body) withHeader new Header("Content-Type", "text/xml; charset=UTF-8")

  def generateOsbResponseAsSoap(statusCode: Int, o: AnyRef): HttpResponse =
    generateOsbResponse(statusCode, writeSoap(o)) withHeader new Header("Content-Type", "text/xml; charset=UTF-8")

  def generateOsbResponseAsJSON(statusCode: Int, body: String): HttpResponse =
    generateOsbResponse(statusCode, body) withHeader new Header("Content-Type", "application/json; charset=UTF-8")

  def none[T](classz: Class[T]): Option[T] = None

  def option(value: String): Option[String] = Option(value)

}
