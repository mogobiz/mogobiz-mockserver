package com.mogobiz.mockserver.io;

import com.mogobiz.mockserver.io.CookieHelper;
import org.junit.Test;
import org.mockserver.model.Cookie;
import scala.Option;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 *
 * Created by smanciot on 29/04/16.
 */
public class CookieHelperTest {

    @Test
    public void testComputeValue() throws ParseException {
        Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse("29/04/2016 14:42:09");
        CookieHelper cookieHelper = new CookieHelper("CookieName", "CookieValue", Option.apply(date), Option.apply("laposte.fr"));
        Cookie cookie = cookieHelper.asCookie();
        assertNotNull(cookie);
        assertEquals("CookieName", cookie.getName().getValue());
//FIXME        assertEquals("CookieValue; Expires=Fri, Apr 29 14:42:09 GMT 2016; Domain=laposte.fr; Path=/; Secure; HttpOnly", cookie.getValue().getValue());
    }
}
