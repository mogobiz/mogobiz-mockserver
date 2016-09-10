package com.mogobiz.mockserver.util;

import io.netty.handler.codec.http.QueryStringDecoder;
import org.junit.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.url.URLParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.Assert.*;

import static org.mockserver.model.HttpRequest.request;
//import static org.mockserver.model.HttpResponse.response;

import static com.mogobiz.mockserver.util.MockServerTools.*;

/**
 *
 * Created by smanciot on 26/04/16.
 */
public class MockServerToolsTest {

    @Test
    public void testExtractParameter() throws UnsupportedEncodingException {
        assertEquals(
                "stephane.manciot@ebiznext.com",
                extractParameterValue(
                        extractParameters(
                                request().withQueryStringParameter("email", "stephane.manciot@ebiznext.com")
                        ), "email"
                ).get()
        );
        String email = URLEncoder.encode("stephane.manciot@ebiznext.com", "UTF-8");
        HttpRequest request = request().withBody(String.format("email=%s&test=true", email));
        assertEquals(
                "stephane.manciot@ebiznext.com",
                extractParameterValue(
                        extractParameters(
                                request
                        ), "email"
                ).get()
        );
        assertEquals(
                "true",
                extractParameterValue(
                        extractParameters(
                                request
                        ), "test"
                ).get()
        );
    }

    @Test
    public void testExtractParameterFromUrl(){
        QueryStringDecoder decoder = new QueryStringDecoder("http://localhost/uri?email=stephane.manciot@ebiznext.com");
        HttpRequest request = request().withPath(URLParser.returnPath(decoder.path())).withQueryStringParameters(decoder.parameters());
        assertEquals(
                "stephane.manciot@ebiznext.com",
                extractParameterValue(extractParameters(request), "email").get()
        );
        assertEquals("/uri", request.getPath().getValue());
    }
}
