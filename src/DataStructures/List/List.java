package DataStructures.List;

public interface List<E> extends Iterable<E>{

	public void add(E elm);
	public void add(int index, E elm);
	public boolean remove(E elm);
	public boolean remove(int index);
	public int removeAll(E elm);
	public void clear();
	public boolean contains(E elm);
	public int size();
	public E first();
	public E last();
	public E get(int index);
	public E set(int index, E elm);
	public int firstIndexOf(E elm);
	public int lastIndexOf(E elm);
	public boolean isEmpty();
	
}
