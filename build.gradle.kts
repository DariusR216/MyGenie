// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
buildscript {
    repositories {
        google()  // Make sure this is included
        jcenter()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.10")
        classpath ("com.google.gms:google-services:4.3.15")  // Make sure this version is updated

    }

}
