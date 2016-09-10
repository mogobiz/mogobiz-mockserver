package com.mogobiz.mockserver.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * Created by smanciot on 18/04/16.
 */
public class XPathHelperTest {

    private static final String BODY = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><PartCustomer xmlns=\"http://ccu.laposte.fr/xml/customer\"><uid>test1@gfi.fr</uid><customerId>5384975541383</customerId><title>mr</title><name>GRZGZER</name><firstName>PRENOM</firstName><birthDate>1994-08-06</birthDate><address><sna4>21 RUE PIERRE BOURDAN</sna4><locality>PARIS</locality><postalCode>75012</postalCode><countryIsoCode>fr</countryIsoCode><mascadiaState>V</mascadiaState></address><acceptEmailsFromLaPoste>true</acceptEmailsFromLaPoste><acceptEmailsFromPartners>true</acceptEmailsFromPartners><acceptSmsFromLaPoste>false</acceptSmsFromLaPoste><acceptTermsOfUse>false</acceptTermsOfUse><UpdateTime>2016-04-08T17:00:56+02:00</UpdateTime></PartCustomer>";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetValue(){
        String value = XPathHelper.getValue(BODY, "/PartCustomer/uid/text()");
        assertEquals("test1@gfi.fr", value);
    }

    @Test
    public void testGetValues(){
        String[] values = XPathHelper.getValues(BODY, "/PartCustomer/uid/text()");
        assertEquals(1, values.length);
        assertEquals("test1@gfi.fr", values[0]);
    }

}
