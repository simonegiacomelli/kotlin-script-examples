import java.awt.EventQueue
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.function.Predicate
import java.util.function.Supplier
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.WindowConstants

println("Hello, World7!")
println(this::class.java.superclass.canonicalName)

println("linea")


//kotlin.io.path.createTempDirectory()
//throw Exception("ciao")
fun swing() {
    val frame = JFrame()
    EventQueue.invokeAndWait {

        frame.defaultCloseOperation = WindowConstants.HIDE_ON_CLOSE
        frame.setSize(200, 300)
        frame.title = "ciccio bello"
        frame.add(JButton("hello").apply {
            setSize(50, 20)
            addActionListener {
                println("action!!")
            }
        })
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                println("closed :)")
            }
        })
        frame.isVisible = true
//    frame.isVisible = false

    }
    println("start sleeping")
    while (frame.isVisible)
        Thread.sleep(500)
    println("done sleeping")

}

class Pippo2(val str: String, val pi:String = "cicc") : Supplier<Int> {
    override fun toString(): String {
        println("toString() was called for `$str` e `$pi`")
        return str
    }

    override fun get(): Int {
        return 1234
    }
}

Pippo2("hello")
