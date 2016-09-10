package com.mogobiz.mockserver.io

import scala.io.Source

/**
  *
  * Created by smanciot on 22/04/16.
  */
object CSVReader {

  def read(source : Source, separator : String = ",") : Stream[Array[String]] = {
    using(source){
      source => source.getLines().map{_.split(separator).map(_.trim)}.toStream
    }.getOrElse(Stream.empty)
  }

}
