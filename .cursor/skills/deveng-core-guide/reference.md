# Deveng-Core: What Exists and When to Use It

Use this as a quick map: **situation → use this from core**. No parameter details; see the library
sources for those.

---

## MultiPlatformUtils

| Situation                                        | Use                                                       |
|--------------------------------------------------|-----------------------------------------------------------|
| Dial a phone number (or fallback copy)           | `MultiPlatformUtils.dialPhoneNumber`                      |
| Copy text to clipboard                           | `MultiPlatformUtils.copyToClipBoard`                      |
| Open maps at lat/lon                             | `MultiPlatformUtils.openMapsWithLocation`                 |
| Open a URL in browser / external app             | `MultiPlatformUtils.openUrl`                              |
| Get platform, language, device id, version, etc. | `MultiPlatformUtils.getPlatformConfig` → `PlatformConfig` |
| Get current device location (lat/lon)            | `MultiPlatformUtils.getCurrentLocation` (suspend)         |
| Share text (system share sheet)                  | `MultiPlatformUtils.shareText`                            |

**Do not:** Implement dial, clipboard, maps, URL open, share, or location with custom/platform code
in the app.

---

## PhotoSaveUtils

| Situation                                             | Use                                                                 |
|-------------------------------------------------------|---------------------------------------------------------------------|
| Save captured image bytes to a file path              | `PhotoSaveUtils.savePhoto`                                          |
| Add GPS EXIF to image bytes (e.g. before save)        | `PhotoSaveUtils.addLocationExif`                                    |
| Notify Android MediaStore so photo appears in Gallery | `PhotoSaveUtils.setApplicationContext` (call once with app context) |

**Do not:** Reimplement “write image to file” or “add location to EXIF” in the app. After camera
capture, use `PhotoSaveUtils` (and optionally `addLocationExif` then `savePhoto`).

---

## Camera

| Situation                                                     | Use                                                                                                                                                          |
|---------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Compose entry: get camera state and lifecycle                 | `rememberCameraKState`                                                                                                                                       |
| Full screen: loading / error / ready + preview + content slot | `CameraKScreen`                                                                                                                                              |
| Custom preview UI                                             | `DefaultCameraPreview` or `CameraPreview`                                                                                                                    |
| Focus indicator overlay                                       | `FocusIndicator`                                                                                                                                             |
| Build a controller with lens, flash, format, plugins, etc.    | Platform builder: `AndroidCameraControllerBuilder`, `IOSCameraControllerBuilder`, `DesktopCameraControllerBuilder` (all implement `CameraControllerBuilder`) |
| Capture image (bytes; app saves via PhotoSaveUtils)           | `CameraController.takePictureToFile` (prefer over deprecated `takePicture`)                                                                                  |
| Flash / torch, zoom, switch lens, start/stop recording        | `CameraController` methods                                                                                                                                   |
| Camera-specific permission checks/requests                    | `Permissions` (hasCameraPermission, RequestCameraPermission, etc.)                                                                                           |
| Plugins (e.g. analysis)                                       | `CameraPlugin` and builder `addPlugin`                                                                                                                       |

**Do not:** Implement a separate camera capture/preview/recording stack when these APIs are
available.

---

## Permissions

| Situation                                                           | Use                                                                                                           |
|---------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| Request any permission, check granted, get state, open app settings | `PermissionsController` (`providePermission`, `isPermissionGranted`, `getPermissionState`, `openAppSettings`) |
| Obtain controller in Compose                                        | `rememberPermissionsControllerFactory()` then `createPermissionsController()`                                 |
| Bind controller to Android activity (required for request flow)     | `BindEffect(permissionsController)`                                                                           |
| Camera permission only                                              | `Permissions.hasCameraPermission` + `RequestCameraPermission`                                                 |
| Storage permission only                                             | `Permissions.hasStoragePermission` + `RequestStoragePermission`                                               |

**Do not:** Reimplement permission request/check or “open settings” without using these.

---

## Pagination

| Situation                                                                              | Use                                                           |
|----------------------------------------------------------------------------------------|---------------------------------------------------------------|
| Load pages from a key-based source (e.g. cursor/offset), track items + loading + error | `PaginatedFlowLoader` (pageSource, getNextKey, state)         |
| UI: lazy list + load-more + pull-to-retry + empty/error messages                       | `PaginatedListView` with `PaginatedListState` from the loader |
| Page result from API                                                                   | `PageResult(items, hasNextPage)`                              |
| Server-style paged response (page, totalPageCount, etc.)                               | `PagedList` / `PagedListResponse` and `mapItems`              |

**Do not:** Reimplement infinite-scroll state and list-with-retry UI when `PaginatedFlowLoader` +
`PaginatedListView` fit.

---

## Presentation (UI components and theme)

| Situation                                     | Use                                                                                                                                                                                                                                                                                                                                                                   |
|-----------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| App-wide theme (Material, typography, colors) | `AppTheme`                                                                                                                                                                                                                                                                                                                                                            |
| Typography                                    | `CoreRegularTextStyle`, `CoreMediumTextStyle`, `CoreSemiBoldTextStyle`, `CoreBoldTextStyle`                                                                                                                                                                                                                                                                           |
| Buttons                                       | `CustomButton`, `CustomIconButton`                                                                                                                                                                                                                                                                                                                                    |
| Header with title / leading / trailing        | `CustomHeader`                                                                                                                                                                                                                                                                                                                                                        |
| Dialogs                                       | `CustomDialog`, `CustomAlertDialog`, `CustomDialogHeader`, `CustomDialogBody`                                                                                                                                                                                                                                                                                         |
| Text input                                    | `CustomTextField`; transformations: `DateTimeVisualTransformation`, `InlineSuffixTransformation`                                                                                                                                                                                                                                                                      |
| Date / range picker                           | `CustomDatePicker`, `CustomDateRangePicker`; selectable dates: `CustomSelectableDates`                                                                                                                                                                                                                                                                                |
| Single/multi choice from list                 | `OptionItemList`, `OptionItem`, `OptionItemListDialog`, `OptionItemLazyListDialog`, `OptionItemMultiSelectLazyListDialog`                                                                                                                                                                                                                                             |
| Dropdown                                      | `CustomDropDownMenu`                                                                                                                                                                                                                                                                                                                                                  |
| Chips                                         | `ChipItem`                                                                                                                                                                                                                                                                                                                                                            |
| Search field                                  | `SearchField`                                                                                                                                                                                                                                                                                                                                                         |
| Picker with label                             | `PickerField`                                                                                                                                                                                                                                                                                                                                                         |
| Switch with label                             | `LabeledSwitch`                                                                                                                                                                                                                                                                                                                                                       |
| Label + content slot                          | `LabeledSlot`                                                                                                                                                                                                                                                                                                                                                         |
| Rating UI                                     | `RatingRow`, `RatingIcon`                                                                                                                                                                                                                                                                                                                                             |
| Navigation (collapsed/expanded/horizontal)    | `NavigationMenu`, `NavigationMenuContentCollapsed`, `NavigationMenuContentExpanded`, `NavigationMenuContentHorizontal` + item composables                                                                                                                                                                                                                             |
| Scrollbar tied to scroll state                | `scrollbarWithScrollState`, `scrollbarWithLazyListState`                                                                                                                                                                                                                                                                                                              |
| Rounded surface                               | `RoundedSurface`                                                                                                                                                                                                                                                                                                                                                      |
| Progress bars                                 | `ProgressIndicatorBars`                                                                                                                                                                                                                                                                                                                                               |
| OTP input                                     | `OtpView`, `OtpDigit`; `rememberShakeOffset` for shake                                                                                                                                                                                                                                                                                                                |
| JSON display                                  | `JsonViewer`; `formatJson` for formatting                                                                                                                                                                                                                                                                                                                             |
| Swipeable card stack (Tinder-style)           | `SwipeCards`, `SwipeCardsState`, `rememberSwipeCardsState`, `SwipeCardsScope` (`items` / `itemsIndexed`, `onSwiping` / `onSwiped`); optional overlay icon buttons via `showSwipeButtons`, `negativeButtonIcon`, `positiveButtonIcon`, `revertButtonIcon` (icons only, theme via `SwipeCardsTheme`); use `state.lastSwipeDirection` with `animateBackSwipe` for revert |

Use these instead of reimplementing equivalent generic components.

---

## Utils (common)

| Situation                                          | Use                                                                                                                                                                  |
|----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Trim/normalize whitespace                          | `String.clearWhiteSpace`                                                                                                                                             |
| Strip non-numeric (optional allowlist)             | `String.clearNonNumeric`                                                                                                                                             |
| Email validation                                   | `String.isValidEmail`                                                                                                                                                |
| Combined clickable with debounce                   | `Modifier.debouncedCombinedClickable`                                                                                                                                |
| Conditional modifier                               | `Modifier.ifTrue`                                                                                                                                                    |
| Disable split motion events                        | `Modifier.disableSplitMotionEvents`                                                                                                                                  |
| Date/time formatting (daily vs full, locale-aware) | `formatDateTime`, `formatDateToString`, `formatToDayMonth`, `formatDateRange`, `getMonthAbbreviationDateFormat`, `getMonthDateFormat`, `getMonthNames`, `getDayName` |
| Date arithmetic / epoch                            | `LocalDateTime.minus`, `LocalDate.toEpochMillis`                                                                                                                     |
| Text width in Dp                                   | `calculateTextWidthAsDp`                                                                                                                                             |
| Logging                                            | `CustomLogger`                                                                                                                                                       |
| String formatting (e.g. placeholders)              | `StringFormatter`                                                                                                                                                    |

---

## Data / config

| Situation                                | Use                                           |
|------------------------------------------|-----------------------------------------------|
| Store/read device-related key-value info | `DeviceInfoStorage` / `DeviceInfoStorageImpl` |

---

When vibecoding, match the current task to a row above and use the indicated core API rather than
generating equivalent logic in the app.
