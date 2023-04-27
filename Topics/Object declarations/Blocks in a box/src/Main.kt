class Block(val color: String) {
    object BlockProperties {
        var length: Int = 0
        var width: Int = 0

        fun blocksInBox(length: Int, width: Int): Int {
            return (width / BlockProperties.width) * (length / BlockProperties.length)
        }
    }
}

fun main() {
    val a = arrayListOf(1,2,3)
    val b = arrayListOf(1,2,3)
    a.removeAll(b)
    b.retainAll(a)
    val number = "123e"
    if (number.toDoubleOrNull() != null) {
        print("eafsafsafasXDD")
    }
}
