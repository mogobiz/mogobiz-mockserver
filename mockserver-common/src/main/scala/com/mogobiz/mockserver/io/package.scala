package com.mogobiz.mockserver

import scala.language.reflectiveCalls
import scala.util.Try

/**
  *
  * Created by smanciot on 22/04/16.
  */
package object io {

  /**
    * loan pattern
    */
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): Try[B] =
    try {
      Try(f(resource))
    } finally resource.close()

}
