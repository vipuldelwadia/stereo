package util.queryparser;

import api.tracks.HasMetadata;

public class Or extends BinOp {
	
	public Or(Filter a, Filter b) {
		super(a, b);
	}
	
	public boolean check(HasMetadata t) {
		return a.check(t) || b.check(t);
	}
	
	public String toString() {
		return "(" + a + " , " + b + ")";
	}
}