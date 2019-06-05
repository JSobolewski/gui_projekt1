package gui_projekt;

import java.util.Comparator;

public class dateComparator implements Comparator<Image> {
	public int compare(Image i1, Image i2) {
		if(i1.year < i2.year) {
			return -1;
		} else if(i1.year != i2.year) {
			return 1;
		} else {
			if(i1.month < i2.month) {
				return -1;
			} else if(i1.month != i2.month) {
				return 1;
			} else {
				if(i1.day < i2.day) {
					return -1;
				} else if(i1.day != i2.day) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
}
