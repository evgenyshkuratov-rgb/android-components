# Android Components Library — Design

**Date**: 2026-02-10
**Status**: Approved

## Goal

Build an Android component library mirroring the iOS `ios-land-component` architecture. Components are production-quality, themed via Android-native patterns, documented with JSON specs for MCP integration, and showcased in a Compose gallery app.

## Key Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Location | Current directory (`android components gallery`) | User preference |
| First component | ChipsView | Parity with iOS library |
| Design system | Full icons-library integration | Same 5 brands, 157 color tokens, 276 icons |
| MCP server | Separate per-project | Keeps repos independent |
| Gallery app | Jetpack Compose | Easier to build and maintain previews |
| Component stack | Kotlin + XML + ViewBinding | Broad compatibility, matches context-menu-android |
| Theming | Custom XML attributes + TypedArray | Android-idiomatic, works naturally in XML layouts |

## Project Structure

```
android-components-gallery/
├── components/                          # Android library module
│   ├── src/main/
│   │   ├── java/com/example/components/
│   │   │   ├── chips/
│   │   │   │   └── ChipsView.kt        # Custom View with XML attrs
│   │   │   └── designsystem/
│   │   │       ├── DSColors.kt          # Color tokens (5 brands, light/dark)
│   │   │       ├── DSTypography.kt      # 37 Roboto text styles
│   │   │       ├── DSSpacing.kt         # Spacing & corner radius tokens
│   │   │       └── DSIcon.kt            # SVG icon loading from icons-library
│   │   ├── res/
│   │   │   ├── values/attrs.xml         # Custom XML attributes for all components
│   │   │   ├── values/colors.xml        # Default brand color values
│   │   │   ├── values-night/colors.xml  # Dark mode overrides
│   │   │   └── layout/                  # Component internal layouts
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── app/                                 # Gallery app (Compose)
│   ├── src/main/java/com/example/gallery/
│   │   ├── MainActivity.kt             # Single activity, Compose content
│   │   ├── catalog/
│   │   │   └── CatalogScreen.kt        # Component list screen
│   │   ├── preview/
│   │   │   └── ChipsViewPreviewScreen.kt
│   │   └── theme/
│   │       └── GalleryTheme.kt         # Compose theme wrapping DS tokens
│   ├── src/main/res/
│   │   ├── font/                        # Roboto variable TTF files
│   │   └── raw/                         # Icons from icons-library
│   └── build.gradle.kts
│
├── specs/                               # JSON specs for MCP
│   ├── index.json
│   └── components/
│       └── ChipsView.json
│
├── mcp-server/                          # MCP server (Node.js)
│   ├── package.json
│   └── index.js
│
├── scripts/
│   └── sync-design-system-counts.sh
│
├── build.gradle.kts                     # Root build config
├── settings.gradle.kts
├── gradle.properties
├── context.md
└── docs/plans/
```

## ChipsView Component

Ports the iOS ChipsView with 3 states and 2 sizes, using Android-native theming.

### States & Sizes

| State | Description |
|-------|-------------|
| `default_state` | Gray background, optional icon + text |
| `active` | Accent background, icon + text |
| `avatar` | Gray background, circular avatar + name + close button |

| Size | Height | Avatar |
|------|--------|--------|
| `small` | 32dp | 24dp |
| `medium` | 40dp | 32dp |

### Custom XML Attributes

```xml
<declare-styleable name="ChipsView">
    <attr name="chipState" format="enum">
        <enum name="default_state" value="0"/>
        <enum name="active" value="1"/>
        <enum name="avatar" value="2"/>
    </attr>
    <attr name="chipSize" format="enum">
        <enum name="small" value="0"/>
        <enum name="medium" value="1"/>
    </attr>
    <attr name="chipText" format="string"/>
    <attr name="chipIcon" format="reference"/>
    <attr name="chipAvatarImage" format="reference"/>
</declare-styleable>
```

### XML Usage

```xml
<com.example.components.chips.ChipsView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:chipState="active"
    app:chipSize="medium"
    app:chipText="Filter"
    app:chipIcon="@drawable/ic_check"/>
```

### Programmatic API

```kotlin
val chip = ChipsView(context)
chip.configure(text = "Filter", state = ChipState.ACTIVE, size = ChipSize.MEDIUM)
chip.configureAvatar(name = "John", avatarImage = bitmap, closeIcon = drawable)
chip.onTap = { /* handle tap */ }
chip.onClose = { /* handle close */ }
```

### Theming

Colors flow through Android's theme overlay system. Each brand defines color resources; the component reads them via `TypedArray` from the current theme. Runtime brand switching uses `ContextThemeWrapper`.

## Design System

### DSColors

Maps all 157 icons-library color tokens across 5 brands with light/dark support.

```kotlin
object DSColors {
    fun backgroundBase(context: Context) = ContextCompat.getColor(context, R.color.ds_background_base)
    fun textPrimary(context: Context) = ContextCompat.getColor(context, R.color.ds_text_primary)
    fun themeAccent(context: Context) = ContextCompat.getColor(context, R.color.ds_theme_accent)
}
```

Brands define separate resource sets. Runtime switching via `ContextThemeWrapper`.

### DSTypography

All 37 Roboto styles as utility functions:

```kotlin
object DSTypography {
    fun applyTitle1B(textView: TextView) { /* 32sp Bold, lineHeight 40 */ }
    fun applyBody1R(textView: TextView) { /* 16sp Regular, lineHeight 22 */ }
}
```

Roboto font files (variable TTF) bundled in `res/font/`.

### DSSpacing

```kotlin
object DSSpacing {
    const val horizontalPadding = 16    // dp
    const val verticalSection = 24
    const val listItemSpacing = 12
    const val chipGap = 8
    const val innerCardPadding = 16
}

object DSCornerRadius {
    const val card = 16f
    const val inputField = 12f
    fun capsule(height: Float) = height / 2f
}
```

### DSIcon

Loads SVG icons from the icons-library. Parses SVG path data into Android `Path` objects, renders via `Canvas` into `Bitmap`/`Drawable`. Same approach as iOS `SVGPathParser` + `DSIcon`, ported to Android graphics APIs.

## Gallery App (Compose)

### Catalog Screen

- Brand logo (rendered from icons-library SVG)
- Light/Dark theme toggle
- Title "Components Library"
- Status line: "1 Component (3d) · 276 Icons (1h) · 157 Colors (1h)"
- Search bar for filtering
- Component cards (title + description + chevron)

### Preview Screen (ChipsView)

Live component instance wrapped in `AndroidView`. Control panels as Compose UI:

1. **State**: Default / Active / Avatar
2. **Size**: 32dp / 40dp
3. **Theme**: System / Light / Dark
4. **Brand**: Frisbee / TDM / Sover / KCHAT / Sense New

Each control change recreates the component.

### Navigation

```kotlin
NavHost(navController, startDestination = "catalog") {
    composable("catalog") { CatalogScreen(navController) }
    composable("preview/{componentId}") { PreviewScreen(it, navController) }
}
```

## MCP Server

Node.js with `@modelcontextprotocol/sdk`, fetching JSON specs from GitHub.

### Tools

1. `list_components` — component index
2. `get_component` — full spec by name
3. `search_components` — keyword search
4. `check_updates` — git diff for new/modified components

### Specs Format

**index.json**: lightweight component list with name + description.
**components/ChipsView.json**: full spec including XML attributes, programmatic API, theming info, usage examples.

## Design System Rules (Mandatory)

Carried over from iOS, adapted for Android:

1. **Icons**: ONLY from icons-library (276 icons), never Android vector resources or Material icons
2. **Colors**: ONLY from icons-library color tokens (157 tokens, 5 brands, Light/Dark), never hardcoded hex
3. **Typography**: ONLY Roboto font, 37 styles from DSTypography, no custom sizes
4. **Spacing**: Standardized tokens from DSSpacing
5. **Corner Radii**: card=16dp, inputField=12dp, capsule=height/2
