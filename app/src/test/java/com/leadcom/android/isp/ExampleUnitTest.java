package com.leadcom.android.isp;

import org.junit.Test;

import java.text.NumberFormat;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testNumberFormat() {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        System.out.print(format.format(123456));
    }
}