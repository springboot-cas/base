package com.gclasscn.base.util;

import java.io.File;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoAttributes;
import it.sauronsoftware.jave.VideoSize;

public class VideoUtils {

	public static void main(String[] args) {
		File source = new File("G:\\backup\\西南模范中学.mp4");
		File target = new File("G:\\A O E.png");// 转图片

		VideoAttributes video = new VideoAttributes();
		video.setCodec("png");// 转图片
		video.setSize(new VideoSize(600, 500));
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("image2");// 转图片
		attrs.setOffset(10f);// 设置偏移位置，即开始转码位置（3秒）
		attrs.setDuration(0.01f);// 设置转码持续时间（1秒）
		attrs.setVideoAttributes(video);
		Encoder encoder = new Encoder();
		try {
			// 获取时长
			MultimediaInfo info = encoder.getInfo(source);
			System.out.println(info.getDuration());
			encoder.encode(source, target, attrs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
