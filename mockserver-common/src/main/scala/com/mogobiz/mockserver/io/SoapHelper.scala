package com.mogobiz.mockserver.io

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import javax.xml.bind.JAXBContext
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.soap.MessageFactory

/**
  *
  * Created by smanciot on 03/05/16.
  */
object SoapHelper {

  def read[T](soapMessage: String, classz: Class[T]): T = {
    val message = MessageFactory.newInstance().createMessage(null,
      new ByteArrayInputStream(soapMessage.getBytes()))
    val unmarshaller = JAXBContext.newInstance(classz).createUnmarshaller()
    unmarshaller.unmarshal(message.getSOAPBody.extractContentAsDocument, classz).getValue
  }

  def write[T](obj: T): String = {
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
    val marshaller = JAXBContext.newInstance(obj.getClass).createMarshaller()
    marshaller.marshal(obj, document)
    val soapMessage = MessageFactory.newInstance().createMessage()
    soapMessage.getSOAPBody.addDocument(document)
    val outputStream = new ByteArrayOutputStream()
    soapMessage.writeTo(outputStream)
    new String(outputStream.toByteArray)
  }

  def extractSoapAction(soapMessage: String): String = {
    MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(soapMessage.getBytes()))
      .getSOAPBody.extractContentAsDocument.getDocumentElement.getLocalName
  }
}
