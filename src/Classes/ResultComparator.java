package Classes;

import java.util.Comparator;

public class ResultComparator implements Comparator<Object>{
	public int compare(Object arg0, Object arg1){
		Document t1=(Document)arg0;
		Document t2=(Document)arg1;
		if(t1.score != t2.score)
			return t1.score<t2.score? 1:-1;
		else
			return 1;
	}

}
