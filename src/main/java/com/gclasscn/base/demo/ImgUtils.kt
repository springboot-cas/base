package com.gclasscn.base.demo

import java.awt.image.BufferedImage
import java.awt.RenderingHints
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileFilter
import javax.imageio.ImageIO
import java.io.FileOutputStream

class ImgUtils {

	companion object {
		fun rotateImage(bufferedImg: BufferedImage, degree: Int) : BufferedImage {
			var width = bufferedImg.getWidth();
			var height = bufferedImg.getHeight();
			var type = bufferedImg.getColorModel().getTransparency();
			var img = BufferedImage(height, width, type);
			var graphics2d = img.createGraphics();
			graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics2d.rotate(Math.toRadians(degree.toDouble()), height.toDouble() / 2, height.toDouble() / 2);
			graphics2d.drawImage(bufferedImg, 0, 0, null);
			graphics2d.dispose();
			return img;
		}
	}

}

fun main(args: Array<String>) {
	var dir = File("G:/测试")
	var files = dir.listFiles(FileFilter { it.name.endsWith("jpg") })
	files.forEach {
		var tempImg = ImageIO.read(it.inputStream())
		var result = ImgUtils.rotateImage(tempImg, 90)
		ImageIO.write(result, "jpg", it); 
	}
}