val (a, b) = ("a", "b")
println(/* file: Binding, offset: 5, type: org.jetbrains.plugins.scala.lang.psi.api.base.patterns.ScReferencePattern */ a.getClass)
println(/* file: Binding, offset: 8, type: org.jetbrains.plugins.scala.lang.psi.api.base.patterns.ScReferencePattern */ b.getClass)
println(classOf[ /* resolved: false */ a])
println(classOf[ /* resolved: false */ b])