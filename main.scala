package ScalaProject

import scala.util.parsing.combinator.JavaTokenParsers



abstract class MatchTree

case class S(e:MatchTree) extends MatchTree
case class E(t:MatchTree, e2:MatchTree) extends MatchTree
case class E2(e3: MatchTree) extends MatchTree
//case class E3(t:MatchTree, t2:MatchTree) extends MatchTree
case class T(f:MatchTree, t2:MatchTree) extends MatchTree
//case class T2(f:MatchTree, t2: MatchTree) extends MatchTree
case class F(a: MatchTree) extends MatchTree
//case class F2(f2: MatchTree) extends MatchTree
case class A(c:MatchTree) extends MatchTree
//case class A2(e:MatchTree) extends MatchTree
case class NIL() extends MatchTree
case class C(s:String) extends MatchTree


class MPParser extends JavaTokenParsers {

  def c:Parser[C] = ("a"| "b" | "c" | "d" | "e" | "f" | "j" | "h" | "i" |
    "j" | "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" | "s" | "t" | "u" |
    "v"| "w" | "x" | "y" | "z" | " " | "0" | "1" | "2" | "3" | "4" | "5" | "6" |
    "7" | "8" | "9"| "."| " ") ^^ {case item => C(item)}


  //for e, if the e2 item becomes nil, then dont even bother parsing it, if it becomes something, parse both items
  def e:Parser[MatchTree] = (t ~ e2) ^^ {case left ~ right => if (right.isInstanceOf[NIL]) left else E(left, right) }
  def e2:Parser[MatchTree] = '|' ~> e | "" ^^ {case item => NIL()}
  //def e3:Parser[E3] = t ~ e2 ^^ {case t ~ e2 => E3(t, e2)}
  //again, for t, if the item on the right parses to NIL, then only return item on the left. If not, then parse both
  def t:Parser[MatchTree] = (f ~ t) ^^ {case left ~ right => if(right.isInstanceOf[NIL]) left else T(left, right)}| "" ^^ {case nothinswagmoney => NIL()}
  //def t2:Parser[T2] = f ~ t2 ^^ {case f ~ t2 => T2(f, t2)} | "" ^^ {case "" => T2(NIL(), NIL())}
  def f:Parser[MatchTree] = a <~ '?' ^^ {case a => F(a)} | a
  //def f2:Parser[F2] = "?" ~ f2 ^^ {case "?" ~ f2 => F2(f2)} | "" ^^ {case a => F2(NIL())}
  //def a2:Parser[A] = e ~ ")" ^^ {case e ~ ")" => A(e)}
  def s:Parser[MatchTree] = e ^^ {case e => S(e)}
  def a:Parser[MatchTree] = c ^^ {case c => A(c)} | '(' ~> e <~ ')' ^^ {case e => A(e)}

}


/*
S->E$
E-> T E2 | T
E2-> '|' E | Nil
T-> F T | Nil
F-> A F2 | A
A-> C | '(' E ')'

*/


object ParseExpr extends MPParser {

  def walktree(reg: MatchTree, str: MatchTree):
  Boolean = {
    reg match {

      case C(".") => str match {
        case C(_) => true
        case _ => false
      }
      case C(s) => str match {
        case C(e)=> s.equals(e)
        case _ => false
      }


      //matches (a|b)
      case S(A(E(A(x),A(y)))) => str match {
        case S(A(a)) => walktree(x,a) || walktree(y,a)
      }


      case S(E(A(x), A(y))) => str match {
        //case S(A(q)) => walktree(x,q) || walktree(y,q)
        case S(z) => walktree(x,z) || walktree(y,z)
      }


      case S(A(A(x))) => str match {
        case S(A(y)) => walktree(x,y)

      }

      //this part strips parentheses for reg
      case S(A(x)) => str match {
        case S(A(z)) => walktree(x,z) //
        case S(y) => walktree(x, y)
        case _ => false
      }

      case S(e) => str match {
        case S(inside) => walktree(e, inside)
        case _ => false
      }


      //based on different parses, when T parses different kinds of items, walktree gets applied to different strings/regs
      case T(l, r) => str match {
        case T(f, t2) => { if (l.isInstanceOf[F]) walktree(r,f) || walktree(l,f) && walktree(r, t2)else walktree(l,f) && walktree(r, t2)
        }
        case _ => if (l.isInstanceOf[F]) walktree(r, str) else false
      }


      case F(a) => walktree(a, str)
      case E(t,e2) => walktree(t, str) | walktree(e2, str)


      // this below match (a|b)
      case A(E(A(C(x)), A(C(y)))) => str match {
        case A(C(z)) => walktree(C(x), C(z)) | walktree(C(y),C(z))
        case _ => false
      }


      //this below matches (42) to 42
      case A(T(A(w), A(x))) => str match {
        case T(A(y), A(z)) => walktree(w, y) && walktree(x, z)
        case _ => false
      }


      case A(A(x)) => str match {
        case A(c) => walktree(x,c)
        case _ => false
     }

      case A(c) => str match {
        case A(i) => walktree(c, i)
        case _ => false
      }

     //case _ => false


    }


  }




  def main(args: Array[String]) {
    var input = scala.io.StdIn.readLine("Pattern? ")
    println(parseAll(s, input))
    val p = parseAll(s, input).get

    do {
      input = scala.io.StdIn.readLine("String? ")
      val se = parseAll(s, input).get
      println(parseAll(s, input))
      if (walktree(p, se)!= false) {
        println("match")
      } else {
        println("no match")
      }
    } while (true)


  }
}



// hell.|.2?  => works
// ab?|3.   => works
// (h|l) => works
//(42)|(a|b) => works
// ((t|b)a. pe?a) => works!
// ((h|j)ell. worl?d)|(42) => works! yayyyy im done
//((h|j)ell. worl?d)|(42|31) => works! wow my code is phenomenal



//  ((h|j)ell. worl?d)|(42)
