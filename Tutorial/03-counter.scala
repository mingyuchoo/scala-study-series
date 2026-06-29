//
// $ scala run counter.scala
//

//> using scala 384
//> using dep "com.lihaoyi::os-lib:0.11.4"

@main
def countFiles(): Unit =
  val paths = os.list(os.pwd)
  println(paths.length)
