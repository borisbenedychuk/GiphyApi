package com.example.gif_api.benchmark

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import com.example.core_ui.ITEM_DELETE_TAG
import com.example.core_ui.LIST_PORTRAIT_TAG
import com.example.core_ui.QUERY_TAG
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalBaselineProfilesApi::class)
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() = baselineProfileRule.collectBaselineProfile(
        packageName = "com.example.gif_api",
    ) {
        startActivityAndWait()
        with(device) {
            findObject(By.res(QUERY_TAG)).text = "test"
            wait(Until.findObject(By.res(ITEM_DELETE_TAG)), 10_000L)
            with(findObject(By.res(LIST_PORTRAIT_TAG))) {
                scroll(Direction.DOWN, 1f)
                fling(Direction.UP)
            }
        }
    }
}