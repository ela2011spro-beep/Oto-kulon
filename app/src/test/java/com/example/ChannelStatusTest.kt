package com.example

import com.example.data.service.ChannelInspectorService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ChannelStatusTest {

    private val service = ChannelInspectorService()

    @Test
    fun `search known channel Ruhi Cenet returns most viewed video and email`() = runBlocking {
        val result = service.searchAndInspectChannel("Ruhi Çenet")
        assertNotNull(result)
        assertEquals("Ruhi Çenet", result.channelName)
        assertEquals("@ruhicenet", result.handle)
        assertTrue(result.businessEmail.contains("gmail") || result.businessEmail.contains("@"))
        assertNotNull(result.mostViewedVideo)
        assertTrue(result.mostViewedVideo.viewCount > 10_000_000)
    }

    @Test
    fun `search custom arbitrary channel returns intelligent details`() = runBlocking {
        val result = service.searchAndInspectChannel("Gezgin Bilimci")
        assertNotNull(result)
        assertEquals("Gezgin Bilimci", result.channelName)
        assertTrue(result.businessEmail.endsWith("@gmail.com"))
        assertNotNull(result.mostViewedVideo.title)
        assertTrue(result.mostViewedVideo.viewCountFormatted.contains("izlenme"))
    }
}
