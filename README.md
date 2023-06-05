# GPTReflectionLibrary
This project is supposed to help you write reflection code without worrying about all the strings connected to it.

The syntax is very simple and easy to use. Examples can be found in the [tests](https://github.com/Skyslycer/GPTReflectionLibrary/blob/master/src/test/java/de/skyslycer/gptreflection/tests/ReflectionHelperTest.java).

## Usage

Latest version: 

<a href="https://repo.skyslycer.de/#/releases/de/skyslycer/GPTReflectionLibrary">
    <img alt="version" src="https://img.shields.io/maven-metadata/v?color=orange&label=version&metadataUrl=https%3A%2F%2Frepo.skyslycer.de%2Freleases%2Fde%2Fskyslycer%2FGPTReflectionLibrary%2Fmaven-metadata.xml&style=for-the-badge"/>
</a>

### Gradle Kotlin

```kotlin
repositories {
    maven("https://repo.skyslycer.de/releases/")
}

dependencies {
    compile("de.skyslycer:GPTReflectionLibrary:VERSION")
}
```

### Gradle Groovy

```groovy
repositories {
    maven {
        url "https://repo.skyslycer.de/releases/"
    }
}

dependencies {
    compile "de.skyslycer:GPTReflectionLibrary:VERSION"
}
```

### Maven

```xml
<repositories>
    <repository>
        <name>SkyslycerRepo</name>
        <url>https://repo.skyslycer.de/releases/</url>
    </repository>
</repositories>
```
```xml
<dependencies>
        <dependency>
        <groupId>de.skyslycer</groupId>
        <artifactId>GPTReflectionLibrary</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

## Experiment
I don't have a personal use for this, but it was an experiment if ChatGPT, using their latest
language model **GPT-4**, can create a library that's actually useful to use.

From the actual library code, tests and workflows and gradle buildscripts, it wrote everything (except this, I ran out of messages).
Obviously not everything worked on the first try and I had to prompt many things, but it resolved all of them
on its own.

I'm really happy that it got this far. 

### Conclusion
ChatGPT and other LLM's won't replace us developers any time soon. What it will do is increase our working speed
and act as an over-powered Google. Once it gets access to the web and the latest information, 
it will become our go-to search engine and dramatically increase our knowledge and working speed.