package com.thoughtworks

import shapeless._

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
object ImplicitDependentTypeSpec {

  trait GetElement[A] {
    type `@`
  }

  implicit def getArrayElement[Element] = new GetElement[Array[Element]] {
    override type `@` = Element
  }

  implicitly[Array[Int] @GetElement =:= Int]
  implicitly[Array[Int] @ImplicitDependentTypeSpec.GetElement =:= Int]

  final case class Foo(bar: Int, baz: String)

  val g = Generic[Foo]
  implicitly[(Generic[Foo]##Repr) =:= g.Repr]
  implicitly[(Generic[Foo]##Repr) =:= shapeless.the.`Generic[Foo]`.Repr]

}
