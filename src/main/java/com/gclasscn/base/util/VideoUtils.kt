package com.gclasscn.base.util

import java.io.File
import it.sauronsoftware.jave.VideoAttributes
import it.sauronsoftware.jave.VideoSize
import it.sauronsoftware.jave.EncodingAttributes
import it.sauronsoftware.jave.Encoder
import java.lang.Exception

open class VideoUtils

fun main(args: Array<String>) {
	var source = File("G:\\backup\\西南模范中学.mp4");
	var target = File("G:\\A O E.png");// 转图片

	var video = VideoAttributes();
	video.setCodec("png");// 转图片
	video.setSize(VideoSize(600, 500));
	var attrs = EncodingAttributes();
	attrs.setFormat("image2");// 转图片
	attrs.setOffset(10f);// 设置偏移位置，即开始转码位置（3秒）
	attrs.setDuration(0.01f);// 设置转码持续时间（1秒）
	attrs.setVideoAttributes(video);
	var encoder = Encoder();
	try {
		// 获取时长
		var info = encoder.getInfo(source);
		System.out.println(info.getDuration());
		encoder.encode(source, target, attrs);
	} catch (e: Exception) {
		e.printStackTrace();
	}
}
