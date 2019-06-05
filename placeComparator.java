package gui_projekt;

import java.util.Comparator;

public class placeComparator implements Comparator<Image> {
	public int compare(Image i1, Image i2) {
		if(i1.place.compareToIgnoreCase(i2.place) < 0) {
			return -1;
		} else if(!(i1.place.equals(i2.place))) {
			return 1;
		} else {
			return 0;
		}
	}
}
