package com.example.gif_api.benchmark

import androidx.benchmark.macro.*
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
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

@RunWith(AndroidJUnit4::class)
class GifSearchBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startUpNoProfile() = startupTest(CompilationMode.None())

    @Test
    fun startUpProfile() = startupTest(CompilationMode.Partial())

    @Test
    fun scrollNoProfile() = scrollingTest(CompilationMode.None())

    @Test
    fun scrollProfile() = scrollingTest(CompilationMode.Partial())

    private fun startupTest(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.example.gif_api",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = compilationMode,
        measureBlock = MacrobenchmarkScope::startActivityAndWait
    )

    private fun scrollingTest(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.example.gif_api",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = compilationMode,
        setupBlock = {
            pressHome()
            startActivityAndWait()
            with(device) {
                findObject(By.res(QUERY_TAG)).text = "test"
                wait(Until.findObject(By.res(ITEM_DELETE_TAG)), 10_000L)
            }
        }
    ) {
        with(device.findObject(By.res(LIST_PORTRAIT_TAG))) {
            scroll(Direction.DOWN, 0.5f)
        }
    }
}

