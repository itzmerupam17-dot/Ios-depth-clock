package com.example

import com.example.model.DepthLockConfig
import com.example.model.WallpaperCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBengaliDigitsConversion() {
        val input = "09:41"
        val converted = DepthLockConfig.convertToBengaliDigits(input)
        assertEquals("০৯:৪১", converted)

        val allDigits = "0123456789"
        val expected = "০১২৩৪৫৬৭৮৯"
        assertEquals(expected, DepthLockConfig.convertToBengaliDigits(allDigits))
    }

    @Test
    fun testWallpaperCatalogIntegrity() {
        val presets = WallpaperCatalog.PRESETS
        assertTrue(presets.isNotEmpty())
        presets.forEach { preset ->
            assertNotNull(preset.id)
            assertNotNull(preset.title)
            assertTrue(preset.depthOverlapPercent in 0.1f..0.9f)
        }
    }
}
