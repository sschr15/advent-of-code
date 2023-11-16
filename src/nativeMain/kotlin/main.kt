import sschr15.aocsolutions.getProperty
import sschr15.aocsolutions.setProperty

fun main(args: Array<String>) {
    for (arg in args) {
        if ("=" in arg) {
            val (prop, value) = arg.split("=", limit = 2)
            setProperty(prop, value)
        } else {
            println(getProperty(arg))
        }
    }

    sschr15.aocsolutions.main()
}
