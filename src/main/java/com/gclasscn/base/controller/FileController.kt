package com.gclasscn.base.controller

import com.gclasscn.base.config.CommonPropertyConfiguration
import com.gclasscn.base.domain.FileInfo
import com.gclasscn.base.domain.PageList
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.Date
import javax.servlet.http.HttpServletRequest

@Controller
class FileController {
	
	private val logger = LoggerFactory.getLogger(FileController::class.java)
	
	@Autowired
	private val config = CommonPropertyConfiguration()
	
	/**
	 * 列表页面
	 */
	@GetMapping("/files/list")
	fun index() = "file/file"

	/**
	 * 列表数据
	 */
	@GetMapping("/files")
	@ResponseBody
	fun listBackups(pageNum: Int, size: Int, query: String): PageList<FileInfo> {
		var dir = File(config.filePath)
		if (!dir.exists()) {
			throw FileNotFoundException("备份文件目录不存在, 请检查配置是否正确")
		}
		var files = dir.listFiles(FileFilter{it.getName().contains(query)})
		var fileInfos = getFileInfos(files.asList(), pageNum, size)
		return PageList<FileInfo>(files.size, size, fileInfos)
	}
	
	private fun getFileInfos(files: List<File>, pageNum: Int, size: Int): List<FileInfo> {
		var subFiles = files.subList((pageNum - 1) * size, if(((pageNum - 1) * size + size) > files.size) files.size else ((pageNum - 1) * size + size))
		var fileInfos = arrayListOf<FileInfo>()
		for (file in subFiles) {
			fileInfos.add(FileInfo(file.getName(), Date(file.lastModified())))
		}
		return fileInfos.sortedByDescending { it.lastModify }
	}

	/**
	 * 下载文件
	 */
	@GetMapping("/files/name")
	fun download(request: HttpServletRequest, name: String): ResponseEntity<FileSystemResource> {
		var file = File(config.filePath + File.separator + name)
		var resource = FileSystemResource(file)
		var headers = headers(file, request)
		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).body(resource)
	}
	
	private fun headers(file: File, request: HttpServletRequest): HttpHeaders {
		var headers = HttpHeaders()
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM)
		headers.setContentLength(file.length())
		headers.setContentDispositionFormData("attachment", encodeFilename(request, file.name))
		headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
		return headers
	}
	
	private fun encodeFilename(request: HttpServletRequest, filename: String): String {
		var userAgent = request.getHeader("User-Agent").toLowerCase()
		try {
			if (userAgent.indexOf("msie") != -1 || userAgent.indexOf("trident") != -1) {
				return URLEncoder.encode(filename, "UTF-8")
			}
			return filename.toByteArray(Charsets.UTF_8).toString(Charsets.ISO_8859_1)
		} catch (e: UnsupportedEncodingException) {
			logger.error("转换文件名称失败, 不支持的编码格式: ", e.message, e)
		}
		return filename
	}

	/**
	 * 删除文件
	 */
	@DeleteMapping("/files/name")
	@ResponseBody
	fun delete(name: String) {
		var file = File(config.filePath + File.separator + name)
		if (file.exists()) {
			file.delete()
		}
	}

	/**
	 * 上传文件
	 */
	@PostMapping("/files")
	@ResponseBody
	fun upload(file: MultipartFile){
		var tempFile = File(config.filePath + File.separator + file.getOriginalFilename())
		if(tempFile.exists()){
			return
		}
		var input: InputStream? = null
		var output = tempFile.outputStream()
		try {
			input = file.getInputStream()
			IOUtils.copy(input, output)
		} catch (e: IOException) {
			logger.error("上传文件异常: {}", e.message, e)
		} finally {
			input?.close()
			output.close()
		}
		thumbnail("f:\\ffmpeg\\ffmpeg.exe", tempFile.getAbsolutePath(), tempFile.getAbsolutePath() + ".jpg", 800, 600, 0, 3, 3)
	}
	
	/**
	 * 按指定时间截取视频的一帧
	 * 
	 * ffmpegPath: ffmpeg可执行文件路径
	 * -ss: hour + ":" + minute + ":" + second, 指定截取时间
	 * -y: 覆盖输出文件
	 * -i: videoPath, 输入的文件
	 * -r: "1", 取一帧
	 * -f: "image2", 指定图片格式
	 * -s: width + "*" + height, thumbPath 指定图片高宽,存储路径
	 */
	fun thumbnail(ffmpegPath: String, videoPath: String, thumbPath: String, width: Int, height: Int, hour: Int, minute: Int, second: Int){
		var builder = ProcessBuilder(ffmpegPath, "-ss", "$hour:$minute:$second", "-y", "-i", videoPath, "-r", "1", "-f", "image2", "-s", "$width*$height", thumbPath)
		try {
			builder.start()
		} catch (e: IOException) {
			logger.error("视频截图异常: {}", e.message, e)
		}
	}
}