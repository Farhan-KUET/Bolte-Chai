package com.farhan.boltechai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.farhan.boltechai.data.db.entity.TileEntity;

import org.junit.Test;

public class TileEntityTest {

    @Test
    public void testTileEntityCreationAndGetters() {
        TileEntity tile = new TileEntity(AppConstants.CAT_FOOD, "ভাত", "Rice", "ic_rice", false, 0);
        assertEquals("ভাত", tile.getLabelBn());
        assertEquals("Rice", tile.getLabelEn());
        assertEquals("ic_rice", tile.getImagePath());
        assertEquals(AppConstants.CAT_FOOD, tile.getCategory());
        assertEquals(0, tile.getSortOrder());
        assertFalse(tile.isCustom());

        tile.setId(42);
        assertEquals(42, tile.getId());
    }

    @Test
    public void testTileEntitySetters() {
        TileEntity tile = new TileEntity(AppConstants.CAT_FOOD, "ভাত", "Rice", "ic_rice", false, 0);
        tile.setLabelBn("পানি");
        tile.setLabelEn("Water");
        tile.setImagePath("ic_water");
        tile.setCategory(AppConstants.CAT_NEEDS);
        tile.setSortOrder(5);
        tile.setCustom(true);

        assertEquals("পানি", tile.getLabelBn());
        assertEquals("Water", tile.getLabelEn());
        assertEquals("ic_water", tile.getImagePath());
        assertEquals(AppConstants.CAT_NEEDS, tile.getCategory());
        assertEquals(5, tile.getSortOrder());
        assertTrue(tile.isCustom());
    }

    @Test
    public void testGetDisplayLabel() {
        TileEntity tile = new TileEntity(AppConstants.CAT_FOOD, "ভাত", "Rice", "ic_rice", false, 0);
        assertEquals("Rice", tile.getDisplayLabel("en"));
        assertEquals("ভাত", tile.getDisplayLabel("bn"));
        assertEquals("ভাত", tile.getDisplayLabel("unknown"));
    }
}
