package com.farhan.boltechai;

import static org.junit.Assert.assertEquals;

import com.farhan.boltechai.data.db.entity.UsageEntity;

import org.junit.Test;

public class UsageEntityTest {

    @Test
    public void testUsageEntityIncrementAndSetters() {
        long now = System.currentTimeMillis();
        UsageEntity usage = new UsageEntity(101L, 1, now, "ভাত", "Rice", AppConstants.CAT_FOOD);
        assertEquals(101L, usage.getTileId());
        assertEquals("ভাত", usage.getLabelBn());
        assertEquals("Rice", usage.getLabelEn());
        assertEquals(AppConstants.CAT_FOOD, usage.getCategory());
        assertEquals(1, usage.getUseCount());
        assertEquals(now, usage.getLastUsed());

        usage.setUseCount(usage.getUseCount() + 1);
        assertEquals(2, usage.getUseCount());

        usage.setTileId(202L);
        assertEquals(202L, usage.getTileId());

        usage.setLabelBn("পানি");
        assertEquals("পানি", usage.getLabelBn());

        usage.setLabelEn("Water");
        assertEquals("Water", usage.getLabelEn());

        usage.setCategory(AppConstants.CAT_NEEDS);
        assertEquals(AppConstants.CAT_NEEDS, usage.getCategory());
    }
}
