package com.reveregroup.carousel.client;

public interface DataProvider<DATATYPE> {
	public int size();
	public String getURL(int index);
	public DATATYPE getData(int index);
}
