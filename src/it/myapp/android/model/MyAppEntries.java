package it.myapp.android.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MyAppEntries {

	@SerializedName("results") private List<MyAppEntry> places;

	public MyAppEntries() {
	}

	public List<MyAppEntry> getPlaces() {
		return places;
	}

	@Override
	public String toString() {
		return "MyAppEntries [places=" + places + "]";
	}

}