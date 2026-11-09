package com.farhan.boltechai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AppConstantsTest {

    @Test
    public void testDeveloperWatermarkConstant() {
        assertEquals("Developed by Farhan Shariar", AppConstants.DEVELOPER_CREDIT);
    }

    @Test
    public void testCategoryConstants() {
        assertEquals("food", AppConstants.CAT_FOOD);
        assertEquals("needs", AppConstants.CAT_NEEDS);
        assertEquals("feelings", AppConstants.CAT_FEELINGS);
        assertEquals("family", AppConstants.CAT_FAMILY);
        assertEquals("school", AppConstants.CAT_SCHOOL);
        assertEquals("market", AppConstants.CAT_MARKET);
        assertEquals("mosque", AppConstants.CAT_MOSQUE);
        assertEquals("hospital", AppConstants.CAT_HOSPITAL);
    }

    @Test
    public void testArasaacUrls() {
        assertTrue(AppConstants.PICTOGRAM_SEARCH_URL.contains("api.arasaac.org"));
        assertTrue(AppConstants.PICTOGRAM_IMAGE_URL.contains("static.arasaac.org"));
    }

    @Test
    public void testHospitalPhrases() {
        assertEquals("আমার ", AppConstants.HOSPITAL_PHRASE_PREFIX);
        assertEquals(" ব্যথা করছে", AppConstants.HOSPITAL_PHRASE_SUFFIX);
    }
}
