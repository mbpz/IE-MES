package com.iemes.util;

import com.iemes.entity.ServerInfoFormMap;
import junit.framework.TestCase;

public class SystemInfoTest extends TestCase {
    public void testScheduledMetricsWithoutSigarNativeLibrary() throws Exception {
        ServerInfoFormMap metrics = SystemInfo.usage();
        for (String key : new String[] {"cpuUsage", "ramUsage", "jvmUsage"}) {
            assertTrue(key + " must be numeric", metrics.get(key) instanceof Number);
            double value = ((Number) metrics.get(key)).doubleValue();
            assertTrue(key + " must be a valid percentage: " + value, value >= 0 && value <= 100);
        }
    }
}
