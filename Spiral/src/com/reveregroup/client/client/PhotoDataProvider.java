package com.reveregroup.carousel.client;

import java.util.List;

public class PhotoDataProvider implements DataProvider<Photo> {

	private List<Photo> photos;
	
	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
	
	public Photo getData(int index) {
		return photos.get(index);
	}

	public String getURL(int index) {
		return photos.get(index).getUrl();
	}

	public int size() {
		if (photos == null) return 0;
		return photos.size();
	}

}
