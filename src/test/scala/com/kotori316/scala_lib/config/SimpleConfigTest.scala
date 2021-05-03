package com.kotori316.scala_lib.config

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import scala.jdk.javaapi.CollectionConverters

object SimpleConfigTest {
  val pre =
    """
      |# Comment
      |a=true
      |b=false
      |c=45
      |e=disabled # Comment2
      |""".stripMargin.linesIterator.toSeq

  class Update {
    @Test
    def testUpdate1(): Unit = {
      val template = new ConfigImpl
      val key1 = ConfigKey.createBoolean(template, "a", defaultValue = false)
      key1.set(true)
      val update1 = ConfigFile.SimpleTextConfig.updateValue(key1, pre)
      assertLinesMatch(CollectionConverters.asJava(pre), CollectionConverters.asJava(update1))
    }

    @Test
    def testUpdate2(): Unit = {
      val template = new ConfigImpl
      val key2 = ConfigKey.createBoolean(template, "b", defaultValue = false)
      key2.set(true)

      val update2 = ConfigFile.SimpleTextConfig.updateValue(key2, pre)
      assertLinesMatch(CollectionConverters.asJava(
        """
          |# Comment
          |a=true
          |b=true
          |c=45
          |e=disabled # Comment2
          |""".stripMargin.linesIterator.toSeq), CollectionConverters.asJava(update2))
    }

    @Test
    def testUpdate3(): Unit = {
      val template = new ConfigImpl
      val key3 = ConfigKey.createInt(template, "c", 15)

      val update3 = ConfigFile.SimpleTextConfig.updateValue(key3, pre)
      assertLinesMatch(CollectionConverters.asJava(
        """
          |# Comment
          |a=true
          |b=false
          |c=15
          |e=disabled # Comment2
          |""".stripMargin.linesIterator.toSeq), CollectionConverters.asJava(update3))
    }

    @Test
    def testUpdate4(): Unit = {
      val template = new ConfigImpl
      val key4 = ConfigKey.createInt(template, "d", 80)
      val update4 = ConfigFile.SimpleTextConfig.updateValue(key4, pre)

      assertLinesMatch(CollectionConverters.asJava(
        """
          |# Comment
          |a=true
          |b=false
          |c=45
          |e=disabled # Comment2
          |d=80
          |""".stripMargin.linesIterator.toSeq), CollectionConverters.asJava(update4))
    }

    @Test
    def testUpdate5(): Unit = {
      val template = new ConfigImpl
      val key = ConfigKey.create(template, "e", "enabled")
      key.set("disabled")
      val update = ConfigFile.SimpleTextConfig.updateValue(key, pre)

      assertLinesMatch(CollectionConverters.asJava(
        """
          |# Comment
          |a=true
          |b=false
          |c=45
          |e=disabled # Comment2
          |""".stripMargin.linesIterator.toSeq), CollectionConverters.asJava(update))
    }

    @Test
    def testUpdateAll(): Unit = {
      val template = new ConfigImpl
      val keys: Seq[ConfigKey[Any]] = Seq(
        ConfigKey.createBoolean(template, "a", defaultValue = true),
        ConfigKey.createBoolean(template, "b", defaultValue = false),
        ConfigKey.createInt(template, "c", 45),
        ConfigKey.createInt(template, "d", 80),
        ConfigKey.create(template, "e", "enabled"),
      ).map(_.asInstanceOf[ConfigKey[Any]])
      val updated = keys.foldLeft("") { case (str, key) => ConfigFile.SimpleTextConfig.updateValue(key, str.linesIterator.toSeq)(key.edInstance).mkString(System.lineSeparator()) }
      assertLinesMatch(
        CollectionConverters.asJava(
          """a=true
            |b=false
            |c=45
            |d=80
            |e=enabled""".stripMargin.linesIterator.toSeq
        ), CollectionConverters.asJava(updated.linesIterator.toSeq))
    }
  }

  class Find {
    @Test
    def find1(): Unit = {
      val template = new ConfigImpl
      val key1 = ConfigKey.createBoolean(template, "a", defaultValue = false)
      val key2 = ConfigKey.createBoolean(template, "b", defaultValue = false)
      val key3 = ConfigKey.createInt(template, "c", 15)
      val key4 = ConfigKey.createInt(template, "d", 80)
      val key5 = ConfigKey.create(template, "e", "enabled")

      assertAll(
        () => assertEquals(Some(true), ConfigFile.SimpleTextConfig.findValue(key1, pre.iterator)),
        () => assertEquals(Some(false), ConfigFile.SimpleTextConfig.findValue(key2, pre.iterator)),
        () => assertEquals(Some(45), ConfigFile.SimpleTextConfig.findValue(key3, pre.iterator)),
        () => assertEquals(None, ConfigFile.SimpleTextConfig.findValue(key4, pre.iterator)),
        () => assertEquals(Some("disabled"), ConfigFile.SimpleTextConfig.findValue(key5, pre.iterator)),
      )
    }

    @ParameterizedTest
    @MethodSource(Array("com.kotori316.scala_lib.config.SimpleConfigTest#find2Arguments"))
    def find2(key: ConfigKey[_], expect: Option[_]): Unit = {
      val configText =
        """
          |; Comment
          |;a=true
          |   #b=false
          |c=45    # What?
          |#e=disabled # Comment2
          |""".stripMargin.linesIterator.toSeq
      val typedKey: ConfigKey[Any] = key.asInstanceOf[ConfigKey[Any]]

      assertEquals(expect, ConfigFile.SimpleTextConfig.findValue(typedKey, configText.iterator)(typedKey.edInstance))
    }

    //noinspection ScalaUnusedSymbol
    @ParameterizedTest
    @MethodSource(Array("com.kotori316.scala_lib.config.SimpleConfigTest#find2Arguments"))
    def find3(key: ConfigKey[_], unused: Option[_]): Unit = {
      val configText = ""
      val typedKey: ConfigKey[Any] = key.asInstanceOf[ConfigKey[Any]]
      assertEquals(None, ConfigFile.SimpleTextConfig.findValue(typedKey, Iterator(configText))(typedKey.edInstance))
      assertEquals(None, ConfigFile.SimpleTextConfig.findValue(typedKey, Iterator.empty)(typedKey.edInstance))
    }
  }

  def find2Arguments(): java.util.List[Array[_]] = {
    val template = ConfigTemplate.debugTemplate
    CollectionConverters.asJava(
      List(
        ConfigKey.createBoolean(template, "a", defaultValue = false),
        ConfigKey.createBoolean(template, "b", defaultValue = false),
        ConfigKey.createInt(template, "d", 80),
        ConfigKey.create(template, "e", "enabled"),
        ConfigKey.createBoolean(template, ";a", defaultValue = false),
        ConfigKey.createBoolean(template, "#b", defaultValue = false),
        ConfigKey.create(template, "#e", "enabled"),
      ).map(k => Array(k, None)) ++
        Seq(Array(ConfigKey.createInt(template, "c", 45), Some(45)))
    )
  }
}
