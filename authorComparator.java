package gui_projekt;

import java.util.Comparator;

public class authorComparator implements Comparator<Image> {
		public int compare(Image i1, Image i2) {
			if(i1.author.compareToIgnoreCase(i2.author) < 0) {
				return -1;
			} else if(!(i1.author.equals(i2.author))) {
				return 1;
			} else {
				return 0;
			}
		}
}
