package com.thoughtworks

import scala.tools.nsc.ast.TreeDSL
import scala.tools.nsc.Global
import scala.tools.nsc.plugins.{Plugin, PluginComponent}
import scala.tools.nsc.transform.Transform

/**
  * @author 杨博 (Yang Bo) &lt;pop.atry@gmail.com&gt;
  */
final class ImplicitDependentType(override val global: Global) extends Plugin {
  override val name: String = "implicit-dependent-type"
  override val components: List[PluginComponent] = List(new PluginComponent with Transform with TreeDSL {
    override val global: Global = ImplicitDependentType.this.global
    override val phaseName: String = ImplicitDependentType.this.name
    override val runsAfter: List[String] = List("parser")
    import global._

    private def startsWithUpperCase(typeTree: Tree) = {
      typeTree match {
        case tq"$prefix.${typeName}" if typeName.toString.charAt(0).isUpper => true
        case Ident(typeName) if typeName.toString.charAt(0).isUpper => true
        case _ => false
      }
    }

    override protected def newTransformer(unit: global.CompilationUnit) = new Transformer {
      override def transform(tree: Tree): Tree = {
        tree match {
          case tq"$typeParameter @$annotation" if startsWithUpperCase(annotation) =>
            atPos(tree.pos) {
              tq"_root_.shapeless.the.${newTermName(show(tq"$annotation[$typeParameter]"))}.`@`"
            }
          case tq"$prefix##$subtree" =>
            val shapelessPrefix = atPos(prefix.pos) {
              q"_root_.shapeless.the.${newTermName(show(prefix))}"
            }
            def replaceSubtree(subtree: Tree): Tree = {
              atPos(subtree.pos) {
                subtree match {
                  case Ident(fieldName) if fieldName.isTermName =>
                    q"$shapelessPrefix.${fieldName.toTermName}"
                  case Ident(fieldName) if fieldName.isTypeName =>
                    tq"$shapelessPrefix.${fieldName.toTypeName}"
                  case q"$head.$tail" =>
                    q"${replaceSubtree(head)}.$tail"
                  case tq"$head.$tail" =>
                    tq"${replaceSubtree(head)}.$tail"
                  case tq"$head#$field" =>
                    tq"${replaceSubtree(head)}#$field"
                  case tq"$head#$field[..$typeParameters]" =>
                    tq"${replaceSubtree(head)}#$field[..$typeParameters]"
                  case tq"$head[..$typeParameters]" =>
                    tq"${replaceSubtree(head)}[..$typeParameters]"
                }
              }
            }
            atPos(tree.pos) {
              replaceSubtree(subtree)
            }
          case _ =>
            super.transform(tree)
        }
      }
    }
  })
  override val description: String = "Provide a ## syntax that resolves implicit dependent types."
}
