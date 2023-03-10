package DataStructures.List;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SinglyLinkedList<E> implements List<E>{
	
	private class Node<E>{
		
		private Node<E> next;
		private E element;
		
		public Node(Node<E> next, E elm) {
			this.element = elm;
			this.next = next;
		}
		
		public Node(E elm) {
			this(null, elm);
		}
		
		public Node() {
			this(null, null);
		}

		public Node<E> getNext() {
			return next;
		}

		public void setNext(Node<E> next) {
			this.next = next;
		}

		public E getElement() {
			return element;
		}

		public void setElement(E element) {
			this.element = element;
		}
		
		public void clear() {
			this.element = null;
			this.next = null;
		}
		
	}

	
	@SuppressWarnings("hiding")
	private class LinkedListIterator<E> implements Iterator<E>{

		private Node<E> curNode;
		
		@SuppressWarnings("unchecked")
		public LinkedListIterator() {
			curNode = (Node<E>) header.getNext();
		}
		
		@Override
		public boolean hasNext() {
			return curNode != null;
		}

		@Override
		public E next() {
			if(hasNext()) {
				E value = curNode.getElement();
				curNode = curNode.getNext();
				return value;
			}
			throw new NoSuchElementException();
		}
		
	}
	
	
	private Node<E> header;
	private int currentSize;
	
	
	public SinglyLinkedList() {
		header = new Node<>();
		currentSize = 0;
	}


	@Override
	public void add(E elm) {
		Node<E> curNode, newNode;
		
		for(curNode = header.getNext(); curNode.getNext() != null; curNode = curNode.getNext());
		newNode = new Node<>(elm);
		curNode.setNext(newNode);
		currentSize++;
	}


	@Override
	public void add(int index, E elm) {
		Node<E> curNode, newNode;
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		
		if(index == currentSize) add(elm);
		else {
			curNode = getNode(index - 1);
			newNode = new Node<>(curNode.getNext(), elm);
			curNode.setNext(newNode);
			currentSize++;
		}
	}
	
	private Node<E> getNode(int index){
		if(index < -1 || index >= size())
			throw new IndexOutOfBoundsException();
		
		Node<E> curNode = header;
		for(int i = 0; i < index; i++) {
			curNode = curNode.getNext();
		}
		return curNode;
	}


	@Override
	public boolean remove(E elm) {
		Node<E> curNode = header;
		Node<E> nextNode = curNode.getNext();
		
		while(nextNode != null && !nextNode.getElement().equals(elm)) {
			curNode = nextNode;
			nextNode = nextNode.getNext();
		}
		
		if(nextNode != null) {
			curNode.setNext(nextNode.getNext());
			nextNode.clear();
			nextNode = null;
			currentSize--;
			return true;
		}
		
		return false;
	}


	@Override
	public boolean remove(int index) {
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		
		Node<E> curNode = getNode(index - 1);
		Node<E> rmNode = curNode.getNext();
		curNode.setNext(rmNode.getNext());
		rmNode.clear();
		rmNode = null;
		currentSize--;
		return true;
	}


	@Override
	public int removeAll(E elm) {
		// TODO Auto-generated method stub
		int counter = 0;
		Node<E> curNode = header;
		Node<E> nextNode = curNode.getNext();
		
		while(nextNode != null) {
			if(nextNode.getElement().equals(elm)) {
				curNode.setNext(nextNode.getNext());
				nextNode.clear();
				currentSize--;
				counter++;
				nextNode = curNode.getNext();
			} else {
				curNode = nextNode;
				nextNode = nextNode.getNext();
			}
		}
		return counter;
	}


	@Override
	public void clear() {
		while(size() > 0) remove(0);
	}


	@Override
	public boolean contains(E elm) {
		return firstIndexOf(elm) != -1;
	}


	@Override
	public int size() {
		return currentSize;
	}


	@Override
	public E first() {
		return header.getNext().getElement();
	}


	@Override
	public E last() {
		return get(size() - 1);
	}


	@Override
	public E get(int index) {
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();
		return getNode(index).getElement();
	}


	@Override
	public E set(int index, E elm) {
		if(index < 0 || index >= size())
			throw new IndexOutOfBoundsException();		
		Node<E> node = getNode(index);
		E value = node.getElement();
		node.setElement(elm);
		return value;
	}


	@Override
	public int firstIndexOf(E elm) {
		Node<E> curNode = header.getNext();
		int pos = -1;
		
		while(curNode != null && !curNode.getElement().equals(elm)) {
			pos++;
			curNode = curNode.getNext();
		}
		
		if(curNode != null) {
			return pos;
		}
		
		return -1;
	}


	@Override
	public int lastIndexOf(E elm) {
		int curPos = 0, lastPos = -1;
		for(Node<E> curNode = header.getNext(); curNode.getNext() != null; curNode = curNode.getNext()) {
			if(curNode.getElement().equals(elm))
				lastPos = curPos;
			curPos++;
		}

		return lastPos;
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


	@Override
	public Iterator<E> iterator() {
		return new LinkedListIterator<>();
	}
	
	

}
