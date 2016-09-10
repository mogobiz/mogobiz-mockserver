package com.mogobiz.mockserver.io;

import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * Created by smanciot on 03/05/16.
 */
public class XMLHelperTest {

    @Test
    public void testReadAndWrite(){
        String xmlMessage = "<ns:getNumberResponse xmlns:ns=\"http://example.com/\"><number>123456789</number></ns:getNumberResponse>";
        Response response = XMLHelper.read(xmlMessage, Response.class);
        assertNotNull(response);
        assertEquals(123456789L, response.getNumber());
        response.setNumber(111111111L);
        xmlMessage = XMLHelper.write(response);
        assertNotNull(xmlMessage);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:getNumberResponse xmlns:ns2=\"http://example.com/\"><number>111111111</number></ns2:getNumberResponse>", xmlMessage);
    }

    @XmlRootElement(name = "getNumberResponse", namespace = "http://example.com/")
    public static class Response {

        private long number;

        public long getNumber() {
            return number;
        }

        public void setNumber(long number) {
            this.number = number;
        }

    }

}
