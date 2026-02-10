# Android Components Library

An Android component library for rapid prototyping, with LLM-friendly JSON specs and an MCP server for Claude integration. Components are production-quality, themed via Android-native patterns (custom XML attributes, TypedArray, ContextThemeWrapper), and showcased in a Jetpack Compose gallery app.

Mirrors the iOS `ios-components` architecture, adapted for Android idioms.

**GitHub:** https://github.com/evgenyshkuratov-rgb/android-components

## Design System Rules (MANDATORY)

These rules are non-negotiable. Every component and screen must follow them.

### Icons -- ONLY from icons-library

- 280 icons + Frisbee logo loaded via `DSIcon.named(context, "icon-name", sizeDp)` from SVG assets
- Colored/branded icons (logos): use `DSIcon.coloredNamed(context, "name", heightDp)` -- preserves SVG fill colors, maintains aspect ratio
- **NEVER** use Android vector drawables, Material icons, `@drawable/*`, or any other icon source
- **NEVER** use Unicode characters as icon substitutes (arrows, chevrons, etc.)
- All icons rendered at runtime from SVG path data via `DSIcon` + `SVGPathParser`
- In Compose screens: convert `BitmapDrawable` to `ImageBitmap` via `asImageBitmap()` with `ColorFilter.tint()` (or no tint for colored icons)

### Colors -- ONLY from icons-library color tokens

- 157 tokens, 5 brands, Light/Dark via `DSColors` and `DSBrand`
- **NEVER** hardcode hex colors in components or screens
- **NEVER** use Android system colors (`Color.BLACK`, Material theme colors for component styling)

| Token | Light | Dark |
|-------|-------|------|
| `backgroundBase` | #FFFFFF | per-brand |
| `backgroundSecond` | #F5F5F5 | per-brand |
| `basicColor08` | #14000000 | #14FFFFFF |
| `basicColor50` | #80000000 | #80FFFFFF |
| `basicColor90` | #E6000000 | #E6FFFFFF |

### Typography -- ONLY Roboto

- 37 styles from `DSTypography`, Roboto variable font bundled in `res/font/`
- **NEVER** use system fonts, other typefaces, or custom sizes outside DS tokens
- In Compose: use `DSTypography.<style>.toComposeTextStyle()` extension from `DSTypographyCompose.kt`
- Shared `RobotoFamily` and `RobotoMonoFamily` defined in `DSTypographyCompose.kt` -- **NEVER** redeclare per-screen

### Spacing

Standardized tokens from `DSSpacing`:

| Token | Value |
|-------|-------|
| `horizontalPadding` | 16dp |
| `verticalSection` | 24dp |
| `listItemSpacing` | 12dp |
| `chipGap` | 8dp |
| `innerCardPadding` | 16dp |

### Corner Radii

| Element | Radius |
|---------|--------|
| Card / Button | 16dp |
| Input field | 12dp |
| Capsule (chips) | height / 2 |
| Circle (avatar) | size / 2 |

## Project Structure

```
android-components/
├── components/                          # Android library module (Kotlin + XML)
│   ├── src/main/
│   │   ├── java/com/example/components/
│   │   │   ├── chips/
│   │   │   │   ├── ChipsView.kt        # Custom View with XML attrs
│   │   │   │   └── ChipsColorScheme.kt # Color scheme data class
│   │   │   └── designsystem/
│   │   │       ├── DSColors.kt          # Color tokens (5 brands, light/dark)
│   │   │       ├── DSTypography.kt      # 37 Roboto text styles
│   │   │       ├── DSSpacing.kt         # Spacing & corner radius tokens
│   │   │       ├── DSIcon.kt            # SVG icon loading from assets
│   │   │       ├── DSBrand.kt           # 5 brand definitions with accent colors
│   │   │       └── SVGPathParser.kt     # SVG path data parser
│   │   ├── res/
│   │   │   ├── values/attrs.xml         # Custom XML attributes for all components
│   │   │   ├── values/ds_colors.xml     # Default brand color values (light)
│   │   │   ├── values-night/ds_colors.xml # Dark mode overrides
│   │   │   ├── font/roboto.ttf          # Roboto variable font
│   │   │   ├── font/roboto_mono.ttf     # Roboto Mono variable font
│   │   │   └── layout/view_chips.xml    # ChipsView internal layout
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── app/                                 # Gallery app (Jetpack Compose)
│   ├── src/main/
│   │   ├── java/com/example/gallery/
│   │   │   ├── MainActivity.kt          # Single activity, Compose NavHost, theme state
│   │   │   ├── catalog/
│   │   │   │   └── CatalogScreen.kt     # Component catalog with search + theme toggle
│   │   │   ├── preview/
│   │   │   │   └── ChipsViewPreviewScreen.kt # Interactive preview
│   │   │   └── theme/
│   │   │       ├── GalleryTheme.kt      # Compose theme wrapping DS tokens
│   │   │       └── DSTypographyCompose.kt # DSTextStyle → Compose TextStyle bridge
│   │   ├── assets/
│   │   │   ├── icons/                   # 280 SVG icons + frisbee-logo.svg
│   │   │   └── design-system-counts.json # Component/icon/color counts with timestamps
│   │   └── res/
│   └── build.gradle.kts
│
├── specs/                               # JSON specs for MCP
│   ├── index.json                       # Component index (name + description)
│   └── components/
│       └── ChipsView.json               # Full component specification
│
├── mcp-server/                          # MCP server (Node.js)
│   ├── package.json
│   └── index.js
│
├── context.md                           # This file
├── build.gradle.kts                     # Root build config
├── settings.gradle.kts
└── gradle.properties
```

## Available Components

### ChipsView

A filter chip component with 3 states and 2 sizes, using Android-native theming.

**States:**

| State | Description |
|-------|-------------|
| `DEFAULT` | Gray background, optional icon + text |
| `ACTIVE` | Accent (brand) background, icon + text |
| `AVATAR` | Gray background, circular avatar + name + close button |

**Sizes:**

| Size | Height | Avatar Diameter |
|------|--------|-----------------|
| `SMALL` | 32dp | 24dp |
| `MEDIUM` | 40dp | 32dp |

**Architecture:**

- Custom XML attributes declared in `res/values/attrs.xml` (chipState, chipSize, chipText, chipIcon, chipAvatarImage, color scheme attrs)
- Programmatic API via `configure()` and `configureAvatar()` methods
- Theming through `ChipsColorScheme` data class -- colors flow from `DSBrand.chipsColorScheme(isDark)`
- Layout inflated from `res/layout/view_chips.xml` using `<merge>` tag
- Capsule shape via `GradientDrawable` with cornerRadius = height/2

**Programmatic Usage:**

```kotlin
val chip = ChipsView(context)
chip.configure(
    text = "Filter option",
    icon = DSIcon.named(context, "user-2", 20f),
    state = ChipsView.ChipState.ACTIVE,
    size = ChipsView.ChipSize.MEDIUM,
    colorScheme = brand.chipsColorScheme(isDark)
)
chip.onTap = { /* handle tap */ }
```

## Brand Theming

Five brands with distinct accent colors and dark-mode background variations:

| Brand | Accent (Light) | Accent (Dark) | Dark BG Base | Dark BG Second |
|-------|---------------|---------------|-------------|----------------|
| Frisbee | #40B259 | #40B259 | #1A1A1A | #313131 |
| TDM | #3E87DD | #3886E1 | #1A1A1A | #313131 |
| Sover | #C7964F | #C4944D | #101D2E | #1C2838 |
| KCHAT | #EA5355 | #E9474E | #1A1A1A | #313131 |
| Sense New | #7548AD | #7548AD | #161419 | #2A282E |

Runtime brand switching uses `DSBrand.chipsColorScheme(isDark)` to generate the color scheme for each component.

## Adding a New Component

1. **Create component class** in `components/src/main/java/com/example/components/<name>/`
   - Extend `View`, `LinearLayout`, or `FrameLayout`
   - Define custom XML attributes in `res/values/attrs.xml`
   - Read attributes via `TypedArray` in constructor
   - Create a color scheme data class for theming
   - Add `configure()` methods for programmatic use

2. **Add brand theming** in `DSBrand.kt`
   - Add a `<name>ColorScheme(isDark: Boolean)` method

3. **Create JSON spec** in `specs/components/<Name>.json`
   - Include name, description, import path, properties, XML usage, Kotlin usage, and tags

4. **Update index** in `specs/index.json`
   - Add entry with name and description

5. **Add preview screen** in `app/` module
   - Create `<Name>PreviewScreen.kt` with live component + control panels

6. **Update gallery catalog** to include the new component card

## JSON Spec Format

**specs/index.json** -- lightweight component list:
```json
{
  "version": "1.0.0",
  "platform": "android",
  "components": [
    { "name": "ComponentName", "description": "Brief description" }
  ]
}
```

**specs/components/<Name>.json** -- full specification:
```json
{
  "name": "ComponentName",
  "description": "Detailed description with states, sizes, and use cases.",
  "import": "com.example.components.<package>.<ClassName>",
  "properties": [
    { "name": "propName", "type": "Type", "description": "What it does" }
  ],
  "xmlUsage": "<XML example/>",
  "kotlinUsage": "Kotlin example code",
  "tags": ["keyword1", "keyword2"]
}
```

## MCP Server

Node.js server using `@modelcontextprotocol/sdk`, fetching JSON specs from GitHub raw content.

| Tool | Description | Parameters |
|------|-------------|------------|
| `list_components` | Returns all components with name and description | None |
| `get_component` | Returns full spec for one component | `name` (string) |
| `search_components` | Keyword search across name, description, tags | `query` (string) |
| `check_updates` | Git diff showing new/modified components since last pull | None |

**Setup:**
```bash
cd mcp-server && npm install
```

**Claude Code config** (`.claude/settings.json` or `claude_desktop_config.json`):
```json
{
  "mcpServers": {
    "android-components": {
      "command": "node",
      "args": ["/path/to/android-components/mcp-server/index.js"]
    }
  }
}
```

## Gallery App

A Jetpack Compose application that showcases all components with interactive controls.

### Catalog Screen
- **Header row**: Frisbee logo (44dp, colored via `DSIcon.coloredNamed`) + Light/Dark segmented control
- **Theme toggle**: switches entire app theme via `MainActivity` state + `WindowCompat.getInsetsController()` for status bar icons
- Title "Components Library" with `DSTypography.title1B`
- Status line: dynamic counts with relative timestamps from `design-system-counts.json` (e.g., "1 Component (3d) · 280 Icons (2h) · 157 Colors (1d)")
- Search bar with `search` icon from icons-library, `DSTypography.body1R`
- Component cards with `DSTypography.subtitle1M` name, `DSTypography.subhead2R` description, `arrow-right-s` chevron
- Press animation (scale 0.95)
- All spacing uses `DSSpacing` tokens, all corner radii use `DSCornerRadius`

### Preview Screens
- **Back button**: `back` icon from icons-library (never Unicode)
- **Title**: component name with `DSTypography.title5B`
- **Brand selector**: segmented control (Frisbee, TDM, Sover, KCHAT, Sense New) using `DSTypography.subhead4M`/`subhead2R`
- **Preview container**: rounded card with brand-colored background, live component via `AndroidView`
- **Controls**: dropdown selectors for State and Size (`select-down` icon, `DSTypography.body1R`), labels with `DSTypography.subhead4M`, segmented control for Theme (Light/Dark)

### Navigation
- Single-activity architecture with Compose `NavHost`
- Routes: `catalog` and `preview/{componentId}`

## Build Instructions

**Prerequisites:** Android SDK (compileSdk 34, minSdk 26), JDK 17, Node.js (for MCP server).

```bash
# Build the full project
./gradlew assembleDebug

# Build only the components library
./gradlew :components:assembleDebug

# Install MCP server dependencies
cd mcp-server && npm install

# Install on device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Tech Stack:** Kotlin 1.9.22, AGP 8.2.2, Jetpack Compose (BOM 2024.02.00), ViewBinding, Material 3.

## For LLMs

- **Icons**: always use `DSIcon.named(context, "name", sizeDp)` for monochrome icons, `DSIcon.coloredNamed(context, "name", heightDp)` for branded/colored icons -- check `list_icons` MCP tool for available names
- **Colors**: always use `DSBrand` and `DSColors` -- check `list_colors` MCP tool for available tokens
- **Typography in Compose**: always use `DSTypography.<style>.toComposeTextStyle()` -- never create inline `TextStyle(fontFamily=..., fontSize=...)` definitions
- **Typography in Views**: use `DSTypography.<style>.apply(textView)` for full-fidelity styling
- **Spacing/Radii**: always use `DSSpacing.*` and `DSCornerRadius.*` tokens -- never hardcode dp values for standard spacing
- **New components**: follow the 6-step checklist above -- create View class, color scheme, JSON spec, preview screen
- **Testing**: build with `./gradlew assembleDebug`, install with `adb install`
- **Naming**: PascalCase for components (`ChipsView`), camelCase for properties, kebab-case for icon names

## Related Repos

- **[icons-library](https://github.com/evgenyshkuratov-rgb/icons-library)** -- Single source of truth for icons (280 SVGs, 6 categories) and color tokens (157 tokens, 5 brands, Light/Dark). Has its own MCP server with `list_icons`, `list_colors`, `get_icon`, `check_updates` tools.
- **[ios-components](https://github.com/evgenyshkuratov-rgb/ios-components)** -- iOS UIKit counterpart of this library, same design system and component specs.
