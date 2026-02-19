plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val generatedAssetsDir = layout.buildDirectory.dir("generated/assets/designSystemCounts")

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

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", generatedAssetsDir)
        }
    }
}

val generateDesignSystemCounts by tasks.registering {
    description = "Pulls latest repos and generates design-system-counts.json from icons-library"

    val outputDir = generatedAssetsDir

    // Never cache — always re-run so git pull + fresh counts happen on every build
    outputs.upToDateWhen { false }
    outputs.dir(outputDir)

    doLast {
        val outDir = outputDir.get().asFile
        outDir.mkdirs()

        val compsRoot = rootProject.projectDir
        val iconsRepo = File(compsRoot.parentFile, "icons-library")

        // 1. Pull latest from both repos (silent, skip if offline)
        fun gitPull(repo: File) {
            try {
                ProcessBuilder("git", "-C", repo.absolutePath, "pull", "--ff-only")
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
            } catch (_: Exception) { /* offline — skip */ }
        }
        gitPull(iconsRepo)
        gitPull(compsRoot)

        // 2. Count assets from icons-library JSON files
        val slurper = groovy.json.JsonSlurper()
        fun jsonArrayLength(file: File, key: String): Int {
            return try {
                @Suppress("UNCHECKED_CAST")
                val map = slurper.parse(file) as Map<String, Any>
                (map[key] as? List<*>)?.size ?: 0
            } catch (_: Exception) { 0 }
        }

        val iconCount = jsonArrayLength(File(iconsRepo, "metadata.json"), "icons")
        val colorCount = jsonArrayLength(File(iconsRepo, "colors.json"), "colors")
        val componentCount = jsonArrayLength(File(compsRoot, "specs/index.json"), "components")

        // 3. Use current timestamp (last check time)
        val now = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val iconsUpdated = now
        val colorsUpdated = now
        val componentsUpdated = now

        // 4. Write bundled JSON
        val entries = mutableListOf<String>()
        entries.add("  \"icons\": $iconCount")
        entries.add("  \"colors\": $colorCount")
        entries.add("  \"components\": $componentCount")
        iconsUpdated?.let { entries.add("  \"icons_updated\": \"$it\"") }
        colorsUpdated?.let { entries.add("  \"colors_updated\": \"$it\"") }
        componentsUpdated?.let { entries.add("  \"components_updated\": \"$it\"") }
        val json = "{\n${entries.joinToString(",\n")}\n}\n"

        File(outDir, "design-system-counts.json").writeText(json)
        logger.lifecycle("design-system-counts.json: $iconCount icons, $colorCount colors, $componentCount components")
    }
}

afterEvaluate {
    tasks.matching { it.name.contains("merge") && it.name.contains("Assets") }.configureEach {
        dependsOn(generateDesignSystemCounts)
    }
}

dependencies {
    implementation(project(":components"))
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
}
