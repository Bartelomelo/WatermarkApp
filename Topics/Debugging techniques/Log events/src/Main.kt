fun String?.capitalize() {
    return when {
        isNullOrBlank() -> {
            println("Before: $this\nAfter: $this")
        }
        length == 1 -> {
            println("Before: $this\nAfter: ${uppercase()}")
        }
        else -> {
            println("Before: $this\nAfter: ${this[0].uppercase() + substring(1)}")
        }
    }
}
