package dstruct;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {
    private int limit;

    public LimitedQueue(int limitg) {
    	super();
    	this.limit = limitg;
    }
    
    //No arguement constructor should not be used
    private LimitedQueue() {
    	super();
    	this.limit=-1;
    }
    
    @Override
    public boolean add(E o) {
        super.add(o);
        while (size() > limit) { super.remove(); }
        return true;
    }
}