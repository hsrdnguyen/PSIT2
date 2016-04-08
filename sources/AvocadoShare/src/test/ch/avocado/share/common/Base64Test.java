package ch.avocado.share.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class Base64Test {

    @Test
    public void testEncode() throws Exception {
        assertEquals("RGFzIGlzdCBlaW4gVGVzdCA6KSDDpMO2w7w=", Base64.encode("Das ist ein Test :) äöü".getBytes()));
    }

    @Test
    public void testDecode() throws Exception {
        assertEquals(new String(Base64.decode("RGFzIGlzdCBlaW4gVGVzdCA6KSDDpMO2w7w="), "UTF-8"), "Das ist ein Test :) äöü");
    }
}