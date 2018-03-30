package com.example.demo.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import com.hxcore.image.FastDFSClient;


public class DownloadImg{

	public static String run(String img_urls) {
		String[] urls = img_urls.split(",");
		StringBuffer sb = new StringBuffer();
		for (String img : urls) {
			String path = "";
			try {
				URL url = new URL(img);
				Image src = ImageIO.read(url);
				int width = src.getWidth(null);
				int height = src.getHeight(null);
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = image.createGraphics();
				g2d.drawImage(src, 0, 0, width, height, null);
				g2d.dispose();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.write(image, "jpg", out);
				byte[] bytes = out.toByteArray();
				out.close();
				path = FastDFSClient.uploadOneJPG(bytes, null, null);
				System.out.println("http://img.hx2cars.com/upload" + path);
				sb.append("http://img.hx2cars.com/upload");
				sb.append(path);
				sb.append(",");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				continue;
			}
		}
		return sb.toString();
	}
}