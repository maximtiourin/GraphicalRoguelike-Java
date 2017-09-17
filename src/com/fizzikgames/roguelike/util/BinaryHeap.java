package com.fizzikgames.roguelike.util;

/**
 * The binary heap stores a tree of nodes where each node has two children. The 
 * heap doesn't keep all of its elements sorted, instead it keeps the top most 
 * element as the most desired element. Whether the element is the lowest or 
 * highest is determined by the value the object supplies with its
 * implementation of the Comparable interface.
 * @author Maxim Tiourin
 * @version 1.02
 */
public class BinaryHeap<T> {
	/**
	 * Initializes the heap with the default heap size of 20.
	 */
	public BinaryHeap() {
		allocationIncrement = DEFAULT_HEAPSIZE;
		init(DEFAULT_HEAPSIZE);
	}
	
	/**
	 * Define a custom heap size to tailor to your needs, the larger the default
	 * size, then the smaller the amount of times the Binary Heap will need to 
	 * allocate memory whenever elements are being added.
	 * @param size size to start the heap at
	 */
	public BinaryHeap(int size) {
		allocationIncrement = size;		
		init(size);
	}
	
	/**
	 * Initialize the heap
	 */
	@SuppressWarnings("unchecked")
	private void init(int size) {
		heap = (T[]) new  Object[size + FIRST_ELEMENT];
		elementCount = 0;
	}
	
	/**
	 * Add the element into the heap and sort it. Reallocates if needed.
	 * @param e element being added
	 */
	@SuppressWarnings("unchecked")
	public void add(T e) {
		elementCount++;
		int insertPos = elementCount;
		
		heap[insertPos] = e; //Insert element at end of the heap
		
		/* Check parents of the child until the object sortValue is greater than
		 * or equal to it's parent, or the child has reached the top most node.
		 */
		int currentPos = insertPos;
		int nextPos;
		boolean settle = false;
		while ((currentPos > FIRST_ELEMENT) && (!settle)) {
			nextPos = (int) Math.floor(currentPos / 2); //Gets the parent of the child node rounded down.
			if (((Comparable<T>) e).compareTo(heap[nextPos]) < 0) {
				//Swap the two elements
				T newChild = heap[nextPos];
				heap[nextPos] = e;
				heap[currentPos] = newChild;
				currentPos = nextPos; //update the elements new position and continue sorting
			}
			else {
				settle = true; //Keeps the element at its current position in the heap and stops sorting
			}
		}
		
		allocate();
	}
	
	/**
	 * Updates the heap starting at the position of the given element. This is useful
	 * when elements have their sortValues change and you don't want to clear the heap and
	 * add all of the elements to be sorted all over again.
	 * @param e element to update
	 */
	@SuppressWarnings("unchecked")
	public void update(T e) {
		int updatePos = FIRST_ELEMENT;
		
		//Find the element in the heap
		for (int i = FIRST_ELEMENT; i < elementCount + 1; i++) {
			if (heap[i].equals(e)) {
				updatePos = i;
			}
		}
		
		/* Check parents of the child until the object sortValue is greater than
		 * or equal to it's parent, or the child has reached the top most node.
		 */
		int currentPos = updatePos;
		int nextPos;
		boolean settle = false;
		while ((currentPos > FIRST_ELEMENT) && (!settle)) {
			nextPos = (int) Math.floor(currentPos / 2); //Gets the parent of the child node rounded down.
			if (((Comparable<T>) e).compareTo(heap[nextPos]) < 0) {
				//Swap the two elements
				T newChild = heap[nextPos];
				heap[nextPos] = e;
				heap[currentPos] = newChild;
				currentPos = nextPos; //update the elements new position and continue sorting
			}
			else {
				settle = true; //Keeps the element at its current position in the heap and stops sorting
			}
		}
	}
	
	/**
	 * Returns the topmost element of the heap, while also removing it from the heap. Does not reallocate size.
	 * @return topmost element
	 */
	@SuppressWarnings("unchecked")
	public T pop() {
		T element = heap[FIRST_ELEMENT];
		
		if (element != null) {
			heap[FIRST_ELEMENT] = heap[elementCount]; //Move the last element to the top most position
			heap[elementCount] = null;
			elementCount--;
		
			int currentPos; //u
			int nextPos = FIRST_ELEMENT; //v
			boolean settle = false;		
			while (!settle) {
				currentPos = nextPos;			
				//Checks to see if the parent and children need to be swapped
				if ((2 * currentPos) + 1 <= elementCount) {
					//Get the child with the lowestSortValue
					if (((Comparable<T>) heap[currentPos]).compareTo(heap[2 * currentPos]) > 0) {
						//If the parent is greater than the first child, set nextPos to first child
						nextPos = 2 * currentPos;
					}
					if (((Comparable<T>) heap[nextPos]).compareTo(heap[(2 * currentPos) + 1]) > 0) {
						//If the parent(or new parent) is greater than the second child, set nextPos to second child
						nextPos = (2 * currentPos) + 1;
					}
				}
				else if ((2 * currentPos) <= elementCount) {
					if (((Comparable<T>) heap[currentPos]).compareTo(heap[2 * currentPos]) > 0) {
						//If the parent is greater than the first child, set nextPos to first child
						nextPos = 2 * currentPos;
					}
				}
				
				//Swap parent and child if it was found the parent is greater
				if (currentPos != nextPos) {
					T newChild = heap[currentPos];
					heap[currentPos] = heap[nextPos];
					heap[nextPos] = newChild;
				}
				else {
					settle = true; //Parent remains where it is
				}
			}
		}
		
		return element;
	}
	
	/**
	 * Removes the element from the heap
	 * @return element
	 */
	@SuppressWarnings("unchecked")
	public T remove(T e) {
		T element = null;
		int updatePos = FIRST_ELEMENT;
		for (int i = FIRST_ELEMENT; i < elementCount + 1; i++) {
			if (heap[i].equals(e)) {
				element = heap[i];
				updatePos = i;
			}
		}
		
		if (element != null) {
			heap[updatePos] = heap[elementCount]; //Move the last element to the top most position
			heap[elementCount] = null;
			elementCount--;
		
			int currentPos; //u
			int nextPos = updatePos; //v
			boolean settle = false;		
			while (!settle) {
				currentPos = nextPos;			
				//Checks to see if the parent and children need to be swapped
				if ((2 * currentPos) + 1 <= elementCount) {
					//Get the child with the lowestSortValue
					if (((Comparable<T>) heap[currentPos]).compareTo(heap[2 * currentPos]) > 0) {
						//If the parent is greater than the first child, set nextPos to first child
						nextPos = 2 * currentPos;
					}
					if (((Comparable<T>) heap[nextPos]).compareTo(heap[(2 * currentPos) + 1]) > 0) {
						//If the parent(or new parent) is greater than the second child, set nextPos to second child
						nextPos = (2 * currentPos) + 1;
					}
				}
				else if ((2 * currentPos) <= elementCount) {
					if (((Comparable<T>) heap[currentPos]).compareTo(heap[2 * currentPos]) > 0) {
						//If the parent is greater than the first child, set nextPos to first child
						nextPos = 2 * currentPos;
					}
				}
				
				//Swap parent and child if it was found the parent is greater
				if (currentPos != nextPos) {
					T newChild = heap[currentPos];
					heap[currentPos] = heap[nextPos];
					heap[nextPos] = newChild;
				}
				else {
					settle = true; //Parent remains where it is
				}
			}
		}
		
		return element;
	}
	
	/**
	 * Returns the topmost element of the heap
	 * @return topmost element
	 */
	public T peek() {
		return heap[FIRST_ELEMENT];
	}
	
	/**
	 * Returns the element with the given index
	 * @param index of element
	 * @return element with given index
	 */
	public T get(int index) {
		return heap[index];
	}
	
	/**
	 * Clears the heap and reallocates
	 */
	public void clear() {
		init(allocationIncrement);
	}
	
	/**
	 * Reallocates the heap if needed. Don't call this unless you have popped a
	 * lot of elements off and want to free up some of the empty space. Note that
	 * the heap only allocates in increments of the supplied heap size, or the 
	 * default size of 20.
	 */
	@SuppressWarnings("unchecked")
	public void allocate() {
		if (heap.length <= FIRST_ELEMENT + elementCount) {
			T newHeap[] = (T[]) new Object[heap.length + allocationIncrement];
			//System.arraycopy(heap, FIRST_ELEMENT, newHeap, FIRST_ELEMENT, elementCount);
			for (int i = FIRST_ELEMENT; i < elementCount + 1; i++) {
				newHeap[i] = heap[i];
			}
			heap = newHeap;
		}
	}
	
	/**
	 * Returns the amount of elements in the heap
	 * @return heap size
	 */
	public int getSize() {
		return elementCount;
	}
	
	/**
	 * Returns a string representation of the binary heap
	 * @return string representation
	 */
	public String toString() {
		String str = "[";
		
		if (elementCount > 0) {
			for (int i = FIRST_ELEMENT; i < elementCount + 1; i++) {
				if (i < elementCount) {
					str += heap[i].toString() + ", ";
				}
				else {
					str += heap[i].toString() + "";
				}
			}
		}
		str += "]";
		
		return str;
	}
	
	private T heap[];
	private int elementCount;
	private int allocationIncrement;
	private final int DEFAULT_HEAPSIZE = 20;
	private final int FIRST_ELEMENT = 1;	
}