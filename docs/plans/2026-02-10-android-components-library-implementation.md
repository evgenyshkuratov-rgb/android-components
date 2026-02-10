# Android Components Library — Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build an Android component library with ChipsView, design system, Compose gallery app, and MCP server — mirroring the iOS ios-land-component architecture.

**Architecture:** Multi-module Gradle project with a `components` library module (Kotlin + XML + ViewBinding) and an `app` gallery module (Jetpack Compose). Design system tokens live in the library. MCP server is a Node.js sidecar fetching JSON specs from GitHub.

**Tech Stack:** Kotlin 1.9.22, AGP 8.2.2, compileSdk 34, minSdk 26, Jetpack Compose (BOM 2024.02.00), ViewBinding, Material 3, Node.js MCP server.

---

### Task 1: Scaffold Gradle Project

**Files:**
- Create: `build.gradle.kts` (root)
- Create: `settings.gradle.kts`
- Create: `gradle.properties`
- Create: `gradle/wrapper/gradle-wrapper.properties`
- Create: `.gitignore`

**Step 1: Create root build.gradle.kts**

```kotlin
// build.gradle.kts
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
```

**Step 2: Create settings.gradle.kts**

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidComponentsGallery"
include(":components")
include(":app")
```

**Step 3: Create gradle.properties**

```properties
# gradle.properties
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

**Step 4: Create gradle wrapper properties**

```properties
# gradle/wrapper/gradle-wrapper.properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

**Step 5: Create .gitignore**

```
.gradle/
build/
*.iml
.idea/
local.properties
captures/
.externalNativeBuild/
.cxx/
*.apk
*.aab
*.ap_
*.dex
node_modules/
mcp-server/node_modules/
```

**Step 6: Initialize Gradle wrapper**

Run: `cd "/Users/evgeny.shkuratov/Clode code projects/android components gallery" && gradle wrapper --gradle-version 8.5`

If `gradle` CLI is not available, copy `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar` from the context-menu-android project.

**Step 7: Initialize git repo**

Run: `cd "/Users/evgeny.shkuratov/Clode code projects/android components gallery" && git init`

**Step 8: Commit**

```bash
git add build.gradle.kts settings.gradle.kts gradle.properties .gitignore gradle/
git commit -m "feat: scaffold Gradle project structure"
```

---

### Task 2: Create Components Library Module

**Files:**
- Create: `components/build.gradle.kts`
- Create: `components/src/main/AndroidManifest.xml`
- Create: `components/src/main/res/values/attrs.xml`

**Step 1: Create components/build.gradle.kts**

```kotlin
// components/build.gradle.kts
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.components"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}
```

**Step 2: Create components/src/main/AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
```

**Step 3: Create attrs.xml for ChipsView custom attributes**

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- components/src/main/res/values/attrs.xml -->
<resources>
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
        <attr name="chipCloseIcon" format="reference"/>
    </declare-styleable>

    <!-- Theme attributes for ChipsView theming -->
    <attr name="chipsBackgroundDefault" format="color"/>
    <attr name="chipsBackgroundActive" format="color"/>
    <attr name="chipsTextPrimary" format="color"/>
    <attr name="chipsCloseIconTint" format="color"/>
</resources>
```

**Step 4: Verify build**

Run: `./gradlew :components:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 5: Commit**

```bash
git add components/
git commit -m "feat: add components library module with ChipsView attrs"
```

---

### Task 3: Design System — Spacing, Colors, Typography

**Files:**
- Create: `components/src/main/java/com/example/components/designsystem/DSSpacing.kt`
- Create: `components/src/main/java/com/example/components/designsystem/DSColors.kt`
- Create: `components/src/main/java/com/example/components/designsystem/DSTypography.kt`
- Create: `components/src/main/res/values/ds_colors.xml`
- Create: `components/src/main/res/values-night/ds_colors.xml`
- Create: `components/src/main/res/font/roboto.ttf` (copy from iOS project)
- Create: `components/src/main/res/font/roboto_mono.ttf` (copy from iOS project)

**Step 1: Create DSSpacing.kt**

```kotlin
// components/src/main/java/com/example/components/designsystem/DSSpacing.kt
package com.example.components.designsystem

object DSSpacing {
    const val horizontalPadding = 16    // dp
    const val verticalSection = 24      // dp
    const val listItemSpacing = 12      // dp
    const val chipGap = 8              // dp
    const val innerCardPadding = 16     // dp
}

object DSCornerRadius {
    const val card = 16f                // dp
    const val inputField = 12f          // dp
    fun capsule(height: Float) = height / 2f
    fun circle(size: Float) = size / 2f
}
```

**Step 2: Create ds_colors.xml (light mode — Frisbee default)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- components/src/main/res/values/ds_colors.xml -->
<resources>
    <!-- Background -->
    <color name="ds_background_base">#FFFFFF</color>
    <color name="ds_background_second">#F5F5F5</color>
    <color name="ds_background_sheet">#FFFFFF</color>

    <!-- Text -->
    <color name="ds_text_primary">#000000</color>
    <color name="ds_text_primary_90">#E6000000</color>
    <color name="ds_text_secondary">#80000000</color>
    <color name="ds_text_tertiary">#4D000000</color>

    <!-- Basic Colors (opacity variants of black) -->
    <color name="ds_basic_100">#000000</color>
    <color name="ds_basic_90">#E6000000</color>
    <color name="ds_basic_80">#CC000000</color>
    <color name="ds_basic_55">#8C000000</color>
    <color name="ds_basic_50">#80000000</color>
    <color name="ds_basic_40">#66000000</color>
    <color name="ds_basic_30">#4D000000</color>
    <color name="ds_basic_10">#1A000000</color>
    <color name="ds_basic_08">#14000000</color>
    <color name="ds_basic_06">#0F000000</color>

    <!-- Separator & Chip -->
    <color name="ds_separator">#1A000000</color>
    <color name="ds_chip_background">#14000000</color>
    <color name="ds_subtle_background">#0F000000</color>

    <!-- System -->
    <color name="ds_success">#40B259</color>
    <color name="ds_danger">#E06141</color>
    <color name="ds_warning">#DC9B1C</color>
    <color name="ds_info">#509EF9</color>

    <!-- White -->
    <color name="ds_white_100">#FFFFFF</color>

    <!-- Badge -->
    <color name="ds_badge_muted">#C9C9C9</color>

    <!-- Brand Accent Colors (Frisbee default) -->
    <color name="ds_accent_frisbee">#40B259</color>
    <color name="ds_accent_tdm">#3E87DD</color>
    <color name="ds_accent_sover">#C7964F</color>
    <color name="ds_accent_kchat">#EA5355</color>
    <color name="ds_accent_sense_new">#7548AD</color>

    <!-- Brand Background Overrides -->
    <color name="ds_bg_base_sover">#FFFFFF</color>
    <color name="ds_bg_base_sense_new">#FFFFFF</color>
    <color name="ds_bg_second_sover">#F5F5F5</color>
    <color name="ds_bg_second_sense_new">#F5F5F5</color>
</resources>
```

**Step 3: Create ds_colors.xml (dark mode)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- components/src/main/res/values-night/ds_colors.xml -->
<resources>
    <!-- Background -->
    <color name="ds_background_base">#1A1A1A</color>
    <color name="ds_background_second">#313131</color>
    <color name="ds_background_sheet">#232325</color>

    <!-- Text -->
    <color name="ds_text_primary">#FFFFFF</color>
    <color name="ds_text_primary_90">#E6FFFFFF</color>
    <color name="ds_text_secondary">#80FFFFFF</color>
    <color name="ds_text_tertiary">#4DFFFFFF</color>

    <!-- Basic Colors (opacity variants of white) -->
    <color name="ds_basic_100">#FFFFFF</color>
    <color name="ds_basic_90">#E6FFFFFF</color>
    <color name="ds_basic_80">#CCFFFFFF</color>
    <color name="ds_basic_55">#8CFFFFFF</color>
    <color name="ds_basic_50">#80FFFFFF</color>
    <color name="ds_basic_40">#66FFFFFF</color>
    <color name="ds_basic_30">#4DFFFFFF</color>
    <color name="ds_basic_10">#1AFFFFFF</color>
    <color name="ds_basic_08">#14FFFFFF</color>
    <color name="ds_basic_06">#0FFFFFFF</color>

    <!-- Separator & Chip -->
    <color name="ds_separator">#1AFFFFFF</color>
    <color name="ds_chip_background">#14FFFFFF</color>
    <color name="ds_subtle_background">#0FFFFFFF</color>

    <!-- System (same in both modes) -->
    <color name="ds_success">#40B259</color>
    <color name="ds_danger">#E06141</color>
    <color name="ds_warning">#DC9B1C</color>
    <color name="ds_info">#509EF9</color>

    <!-- White -->
    <color name="ds_white_100">#FFFFFF</color>

    <!-- Badge -->
    <color name="ds_badge_muted">#484848</color>

    <!-- Brand Accent Colors (dark variants) -->
    <color name="ds_accent_frisbee">#40B259</color>
    <color name="ds_accent_tdm">#3886E1</color>
    <color name="ds_accent_sover">#C4944D</color>
    <color name="ds_accent_kchat">#E9474E</color>
    <color name="ds_accent_sense_new">#7548AD</color>

    <!-- Brand Background Overrides (dark) -->
    <color name="ds_bg_base_sover">#101D2E</color>
    <color name="ds_bg_base_sense_new">#161419</color>
    <color name="ds_bg_second_sover">#1C2838</color>
    <color name="ds_bg_second_sense_new">#2A282E</color>
</resources>
```

**Step 4: Create DSColors.kt**

```kotlin
// components/src/main/java/com/example/components/designsystem/DSColors.kt
package com.example.components.designsystem

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.components.R

object DSColors {
    fun backgroundBase(context: Context) = ContextCompat.getColor(context, R.color.ds_background_base)
    fun backgroundSecond(context: Context) = ContextCompat.getColor(context, R.color.ds_background_second)
    fun backgroundSheet(context: Context) = ContextCompat.getColor(context, R.color.ds_background_sheet)

    fun textPrimary(context: Context) = ContextCompat.getColor(context, R.color.ds_text_primary)
    fun textPrimary90(context: Context) = ContextCompat.getColor(context, R.color.ds_text_primary_90)
    fun textSecondary(context: Context) = ContextCompat.getColor(context, R.color.ds_text_secondary)
    fun textTertiary(context: Context) = ContextCompat.getColor(context, R.color.ds_text_tertiary)

    fun basic100(context: Context) = ContextCompat.getColor(context, R.color.ds_basic_100)
    fun basic90(context: Context) = ContextCompat.getColor(context, R.color.ds_basic_90)
    fun basic50(context: Context) = ContextCompat.getColor(context, R.color.ds_basic_50)
    fun basic08(context: Context) = ContextCompat.getColor(context, R.color.ds_basic_08)

    fun separator(context: Context) = ContextCompat.getColor(context, R.color.ds_separator)
    fun chipBackground(context: Context) = ContextCompat.getColor(context, R.color.ds_chip_background)

    fun success(context: Context) = ContextCompat.getColor(context, R.color.ds_success)
    fun danger(context: Context) = ContextCompat.getColor(context, R.color.ds_danger)
    fun warning(context: Context) = ContextCompat.getColor(context, R.color.ds_warning)

    fun white100(context: Context) = ContextCompat.getColor(context, R.color.ds_white_100)
    fun badgeMuted(context: Context) = ContextCompat.getColor(context, R.color.ds_badge_muted)
}
```

**Step 5: Copy Roboto font files**

Run:
```bash
mkdir -p "/Users/evgeny.shkuratov/Clode code projects/android components gallery/components/src/main/res/font"
cp "/Users/evgeny.shkuratov/Clode code projects/ios-land-component/GalleryApp/GalleryApp/Resources/Fonts/Roboto.ttf" "/Users/evgeny.shkuratov/Clode code projects/android components gallery/components/src/main/res/font/roboto.ttf"
cp "/Users/evgeny.shkuratov/Clode code projects/ios-land-component/GalleryApp/GalleryApp/Resources/Fonts/RobotoMono.ttf" "/Users/evgeny.shkuratov/Clode code projects/android components gallery/components/src/main/res/font/roboto_mono.ttf"
```

Note: Android resource file names must be lowercase with underscores only. If the TTF files are variable fonts, Android may need them as `.ttf` in `res/font/`. If they fail to compile (variable fonts not supported in `res/font/` on older AGP), place them in `assets/fonts/` instead and load with `Typeface.createFromAsset()`.

**Step 6: Create DSTypography.kt**

```kotlin
// components/src/main/java/com/example/components/designsystem/DSTypography.kt
package com.example.components.designsystem

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.components.R

data class DSTextStyle(
    val sizeSp: Float,
    val weight: Int,        // Typeface.NORMAL, Typeface.BOLD, or custom weight
    val lineHeightSp: Float,
    val letterSpacingSp: Float = 0f,
    val isMono: Boolean = false
) {
    fun apply(textView: TextView) {
        textView.textSize = sizeSp
        textView.setLineSpacing(0f, lineHeightSp / sizeSp)
        if (letterSpacingSp != 0f) {
            textView.letterSpacing = letterSpacingSp / sizeSp
        }
        val typeface = getTypeface(textView.context)
        if (typeface != null) {
            textView.typeface = Typeface.create(typeface, weight, false)
        }
    }

    fun getTypeface(context: Context): Typeface? {
        return try {
            val resId = if (isMono) R.font.roboto_mono else R.font.roboto
            ResourcesCompat.getFont(context, resId)
        } catch (e: Exception) {
            null // Falls back to system default
        }
    }
}

object DSTypography {
    // Titles
    val title1B = DSTextStyle(sizeSp = 32f, weight = 700, lineHeightSp = 40f, letterSpacingSp = 0.11f)
    val title2B = DSTextStyle(sizeSp = 28f, weight = 700, lineHeightSp = 32f)
    val title3B = DSTextStyle(sizeSp = 24f, weight = 700, lineHeightSp = 32f)
    val title4R = DSTextStyle(sizeSp = 24f, weight = 400, lineHeightSp = 32f)
    val title5B = DSTextStyle(sizeSp = 20f, weight = 700, lineHeightSp = 28f)
    val title6M = DSTextStyle(sizeSp = 20f, weight = 500, lineHeightSp = 28f)
    val title7R = DSTextStyle(sizeSp = 20f, weight = 400, lineHeightSp = 28f)

    // Subtitles
    val subtitle1M = DSTextStyle(sizeSp = 18f, weight = 500, lineHeightSp = 24f)
    val subtitle2R = DSTextStyle(sizeSp = 18f, weight = 400, lineHeightSp = 24f)

    // Body
    val body1R = DSTextStyle(sizeSp = 16f, weight = 400, lineHeightSp = 20f)
    val body2B = DSTextStyle(sizeSp = 16f, weight = 700, lineHeightSp = 22f, letterSpacingSp = 0.32f)
    val body3M = DSTextStyle(sizeSp = 16f, weight = 500, lineHeightSp = 22f)
    val body4M = DSTextStyle(sizeSp = 14f, weight = 500, lineHeightSp = 16f)
    val body5R = DSTextStyle(sizeSp = 14f, weight = 400, lineHeightSp = 16f)

    // Subheads
    val subhead1B = DSTextStyle(sizeSp = 14f, weight = 700, lineHeightSp = 20f)
    val subhead2R = DSTextStyle(sizeSp = 14f, weight = 400, lineHeightSp = 20f)
    val subhead3R = DSTextStyle(sizeSp = 13f, weight = 400, lineHeightSp = 16f)
    val subhead4M = DSTextStyle(sizeSp = 14f, weight = 500, lineHeightSp = 20f)

    // Captions
    val caption1B = DSTextStyle(sizeSp = 12f, weight = 700, lineHeightSp = 16f)
    val caption2R = DSTextStyle(sizeSp = 12f, weight = 400, lineHeightSp = 14f)
    val caption3M = DSTextStyle(sizeSp = 11f, weight = 500, lineHeightSp = 14f)
    val subcaptionR = DSTextStyle(sizeSp = 11f, weight = 400, lineHeightSp = 13f)

    // Bubble (Chat Messages)
    val bubbleR13 = DSTextStyle(sizeSp = 13f, weight = 400, lineHeightSp = 16f)
    val bubbleR14 = DSTextStyle(sizeSp = 14f, weight = 400, lineHeightSp = 18f)
    val bubbleR15 = DSTextStyle(sizeSp = 15f, weight = 400, lineHeightSp = 20f)
    val bubbleR16 = DSTextStyle(sizeSp = 16f, weight = 400, lineHeightSp = 22f)
    val bubbleR18 = DSTextStyle(sizeSp = 18f, weight = 400, lineHeightSp = 24f)
    val bubbleR20 = DSTextStyle(sizeSp = 20f, weight = 400, lineHeightSp = 24f)
    val bubbleR22 = DSTextStyle(sizeSp = 24f, weight = 400, lineHeightSp = 30f)
    val bubbleM13 = DSTextStyle(sizeSp = 13f, weight = 500, lineHeightSp = 16f)

    // Bubble Mono
    val bubbleMonoR13 = DSTextStyle(sizeSp = 13f, weight = 400, lineHeightSp = 16f, isMono = true)
    val bubbleMonoR14 = DSTextStyle(sizeSp = 14f, weight = 400, lineHeightSp = 18f, isMono = true)
    val bubbleMonoR15 = DSTextStyle(sizeSp = 15f, weight = 400, lineHeightSp = 20f, isMono = true)
    val bubbleMonoR16 = DSTextStyle(sizeSp = 16f, weight = 400, lineHeightSp = 22f, isMono = true)
    val bubbleMonoR18 = DSTextStyle(sizeSp = 18f, weight = 400, lineHeightSp = 24f, isMono = true)
    val bubbleMonoR20 = DSTextStyle(sizeSp = 20f, weight = 400, lineHeightSp = 24f, isMono = true)
    val bubbleMonoR22 = DSTextStyle(sizeSp = 24f, weight = 400, lineHeightSp = 30f, isMono = true)
}
```

**Step 7: Verify build**

Run: `./gradlew :components:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 8: Commit**

```bash
git add components/src/main/java/com/example/components/designsystem/
git add components/src/main/res/values/ds_colors.xml
git add components/src/main/res/values-night/ds_colors.xml
git add components/src/main/res/font/
git commit -m "feat: add design system tokens (colors, typography, spacing)"
```

---

### Task 4: Design System — DSBrand & DSIcon

**Files:**
- Create: `components/src/main/java/com/example/components/designsystem/DSBrand.kt`
- Create: `components/src/main/java/com/example/components/designsystem/DSIcon.kt`
- Create: `components/src/main/java/com/example/components/designsystem/SVGPathParser.kt`

**Step 1: Create DSBrand.kt**

```kotlin
// components/src/main/java/com/example/components/designsystem/DSBrand.kt
package com.example.components.designsystem

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import com.example.components.R
import com.example.components.chips.ChipsColorScheme

enum class DSBrand(val displayName: String) {
    FRISBEE("Frisbee"),
    TDM("TDM"),
    SOVER("Sover"),
    KCHAT("KCHAT"),
    SENSE_NEW("Sense New");

    fun accentColor(isDark: Boolean): Int {
        return when (this) {
            FRISBEE -> Color.parseColor("#40B259")
            TDM -> Color.parseColor(if (isDark) "#3886E1" else "#3E87DD")
            SOVER -> Color.parseColor(if (isDark) "#C4944D" else "#C7964F")
            KCHAT -> Color.parseColor(if (isDark) "#E9474E" else "#EA5355")
            SENSE_NEW -> Color.parseColor("#7548AD")
        }
    }

    fun backgroundBase(isDark: Boolean): Int {
        if (!isDark) return Color.parseColor("#FFFFFF")
        return when (this) {
            SOVER -> Color.parseColor("#101D2E")
            SENSE_NEW -> Color.parseColor("#161419")
            else -> Color.parseColor("#1A1A1A")
        }
    }

    fun backgroundSecond(isDark: Boolean): Int {
        if (!isDark) return Color.parseColor("#F5F5F5")
        return when (this) {
            SOVER -> Color.parseColor("#1C2838")
            SENSE_NEW -> Color.parseColor("#2A282E")
            else -> Color.parseColor("#313131")
        }
    }

    fun basicColor08(isDark: Boolean): Int {
        return Color.parseColor(if (isDark) "#14FFFFFF" else "#00000014")
    }

    fun basicColor90(isDark: Boolean): Int {
        return Color.parseColor(if (isDark) "#E6FFFFFF" else "#000000E6")
    }

    fun basicColor50(isDark: Boolean): Int {
        return Color.parseColor(if (isDark) "#80FFFFFF" else "#00000080")
    }

    fun chipsColorScheme(isDark: Boolean): ChipsColorScheme {
        return ChipsColorScheme(
            backgroundDefault = basicColor08(isDark),
            backgroundActive = accentColor(isDark),
            textPrimary = basicColor90(isDark),
            closeIconTint = basicColor50(isDark)
        )
    }

    companion object {
        fun isDarkMode(context: Context): Boolean {
            return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
    }
}
```

**Step 2: Create SVGPathParser.kt**

```kotlin
// components/src/main/java/com/example/components/designsystem/SVGPathParser.kt
package com.example.components.designsystem

import android.graphics.Path
import android.graphics.PointF

object SVGPathParser {

    fun parse(d: String, fillRule: Path.FillType = Path.FillType.WINDING): Path {
        val path = Path()
        path.fillType = fillRule
        val tokens = tokenize(d)
        var i = 0
        var currentCommand = 'M'
        val currentPoint = PointF(0f, 0f)

        while (i < tokens.size) {
            val token = tokens[i]
            if (token.length == 1 && token[0].isLetter()) {
                currentCommand = token[0]
                i++
            }

            when (currentCommand) {
                'M' -> {
                    val x = number(tokens, i); i++
                    val y = number(tokens, i); i++
                    path.moveTo(x, y)
                    currentPoint.set(x, y)
                    currentCommand = 'L'
                }
                'm' -> {
                    val dx = number(tokens, i); i++
                    val dy = number(tokens, i); i++
                    path.rMoveTo(dx, dy)
                    currentPoint.offset(dx, dy)
                    currentCommand = 'l'
                }
                'L' -> {
                    val x = number(tokens, i); i++
                    val y = number(tokens, i); i++
                    path.lineTo(x, y)
                    currentPoint.set(x, y)
                }
                'l' -> {
                    val dx = number(tokens, i); i++
                    val dy = number(tokens, i); i++
                    path.rLineTo(dx, dy)
                    currentPoint.offset(dx, dy)
                }
                'H' -> {
                    val x = number(tokens, i); i++
                    path.lineTo(x, currentPoint.y)
                    currentPoint.x = x
                }
                'h' -> {
                    val dx = number(tokens, i); i++
                    path.rLineTo(dx, 0f)
                    currentPoint.x += dx
                }
                'V' -> {
                    val y = number(tokens, i); i++
                    path.lineTo(currentPoint.x, y)
                    currentPoint.y = y
                }
                'v' -> {
                    val dy = number(tokens, i); i++
                    path.rLineTo(0f, dy)
                    currentPoint.y += dy
                }
                'C' -> {
                    val x1 = number(tokens, i); i++
                    val y1 = number(tokens, i); i++
                    val x2 = number(tokens, i); i++
                    val y2 = number(tokens, i); i++
                    val x = number(tokens, i); i++
                    val y = number(tokens, i); i++
                    path.cubicTo(x1, y1, x2, y2, x, y)
                    currentPoint.set(x, y)
                }
                'c' -> {
                    val x1 = number(tokens, i); i++
                    val y1 = number(tokens, i); i++
                    val x2 = number(tokens, i); i++
                    val y2 = number(tokens, i); i++
                    val dx = number(tokens, i); i++
                    val dy = number(tokens, i); i++
                    path.rCubicTo(x1, y1, x2, y2, dx, dy)
                    currentPoint.offset(dx, dy)
                }
                'Z', 'z' -> {
                    path.close()
                }
                else -> i++
            }
        }
        return path
    }

    private fun tokenize(d: String): List<String> {
        val tokens = mutableListOf<String>()
        val current = StringBuilder()

        for (ch in d) {
            when {
                ch.isLetter() -> {
                    if (current.isNotEmpty()) { tokens.add(current.toString()); current.clear() }
                    tokens.add(ch.toString())
                }
                ch == ',' || ch == ' ' || ch == '\n' || ch == '\t' -> {
                    if (current.isNotEmpty()) { tokens.add(current.toString()); current.clear() }
                }
                ch == '-' && current.isNotEmpty() && current.last() != 'e' && current.last() != 'E' -> {
                    tokens.add(current.toString()); current.clear()
                    current.append(ch)
                }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) tokens.add(current.toString())
        return tokens
    }

    private fun number(tokens: List<String>, index: Int): Float {
        return if (index < tokens.size) tokens[index].toFloatOrNull() ?: 0f else 0f
    }
}
```

**Step 3: Create DSIcon.kt**

```kotlin
// components/src/main/java/com/example/components/designsystem/DSIcon.kt
package com.example.components.designsystem

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.InputStream

object DSIcon {

    /**
     * Load an SVG icon from assets/icons/ directory by name.
     * Returns a tintable Drawable (rendered as template).
     */
    fun named(context: Context, name: String, sizeDp: Float = 24f): Drawable? {
        val svgString = loadSvgFromAssets(context, "icons/$name.svg") ?: return null
        val density = context.resources.displayMetrics.density
        val sizePx = (sizeDp * density).toInt()

        val viewBox = parseViewBox(svgString) ?: RectF(0f, 0f, 24f, 24f)
        val paths = parsePaths(svgString)

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scaleX = sizePx / viewBox.width()
        val scaleY = sizePx / viewBox.height()
        canvas.translate(-viewBox.left * scaleX, -viewBox.top * scaleY)
        canvas.scale(scaleX, scaleY)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL

        for (path in paths) {
            canvas.drawPath(path, paint)
        }

        return BitmapDrawable(context.resources, bitmap)
    }

    /**
     * Load an SVG icon with its original fill colors (non-tintable).
     */
    fun coloredNamed(context: Context, name: String, heightDp: Float): Drawable? {
        val svgString = loadSvgFromAssets(context, "icons/$name.svg") ?: return null
        val density = context.resources.displayMetrics.density
        val heightPx = (heightDp * density).toInt()

        val viewBox = parseViewBox(svgString) ?: RectF(0f, 0f, 24f, 24f)
        val coloredPaths = parseColoredPaths(svgString)

        val aspectRatio = viewBox.width() / viewBox.height()
        val widthPx = (heightPx * aspectRatio).toInt()

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scaleX = widthPx.toFloat() / viewBox.width()
        val scaleY = heightPx.toFloat() / viewBox.height()
        canvas.translate(-viewBox.left * scaleX, -viewBox.top * scaleY)
        canvas.scale(scaleX, scaleY)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL

        for ((path, color) in coloredPaths) {
            paint.color = color
            canvas.drawPath(path, paint)
        }

        return BitmapDrawable(context.resources, bitmap)
    }

    // --- SVG Parsing ---

    private fun loadSvgFromAssets(context: Context, path: String): String? {
        return try {
            context.assets.open(path).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseViewBox(svg: String): RectF? {
        val regex = Regex("""viewBox="([^"]+)"""")
        val match = regex.find(svg) ?: return null
        val parts = match.groupValues[1].split(" ").mapNotNull { it.toFloatOrNull() }
        if (parts.size != 4) return null
        return RectF(parts[0], parts[1], parts[0] + parts[2], parts[1] + parts[3])
    }

    private fun parsePaths(svg: String): List<Path> {
        val results = mutableListOf<Path>()
        val pathRegex = Regex("""<path[^>]*/>|<path[^>]*>""")

        for (match in pathRegex.findAll(svg)) {
            val tag = match.value
            val d = extractAttribute("d", tag) ?: continue
            val fillRule = if (extractAttribute("fill-rule", tag) == "evenodd")
                Path.FillType.EVEN_ODD else Path.FillType.WINDING
            results.add(SVGPathParser.parse(d, fillRule))
        }
        return results
    }

    private fun parseColoredPaths(svg: String): List<Pair<Path, Int>> {
        val results = mutableListOf<Pair<Path, Int>>()
        val pathRegex = Regex("""<path[^>]*/>|<path[^>]*>""")

        for (match in pathRegex.findAll(svg)) {
            val tag = match.value
            val d = extractAttribute("d", tag) ?: continue
            val fillRule = if (extractAttribute("fill-rule", tag) == "evenodd")
                Path.FillType.EVEN_ODD else Path.FillType.WINDING
            val path = SVGPathParser.parse(d, fillRule)

            val hex = extractAttribute("fill", tag)
            val color = if (hex != null && hex != "none") {
                try { Color.parseColor(hex) } catch (e: Exception) { Color.BLACK }
            } else Color.BLACK

            results.add(path to color)
        }
        return results
    }

    private fun extractAttribute(name: String, tag: String): String? {
        val regex = Regex("""$name="([^"]+)"""")
        return regex.find(tag)?.groupValues?.get(1)
    }
}
```

**Step 4: Verify build**

Run: `./gradlew :components:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 5: Commit**

```bash
git add components/src/main/java/com/example/components/designsystem/DSBrand.kt
git add components/src/main/java/com/example/components/designsystem/DSIcon.kt
git add components/src/main/java/com/example/components/designsystem/SVGPathParser.kt
git commit -m "feat: add DSBrand, DSIcon SVG renderer, and SVGPathParser"
```

---

### Task 5: ChipsView Component

**Files:**
- Create: `components/src/main/java/com/example/components/chips/ChipsColorScheme.kt`
- Create: `components/src/main/java/com/example/components/chips/ChipsView.kt`
- Create: `components/src/main/res/layout/view_chips.xml`

**Step 1: Create ChipsColorScheme.kt**

```kotlin
// components/src/main/java/com/example/components/chips/ChipsColorScheme.kt
package com.example.components.chips

import android.graphics.Color

data class ChipsColorScheme(
    val backgroundDefault: Int,
    val backgroundActive: Int,
    val textPrimary: Int,
    val closeIconTint: Int
) {
    companion object {
        val DEFAULT = ChipsColorScheme(
            backgroundDefault = Color.parseColor("#14000000"),
            backgroundActive = Color.parseColor("#40B259"),
            textPrimary = Color.parseColor("#E6000000"),
            closeIconTint = Color.parseColor("#80000000")
        )
    }
}
```

**Step 2: Create view_chips.xml layout**

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- components/src/main/res/layout/view_chips.xml -->
<merge xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    tools:parentTag="android.widget.LinearLayout">

    <ImageView
        android:id="@+id/avatarImageView"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:scaleType="centerCrop"
        android:visibility="gone"
        tools:visibility="visible" />

    <ImageView
        android:id="@+id/iconImageView"
        android:layout_width="20dp"
        android:layout_height="20dp"
        android:scaleType="centerInside"
        android:visibility="gone"
        tools:visibility="visible" />

    <TextView
        android:id="@+id/textLabel"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:maxWidth="224dp"
        android:ellipsize="end"
        android:maxLines="1"
        tools:text="Filter option" />

    <ImageView
        android:id="@+id/closeButton"
        android:layout_width="36dp"
        android:layout_height="36dp"
        android:scaleType="center"
        android:visibility="gone"
        android:background="?attr/selectableItemBackgroundBorderless"
        tools:visibility="visible" />

</merge>
```

**Step 3: Create ChipsView.kt**

```kotlin
// components/src/main/java/com/example/components/chips/ChipsView.kt
package com.example.components.chips

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.components.R

class ChipsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    enum class ChipState { DEFAULT, ACTIVE, AVATAR }
    enum class ChipSize(val heightDp: Int, val avatarDp: Int) {
        SMALL(32, 24),
        MEDIUM(40, 32)
    }

    // Public callbacks
    var onTap: (() -> Unit)? = null
    var onClose: (() -> Unit)? = null

    // Internal state
    private var currentState: ChipState = ChipState.DEFAULT
    private var currentSize: ChipSize = ChipSize.SMALL
    private var colorScheme: ChipsColorScheme = ChipsColorScheme.DEFAULT

    // Views
    private val avatarImageView: ImageView
    private val iconImageView: ImageView
    private val textLabel: TextView
    private val closeButton: ImageView

    private val density = context.resources.displayMetrics.density

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        LayoutInflater.from(context).inflate(R.layout.view_chips, this, true)

        avatarImageView = findViewById(R.id.avatarImageView)
        iconImageView = findViewById(R.id.iconImageView)
        textLabel = findViewById(R.id.textLabel)
        closeButton = findViewById(R.id.closeButton)

        closeButton.setOnClickListener { onClose?.invoke() }
        setOnClickListener { onTap?.invoke() }

        // Read XML attributes
        if (attrs != null) {
            val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ChipsView)
            try {
                val stateInt = ta.getInt(R.styleable.ChipsView_chipState, 0)
                currentState = ChipState.entries[stateInt]

                val sizeInt = ta.getInt(R.styleable.ChipsView_chipSize, 0)
                currentSize = ChipSize.entries[sizeInt]

                val text = ta.getString(R.styleable.ChipsView_chipText)
                if (text != null) textLabel.text = text

                val iconDrawable = ta.getDrawable(R.styleable.ChipsView_chipIcon)
                if (iconDrawable != null) {
                    iconImageView.setImageDrawable(iconDrawable)
                    iconImageView.visibility = VISIBLE
                }

                // Read theme attrs for color scheme
                val bgDefault = ta.getColor(R.styleable.ChipsView_chipsBackgroundDefault, colorScheme.backgroundDefault)
                val bgActive = ta.getColor(R.styleable.ChipsView_chipsBackgroundActive, colorScheme.backgroundActive)
                val textPrimary = ta.getColor(R.styleable.ChipsView_chipsTextPrimary, colorScheme.textPrimary)
                val closeTint = ta.getColor(R.styleable.ChipsView_chipsCloseIconTint, colorScheme.closeIconTint)
                colorScheme = ChipsColorScheme(bgDefault, bgActive, textPrimary, closeTint)
            } finally {
                ta.recycle()
            }
        }

        updateAppearance()
    }

    // --- Public API ---

    fun configure(
        text: String,
        icon: Drawable? = null,
        state: ChipState = ChipState.DEFAULT,
        size: ChipSize = ChipSize.SMALL,
        colorScheme: ChipsColorScheme = ChipsColorScheme.DEFAULT
    ) {
        currentState = state
        currentSize = size
        this.colorScheme = colorScheme

        textLabel.text = text
        iconImageView.setImageDrawable(icon)
        iconImageView.visibility = if (icon != null) VISIBLE else GONE

        avatarImageView.visibility = GONE
        closeButton.visibility = GONE

        updateAppearance()
    }

    fun configureAvatar(
        name: String,
        avatarImage: Bitmap? = null,
        closeIcon: Drawable? = null,
        size: ChipSize = ChipSize.SMALL,
        colorScheme: ChipsColorScheme = ChipsColorScheme.DEFAULT
    ) {
        currentState = ChipState.AVATAR
        currentSize = size
        this.colorScheme = colorScheme

        textLabel.text = name

        avatarImageView.setImageBitmap(avatarImage)
        avatarImageView.visibility = VISIBLE

        val avatarSizePx = dpToPx(size.avatarDp)
        avatarImageView.layoutParams = LayoutParams(avatarSizePx, avatarSizePx)

        // Make avatar circular
        avatarImageView.outlineProvider = android.view.ViewOutlineProvider.BACKGROUND
        avatarImageView.clipToOutline = true
        val avatarBg = GradientDrawable()
        avatarBg.shape = GradientDrawable.OVAL
        avatarImageView.background = avatarBg

        iconImageView.visibility = GONE

        closeButton.visibility = VISIBLE
        if (closeIcon != null) {
            closeButton.setImageDrawable(closeIcon)
        }

        updateAppearance()
    }

    // --- Private ---

    private fun updateAppearance() {
        val heightPx = dpToPx(currentSize.heightDp)

        // Set fixed height
        layoutParams = layoutParams?.also {
            it.height = heightPx
        } ?: LayoutParams(LayoutParams.WRAP_CONTENT, heightPx)

        // Capsule background
        val bgDrawable = GradientDrawable()
        bgDrawable.cornerRadius = heightPx / 2f

        // Font
        val robotoTypeface = try {
            ResourcesCompat.getFont(context, R.font.roboto)
        } catch (e: Exception) {
            Typeface.DEFAULT
        }

        when (currentState) {
            ChipState.DEFAULT -> {
                bgDrawable.setColor(colorScheme.backgroundDefault)
                textLabel.setTextColor(colorScheme.textPrimary)
                textLabel.typeface = Typeface.create(robotoTypeface, 500, false)
                textLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                iconImageView.setColorFilter(colorScheme.textPrimary)
            }
            ChipState.ACTIVE -> {
                bgDrawable.setColor(colorScheme.backgroundActive)
                textLabel.setTextColor(colorScheme.textPrimary)
                textLabel.typeface = Typeface.create(robotoTypeface, 500, false)
                textLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                iconImageView.setColorFilter(colorScheme.textPrimary)
            }
            ChipState.AVATAR -> {
                bgDrawable.setColor(colorScheme.backgroundDefault)
                textLabel.setTextColor(colorScheme.textPrimary)
                textLabel.typeface = Typeface.create(robotoTypeface, 400, false)
                textLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                textLabel.letterSpacing = 0.25f / 14f
                closeButton.setColorFilter(colorScheme.closeIconTint)
            }
        }

        background = bgDrawable
        updatePadding()
    }

    private fun updatePadding() {
        when (currentState) {
            ChipState.DEFAULT, ChipState.ACTIVE -> {
                val leadPad = dpToPx(if (currentSize == ChipSize.SMALL) 8 else 12)
                val trailPad = dpToPx(12)
                setPadding(leadPad, 0, trailPad, 0)

                // Uniform 8dp gap
                (iconImageView.layoutParams as? MarginLayoutParams)?.marginEnd = dpToPx(8)
                (textLabel.layoutParams as? MarginLayoutParams)?.marginEnd = 0

                // Standard close button size
                closeButton.layoutParams = LayoutParams(dpToPx(36), dpToPx(36))
            }
            ChipState.AVATAR -> {
                val leadPad = dpToPx(4)
                setPadding(leadPad, 0, 0, 0)

                // 8dp between avatar and name
                (avatarImageView.layoutParams as? MarginLayoutParams)?.marginEnd = dpToPx(8)
                // No gap before close button
                (textLabel.layoutParams as? MarginLayoutParams)?.marginEnd = 0

                // Close button fits within chip
                val closeSizePx = dpToPx(currentSize.heightDp) - dpToPx(8)
                closeButton.layoutParams = LayoutParams(closeSizePx, closeSizePx)
            }
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * density).toInt()
}
```

**Step 4: Verify build**

Run: `./gradlew :components:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 5: Commit**

```bash
git add components/src/main/java/com/example/components/chips/
git add components/src/main/res/layout/view_chips.xml
git commit -m "feat: add ChipsView component with 3 states and 2 sizes"
```

---

### Task 6: Gallery App Module — Compose Setup

**Files:**
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/example/gallery/MainActivity.kt`
- Create: `app/src/main/java/com/example/gallery/theme/GalleryTheme.kt`
- Create: `app/src/main/res/values/themes.xml`
- Create: `app/src/main/res/values-night/themes.xml`
- Create: `app/src/main/res/values/strings.xml`

**Step 1: Create app/build.gradle.kts**

```kotlin
// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.gallery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gallery"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    implementation(project(":components"))

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Compose Activity & Navigation
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // AndroidView interop (included in compose-ui)
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}
```

**Step 2: Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- app/src/main/AndroidManifest.xml -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Gallery">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

**Step 3: Create themes.xml (light)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- app/src/main/res/values/themes.xml -->
<resources>
    <style name="Theme.Gallery" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="android:statusBarColor">@color/ds_background_base</item>
        <item name="android:windowLightStatusBar">true</item>
        <item name="android:navigationBarColor">@color/ds_background_base</item>
        <item name="android:windowLightNavigationBar">true</item>
        <item name="colorPrimary">@color/ds_success</item>
        <item name="colorSurface">@color/ds_background_base</item>
        <item name="colorOnSurface">@color/ds_text_primary</item>
    </style>
</resources>
```

**Step 4: Create themes.xml (dark)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- app/src/main/res/values-night/themes.xml -->
<resources>
    <style name="Theme.Gallery" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="android:statusBarColor">@color/ds_background_base</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:navigationBarColor">@color/ds_background_base</item>
        <item name="android:windowLightNavigationBar">false</item>
        <item name="colorPrimary">@color/ds_success</item>
        <item name="colorSurface">@color/ds_background_base</item>
        <item name="colorOnSurface">@color/ds_text_primary</item>
    </style>
</resources>
```

**Step 5: Create strings.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- app/src/main/res/values/strings.xml -->
<resources>
    <string name="app_name">Components Gallery</string>
</resources>
```

**Step 6: Create GalleryTheme.kt**

```kotlin
// app/src/main/java/com/example/gallery/theme/GalleryTheme.kt
package com.example.gallery.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.components.designsystem.DSColors

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF40B259),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surfaceVariant = Color(0xFFF5F5F5),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF40B259),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFFFFFFF),
    background = Color(0xFF1A1A1A),
    onBackground = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF313131),
)

@Composable
fun GalleryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
```

**Step 7: Create MainActivity.kt (minimal, navigation added in Task 8)**

```kotlin
// app/src/main/java/com/example/gallery/MainActivity.kt
package com.example.gallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gallery.catalog.CatalogScreen
import com.example.gallery.preview.ChipsViewPreviewScreen
import com.example.gallery.theme.GalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GalleryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "catalog") {
                        composable("catalog") {
                            CatalogScreen(
                                onComponentClick = { componentId ->
                                    navController.navigate("preview/$componentId")
                                }
                            )
                        }
                        composable("preview/{componentId}") { backStackEntry ->
                            val componentId = backStackEntry.arguments?.getString("componentId") ?: ""
                            ChipsViewPreviewScreen(
                                componentId = componentId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
```

**Step 8: Create placeholder CatalogScreen.kt**

```kotlin
// app/src/main/java/com/example/gallery/catalog/CatalogScreen.kt
package com.example.gallery.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CatalogScreen(onComponentClick: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Catalog — placeholder")
    }
}
```

**Step 9: Create placeholder ChipsViewPreviewScreen.kt**

```kotlin
// app/src/main/java/com/example/gallery/preview/ChipsViewPreviewScreen.kt
package com.example.gallery.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ChipsViewPreviewScreen(componentId: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Preview: $componentId — placeholder")
    }
}
```

**Step 10: Copy default mipmap launcher icons**

Run: Copy default ic_launcher PNGs from any Android project template or generate them. Minimal approach — create a simple placeholder:
```bash
mkdir -p app/src/main/res/mipmap-hdpi app/src/main/res/mipmap-mdpi app/src/main/res/mipmap-xhdpi app/src/main/res/mipmap-xxhdpi app/src/main/res/mipmap-xxxhdpi
```
Copy mipmap resources from `context-menu-android` project:
```bash
SRC="/Users/evgeny.shkuratov/Clode code projects/context-menu-android/app/src/main/res"
DEST="/Users/evgeny.shkuratov/Clode code projects/android components gallery/app/src/main/res"
for density in mipmap-hdpi mipmap-mdpi mipmap-xhdpi mipmap-xxhdpi mipmap-xxxhdpi; do
    cp -r "$SRC/$density/"* "$DEST/$density/" 2>/dev/null || true
done
```

**Step 11: Verify build**

Run: `./gradlew :app:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 12: Commit**

```bash
git add app/
git commit -m "feat: add Compose gallery app with navigation scaffold"
```

---

### Task 7: Gallery App — Catalog Screen

**Files:**
- Modify: `app/src/main/java/com/example/gallery/catalog/CatalogScreen.kt`

**Step 1: Implement full CatalogScreen**

Replace the placeholder `CatalogScreen.kt` with the full implementation:

```kotlin
// app/src/main/java/com/example/gallery/catalog/CatalogScreen.kt
package com.example.gallery.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.R as ComponentsR

private val RobotoFamily = FontFamily(
    Font(ComponentsR.font.roboto, FontWeight.Normal),
    Font(ComponentsR.font.roboto, FontWeight.Medium),
    Font(ComponentsR.font.roboto, FontWeight.Bold),
)

private data class ComponentEntry(
    val id: String,
    val name: String,
    val description: String
)

private val components = listOf(
    ComponentEntry("ChipsView", "ChipsView", "Filter chips with Default, Active, and Avatar states")
)

@Composable
fun CatalogScreen(onComponentClick: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredComponents = remember(searchQuery) {
        if (searchQuery.isBlank()) components
        else components.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        item { Spacer(modifier = Modifier.height(28.dp).statusBarsPadding()) }

        // Title
        item {
            Text(
                text = "Components Library",
                style = TextStyle(
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    letterSpacing = 0.11.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Status line
        item {
            Text(
                text = "1 Component \u00B7 276 Icons \u00B7 157 Colors",
                style = TextStyle(
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Search bar
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(28.dp)) }

        // Component cards
        items(filteredComponents) { component ->
            ComponentCard(
                name = component.name,
                description = component.description,
                onClick = { onComponentClick(component.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = RobotoFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search components\u2026",
                        style = TextStyle(
                            fontFamily = RobotoFamily,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier
    )
}

@Composable
private fun ComponentCard(
    name: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.95f else 1f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = TextStyle(
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                maxLines = 2
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "\u203A",
            style = TextStyle(fontSize = 24.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}
```

**Step 2: Verify build**

Run: `./gradlew :app:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add app/src/main/java/com/example/gallery/catalog/CatalogScreen.kt
git commit -m "feat: implement catalog screen with search and component cards"
```

---

### Task 8: Gallery App — ChipsView Preview Screen

**Files:**
- Modify: `app/src/main/java/com/example/gallery/preview/ChipsViewPreviewScreen.kt`

**Step 1: Implement full ChipsViewPreviewScreen**

Replace placeholder with full implementation:

```kotlin
// app/src/main/java/com/example/gallery/preview/ChipsViewPreviewScreen.kt
package com.example.gallery.preview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.components.chips.ChipsView
import com.example.components.designsystem.DSBrand
import com.example.components.designsystem.DSIcon
import com.example.components.R as ComponentsR

private val RobotoFamily = FontFamily(
    Font(ComponentsR.font.roboto, FontWeight.Normal),
    Font(ComponentsR.font.roboto, FontWeight.Medium),
    Font(ComponentsR.font.roboto, FontWeight.Bold),
)

@Composable
fun ChipsViewPreviewScreen(componentId: String, onBack: () -> Unit) {
    val context = LocalContext.current

    var selectedState by remember { mutableIntStateOf(0) }
    var selectedSize by remember { mutableIntStateOf(0) }
    var selectedBrand by remember { mutableIntStateOf(0) }
    var selectedTheme by remember { mutableIntStateOf(0) } // 0=Light, 1=Dark

    val chipState = ChipsView.ChipState.entries[selectedState]
    val chipSize = ChipsView.ChipSize.entries[selectedSize]
    val brand = DSBrand.entries[selectedBrand]
    val isDark = selectedTheme == 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Back button + title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "\u2190",
                style = TextStyle(fontSize = 24.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable { onBack() }.padding(end = 12.dp)
            )
            Text(
                text = componentId,
                style = TextStyle(
                    fontFamily = RobotoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Brand selector
        ControlRow(label = "Brand") {
            SegmentedControl(
                options = DSBrand.entries.map { it.displayName },
                selectedIndex = selectedBrand,
                onSelect = { selectedBrand = it }
            )
        }

        // Preview container
        val bgColor = Color(brand.backgroundSecond(isDark))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            // Key forces recomposition on state change
            key(selectedState, selectedSize, selectedBrand, selectedTheme) {
                AndroidView(
                    factory = { ctx ->
                        val chip = ChipsView(ctx)
                        val colorScheme = brand.chipsColorScheme(isDark)

                        when (chipState) {
                            ChipsView.ChipState.DEFAULT, ChipsView.ChipState.ACTIVE -> {
                                val icon = DSIcon.named(ctx, "user-2", 20f)
                                chip.configure(
                                    text = "Filter option",
                                    icon = icon,
                                    state = chipState,
                                    size = chipSize,
                                    colorScheme = colorScheme
                                )
                            }
                            ChipsView.ChipState.AVATAR -> {
                                val closeIcon = DSIcon.named(ctx, "close-s", 24f)
                                val avatar = createPlaceholderAvatar(ctx, chipSize.avatarDp, brand, isDark)
                                chip.configureAvatar(
                                    name = "\u0418\u043C\u044F",
                                    avatarImage = avatar,
                                    closeIcon = closeIcon,
                                    size = chipSize,
                                    colorScheme = colorScheme
                                )
                            }
                        }
                        chip
                    },
                    modifier = Modifier.wrapContentSize()
                )
            }
        }

        // Controls
        ControlRow(label = "State") {
            SegmentedControl(
                options = listOf("Default", "Active", "Avatar"),
                selectedIndex = selectedState,
                onSelect = { selectedState = it }
            )
        }

        ControlRow(label = "Size") {
            SegmentedControl(
                options = listOf("32dp", "40dp"),
                selectedIndex = selectedSize,
                onSelect = { selectedSize = it }
            )
        }

        ControlRow(label = "Theme") {
            SegmentedControl(
                options = listOf("Light", "Dark"),
                selectedIndex = selectedTheme,
                onSelect = { selectedTheme = it }
            )
        }
    }
}

@Composable
private fun ControlRow(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = RobotoFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

@Composable
private fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.surface
                        else Color.Transparent
                    )
                    .clickable { onSelect(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = TextStyle(
                        fontFamily = RobotoFamily,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private fun createPlaceholderAvatar(
    context: android.content.Context,
    sizeDp: Int,
    brand: DSBrand,
    isDark: Boolean
): Bitmap {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    bgPaint.color = brand.backgroundSecond(isDark)
    canvas.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, bgPaint)

    val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    iconPaint.color = brand.basicColor50(isDark)
    iconPaint.textSize = sizePx * 0.4f
    iconPaint.textAlign = Paint.Align.CENTER
    canvas.drawText("\u263A", sizePx / 2f, sizePx / 2f + sizePx * 0.15f, iconPaint)

    return bitmap
}
```

**Step 2: Verify build**

Run: `./gradlew :app:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add app/src/main/java/com/example/gallery/preview/ChipsViewPreviewScreen.kt
git commit -m "feat: implement interactive ChipsView preview with state/size/theme/brand controls"
```

---

### Task 9: MCP Server

**Files:**
- Create: `mcp-server/package.json`
- Create: `mcp-server/index.js`

**Step 1: Create package.json**

```json
{
  "name": "@evgenyshkuratov-rgb/android-components-mcp",
  "version": "1.0.0",
  "description": "MCP server for Android components library - provides component specs to Claude",
  "type": "module",
  "bin": {
    "android-components-mcp": "./index.js"
  },
  "files": [
    "index.js"
  ],
  "dependencies": {
    "@modelcontextprotocol/sdk": "^1.0.0"
  },
  "keywords": [
    "mcp",
    "android",
    "components",
    "kotlin",
    "claude"
  ],
  "author": "evgenyshkuratov-rgb",
  "license": "MIT"
}
```

**Step 2: Create index.js**

Port the iOS MCP server, changing names and GitHub URLs for the Android repo:

```javascript
#!/usr/bin/env node

import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { execSync } from "child_process";
import { dirname, resolve } from "path";
import { fileURLToPath } from "url";

const __dirname = dirname(fileURLToPath(import.meta.url));
const REPO_ROOT = resolve(__dirname, "..");

function git(cmd) {
  return execSync(cmd, { cwd: REPO_ROOT, encoding: "utf-8", timeout: 15000 }).trim();
}

const GITHUB_BASE = "https://raw.githubusercontent.com/evgenyshkuratov-rgb/android-components/main/specs";

const server = new McpServer({
  name: "android-components",
  version: "1.0.0"
});

server.tool(
  "list_components",
  "List all available Android components with their descriptions",
  {},
  async () => {
    try {
      const res = await fetch(`${GITHUB_BASE}/index.json`);
      if (!res.ok) {
        return { content: [{ type: "text", text: `Failed to fetch component index: ${res.status} ${res.statusText}` }] };
      }
      const data = await res.json();
      return { content: [{ type: "text", text: JSON.stringify(data.components, null, 2) }] };
    } catch (error) {
      return { content: [{ type: "text", text: `Error fetching components: ${error.message}` }] };
    }
  }
);

server.tool(
  "get_component",
  "Get full specification for an Android component including properties, usage examples, and tags",
  {
    name: { type: "string", description: "Component name (e.g., ChipsView)", required: true }
  },
  async ({ name }) => {
    try {
      const res = await fetch(`${GITHUB_BASE}/components/${name}.json`);
      if (!res.ok) {
        return { content: [{ type: "text", text: `Component "${name}" not found. Use list_components to see available components.` }] };
      }
      const spec = await res.json();
      return { content: [{ type: "text", text: JSON.stringify(spec, null, 2) }] };
    } catch (error) {
      return { content: [{ type: "text", text: `Error fetching component "${name}": ${error.message}` }] };
    }
  }
);

server.tool(
  "search_components",
  "Search for Android components by keyword (matches name, description, or tags)",
  {
    query: { type: "string", description: "Search query", required: true }
  },
  async ({ query }) => {
    try {
      const res = await fetch(`${GITHUB_BASE}/index.json`);
      if (!res.ok) {
        return { content: [{ type: "text", text: `Failed to fetch component index: ${res.status} ${res.statusText}` }] };
      }
      const data = await res.json();
      const q = query.toLowerCase();
      const matches = data.components.filter(c =>
        c.name.toLowerCase().includes(q) || c.description.toLowerCase().includes(q)
      );
      if (matches.length === 0) {
        return { content: [{ type: "text", text: `No components found matching "${query}".` }] };
      }
      return { content: [{ type: "text", text: JSON.stringify(matches, null, 2) }] };
    } catch (error) {
      return { content: [{ type: "text", text: `Error searching components: ${error.message}` }] };
    }
  }
);

server.tool(
  "check_updates",
  "Check for upstream changes in the Android components library",
  {},
  async () => {
    try {
      try { git("git fetch origin main --quiet"); } catch { return { content: [{ type: "text", text: "Could not fetch from remote." }] }; }

      let commitsBehind;
      try { commitsBehind = parseInt(git("git rev-list --count main..origin/main"), 10); } catch { commitsBehind = 0; }

      if (commitsBehind === 0) {
        return { content: [{ type: "text", text: "Up to date — no new changes on remote." }] };
      }

      const newFiles = git("git diff --name-only --diff-filter=A main..origin/main").split("\n").filter(Boolean);
      const modifiedFiles = git("git diff --name-only --diff-filter=M main..origin/main").split("\n").filter(Boolean);
      const deletedFiles = git("git diff --name-only --diff-filter=D main..origin/main").split("\n").filter(Boolean);

      const newComps = newFiles.filter(f => f.startsWith("components/") || f.startsWith("specs/"));
      const modComps = modifiedFiles.filter(f => f.startsWith("components/") || f.startsWith("specs/"));
      const delComps = deletedFiles.filter(f => f.startsWith("components/") || f.startsWith("specs/"));

      const log = git('git log --format="%h %s (%an, %cr)" main..origin/main');

      const lines = [`## Android Components: ${commitsBehind} commit(s) behind remote\n`];
      if (newComps.length > 0) lines.push(`**New:** ${newComps.join(", ")}`);
      if (modComps.length > 0) lines.push(`**Modified:** ${modComps.join(", ")}`);
      if (delComps.length > 0) lines.push(`**Deleted:** ${delComps.join(", ")}`);
      if (newComps.length === 0 && modComps.length === 0 && delComps.length === 0) {
        lines.push(`**Changed:** ${[...newFiles, ...modifiedFiles, ...deletedFiles].join(", ")}`);
      }
      lines.push(`\n**Commits:**\n${log}`);

      return { content: [{ type: "text", text: lines.join("\n") }] };
    } catch (error) {
      return { content: [{ type: "text", text: `Error: ${error.message}` }] };
    }
  }
);

const transport = new StdioServerTransport();
await server.connect(transport);
```

**Step 3: Install dependencies**

Run: `cd "/Users/evgeny.shkuratov/Clode code projects/android components gallery/mcp-server" && npm install`

**Step 4: Commit**

```bash
git add mcp-server/package.json mcp-server/index.js
git commit -m "feat: add MCP server with 4 tools (list, get, search, check_updates)"
```

---

### Task 10: JSON Specs & Documentation

**Files:**
- Create: `specs/index.json`
- Create: `specs/components/ChipsView.json`
- Create: `context.md`

**Step 1: Create specs/index.json**

```json
{
  "version": "1.0.0",
  "platform": "android",
  "components": [
    {
      "name": "ChipsView",
      "description": "Filter chip with Default, Active, and Avatar states in two sizes (32dp, 40dp)"
    }
  ]
}
```

**Step 2: Create specs/components/ChipsView.json**

```json
{
  "name": "ChipsView",
  "description": "A filter chip component with multiple states (Default, Active, Avatar) and sizes (32dp, 40dp). Used for filters, tags, and user selections. Supports Android-native theming via custom XML attributes.",
  "import": "com.example.components.chips.ChipsView",
  "properties": [
    { "name": "chipText", "type": "String", "description": "The text displayed in the chip" },
    { "name": "chipIcon", "type": "Drawable?", "description": "Optional icon displayed on the left side" },
    { "name": "chipState", "type": "ChipState", "description": "Visual state: DEFAULT, ACTIVE, or AVATAR" },
    { "name": "chipSize", "type": "ChipSize", "description": "Size variant: SMALL (32dp) or MEDIUM (40dp)" },
    { "name": "chipAvatarImage", "type": "Bitmap?", "description": "User avatar image (avatar state only)" },
    { "name": "onTap", "type": "(() -> Unit)?", "description": "Callback when chip is tapped" },
    { "name": "onClose", "type": "(() -> Unit)?", "description": "Callback when close button tapped (avatar state only)" }
  ],
  "xmlUsage": "<com.example.components.chips.ChipsView\n    android:layout_width=\"wrap_content\"\n    android:layout_height=\"wrap_content\"\n    app:chipState=\"active\"\n    app:chipSize=\"medium\"\n    app:chipText=\"Filter\"\n    app:chipIcon=\"@drawable/ic_check\"/>",
  "kotlinUsage": "val chip = ChipsView(context)\nchip.configure(\n    text = \"Filter option\",\n    icon = drawable,\n    state = ChipsView.ChipState.ACTIVE,\n    size = ChipsView.ChipSize.MEDIUM,\n    colorScheme = brand.chipsColorScheme(isDark)\n)\nchip.onTap = { /* handle tap */ }",
  "tags": ["chips", "filter", "tag", "selection", "avatar", "button"]
}
```

**Step 3: Create context.md**

Write full project documentation (following the iOS context.md pattern but for Android). See the design doc for structure details. Include: project overview, design system rules, project structure, available components, MCP server tools, adding new components guide.

**Step 4: Commit**

```bash
git add specs/ context.md
git commit -m "feat: add JSON specs and project documentation"
```

---

### Task 11: Copy Icons from icons-library

**Files:**
- Create: `app/src/main/assets/icons/` (directory with SVG files)

**Step 1: Copy icon SVGs from icons-library**

Run:
```bash
mkdir -p "/Users/evgeny.shkuratov/Clode code projects/android components gallery/app/src/main/assets/icons"
cp "/Users/evgeny.shkuratov/Clode code projects/Icons library/icons/"*.svg "/Users/evgeny.shkuratov/Clode code projects/android components gallery/app/src/main/assets/icons/"
```

Note: Icons are loaded at runtime via `DSIcon.named()` which reads from `assets/icons/`. The components library itself does not bundle icons — they are provided by the consumer app.

**Step 2: Commit**

```bash
git add app/src/main/assets/icons/
git commit -m "feat: bundle icons-library SVGs in gallery app assets"
```

---

### Task 12: Final Build & Verify

**Step 1: Full build**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL for both `:components` and `:app`

**Step 2: Fix any compilation errors**

Address any issues from the build (font loading, resource references, import mismatches).

**Step 3: Install on emulator/device**

Run:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.gallery/.MainActivity
```

**Step 4: Verify visually**

- Catalog screen shows "Components Library" title and ChipsView card
- Search bar filters components
- Tapping ChipsView card navigates to preview
- Preview shows live ChipsView with state/size/brand/theme controls
- All 3 states render correctly
- Both sizes render correctly
- Brand switching changes accent colors
- Theme switching changes background/text colors

**Step 5: Final commit**

```bash
git add -A
git commit -m "feat: complete Android components library v1.0 with ChipsView"
```
