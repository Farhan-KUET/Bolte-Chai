package com.farhan.boltechai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.farhan.boltechai.data.model.PictogramResult;

import org.junit.Test;

import java.util.Collections;

public class PictogramResultTest {

    @Test
    public void testPictogramResultGettersAndSetters() {
        PictogramResult result = new PictogramResult();
        result.set_id(12345);

        assertEquals(12345, result.get_id());
        assertTrue(result.getImageUrl().contains("12345"));
        assertTrue(result.getImageUrl().endsWith("12345_500.png"));

        PictogramResult.Keyword kw = new PictogramResult.Keyword();
        kw.setKeyword("water");
        kw.setType(1);
        kw.setHasLocution(true);

        result.setKeywords(Collections.singletonList(kw));
        assertNotNull(result.getKeywords());
        assertEquals(1, result.getKeywords().size());
        assertEquals("water", result.getLabel("en"));
        assertEquals("water", result.getKeywords().get(0).getKeyword());
        assertEquals(1, result.getKeywords().get(0).getType());
        assertTrue(result.getKeywords().get(0).isHasLocution());
    }

    @Test
    public void testPictogramResultFallbackLabel() {
        PictogramResult result = new PictogramResult();
        result.set_id(999);
        result.setKeywords(null);
        assertEquals("Pictogram", result.getLabel("bn"));
    }
}
