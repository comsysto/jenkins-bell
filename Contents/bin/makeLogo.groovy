import javax.imageio.ImageIO

Logo logo = new Logo()
def sizes = [1024, 512, 128, 64]
sizes.each{
    logo.setSize(it, it)
    def image = logo.makeImage()
    ImageIO.write(image, "png", new File("../Resources/JenkinsBell-${it}.png"))

}
