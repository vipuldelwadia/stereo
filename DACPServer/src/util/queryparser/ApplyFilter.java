package util.queryparser;


import java.util.ArrayList;
import java.util.List;

import api.tracks.HasMetadata;


public class ApplyFilter {

	public static <E extends HasMetadata> List<E> filter(Filter filter, Iterable<E> items) {
		
		List<E> es = new ArrayList<E>();
		
		for (E e: items) {
			if (filter.check(e)) {
				es.add(e);
			}
		}
		
		return es;
	}
}
