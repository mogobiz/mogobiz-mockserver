package com.mogobiz.mockserver.io

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

/**
  *
  * Created by smanciot on 29/04/16.
  */
case class CookieHelper(
  name: String,
  value: String,
  expires: Option[Date],
  domain: Option[String],
  path: String,
  secure: Boolean,
  httpOnly: Boolean
) {
  def this(name: String, value: String, expires: Option[Date], domain: Option[String]){
    this(name, value, expires, domain, "/", true, true)
  }
  def asCookie : Cookie = {
    var _value = Seq(value)
    expires match {
      case Some(date) => _value = _value :+ ("Expires=" + new SimpleDateFormat("EEE, MMM dd HH:mm:ss zzz yyyy", Locale.ROOT).format(date))
      case none =>
    }
    domain match {
      case Some(s) => _value = _value :+ ("Domain=" + s)
      case _ =>
    }
    _value = _value :+ ("Path=" + path)
    if(secure) _value = _value :+ "Secure"
    if(httpOnly) _value = _value :+ "HttpOnly"
    new Cookie(name, _value.mkString("; "))
  }
}
