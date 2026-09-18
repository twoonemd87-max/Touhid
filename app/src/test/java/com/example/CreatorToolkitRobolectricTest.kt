package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GeneratorType
import com.example.data.model.Language
import com.example.data.model.Platform
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CreatorToolkitRobolectricTest {

  @Test
  fun `verify app name resource`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Creator Toolkit", appName)
  }

  @Test
  fun `verify generator types and platforms`() {
    assertEquals(5, GeneratorType.values().size)
    assertEquals(4, Platform.values().size)
    assertEquals(2, Language.values().size)

    assertNotNull(GeneratorType.TITLE)
    assertNotNull(GeneratorType.DESCRIPTION)
    assertNotNull(GeneratorType.HASHTAGS)
    assertNotNull(GeneratorType.IDEA)
    assertNotNull(GeneratorType.THUMBNAIL)
  }
}
