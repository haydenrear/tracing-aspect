plugins {
    id("com.hayden.kotlin")
    id("com.hayden.apt")
    id("com.hayden.spring")
    id("com.hayden.no-main-class")
}

group = "com.hayden"
version = "1.0.0"

dependencies {

}

tasks.register("prepareKotlinBuildScriptModel")
