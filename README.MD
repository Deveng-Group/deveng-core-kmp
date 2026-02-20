# Deveng Core KMP

Kotlin Multiplatform core library: shared UI, utilities, data, and permissions for Android, iOS, Desktop, and Web.

## Overview

Deveng Core KMP is a KMP library that provides:

- **UI & theming** — Compose components (buttons, dialogs, pickers, navigation, OTP, option lists, etc.) with a customizable theme and typography system.
- **Data & domain** — Key-value storage (e.g. device identifier), pagination models and loaders, camera/capture (`SharedImage`, `CameraManager`) with expect/actual per platform.
- **Utilities** — Platform detection and config, date/time formatting, image resize/compress, string formatting, logging, dispatchers, clipboard, dial, maps, share, location.
- **Permissions** — Unified permission API (camera, gallery, location, Bluetooth, contacts, notifications, motion) with Android and iOS implementations.

## Supported Platforms

| Platform | Support |
|----------|---------|
| Android  | ✅      |
| iOS      | ✅ (device + simulator) |
| Desktop  | ✅ (JVM) |
| Web      | ✅ (Wasm/JS) |

## Installation

Add the dependency to your module (Kotlin DSL):

```kotlin
dependencies {
    implementation("global.deveng:core-kmp:VERSION")
}
```

Replace `VERSION` with the [latest release](https://central.sonatype.com/artifact/global.deveng/core-kmp) or the version from the repo.

## Table of Contents

- [Quick Start](#quick-start)
- [Library Modules](#library-modules)
- [UI Components & Theming](#ui-components--theming)
- [Utilities](#utilities)
- [Data & Domain](#data--domain)
- [Permissions](#permissions)
- [Pagination](#pagination)
- [Sample App](#sample-app)
- [Building](#building)
- [License](#license)

## Quick Start

**UI** — Wrap your composables in `AppTheme` and use shared components:

```kotlin
AppTheme {
    CustomButton(text = "Click me", onClick = { })
}
```

**Platform & utils** — Use `MultiPlatformUtils` (construct with `Context` on Android; no-arg on other platforms) for platform config and common actions:

```kotlin
val utils = MultiPlatformUtils(context) // Android; use no-arg constructor on iOS/Desktop/Wasm
val config = utils.getPlatformConfig() // platform, language, uuid, deviceName, packageVersionName
utils.copyToClipBoard("text")
utils.openUrl("https://example.com")
```

## Library Modules

The `deveng-core` module is organized as follows:

| Area | Description |
|------|-------------|
| **UI & theming** | `ComponentTheme`, `AppTheme`, typography; buttons, dialogs, text fields, date/range pickers, OTP, navigation menus, option lists, chips, search, progress, scrollbars, JSON viewer, etc. |
| **Data** | `DeviceInfoStorage` (e.g. device identifier); `PagedListResponse`, `BasePaginatedResponse` for paginated API responses. |
| **Domain** | `PagedList` for pagination; `SharedImage`, `CameraManager`, `rememberCameraManager` for camera/photo capture (Android, iOS; stubs on Desktop/Web). |
| **Utilities** | `Platform`, `PlatformConfig`, `MultiPlatformUtils` (dial, clipboard, open URL, maps, share, location); date/time formatting; `ImageSizeProcessor` and `ImageProcessingProfile`; `StringFormatter`, `CustomLogger`, `CustomDispatchers`. |
| **Permissions** | `Permission` enum, `PermissionsController`, `PermissionState`; Android and iOS implementations. |
| **Pagination (UI)** | `PaginatedFlowLoader`, `PaginatedListView`, page state. |

## UI Components & Theming

### Step-by-Step Theming Guide

#### Step 1: Basic Usage (Default Theme)

Components work out of the box with default colors and Urbanist font:

```kotlin
AppTheme {
    CustomButton(text = "Button", onClick = { })
    CustomAlertDialog(
        isDialogVisible = true,
        title = "Title",
        description = "Description",
        positiveButtonText = "OK",
        onPositiveButtonClick = { }
    )
}
```

#### Step 2: Customize Colors

Create a `ComponentTheme` to override component colors:

```kotlin
val theme = ComponentTheme(
    button = ButtonTheme(
        containerColor = Color.Blue,
        contentColor = Color.White
    ),
    alertDialog = AlertDialogTheme(
        headerColor = Color.White,
        titleColor = Color.Black
    )
)

AppTheme(componentTheme = theme) {
    // Components use your custom colors
}
```

#### Step 3: Customize Font Family

Change the default font (Urbanist) globally:

```kotlin
// System fonts
val theme = ComponentTheme(
    typography = TypographyTheme(
        fontFamily = FontFamily.SansSerif
    )
)

// Custom font files
val customFont = FontFamily(
    Font(resource = Res.font.my_font_regular, weight = FontWeight(400)),
    Font(resource = Res.font.my_font_bold, weight = FontWeight(700))
)

val theme = ComponentTheme(
    typography = TypographyTheme(fontFamily = customFont)
)
```

#### Step 4: Override at Component Level

Override theme values for specific components:

```kotlin
AppTheme(componentTheme = theme) {
    CustomButton(
        text = "Special Button",
        containerColor = Color.Red, // Overrides theme
        textStyle = BoldTextStyle().copy(fontSize = 20.sp)
    )
}
```

### Theme Structure

```
ComponentTheme
├── typography: TypographyTheme
│   └── fontFamily: FontFamily? (default: Urbanist)
├── button: ButtonTheme
│   ├── containerColor, contentColor
│   ├── disabledContainerColor, disabledContentColor
│   └── defaultTextStyle
├── alertDialog: AlertDialogTheme
│   ├── headerColor, bodyColor
│   ├── titleColor, descriptionColor
│   ├── positiveButtonColor, negativeButtonColor
│   └── titleTextStyle, descriptionTextStyle, buttonTextStyle
├── surface: SurfaceTheme
│   └── defaultColor, defaultContentColor
└── dialogHeader: DialogHeaderTheme
    └── titleColor, iconTint, titleTextStyle
```

### Typography

**Available functions:** `RegularTextStyle()` (400), `MediumTextStyle()` (500), `SemiBoldTextStyle()` (600), `BoldTextStyle()` (700). All use the font family from `ComponentTheme.typography.fontFamily` (default: Urbanist).

```kotlin
Text(text = "Text", style = RegularTextStyle())
Text(text = "Custom", style = BoldTextStyle().copy(fontSize = 24.sp, color = Color.Blue))
CustomButton(text = "Button", textStyle = SemiBoldTextStyle().copy(fontSize = 18.sp), onClick = { })
```

### Components (overview)

- **Actions:** `CustomButton`, `CustomIconButton`, `LabeledSwitch`
- **Dialogs:** `CustomAlertDialog`, `CustomDialog`, `CustomDialogHeader`, `CustomDialogBody`
- **Inputs:** `CustomTextField`, `SearchField`, `PickerField`, `CustomDatePicker`, `CustomDateRangePicker`, `OtpView`
- **Layout:** `CustomHeader`, `RoundedSurface`, `LabeledSlot`, `Slot`, `ChipItem`
- **Lists & selection:** `OptionItemList`, `OptionItemLazyListDialog`, `OptionItemMultiSelectLazyListDialog`, `CustomDropDownMenu`
- **Navigation:** `NavigationMenu` (horizontal, expanded, collapsed)
- **Feedback:** `RatingRow`, `ProgressIndicatorBars`, `JsonViewer`
- **Scrolling:** `PaginatedListView`, `ScrollbarWithScrollState`, `ScrollbarWithLazyListState`

### Complete UI Example

```kotlin
@Composable
fun MyApp() {
    val theme = ComponentTheme(
        typography = TypographyTheme(fontFamily = FontFamily.SansSerif),
        button = ButtonTheme(
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White,
            defaultTextStyle = SemiBoldTextStyle().copy(fontSize = 18.sp)
        ),
        alertDialog = AlertDialogTheme(
            headerColor = Color.White,
            titleColor = Color.Black,
            titleTextStyle = BoldTextStyle().copy(fontSize = 20.sp)
        )
    )

    AppTheme(componentTheme = theme) {
        Column {
            CustomButton(text = "Primary Action", onClick = { })
            CustomAlertDialog(
                isDialogVisible = showDialog,
                title = "Confirm",
                description = "Are you sure?",
                positiveButtonText = "Yes",
                negativeButtonText = "No",
                onPositiveButtonClick = { showDialog = false },
                onNegativeButtonClick = { showDialog = false },
                onDismissRequest = { showDialog = false }
            )
        }
    }
}
```

### Best Practices (UI)

1. **Define theme once** — Create `ComponentTheme` at app level.
2. **Use theme for consistency** — Override only when needed.
3. **Font hierarchy** — Titles: 20–24sp (Bold), Body: 14–16sp (Regular), Buttons: 16–18sp (SemiBold).
4. **Accessibility** — Ensure sufficient color contrast and readable font sizes.
5. **Custom fonts** — Include all weights (400, 500, 600, 700) your app uses.

---

## Utilities

**Platform & actions** — `MultiPlatformUtils` (use `Context` on Android; no-arg on iOS/Desktop/Wasm):

```kotlin
val utils = MultiPlatformUtils(context)
val config = utils.getPlatformConfig() // platform, systemLanguage, uuid, deviceName, packageVersionName
utils.copyToClipBoard("Copied text")
utils.openUrl("https://example.com")
utils.openMapsWithLocation(41.0082, 28.9784)
utils.shareText("Share this")
val (lat, lng) = utils.getCurrentLocation() ?: (0.0 to 0.0)
```

**Date/time** — Format and selectable dates:

```kotlin
val formatted = formatDateTime(localDateTime, isDaily = false)  // e.g. "20.02.2025"
val dateString = localDate.format(dotLocalDateFormat)
val selectableDates = CustomSelectableDates(...)  // for date picker constraints
```

**Image processing** — Resize and compress image bytes:

```kotlin
val processor = ImageSizeProcessor()
val compressed = processor.resizeAndCompressBytes(
    inputBytes = imageBytes,
    profile = ImageProcessingProfile.MEDIUM  // or targetMaxSizePx + quality
)
val bitmap = imageBytes.toImageBitmap()
```

**String & logging**:

```kotlin
val cleaned = StringFormatter().formatInput(input, clearNonNumeric = true)
CustomLogger.isLoggingEnabled = true
CustomLogger.log("Debug message")
```

---

## Data & Domain

**Device storage** — Persist a generated device identifier:

```kotlin
val storage = DeviceInfoStorageImpl(settings)  // Settings from russhwolf/settings
var uuid = storage.getGeneratedPlatformIdentifier()
if (uuid == null) {
    uuid = UUID.randomUUID().toString()
    storage.setGeneratedPlatformIdentifier(uuid)
}
```

**Pagination** — Map API response to domain model:

```kotlin
@Serializable
data class MyItem(val id: String, val name: String)

val response: PagedListResponse<MyItemDto> = api.getPage(page, size)
val pagedList = response.mapItems { dto -> MyItem(dto.id, dto.name) }
// pagedList.items, pagedList.hasNextPage, pagedList.totalPageCount, etc.
```

**Camera** — Capture or pick image (Android/iOS):

```kotlin
val cameraManager = rememberCameraManager(onResult = { sharedImage ->
    sharedImage?.let { image ->
        val bytes = image.toByteArray()
        val name = image.fileName
        // upload or use bytes
    }
})
// Later: cameraManager.launch()
```

---

## Permissions

Check and request runtime permissions (Android/iOS):

```kotlin
val factory = rememberPermissionsControllerFactory()
val permissionsController = factory.createPermissionsController()

// Check state
val state = permissionsController.getPermissionState(Permission.CAMERA)
if (state != PermissionState.Granted) {
    try {
        permissionsController.providePermission(Permission.CAMERA)
    } catch (e: DeniedAlwaysException) {
        permissionsController.openAppSettings()
    } catch (e: RequestCanceledException) {
        // User canceled
    }
}

val hasLocation = permissionsController.isPermissionGranted(Permission.LOCATION)
```

---

## Pagination

Load paged data with `PaginatedFlowLoader` and show in `PaginatedListView`:

```kotlin
val loader = remember(scope, pageSize) {
    PaginatedFlowLoader(
        initialKey = 0,
        scope = scope,
        pageSize = 20,
        pageSource = { page, size ->
            val response = api.fetchPage(page, size)
            PageResult(items = response.items, hasNextPage = response.hasNextPage)
        },
        getNextKey = { page, _ -> page + 1 }
    )
}
LaunchedEffect(Unit) { loader.reset() }

val state by loader.state.collectAsState()
PaginatedListView(
    state = state,
    onScrollReachNextPageThreshold = { loader.loadNextPage() },
    onSwipeAtListsEnd = { loader.reset() },
    itemSlot = { item -> ItemRow(item) }
)

// Refresh or new filters
loader.updatePageSource(newKey, newPageSource, reload = true)
```

---

## Sample App

The **sample** app in `sample/composeApp` demonstrates theming, main UI components, and multiplatform run targets. Open the project in Android Studio and run the `composeApp` configuration for Android, iOS, Desktop, or Wasm.

---

## Building

```bash
git clone https://github.com/Deveng-Group/deveng-core-kmp.git
cd deveng-core-kmp
```

Open in Android Studio or build from the command line:

```bash
./gradlew :deveng-core:build
./gradlew :sample:composeApp:assembleDebug   # Android sample
```

---

## License

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0). See the [LICENSE](LICENSE) file for details.
