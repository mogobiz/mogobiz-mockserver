package com.mogobiz.mockserver.io

import java.io.InputStream
import java.util.regex.Pattern

import org.apache.commons.codec.binary.Base64

/**
  * Created by smanciot on 19/05/16.
  */
object Base64Tools {

  val DATA_ENCODED = Pattern.compile("data:(.*);base64,(.*)")

  def read(is: InputStream): Array[Byte] = Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray

  def encodeBase64(is: InputStream, mimeType: Option[String]): String = {
    encodeBase64(read(is), mimeType)
  }

  def encodeBase64(bytes: Array[Byte], mimeType: Option[String]): String = {
    val encoded =  Base64.encodeBase64String(bytes)
    mimeType match {
      case Some(m) => s"data:$mimeType;base64," + encoded
      case None    => encoded
    }
  }

  def decodeBase64(encoded: String = ""): Array[Byte] = {
    var ret = encoded
    var encodedBase64 = Base64.isBase64(ret)
    val matcher = DATA_ENCODED.matcher(encoded)
    if (matcher.find() && matcher.groupCount() > 1) {
      ret = matcher.group(2)
      encodedBase64 = true
    }
    if(encodedBase64){
      Base64.decodeBase64(ret)
    }
    else{
      ret.getBytes("UTF-8")
    }
  }
}
