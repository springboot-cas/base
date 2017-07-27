package com.gclasscn.base.util

import org.slf4j.LoggerFactory
import java.net.URL
import java.net.MalformedURLException
import java.io.FileOutputStream
import java.time.Instant
import org.apache.commons.io.IOUtils
import java.math.BigDecimal
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.File
import java.io.FileReader
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileWriter

class HttpDownloadUtils {

	companion object {

		private val logger = LoggerFactory.getLogger(HttpDownloadUtils::class.java)

		@JvmStatic fun main(args: Array<String>) {
//			download("http://qingdao.gclasscn.com:9002/v1/files/fileid/59632bf798c2590008678b3a/mode/attachment", "test.mp4")
			copyFile()
		}

		/**
		 * path: http路径
		 * filename: 保存路径
		 */
		fun download(path: String, filename: String) {
			var url: URL?
			try {
				url = URL(path)
			} catch (e: MalformedURLException) {
				logger.error("下载路径异常,下载路径: $path, ${e.message}, $e")
				return
			}
			var output = File(filename)
			output.writeBytes(url.readBytes())
		}
		
		fun copyFile(){
			var file1 = File("G:\\1.log")
			var file2 = File("G:\\2.log")
			
			file1.forEachLine(Charsets.UTF_8){
				var temp = it
				while(temp.length > 15){
					file2.appendText(temp.substring(0, 15) + "\n", Charsets.UTF_8)
					temp = temp.substring(15, temp.length)
				}
				file2.appendText(temp + "\n", Charsets.UTF_8)
			}
		}
		
	}

}