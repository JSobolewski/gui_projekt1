package gui_projekt;

import java.awt.*;
//import java.io.File;
import java.util.Arrays;

import javax.swing.*;

public class Image {
	public int id, day, month, year;
	public String dir, author, place;
	public String[] tags = new String[10];
	
	
	public ImageIcon imgIcon;
	public java.awt.Image scaledImage;
	public JLabel imgLabel;
	
	
	public Image(int id, String dir, String author, String place, int day, int month, int year, String[] tags) {
		this.id = id;
		this.dir = dir;
		this.author = author;
		this.place = place;
		this.day = day;
		this.month = month;
		this.year = year;
		
		this.tags = tags;
	}
	
	public Image() {
		this.id = Utils.imagesCount;
		this.dir = "-1";
		this.author = "-1";
		this.place = "-1";
		this.day = -1;
		this.month = -1;
		this.year = -1;
		
		this.tags = null;
	}
	
	public void edit() {
		
	}
	
	public void removeFromDB() {
		Utils.images.remove(this.id);
		Utils.imagesCount = Utils.images.size();
		
		Utils.correctIds();
	}
	
	public void show() {		
		imgIcon = new ImageIcon(this.dir);
		scaledImage = this.imgIcon.getImage().getScaledInstance(250, 250, java.awt.Image.SCALE_SMOOTH);
		imgLabel = new JLabel(new ImageIcon(scaledImage));
		
		imgLabel.setPreferredSize(new Dimension(250, 250));
		Utils.imagesPanel.add(imgLabel);		
	}
	
	public JLabel getLabel() {
		return this.imgLabel;
	}
	
	public void addToDB() {
		Utils.images.add(this);
		Utils.imagesCount = Utils.images.size();
		
		Utils.correctIds();
	}
	
	@Override
	public String toString() {
		return "Image [id=" + id + ", day=" + day + ", month=" + month + ", year=" + year + ", dir=" + dir + ", author="
				+ author + ", place=" + place + ", tags=" + Arrays.toString(tags) + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDate() {
		StringBuilder dateBuilder = new StringBuilder();
		if(this.day < 10) {
			dateBuilder.append("0");
			dateBuilder.append(Integer.toString(this.day));
		} else {
			dateBuilder.append(Integer.toString(this.day));
		}
		dateBuilder.append(".");
		if(this.month < 10) {
			dateBuilder.append("0");
			dateBuilder.append(Integer.toString(this.month));
		} else {
			dateBuilder.append(Integer.toString(this.month));
		}
		dateBuilder.append("." + Integer.toString(this.year));
		
		return dateBuilder.toString();
	}
	
	public void setDate(String date) {
		int[] dateSplitted = new int[3];
		dateSplitted = Utils.splitDate(date);
		this.setDay(dateSplitted[0]);
		this.setMonth(dateSplitted[1]);
		this.setYear(dateSplitted[2]);
	}
	
	public void setTags(String tags) {
		String[] tagsSplitted = new String[10];
		tagsSplitted = tags.split(", ");
		int i = 0;
		for(String t : tagsSplitted) {
			if(!t.isEmpty()) {
				this.tags[i] = t;
				i++;
			}
		}
	}
	
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getTags() {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < this.tags.length-1; i++) {
			builder.append(this.tags[i] + ", ");
		}
		builder.append(this.tags[this.tags.length-1]);
		return builder.toString();
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
	
}
