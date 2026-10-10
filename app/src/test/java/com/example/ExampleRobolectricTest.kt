package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.TransportType
import com.example.data.model.TravelSearchQuery
import com.example.data.repository.TravelInventoryRepository
import kotlinx.coroutines.test.runTest
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
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Vaygo", appName)
    }

    @Test
    fun `test travel search and budget categorization`() = runTest {
        val repo = TravelInventoryRepository()
        val query = TravelSearchQuery(
            origin = "Visakhapatnam",
            destination = "Hyderabad",
            totalBudget = 1500
        )
        val results = repo.searchOptions(query)
        assertTrue(results.isNotEmpty())

        val categories = repo.categorizePlans(results, 1500)
        assertTrue(categories.isNotEmpty())

        val cheapest = categories.find { it.badge == "Cheapest Feasible" }
        assertNotNull(cheapest)
        assertTrue(cheapest!!.option.totalEstimatedCost <= 1500)
    }
}
