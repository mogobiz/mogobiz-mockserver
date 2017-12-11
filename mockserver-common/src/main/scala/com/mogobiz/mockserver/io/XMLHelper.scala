package com.mogobiz.mockserver.io

import java.io.{ByteArrayInputStream, StringWriter}
import javax.xml.bind.JAXBContext

/**
  *
  * Created by smanciot on 06/05/16.
  */
object XMLHelper {

  def read[T](message: String, classz: Class[T]): T = {
    val unmarshaller = JAXBContext.newInstance(classz).createUnmarshaller()
    unmarshaller.unmarshal(new ByteArrayInputStream(message.getBytes("UTF-8"))).asInstanceOf[T]
  }

  def write[T](obj: T): String = {
    val marshaller = JAXBContext.newInstance(obj.getClass).createMarshaller()
    val writer = new StringWriter()
    marshaller.marshal(obj, writer)
    writer.toString
  }

}
