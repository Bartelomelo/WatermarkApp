package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.lang.NumberFormatException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun saveImage(image: BufferedImage, imageFile: File, output: String) {
    ImageIO.write(image, "png", imageFile)
    if (imageFile.exists()) {
        println("The watermarked image $output has been created.")
    }
}

fun imageNumComponents(image: BufferedImage) {
    if (image.colorModel.pixelSize !in listOf(24, 32)) {
        throw ImageColorComponentsException("The image isn't 24 or 32-bit.")
    }
}

fun watermarkNumComponents(image: BufferedImage) {
    if (image.colorModel.pixelSize !in listOf(24, 32)) {
        throw ImageColorComponentsException("The watermark isn't 24 or 32-bit.")
    }
}

fun imageComponents(image: BufferedImage) {
    if (image.colorModel.numComponents != 3) {
        throw ImageComponentsException("The number of image color components isn't 3.")
    }
}

fun watermarkComponents(image: BufferedImage) {
    if (image.colorModel.numComponents != 3 && image.colorModel.numComponents != 4) {
        throw ImageComponentsException("The number of watermark color components isn't 3.")
    }
}

fun checkSize(image: BufferedImage, watermark: BufferedImage) {
    if (image.width < watermark.width || image.height < watermark.height) {
        throw ImagesSizeException("The watermark's dimensions are larger.")
    }
}

fun transparencyOutOfRange(transparencyPercentage: Int) {
    if (transparencyPercentage !in 0..100) {
        throw TransparencyOutOfRangeException("The transparency percentage is out of range.")
    }
}

fun checkOutputExtension(output: String) {
    if (output.split(".")[1] != "jpg" && output.split(".")[1] != "png") {
        throw WrongOutputImageFileException("The output file extension isn't \"jpg\" or \"png\".")
    }
}

fun fileNotExists(imageName: String) {
    if (!File(imageName).exists()) {
        throw FileNotExistsException("The file $imageName doesn't exist.")
    }
}

fun invalidTransparencyColor(transparencyColorTab: MutableList<String>) {
    var wrongColorNumber: Boolean = false
    for (color in transparencyColorTab) {
        if (!color.all { char -> char.isDigit() }) {
            throw InvalidTransparencyColorException("The transparency color input is invalid.")
        }
    }

    for (color in transparencyColorTab) {
        if (color.toInt() > 255 || color.toInt() < 0) {
            wrongColorNumber = true
        }
        if (transparencyColorTab.size != 3 || wrongColorNumber) {
            throw InvalidTransparencyColorException("The transparency color input is invalid.")
        }
    }
}

fun blendImage() {
    try {
        var watermarkPos: List<String>
        var posX: Int = 0
        var posY: Int = 0
        var transparencyColorExists: Boolean = false
        val transparencyColor = mutableListOf<String>()
        var alphaChannelDecision: String = ""
        var color: Color
        println("Input the image filename:")
        val imageName: String = readln()
        fileNotExists(imageName)
        val image: BufferedImage = ImageIO.read(File(imageName))
        imageComponents(image)
        imageNumComponents(image)
        println("Input the watermark image filename:")
        val watermark: String = readln()
        fileNotExists(watermark)
        val watermarkImage: BufferedImage = ImageIO.read(File(watermark))
        watermarkComponents(watermarkImage)
        watermarkNumComponents(watermarkImage)
        checkSize(image, watermarkImage)
        val outputImage: BufferedImage = image
        if (!watermarkImage.colorModel.hasAlpha()) {
            println("Do you want to set a transparency color?")
            val choice: String = readln()
            if (choice == "yes") {
                transparencyColorExists = true
                println("Input a transparency color ([Red] [Green] [Blue]):")
                val transparencyInput = readLine()!!.split(" ")
                transparencyColor.addAll(transparencyInput)
                invalidTransparencyColor(transparencyColor)
            } else {
                transparencyColorExists = false
            }
        } else {
            if (watermarkImage.transparency == 3) {
                println("Do you want to use the watermark's Alpha channel?")
                alphaChannelDecision = readln()
            }
        }
        println("Input the watermark transparency percentage (Integer 0-100):")
        val transparency: Int = readln().toInt()
        transparencyOutOfRange(transparency)
        println("Choose the position method (single, grid):")
        val posMethod: String = readln()
        if (posMethod != "single" && posMethod != "grid") {
            println("The position method input is invalid.")
            exitProcess(0)
        }
        if (posMethod == "single") {
            println("Input the watermark position ([x 0-${image.width - watermarkImage.width}] [y 0-${image.height - watermarkImage.height}]):")
            val watermarkPoss = readln().split(" ")
            for (water in watermarkPoss) {
                if (!water.matches("-?[0-9]+(\\.[0-9]+)?".toRegex())) {
                    throw PositionInvalidException("The position input is invalid.")
                }

            }
            watermarkPos = watermarkPoss

            try {
                if (watermarkPos[0].toInt() !in 0..image.width - watermarkImage.width || watermarkPos[1].toInt() !in 0..image.height - watermarkImage.height) {
                    throw WatermarkPositionOutOfRangeException("The position input is out of range.")
                }
            } catch (e: WatermarkPositionOutOfRangeException) {
                println(e.message)
                exitProcess(0)
            }
            posX = watermarkPos[0].toInt()
            posY = watermarkPos[1].toInt()
        }
        println("Input the output image filename (jpg or png extension):")
        val output: String = readln()
        checkOutputExtension(output)

        if (posMethod == "grid") {
            for (x in 0 until image.width) {
                for (y in 0 until image.height) {
                    val i = Color(image.getRGB(x, y))
                    if (alphaChannelDecision == "yes") {
                        val w = Color(watermarkImage.getRGB(x % watermarkImage.width, y % watermarkImage.height), true)
                        color = if (w.alpha == 0) {
                            Color(i.red, i.green, i.blue)
                        } else {
                            Color(
                                (transparency * w.red + (100 - transparency) * i.red) / 100,
                                (transparency * w.green + (100 - transparency) * i.green) / 100,
                                (transparency * w.blue + (100 - transparency) * i.blue) / 100
                            )
                        }
                    } else {
                        if (transparencyColorExists) {
                            val w = Color(watermarkImage.getRGB(x % watermarkImage.width, y % watermarkImage.height))
                            color =
                                if (transparencyColor.isNotEmpty() && w.red == transparencyColor[0].toInt() && w.green == transparencyColor[1].toInt() && w.blue == transparencyColor[2].toInt()) {
                                    Color(i.red, i.green, i.blue)
                                } else {
                                    Color(
                                        (transparency * w.red + (100 - transparency) * i.red) / 100,
                                        (transparency * w.green + (100 - transparency) * i.green) / 100,
                                        (transparency * w.blue + (100 - transparency) * i.blue) / 100
                                    )
                                }

                        } else {
                            val w = Color(watermarkImage.getRGB(x % watermarkImage.width, y % watermarkImage.height))
                            color = Color(
                                (transparency * w.red + (100 - transparency) * i.red) / 100,
                                (transparency * w.green + (100 - transparency) * i.green) / 100,
                                (transparency * w.blue + (100 - transparency) * i.blue) / 100
                            )
                        }
                    }
                    outputImage.setRGB(x, y, color.rgb)
                }

            }

        } else if (posMethod == "single") {
            for (x in posX until posX + watermarkImage.width) {
                for (y in posY until posY + watermarkImage.height) {
                    val i = Color(image.getRGB(x, y))
                    if (alphaChannelDecision == "yes") {
                        val w = Color(watermarkImage.getRGB(x - posX, y - posY), true)
                        color = if (w.alpha == 0) {
                            Color(i.red, i.green, i.blue)
                        } else {
                            Color(
                                (transparency * w.red + (100 - transparency) * i.red) / 100,
                                (transparency * w.green + (100 - transparency) * i.green) / 100,
                                (transparency * w.blue + (100 - transparency) * i.blue) / 100
                            )
                        }
                    } else {
                        if (transparencyColorExists) {
                            val w = Color(watermarkImage.getRGB(x - posX, y - posY))
                            color =
                                if (transparencyColor.isNotEmpty() && w.red == transparencyColor[0].toInt() && w.green == transparencyColor[1].toInt() && w.blue == transparencyColor[2].toInt()) {
                                    Color(i.red, i.green, i.blue)
                                } else {
                                    Color(
                                        (transparency * w.red + (100 - transparency) * i.red) / 100,
                                        (transparency * w.green + (100 - transparency) * i.green) / 100,
                                        (transparency * w.blue + (100 - transparency) * i.blue) / 100
                                    )
                                }

                        } else {
                            val w = Color(watermarkImage.getRGB(x, y))
                            color = Color(
                                (transparency * w.red + (100 - transparency) * i.red) / 100,
                                (transparency * w.green + (100 - transparency) * i.green) / 100,
                                (transparency * w.blue + (100 - transparency) * i.blue) / 100
                            )
                        }
                    }

                    outputImage.setRGB(x, y, color.rgb)
                }
            }
        }
        saveImage(outputImage, File(output), output)
    } catch (e: FileNotExistsException) {
        println(e.message)
    } catch (e: ImageComponentsException) {
        println(e.message)
    } catch (e: ImageColorComponentsException) {
        println(e.message)
    } catch (e: FileNotExistsException) {
        println(e.message)
    } catch (e: ImagesSizeException) {
        println(e.message)
    } catch (e: ImageComponentsException) {
        println(e.message)
    } catch (e: InvalidTransparencyColorException) {
        println(e.message)
    } catch (e: ImageColorComponentsException) {
        println(e.message)
    } catch (e: NumberFormatException) {
        println("The transparency percentage isn't an integer number.")
    } catch (e: TransparencyOutOfRangeException) {
        println(e.message)
    } catch (e: PositionInvalidException) {
        println(e.message)
    } catch (e: WrongOutputImageFileException) {
        println(e.message)
    }
}


fun main() {
    blendImage()
}