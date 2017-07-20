package com.gclasscn.kotlin

import java.util.regex.Pattern
import java.util.regex.Matcher
import kotlin.jvm.JvmStatic
import java.net.URL
import java.io.FileOutputStream
import java.io.File

object Main {

	// 获取img标签正则  
	private final val IMG_NODE_REG = "<(img|IMG)(.*?)(/>|></img>|>)"
	// 获取src路径的正则  
	private final val IMG_SRC_REG = "(src|SRC)=(\"|\')(.*?)(\"|\')"
	
	@JvmStatic fun main(args: Array<String>) {
		//获得html文本内容  
		var html = getHtml("http://www.sj33.cn/sc/png/")
		//获取图片标签  
		var imgNodes = listImageNodes(html)
		//获取图片src地址  
		var imgSrcs = listImageSrcs(imgNodes)
		//下载图片  
		download("测试", 1, imgSrcs)
	}


	/***
	 * 获取HTML内容
	 */
	private fun getHtml(url: String) = buildString {
		var uri = URL(url)
		uri.openStream().bufferedReader(Charsets.UTF_8).use {
			append(it.readText())
		}
	}

	/***
	 * 获取ImageUrl地址
	 */
	private fun listImageNodes(html: String) : List<String> {
		var matcher = Pattern.compile(IMG_NODE_REG).matcher(html)
		var imgUrls = ArrayList<String>()
		while (matcher.find()) {
			imgUrls.add(matcher.group(2))
		}
		return imgUrls
	}

	/***
	 * 获取ImageSrc地址
	 */
	private fun listImageSrcs(imageUrls: List<String>) : List<String> {
		var imgSrcs = ArrayList<String>()
		for (image in imageUrls) {
			var matcher = Pattern.compile(IMG_SRC_REG).matcher(image)
			while (matcher.find()) {
				imgSrcs.add(matcher.group(3))
			}
		}
		return imgSrcs
	}

	/***
	 * 下载图片
	 */
	private fun download(name: String, start: Int, imgSrcs: List<String>){
		var dir = File("G:/$name")
		if(!dir.exists()){
			dir.mkdir()
		}
		println("开始下载")
		var count = 0;
		for ((index, src) in imgSrcs.withIndex()) {
			var file = File("G:/$name/${start + index}.jpg")
			try {
				var url = URL(src)
				url.openStream().use {
					file.writeBytes(it.readBytes())
				}
				count = start + index
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
		println("下载结束, 文件编号: $count")
	}

}