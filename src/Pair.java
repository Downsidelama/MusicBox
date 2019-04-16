
class Pair<T1, T2> {
	private T1 p1;
	private T2 p2;

	Pair(T1 p1, T2 p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	T2 getP2() {
		return p2;
	}

	T1 getP1() {
		return p1;
	}
}
