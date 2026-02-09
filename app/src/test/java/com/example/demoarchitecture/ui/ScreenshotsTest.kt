package com.example.demoarchitecture.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import com.android.resources.ScreenOrientation
import com.example.demoarchitecture.ui.helloworld.HelloWorldContent
import com.example.demoarchitecture.ui.helloworld.HelloWorldViewModel
import com.example.demoarchitecture.ui.home.HomeScreen
import com.example.demoarchitecture.ui.second.SecondScreen
import com.example.demoarchitecture.ui.theme.DemoArchitectureTheme
import org.junit.Rule
import org.junit.Test

class ScreenshotsTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material3.Light.NoActionBar"
    )

    @Test
    fun homeScreen() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HomeScreen(
                    onNavigateToHelloWorld = {},
                    onNavigateToSecond = {}
                )
            }
        }
    }

    @Test
    fun secondScreen() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                SecondScreen(
                    onNavigateBack = {}
                )
            }
        }
    }

    @Test
    fun helloWorldIdle() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Idle,
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }

    @Test
    fun helloWorldLoading() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Loading,
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }

    @Test
    fun helloWorldSuccess() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Success("Hello World!"),
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }

    @Test
    fun helloWorldError() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Error("Something went wrong"),
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }
}

class ScreenshotsDarkTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(nightMode = NightMode.NIGHT),
        theme = "android:Theme.Material3.Dark.NoActionBar"
    )

    @Test
    fun homeScreenDark() {
        paparazzi.snapshot {
            DemoArchitectureTheme(darkTheme = true) {
                HomeScreen(
                    onNavigateToHelloWorld = {},
                    onNavigateToSecond = {}
                )
            }
        }
    }

    @Test
    fun secondScreenDark() {
        paparazzi.snapshot {
            DemoArchitectureTheme(darkTheme = true) {
                SecondScreen(
                    onNavigateBack = {}
                )
            }
        }
    }

    @Test
    fun helloWorldIdleDark() {
        paparazzi.snapshot {
            DemoArchitectureTheme(darkTheme = true) {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Idle,
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }

    @Test
    fun helloWorldLoadingDark() {
        paparazzi.snapshot {
            DemoArchitectureTheme(darkTheme = true) {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Loading,
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }

    @Test
    fun helloWorldSuccessDark() {
        paparazzi.snapshot {
            DemoArchitectureTheme(darkTheme = true) {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Success("Hello World!"),
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }

    @Test
    fun helloWorldErrorDark() {
        paparazzi.snapshot {
            DemoArchitectureTheme(darkTheme = true) {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Error("Something went wrong"),
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }
}

class ScreenshotsTabletTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_C,
        theme = "android:Theme.Material3.Light.NoActionBar"
    )

    @Test
    fun homeScreenTablet() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HomeScreen(
                    onNavigateToHelloWorld = {},
                    onNavigateToSecond = {}
                )
            }
        }
    }

    @Test
    fun secondScreenTablet() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                SecondScreen(
                    onNavigateBack = {}
                )
            }
        }
    }

    @Test
    fun helloWorldSuccessTablet() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Success("Hello World!"),
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }
}

class ScreenshotsLandscapeTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(orientation = ScreenOrientation.LANDSCAPE),
        theme = "android:Theme.Material3.Light.NoActionBar"
    )

    @Test
    fun homeScreenLandscape() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HomeScreen(
                    onNavigateToHelloWorld = {},
                    onNavigateToSecond = {}
                )
            }
        }
    }

    @Test
    fun secondScreenLandscape() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                SecondScreen(
                    onNavigateBack = {}
                )
            }
        }
    }

    @Test
    fun helloWorldSuccessLandscape() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Success("Hello World!"),
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }
}

class ScreenshotsLargeFontTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(fontScale = 1.5f),
        theme = "android:Theme.Material3.Light.NoActionBar"
    )

    @Test
    fun homeScreenLargeFont() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HomeScreen(
                    onNavigateToHelloWorld = {},
                    onNavigateToSecond = {}
                )
            }
        }
    }

    @Test
    fun helloWorldSuccessLargeFont() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Success("Hello World!"),
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }
}

class ScreenshotsSmallPhoneTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.NEXUS_4,
        theme = "android:Theme.Material3.Light.NoActionBar"
    )

    @Test
    fun homeScreenSmall() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HomeScreen(
                    onNavigateToHelloWorld = {},
                    onNavigateToSecond = {}
                )
            }
        }
    }

    @Test
    fun helloWorldSuccessSmall() {
        paparazzi.snapshot {
            DemoArchitectureTheme {
                HelloWorldContent(
                    state = HelloWorldViewModel.State.HelloWorldState.Success("Hello World!"),
                    onNavigateBack = {},
                    onLoadData = {}
                )
            }
        }
    }
}
