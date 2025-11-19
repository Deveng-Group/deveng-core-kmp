# Deveng Core KMP

Kotlin Multiplatform library with customizable UI components and theming system.

## Quick Start

```kotlin
AppTheme {
    CustomButton(text = "Click me", onClick = { })
}
```

## Step-by-Step Theming Guide

### Step 1: Basic Usage (Default Theme)

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

### Step 2: Customize Colors

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

### Step 3: Customize Font Family

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

### Step 4: Override at Component Level

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

## Typography

### Available Functions

- `RegularTextStyle()` - Weight 400
- `MediumTextStyle()` - Weight 500
- `SemiBoldTextStyle()` - Weight 600
- `BoldTextStyle()` - Weight 700

All functions use the font family from `ComponentTheme.typography.fontFamily` (defaults to Urbanist).

### Usage

```kotlin
// Basic usage
Text(text = "Text", style = RegularTextStyle())

// Customize
Text(
    text = "Custom",
    style = BoldTextStyle().copy(
        fontSize = 24.sp,
        color = Color.Blue
    )
)

// In components
CustomButton(
    text = "Button",
    textStyle = SemiBoldTextStyle().copy(fontSize = 18.sp),
    onClick = { }
)
```

### Font Customization

Set font globally via theme:

```kotlin
ComponentTheme(
    typography = TypographyTheme(
        fontFamily = FontFamily.SansSerif // or custom FontFamily
    )
)
```

Override for specific text:

```kotlin
Text(
    text = "Custom font",
    style = RegularTextStyle().copy(
        fontFamily = FontFamily.Monospace
    )
)
```

## Complete Example

```kotlin
@Composable
fun MyApp() {
    // Step 1: Define custom theme
    val theme = ComponentTheme(
        // Custom font
        typography = TypographyTheme(
            fontFamily = FontFamily.SansSerif
        ),
        // Custom button colors
        button = ButtonTheme(
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White,
            defaultTextStyle = SemiBoldTextStyle().copy(fontSize = 18.sp)
        ),
        // Custom dialog colors
        alertDialog = AlertDialogTheme(
            headerColor = Color.White,
            titleColor = Color.Black,
            titleTextStyle = BoldTextStyle().copy(fontSize = 20.sp)
        )
    )
    
    // Step 2: Apply theme
    AppTheme(componentTheme = theme) {
        Column {
            // Step 3: Use components
            CustomButton(
                text = "Primary Action",
                onClick = { }
            )
            
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

## Theme Structure

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

## Best Practices

1. **Define theme once** - Create `ComponentTheme` at app level
2. **Use theme for consistency** - Override only when needed
3. **Font hierarchy** - Titles: 20-24sp (Bold), Body: 14-16sp (Regular), Buttons: 16-18sp (SemiBold)
4. **Accessibility** - Ensure sufficient color contrast and readable font sizes
5. **Custom fonts** - Include all weights (400, 500, 600, 700) your app uses

## License

[Add your license information here]
