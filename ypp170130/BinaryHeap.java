// Starter code for LP5

// Change to your netid
package ypp170130;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 *     Team No: 39
 *     @author Pranita Hatte: prh170230
 *     @author Prit Thakkar: pvt170000
 *     @author Shivani Thakkar: sdt170030
 *     @author Yash Pradhan: ypp170130
 *
 *     Long Project 5: Minimum spanning tree algorithms
 *
 *     Implementation of Binary Heap and its nested class IndexedHeap.
 */

public class BinaryHeap<T extends Comparable<? super T>> {
    Comparable[] pq;
    int size;

    /**
     * Constructor for building an empty priority queue using natural ordering of T
     * @param maxCapacity size of BinaryHeap
     */
    public BinaryHeap(int maxCapacity) {
        pq = new Comparable[maxCapacity];
        size = 0;
    }

    /**
     * method to check BinaryHeap is full or not
     * @return true if BinaryHeap is full, else false
     */
    private boolean isFull(){
        if(size == pq.length){
            return true;
        }
        return false;
    }

    /**
     * adds element x of type T to pq, resized the BinaryHeap if size if full
     * @param x element to be added
     * @return true if added successfully
     */
    public boolean add(T x) {
        if(isFull()) {
            resize();
        }
        move(size, x);
        percolateUp(size);
        size++;
        return true;
    }

    /**
     * adds element x of type T
     * @param x element to be added
     * @return true if added successfully
     */
    public boolean offer(T x) {
        return add(x);
    }

    // throw exception if pq is empty

    /**
     * remove element from BinaryHeap
     * @return element on top of BinaryHeap
     * @throws NoSuchElementException if BinaryHeap is empty
     */
    public T remove() throws NoSuchElementException {
        T result = poll();
        if(result==null){
            throw new NoSuchElementException();
        }
        return result;
    }

    /**
     * remove element from BinaryHeap
     * @return element on top of BinaryHeap, else null if BinaryHeap is empty
     */
    public T poll() {
        if(isEmpty())
            return null;
        T min = (T) pq[0];
        move(0, pq[--size]);
        percolateDown(0);
        return min;
    }

    /**
     *
     * @return minimum element in BinaryHeap
     */
    public T min() {
        return peek();
    }

    // return null if pq is empty

    /**
     *
     * @return null if BinaryHeap is empty, else top element on BinaryHeap
     */
    public T peek() {
        if(isEmpty()){
            return null;
        }
        return (T) pq[0];
    }

    /**
     * @param i index of the element
     * @return parent of ith element
     */
    int parent(int i) {
        return (i-1)/2;
    }

    /**
     * @param i index of element
     * @return left child of ith element
     */
    int leftChild(int i) {
        return 2*i + 1;
    }

    /**
     * pq[index] may violate heap order with parent, so rearranging
     * @param index index of the element
     */
    void percolateUp(int index) {
        T x = (T) pq[index];
        while((index > 0) && (x.compareTo((T)pq[parent(index)]) == -1)){
            move(index, pq[parent(index)]);
            index = parent(index);
        }
        move(index, x);
    }

    /**
     * pq[index] may violate heap order with children, so rearranging
     * @param index index of the element
     */
    void percolateDown(int index) {
        T x = (T) pq[index];
        int c = leftChild(index);
        while(c < size) {
            if(c+1<size && pq[c].compareTo(pq[c+1]) > 0) {
                c = c + 1;
            }
            if(x.compareTo((T)pq[c]) <= 0) {
                break;
            }
            move(index, pq[c]);
            index = c;
            c = leftChild(index);
        }
        move(index, x);
    }

    /**
     * move element x to destination index
     * @param dest index
     * @param x element
     */
    void move(int dest, Comparable x) {
        pq[dest] = x;
    }

    /**
     * @param a element
     * @param b element
     * @return 1 if a>b, -1 if a<b, else 0
     */
    int compare(Comparable a, Comparable b) {
        return ((T) a).compareTo((T) b);
    }

    /**
     * Create a heap.
     */
    void buildHeap() {
        for(int i=parent(size-1); i>=0; i--) {
            percolateDown(i);
        }
    }

    /**
     * check BinaryHeap is empty or not
     * @return true if empty, else false
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * @return total elements in BinaryHeap
     */
    public int size() {
        return size;
    }

    /**
     * Resize array to double the current size
     */
    void resize() {
        int newLength = pq.length * 2;
        Comparable[] pq1 = new Comparable[newLength];

        for(int i = 0; i< pq.length; i++){
            pq1[i] = pq[i];
        }
        pq = pq1;

    }

    public interface Index {
        public void putIndex(int index);
        public int getIndex();
    }

    public static class IndexedHeap<T extends Index & Comparable<? super T>> extends BinaryHeap<T> {

        /**
         *  Build a priority queue with a given array
         * @param capacity size of IndexedHeap
         */
        IndexedHeap(int capacity) {
            super(capacity);
        }


        /**
         * restore heap order property after the priority of x has decreased
         * @param x element
         */
        void decreaseKey(T x) {
            percolateUp(x.getIndex());
        }

        @Override
        void move(int i, Comparable x) {
            super.move(i, x);
            ((T)x).putIndex(i);
        }
    }

    public static void main(String[] args) {
        Integer[] arr = {0,9,7,5,3,1,8,6,4,2};
        BinaryHeap<Integer> h = new BinaryHeap(arr.length);

        System.out.print("Before:");
        for(Integer x: arr) {
            h.offer(x);
            System.out.print(" " + x);
        }
        System.out.println();

        for(int i=0; i<arr.length; i++) {
            arr[i] = h.poll();
        }

        System.out.print("After :");
        for(Integer x: arr) {
            System.out.print(" " + x);
        }
        System.out.println();
    }
}