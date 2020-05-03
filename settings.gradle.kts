rootProject.name = "interference"


val atomicFuVersion: String by extra("0.14.3")

pluginManagement{
    repositories {
        gradlePluginPortal()
        jcenter()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "kotlinx-atomicfu"-> useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
                "scientifik.mpp", "scientifik.publish", "scientifik.jvm", "scientifik.js" -> useModule("scientifik:gradle-tools:${requested.version}")
                "org.openjfx.javafxplugin" -> useModule("org.openjfx:javafx-plugin:${requested.version}")
            }
        }
    }
}


//val kmathPath: String? by extra
//kmathPath?.let {
//    includeBuild(it)
//}
