package agent;

public class Rect {
	public int llx;
	public int lly;
	public int urx;
	public int ury;
	public Rect(int llx, int lly, int urx, int ury) {
		this.llx = llx;
		this.lly = lly;
		this.urx = urx;
		this.ury = ury;
	}
	
	public int area() {
		return (urx-llx)*(ury-lly);
	}

	public boolean interesect(Rect newRect) {
        return (newRect.llx <= this.urx || this.llx <= newRect.urx) && (newRect.lly <= this.ury || this.lly <= newRect.ury);
	}
}
