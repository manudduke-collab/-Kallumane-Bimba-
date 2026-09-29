package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.SampleDataGenerator
import com.example.data.model.MediaType
import com.example.util.KannadaKinshipCalculator
import com.example.util.RelationshipEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Vamsha Vriksha", appName)
  }

  @Test
  fun `kinship calculation for father and child`() {
    val sampleMembers = SampleDataGenerator.getSampleMembers()
    val father = sampleMembers.first { it.id == 6L } // Venkatesh Rao
    val son = sampleMembers.first { it.id == 11L } // Ananth Rao

    val sonToFatherKinship = KannadaKinshipCalculator.calculateKinship(son, father, sampleMembers)
    assertTrue(sonToFatherKinship.kannadaTerm.contains("ತಂದೆ") || sonToFatherKinship.kannadaTerm.contains("ಅಪ್ಪ"))

    val fatherToSonKinship = KannadaKinshipCalculator.calculateKinship(father, son, sampleMembers)
    assertTrue(fatherToSonKinship.kannadaTerm.contains("ಮಗ"))
  }

  @Test
  fun `relationship mapping identifies direct relatives correctly`() {
    val sampleMembers = SampleDataGenerator.getSampleMembers()
    val ananth = sampleMembers.first { it.id == 11L }

    val directRelatives = RelationshipEngine.getDirectRelatives(ananth, sampleMembers)
    assertEquals(6L, directRelatives.father?.id)
    assertEquals(7L, directRelatives.mother?.id)
    assertEquals(12L, directRelatives.spouse?.id)
    assertTrue(directRelatives.siblings.any { it.id == 13L }) // Raghavendra (Brother)
    assertTrue(directRelatives.siblings.any { it.id == 14L }) // Sowmya (Sister)
    assertTrue(directRelatives.children.any { it.id == 17L }) // Advaith (Son)
    assertTrue(directRelatives.children.any { it.id == 18L }) // Ananya (Daughter)
  }

  @Test
  fun `sample media includes horoscopes documents and certificates`() {
    val mediaList = SampleDataGenerator.getSampleMedia()
    assertTrue(mediaList.isNotEmpty())
    assertTrue(mediaList.any { it.mediaType == MediaType.KUNDALI })
    assertTrue(mediaList.any { it.mediaType == MediaType.DOCUMENT })
    assertTrue(mediaList.any { it.mediaType == MediaType.CERTIFICATE })
    assertTrue(mediaList.any { it.mediaType == MediaType.LETTER })
  }

  @Test
  fun `sample data generation contains Karnataka cultural heritage`() {
    val heritage = SampleDataGenerator.getSampleHeritage()
    assertNotNull(heritage)
    assertTrue(heritage.mulaOoru.contains("ತೀರ್ಥಹಳ್ಳಿ") || heritage.mulaOoru.contains("ಶಿವಮೊಗ್ಗ"))
    assertTrue(heritage.primaryGothra.contains("ಕಾಶ್ಯಪ") || heritage.primaryGothra.contains("Kashyapa"))
  }
}
