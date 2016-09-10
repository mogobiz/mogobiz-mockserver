package com.mogobiz.mockserver.io;

import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.Assert.*;

/**
 *
 * Created by smanciot on 03/05/16.
 */
public class SoapHelperTest {

    @Test
    public void testReadAndWrite(){
        String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://example.com/\">\n" +
                "    <soapenv:Body>\n" +
                "        <ns:getNumberResponse>\n" +
                "            <number>123456789</number>\n" +
                "        </ns:getNumberResponse>\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        Response response = SoapHelper.read(soapMessage, Response.class);
        assertNotNull(response);
        assertEquals(123456789L, response.getNumber());
        response.setNumber(111111111L);
        soapMessage = SoapHelper.write(response);
        assertNotNull(soapMessage);
        assertEquals("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><ns2:getNumberResponse xmlns:ns2=\"http://example.com\"><number>111111111</number></ns2:getNumberResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>", soapMessage);
    }

    @XmlRootElement(name = "getNumberResponse", namespace = "http://example.com")
    public static class Response {

        private long number;

        public long getNumber() {
            return number;
        }

        public void setNumber(long number) {
            this.number = number;
        }

    }

    @Test
    public void testExtractSoapAction(){
        String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://example.com/\">\n" +
                "    <soapenv:Body>\n" +
                "        <ns:getNumberResponse>\n" +
                "            <number>123456789</number>\n" +
                "        </ns:getNumberResponse>\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>";
        String soapAction = SoapHelper.extractSoapAction(soapMessage);
        assertNotNull(soapAction);
        assertEquals("getNumberResponse", soapAction);
    }
}
