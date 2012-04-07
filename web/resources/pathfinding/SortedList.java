package web.resources.pathfinding;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Author: Tom
 * Date: 01/04/12
 * Time: 19:39
 */
public class SortedList {

	private ArrayList list = new ArrayList();

	public Object first() {
		return list.get(0);
	}

	public boolean isEmpty(){
		return size()==0;
	}

	public void clear() {
		list.clear();
	}

	public void add(Object o) {
		list.add(o);
		Collections.sort(list);
	}

	public void remove(Object o) {
		list.remove(o);
	}

	public int size() {
		return list.size();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public ArrayList getList(){
		return list;
	}

}