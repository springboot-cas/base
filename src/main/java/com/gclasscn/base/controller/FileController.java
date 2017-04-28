package com.gclasscn.base.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gclasscn.base.config.CommonPropertyConfiguration;
import com.gclasscn.base.domain.FileInfo;
import com.gclasscn.base.domain.PageList;
import com.google.common.collect.Lists;

@Controller
public class FileController {
	
	private Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private CommonPropertyConfiguration config;
	
	/**
	 * 列表页面
	 */
	@RequestMapping(value = "/files/list", method = RequestMethod.GET)
	public String index() {
		return "file/file";
	}

	/**
	 * 列表数据
	 */
	@RequestMapping(value = "/files", method = RequestMethod.GET)
	public @ResponseBody PageList<FileInfo> listBackups(Integer pageNum, Integer size, String query) throws FileNotFoundException {
		File dir = new File(config.getFilePath());
		if (!dir.exists()) {
			throw new FileNotFoundException("备份文件目录不存在, 请检查配置是否正确");
		}
		List<File> files = Arrays.asList(dir.listFiles(file -> file.getName().contains(query)));
		List<FileInfo> fileInfos = getFileInfos(files, pageNum, size);
		return new PageList<>(files.size(), size, fileInfos);
	}

	/**
	 * 下载文件
	 */
	@RequestMapping(value = "/files/name", method = RequestMethod.GET)
	public ResponseEntity<FileSystemResource> download(HttpServletRequest request, String name) {
		File file = new File(config.getFilePath() + File.separator + name);
		FileSystemResource resource = new FileSystemResource(file);
		HttpHeaders headers = headers(file.length(), request, name);
		return ResponseEntity.ok().headers(headers).body(resource);
	}
	
	private HttpHeaders headers(Long length, HttpServletRequest request, String filename){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentLength(length);
		headers.setContentDispositionFormData("attachment", encodeFilename(request, filename));
		return headers;
	}
	
	private String encodeFilename(HttpServletRequest request, String filename){
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		try {
			if (userAgent.indexOf("msie") != -1 || userAgent.indexOf("trident") != -1) {
				return URLEncoder.encode(filename, "UTF-8");
			} else {
				return new String(filename.getBytes(), "ISO-8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			return filename;
		}
	}

	/**
	 * 删除文件
	 */
	@RequestMapping(value = "/files/name", method = RequestMethod.DELETE)
	public @ResponseBody void delete(String name) {
		File file = new File(config.getFilePath() + File.separator + name);
		if (file.exists()) {
			file.delete();
		}
	}

	private List<FileInfo> getFileInfos(List<File> files, Integer pageNum, Integer size) {
		List<File> subFiles = files.subList((pageNum - 1) * size,
				((pageNum - 1) * size + size) > files.size() ? files.size() : ((pageNum - 1) * size + size));
		List<FileInfo> fileInfos = Lists.newArrayList();
		for (File file : subFiles) {
			fileInfos.add(new FileInfo(file.getName(), new Date(file.lastModified())));
		}
		fileInfos.sort((b1, b2) -> b2.getLastModify().compareTo(b1.getLastModify()));
		return fileInfos;
	}
	
	/**
	 * 上传文件
	 */
	@RequestMapping(value = "/files", method = RequestMethod.POST)
	public @ResponseBody void upload(MultipartFile file){
		File tempFile = new File(config.getFilePath() + File.separator + file.getOriginalFilename());
		if(tempFile.exists()){
			return ;
		}
		try (InputStream in = file.getInputStream(); OutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		} catch (IOException e) {
			logger.error("上传文件异常: {}", e.getMessage(), e);
		}
//		thumbnail("f:\\ffmpeg\\ffmpeg.exe", tempFile.getAbsolutePath(), tempFile.getAbsolutePath() + ".jpg", 800, 600, 0, 3, 3);
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
	public void thumbnail(String ffmpegPath, String videoPath, String thumbPath, int width, int height, int hour, int minute, int second){
		ProcessBuilder builder = new ProcessBuilder(ffmpegPath, "-ss", hour + ":" + minute + ":" + second, "-y", "-i", videoPath, "-r", "1", "-f", "image2", "-s", width + "*" + height, thumbPath);
		try {
			builder.start();
		} catch (IOException e) {
			logger.error("视频截图异常: {}", e.getMessage(), e);
		}
	}
}
