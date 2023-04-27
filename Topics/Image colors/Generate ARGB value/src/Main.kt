import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun printARGB() {
    val (a, r, g, b) = readln().split(" ").map { it.toInt() }
    try {
        val color = Color(r, g, b, a).rgb
        println(color.toUInt())

    }catch (e: IllegalArgumentException) {
        println("Invalid input")
    }
}

fun saveImage(image: BufferedImage, imageFile: File) {
    ImageIO.write(image, "png", imageFile)
}

fun main() {
    val height: Int = 800
    val width: Int = 600

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val imageFile = File("myFirstImage.png")

    //saveImage(image, imageFile)
    val graphics = image.createGraphics()

    graphics.color = Color.RED
    graphics.drawRect(100, 100, 300, 300)
    saveImage(image, imageFile)
}