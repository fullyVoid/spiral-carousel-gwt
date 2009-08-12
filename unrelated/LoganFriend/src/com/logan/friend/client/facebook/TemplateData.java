package com.logan.friend.client.facebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * <a href="http://wiki.developers.facebook.com/index.php/Template_Data">http://wiki.developers.facebook.com/index.php/Template_Data</a>
 * @author dwolverton
 *
 */
public class TemplateData {
	private List<Image> images;
	private Flash flash;
	private MP3 mp3;
	private Video video;
	private Map<String, String> data;

	private List<Long> targets;
	private String bodyGeneral;
	private String prompt;

	public JavaScriptObject getTemplateDataAsJavaScriptObject() {
		StringBuilder json = new StringBuilder("{");

		if (images != null && images.size() > 0) {
			json.append("images:[");
			boolean first = true;
			for (Image image : images) {
				if (first) {
					first = false;
				} else {
					json.append(",");
				}
				json.append("{src:'").append(image.source);
				json.append("',href:'").append(image.href);
				json.append("'}");
			}
			json.append("]");
		}
		if (flash != null) {
			if (json.length() != 1)
				json.append(",");
			json.append("flash:{swfsrc: '").append(flash.source).append("',imgsrc: '").append(flash.previewImage)
					.append("'");
			if (flash.width != null) {
				json.append(",width:").append(flash.width);
			}
			if (flash.height != null) {
				json.append(",height:").append(flash.height);
			}
			if (flash.expandedWidth != null) {
				json.append(",expandedWidth:").append(flash.expandedWidth);
			}
			if (flash.expandedHeight != null) {
				json.append(",expandedHeight:").append(flash.expandedHeight);
			}
			json.append("}");
		}
		if (mp3 != null) {
			if (json.length() != 1)
				json.append(",");
			json.append("mp3:{src:'").append(mp3.source);
			json.append("',album:'").append(mp3.album);
			json.append("',title:'").append(mp3.title);
			json.append("',artist:'").append(mp3.artist);
			json.append("'}");
		}
		if (video != null) {
			if (json.length() != 1)
				json.append(",");
			json.append("video:{video_src:'").append(video.source);
			json.append("',preview_img:'").append(video.previewImage);
			json.append("'}");
		}

		if (data != null && data.size() != 0) {
			if (json.length() != 1)
				json.append(",");
			boolean first = true;
			for (Map.Entry<String, String> entry : data.entrySet()) {
				if (first) {
					first = false;
				} else {
					json.append(",");
				}
				json.append("'").append(entry.getKey()).append("':'").append(entry.getValue()).append("'");
			}
		}

		if (json.length() == 1)
			return null;
		
		json.append("}");
		return parseJSON(json.toString());
	}

	public JsArrayString getTargetsAsJSArray() {
		if (targets == null || targets.size() == 0)
			return null;

		JsArrayString array = JsArrayString.createArray().cast();
		int i = 0;
		NumberFormat format = NumberFormat.getFormat("#");
		for (Long target : targets) {
			if (target != null)
				array.set(i++, format.format(target.doubleValue()));
		}
		return array;
	}

	private native JavaScriptObject parseJSON(String json) /*-{
		return eval('(' + json + ')');
	}-*/;

	public void put(String key, String value) {
		if (data == null)
			data = new HashMap<String, String>();
		data.put(key, value);
	}

	public String get(String key) {
		if (data == null)
			return null;
		return data.get(key);
	}

	public void remove(String key) {
		if (data == null)
			return;
		data.remove(key);
	}

	public void addImage(String source, String href) {
		if (images == null)
			images = new ArrayList<Image>();
		images.add(new Image(source, href));
	}

	public void addTarget(Long id) {
		if (id == null)
			return;
		if (targets == null)
			targets = new ArrayList<Long>();
		targets.add(id);
	}

	public void setVideo(String source, String previewImage) {
		video = new Video(source, previewImage);
	}

	public void setMP3(String source, String album, String title, String artist) {
		mp3 = new MP3(source, album, title, artist);
	}

	public void setFlash(String source, String previewImage) {
		flash = new Flash(source, previewImage);
	}

	public void setFlash(String source, String previewImage, Integer width, Integer height) {
		flash = new Flash(source, previewImage, width, height);
	}

	public void setFlash(String source, String previewImage, Integer width, Integer height, Integer expandedWidth,
			Integer expandedHeight) {
		flash = new Flash(source, previewImage, width, height, expandedWidth, expandedHeight);
	}

	public Flash getFlash() {
		return flash;
	}

	public void setFlash(Flash flash) {
		this.flash = flash;
	}

	public MP3 getMP3() {
		return mp3;
	}

	public void setMP3(MP3 mp3) {
		this.mp3 = mp3;
	}

	public List<Long> getTargets() {
		return targets;
	}

	public void setTargets(List<Long> targets) {
		this.targets = targets;
	}

	public String getBodyGeneral() {
		return bodyGeneral;
	}

	public void setBodyGeneral(String bodyGeneral) {
		this.bodyGeneral = bodyGeneral;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public static class Image {
		public Image() {
		}

		public Image(String source, String href) {
			this.source = source;
			this.href = href;
		}

		public String source;
		public String href;
	}

	public static class Flash {
		public Flash() {
		}

		public Flash(String source, String previewImage) {
			this.source = source;
			this.previewImage = previewImage;
		}

		public Flash(String source, String previewImage, Integer width, Integer height) {
			this.source = source;
			this.previewImage = previewImage;
			this.width = width;
			this.height = height;
		}

		public Flash(String source, String previewImage, Integer width, Integer height, Integer expandedHeight,
				Integer expandedWidth) {
			this.source = source;
			this.previewImage = previewImage;
			this.width = width;
			this.height = height;
			this.expandedWidth = expandedWidth;
			this.expandedHeight = expandedHeight;
		}

		public String source;
		public String previewImage;
		public Integer width;
		public Integer height;
		public Integer expandedWidth;
		public Integer expandedHeight;
	}

	public static class MP3 {
		public MP3() {
		}

		public MP3(String source, String album, String title, String artist) {
			this.source = source;
			this.album = album;
			this.title = title;
			this.artist = artist;
		}

		public String source;
		public String album;
		public String title;
		public String artist;
	}

	public static class Video {
		public Video() {
		}

		public Video(String source, String previewImage) {
			this.source = source;
			this.previewImage = previewImage;
		}

		public String source;
		public String previewImage;
	}

}
