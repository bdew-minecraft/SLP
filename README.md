# Scala language provider for Minecraft 1.15.2.

Branch 1.15.2

This mod adds Scala library to Minecraft 1.15.2 with Forge.
In fact, there are no binary changes between 1.14.4 branch and 1.15.2 branch.

### Usage

* For Developer - In your `build.gradle`, add below code in top level.

  ```groovy
  dependencies {
      def scala_version = getProperty("scala_version")
      def scala_major = getProperty("scala_major")
  
      // Change forge and minecraft version.
      minecraft 'net.minecraftforge:forge:1.15.2-31.1.27'
      implementation "org.scala-lang:scala-library:${scala_version}"
  
  }
  ```

  * Properties are set in your `gradle.properties` file or just hardcoded like `def scala_version = "2.13.1"`.
  * `scala_version` should be 2.13.1 because this project contains binary of Scala 2.13.1. Make sure your version matches the version this mod provides. See [this file](https://github.com/Kotori316/SLP/blob/1.15.2/gradle.properties)
  * `scala_major` must be 2.13.

  If you want to write Mod entry class in Scala, add this jar to dependency.
  And.
  * Set `modloader` in your `mods.toml` file to "kotori_scala". (`modLoader="kotori_scala"`) Loader version is like `loaderVersion="[0,)"`.
  * See [`ScalaMC.scala`](https://github.com/Kotori316/SLP/blob/1.15.2/src/main/scala/com/kotori316/scala_lib/ScalaMC.scala) and [`mods.toml`](https://github.com/Kotori316/SLP/blob/1.15.2/src/main/resources/META-INF/mods.toml) in this project.
  * **IMPORTANT** - You can get Event Bus for each mod by calling `ScalaLoadingContext.get().getModEventBus` and `IEventBus#addListener(Consumer)`, `IEventBus#register(Object)` work fine. But you can't use `@Mod.EventBusSubscriber` for inner objects because they don't have static methods in byte code. As for outer objects,  `@Mod.EventBusSubscriber` may work if you pass FORGE as parameter but **crash** if you pass MOD due to the controlled access modifier.

  Then you can change mod entry class to Scala Object.

* For Player - Download Jar file from [Curse Forge](https://www.curseforge.com/minecraft/mc-mods/scalable-cats-force) and move the file to your `mods` folder. This mod doesn't appear in mods list.

### API
* [Scala](https://www.scala-lang.org/) - [GitHub](https://github.com/scala/scala) is licenced under the [Apache License, Version 2.0](https://www.scala-lang.org/license/).
* [Cats](https://typelevel.org/cats/) - [GitHub](https://github.com/typelevel/cats) is licenced under the [Licence](https://github.com/typelevel/cats/blob/master/COPYING).
