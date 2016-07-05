package org.jetbrains.plugins.scala.lang.typeConformance.generated

import org.jetbrains.plugins.scala.lang.typeConformance.TypeConformanceTestBase

/**
  * Created by Svyatoslav Ilinskiy on 05.07.16.
  */
class PrimitivesConformanceTest extends TypeConformanceTestBase {
  def test3074A() = {
    doTest(
      """
        |val a: Array[Byte] = Array(1, 2, 3)
        |
        |/* True */
      """.stripMargin)
  }

  def test3074B(): Unit = {
    doTest(
      """
        |val a: Byte = foo(1)
        |def foo[T](t: T): T = {
        |  println("generic")
        |  t
        |}
        |def foo(t: Byte): Byte = {
        |  println("byte")
        |  t
        |}
        |//True
      """.stripMargin)
  }

}
