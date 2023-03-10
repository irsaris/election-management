package DataStructures.List;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayList<E> implements List<E>{

	private E[] elements;
	private int currentSize;
	
	private static final int DEFAULT_SIZE = 25;
	
	@SuppressWarnings("hiding")
	private class ArrayListIterator<E> implements Iterator<E>{
		int currentPosition = 0;
		
		public ArrayListIterator() {
			currentPosition = 0;
		}
		@Override
		public boolean hasNext() {
			return currentPosition < size();
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			if(hasNext()) {
				E elm = (E) elements[currentPosition];
				currentPosition++;
				return elm;
			}
			throw new NoSuchElementException();
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public ArrayList(int initialCapacity) {
		if(initialCapacity < 1)
			throw new IllegalArgumentException("Size must be at least 1");
		
		currentSize = 0;
		elements = (E[]) new Object[initialCapacity];
		
	}
	
	public ArrayList() {
		this(DEFAULT_SIZE);
	}

	@Override
	public void add(E elm) {
		
		if(currentSize == elements.length)
			reAllocate();
		
		elements[currentSize] = elm;
		currentSize++;
		
	}
	
	private void reAllocate() {
		E[] newElements = (E[]) new Object[currentSize * 2];
		
		for (int i = 0; i < currentSize; i++) 
			newElements[i] = elements[i];
		
		elements = newElements;
	}

	@Override
	public void add(int index, E elm) {
        if (elm == null)
            throw new IllegalArgumentException();

        if(index > currentSize || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if(currentSize  == elements.length) {
            reAllocate();
        }
        if(index == currentSize) {
            add(elm);
        }

        else{
            for(int i = currentSize; i > index; i--) {
                elements[i] = elements[i-1];
            }
            elements[index] = elm;
            currentSize++;
        }
    }

	@Override
	public boolean remove(E elm) {
		int pos = -1;
		
		for(int i = 0; i < currentSize; i++) {
			if(elements[i].equals(elm)) {
				pos = i;
				break;
			}
		}
		
		if(pos >= 0) {
			for(int i = pos; i < currentSize - 1; i++) {
				elements[i] = elements[i+1];
			}
			currentSize--;
			elements[currentSize] = null;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean remove(int index) {
		if(index < 0 || index >= currentSize)
			throw new IndexOutOfBoundsException("Index must be [0, size())");
		
		for(int i = index; i < currentSize - 1; i++) {
			elements[i] = elements[i+1];
		}
		currentSize--;
		elements[currentSize] = null;
		return true;
	}

	@Override
	public int removeAll(E elm) {
		//Inefficient!
		int copiesRemoved = 0;
		while(remove(elm)) copiesRemoved++;
		return copiesRemoved;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		for (int i = 0; i < currentSize; i++) {
			elements[i] = null;
		}
		currentSize = 0;
	}

	@Override
	public boolean contains(E elm) {
		for(int i = 0; i < currentSize; i++) {
			if(elements[i].equals(elm)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return currentSize;
	}

	@Override
	public E first() {
		return elements[0];
	}

	@Override
	public E last() {
		if(isEmpty()) return null;
		return elements[currentSize - 1];
	}

	@Override
	public E get(int index) {
		if(index < 0 || index >= currentSize)
			throw new IndexOutOfBoundsException("Index must be [0, size())");		
		
		return elements[index];
	}

	@Override
	public E set(int index, E elm) {
		if(index < 0 || index >= currentSize)
			throw new IndexOutOfBoundsException("Index must be [0, size())");	
		E value = elements[index];
		elements[index] = elm;
		return value;
	}

	@Override
	public int firstIndexOf(E elm) {
		for(int i = 0; i < currentSize; i++) {
			if(elements[i].equals(elm)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(E elm) {
		for(int i = currentSize - 1; i >= 0; i--) {
			if(elements[i].equals(elm)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Iterator<E> iterator() {
		return new ArrayListIterator<>();
	}
	
	
}
