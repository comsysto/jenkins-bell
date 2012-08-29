#!/usr/bin/env groovy

import javax.imageio.ImageIO

Logo logo = new Logo()
def sizes = [512, 256, 128, 32, 16]
sizes.each{
    logo.setSize(it, it)
    def image = logo.makeImage()
    ImageIO.write(image, "png", new File("../Resources/JenkinsBell-${it}.png"))

}
