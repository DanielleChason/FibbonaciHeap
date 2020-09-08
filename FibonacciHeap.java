import java.util.Arrays;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 * By: chason1 - 205491236, harelrom - 316553155
 */
public class FibonacciHeap
{
	
	private HeapNode min; //The min of the heap
	private HeapNode first; // The first node in the heap
	private int size=0; //The number of nodes in the heap
	private int trees; //The number of trees in the heap
	protected int marked;// The number of marked trees in the heap
	protected static int cuts; //The number of cuts
	protected static int links; //The number of links
	

	
	public FibonacciHeap() {  //Empty builder
		
		
	}
	
	public FibonacciHeap (HeapNode node, int size) {
		this.min=node;
		this.first=node;
		this.size=size;
		this.trees=1;
	}

   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return this.size==0; //If the size is 0 so the heap is empty
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode node = new HeapNode (key);
    	if (this.isEmpty()) { //Heap is empty
    		this.min=node;
    		node.setPrev(node);
    		node.setNext(node);

    	}
    	
    	else { //Heap is not empty
    		if (node.getKey()<min.getKey()) { //update min, if needed
    			this.min=node;
    		}
    		node.setNext(first);  //set next and prev
    		node.setPrev(this.first.getPrev());
    		this.first.getPrev().setNext(node);
    		first.setPrev(node);
    	}
    	
    	//set all the field 
		this.first=node;
		node.setParent(null);
		node.setChild(null);
		this.size= this.size+1;
		this.trees=this.trees+1;
    	node.mark=false; //Set to unmarked node
    	node.setRank(0);
    	    	
    	return node; 
    	
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */

    public void deleteMin()
    {
    	
    	//make all of min's children roots and update pointers accordingly.
    	//check if min is the only root
    	if (min.getNext() == min) {
    		//check if min is the only node
    		if(min.getChild() == null) {
    			first = min = null;
    			size = trees = marked = 0;
    			return;
    		}
    		else {
    			first = min.getChild();
    			//set parents and marks
    			HeapNode temp = first;
    			temp.setParent(null);
    			if (temp.isMark()) {
    				temp.setMark(false);
    				this.marked--;
    			}
    			temp = temp.getNext();
    			while (temp != first) {
        			temp.setParent(null);
        			if (temp.isMark()) {
        				temp.setMark(false);
        				this.marked--;
        			}
        			temp = temp.getNext();
    			}
    			
    		}
    	}
    	
    	//if its not the only root:
    	else {
    		//if min has no children
    		if(min.getChild() == null) {
    			//if first == min update first
    			if (first == min)
    				first = min.getNext();
    			min.getPrev().setNext(min.getNext());
    			min.getNext().setPrev(min.getPrev());
    		}
    		//if min does have children
    		else {
    			//if first == min update first
    			if (first == min)
    				first = min.getChild();
    			min.getChild().getPrev().setNext(min.getNext());
    			min.getNext().setPrev(min.getChild().getPrev());
    			min.getChild().setPrev(min.getPrev());
    			min.getPrev().setNext(min.getChild());
    			//set parents and marks
    			HeapNode temp = min.getChild();
    			while (temp != min.getNext()) {
        			temp.setParent(null);
        			if (temp.isMark()) {
        				temp.setMark(false);
        				this.marked--;
        			}
        			temp = temp.getNext();
    			}
    		}
    	}
    	

    	//consolidate
    	
    	HeapNode temp = first.getNext();
    	HeapNode originalFirst = first;
    	first.setNext(first);
    	first.setPrev(first);
    	min = first;
    	trees = 1;
    	while (temp != originalFirst) {
    		if (temp.getKey() < min.getKey())
    			min = temp;
    		HeapNode holdNext = temp.getNext();
    		HeapNode innerTemp = first;
    		boolean inserted = false;
    		while (!inserted) {
    			if (temp.getRank() >= innerTemp.getNext().getRank() && innerTemp.getNext() != first) {
    				innerTemp = innerTemp.getNext();
    			}
    			else if (temp.getRank() == innerTemp.getRank()) {
    	    		//used to check if the newly made tree should be first
    	    		boolean checkFirst = false;
    				HeapNode savePrev = innerTemp.getPrev();
    				HeapNode saveNext = innerTemp.getNext();
    				if (innerTemp == first)
    					checkFirst = true;
    				innerTemp = link(innerTemp,temp);
    				//check if those are the only two trees
    				if (saveNext == innerTemp || saveNext == innerTemp.getChild()) {
        				innerTemp.setNext(innerTemp);
        				innerTemp.setPrev(innerTemp);
    				}
    				else {
	    				innerTemp.setNext(saveNext);
	    				innerTemp.setPrev(savePrev);
	    				saveNext.setPrev(innerTemp);
	    				savePrev.setNext(innerTemp);
    				}
    				if (checkFirst) {
    					first = innerTemp;
    				}
    				while (innerTemp.getRank() == innerTemp.getNext().getRank() && innerTemp != innerTemp.getNext()) {
    					savePrev = innerTemp.getPrev();
        				saveNext = innerTemp.getNext().getNext();
        				if (innerTemp.getNext() == first)
        					checkFirst = true;
    					innerTemp = link(innerTemp,innerTemp.getNext());
        				//check if those are the only two trees
        				if (saveNext == innerTemp || savePrev == innerTemp) {
            				innerTemp.setNext(innerTemp);
            				innerTemp.setPrev(innerTemp);
        				}
        				else {
	        				innerTemp.setNext(saveNext);
	        				innerTemp.setPrev(savePrev);
	        				saveNext.setPrev(innerTemp);
	        				savePrev.setNext(innerTemp);
        				}
        				trees -=1;
    				}
    				if (innerTemp.getKey() <= min.getKey())
    					min = innerTemp;
    				if (checkFirst) {
    					first = innerTemp;
    				}
    				inserted = true;
    			}
    			else if (temp.getRank() > innerTemp.getRank()) {
    				temp.setPrev(innerTemp);
    				temp.setNext(innerTemp.getNext());
    				temp.getPrev().setNext(temp);
    				temp.getNext().setPrev(temp);
    				trees+=1;
    				if (temp.getKey() < min.getKey())
    					min = temp;
    				inserted = true;
    			}
    			else {
    				temp.setNext(first);
    				temp.setPrev(first.getPrev());
    				first = temp;
    				temp.getPrev().setNext(temp);
    				temp.getNext().setPrev(temp);
    				trees+=1;
    				inserted = true;
    			}
    		}
    	temp = holdNext;
    	}
    	size -= 1;
    	
    }
    
    

    /**
     * public HeapNode link()
     * 
     * @pre: x.rank = y.rank
     * @post: x = x + y
     */
     public HeapNode link(HeapNode x, HeapNode y){
    	 //make sure we attach big key beneath small key
    	 if (x.getKey() > y.getKey()) {
    		 HeapNode temp;
    		 temp = x;
    		 x = y;
    		 y = temp;
    	 }
    	 //increase x rank
     	x.setRank(x.getRank()+1);
     	
     	//set pointers if x has no children
     	if (x.getChild() == null) {
     		x.setChild(y);
     		y.setNext(y);
     		y.setPrev(y);
     	}
     	//set pointers if x has children
     	else {
     		y.setNext(x.getChild());
     		y.setPrev(y.getNext().getPrev());
     		y.getNext().setPrev(y);
     		y.getPrev().setNext(y);
     		x.setChild(y);
     	}
 		y.setParent(x);
 		links+=1;
     	return x;
     } 

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	HeapNode temp=heap2.first.getPrev(); //add the trees of heap2 to the "end" of this heap
    	heap2.first.setPrev(this.first.getPrev()); //update set and prev
    	this.first.getPrev().setNext(heap2.first);
    	this.first.setPrev(temp);
    	temp.setNext(this.first);
    	
    	if (heap2.min.getKey()<this.min.getKey()) { //update min, if needed
    		this.min=heap2.min;
    	}
    	this.size=this.size+heap2.size;  //update size
    	this.trees=this.trees+heap2.trees;//update num of trees

    }
    
    

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size; 
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	int cnt=this.first.getRank(); 
    	HeapNode node = this.first.getNext();
    	while (node.getKey()!=this.first.getKey()) { //find the highest rank
    		if (node.getRank()>cnt) {
    			cnt=node.getRank();
    		}
    		node=node.getNext();
    	}
    	int[] arr = new int[ cnt+1]; //set the array with the size of the highest rank+1
        arr [this.first.getRank()]+=1;
        node = first.getNext();
        while (node.getKey()!=this.first.getKey()) { //updtaing the array
        	arr[node.getRank()]+=1;
        	node=node.getNext();
        }
        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	this.decreaseKey(x, x.getKey()-this.min.getKey()+1);//x is now the new minimun
    	this.min=x; //update min
    	
    	this.deleteMin();    	
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	
    	
    	x.setKey(x.getKey()-delta); //update key
    	if (x.getKey()<this.min.getKey()) {
			this.min=x;
		}
    	
    
    	if (x.getParent()!=null && x.getKey()<x.getParent().getKey()) {  //if the child is now smaller than the parent
    	
    		HeapNode node = x.getParent();
    		 //cutting the node and his children
    		while (node!=null) {
    			cut(x); //cutting x
        		if (!node.isMark() && node.getParent()!=null) { //the parent is not marked and it is not a root	
        			node.mark=true;
        			this.marked = this.marked+1;
        			break;
        		}
        	//the parent already marked or it is a root
        		x=node;
        		node=node.getParent();
        			
        	}
    			
    	
    	}
    	
    	
    	return; // should be replaced by student code
    }
    
    
    public void cut(HeapNode x) {
		//Add the new root to the list of trees    	
    	int key=x.getKey();
    	if (x.getNext().getKey()!=key) {  //set next and prev after the cutting
    		x.getParent().setChild(x.getNext());
    		x.getNext().setPrev(x.getPrev());
    		x.getPrev().setNext(x.getNext());
    	}
    	else {
    		x.getParent().setChild(null);
    	}
		x.getParent().setRank(x.getParent().getRank()-1);
		
        x.setNext(first); //add x to the root list
		x.setPrev(this.first.getPrev()); //set next and prev of x after the cutting
		this.first.getPrev().setNext(x);
		first.setPrev(x);
		x.setParent(null);
		this.first=x;
		if (x.mark == true) {  //change the root to unmark, if needed
			x.mark=false;
			this.marked=this.marked-1;
		}
		cuts+=1;  //update cut
		this.trees=this.trees+1; //update num of trees
		
    }
    
   
    

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.trees+(2*this.marked); 
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return links;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return cuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k(logk + deg(H)). 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {    
    	
       int [] arr= new int [k];
       int cnt=0;
       if (k==0 || H.size()==0 || k>H.size()) { // return empry array in those cases
    	   return arr;
       }  
       FibonacciHeap helpHeap = new FibonacciHeap (); //Help Heap
       arr[cnt]=H.findMin().getKey(); //insert the first node in the tree
       cnt++;
       HeapNode curr= H.findMin();//curr= root
       HeapNode node = null;
       while (cnt<k && curr!=null) { //do k-1 times
    	   if (curr.getChild()!=null) {
    		  HeapNode n1 =helpHeap.insert(curr.getChild().getKey());
    		  n1.setInOriginal(curr.getChild()); //pointer to the same node in the original heap
    		   node = curr.getChild().getNext();
    		   int key= curr.getChild().getKey();
    		   while (node.getKey()!=key) {
    			   HeapNode n2=helpHeap.insert(node.getKey());  //insert all the sons to helpHeap
    			   n2.setInOriginal(node);  //pointer to the same node in the original heap
    			   node=node.getNext();
    		   }
    	   }
    	   
    	   curr=helpHeap.findMin().getInOriginal();  //find the min from helpHeap in original tree
    	   arr[cnt]=helpHeap.findMin().getKey(); //insert the min in helpHeap to the array
    	   helpHeap.deleteMin(); //delete the min from helpHeap
    	   cnt++;
       }
       
       
        return arr; // should be replaced by student code
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key=0;
	private int rank;
	private boolean mark;
	private HeapNode child;
	private HeapNode next;
	private HeapNode prev;
	private HeapNode parent;
	private HeapNode inOrginial;
		

  	public HeapNode(int key) {
	    this.key = key;
      }

  	public int getKey() {
	    return this.key;
      }
  	
  	public void setKey(int key) { 
	    this.key=key;
      }
  	
  	public int getRank() {
	    return this.rank;
      }
  	
  	public void setRank(int rank) {
	    this.rank=rank;
      }
  	
  	public boolean isMark() {  
	    return this.mark;
      }
  	
  	public void setMark(boolean mark) {  
	    this.mark=mark;
      }
  	
  	public HeapNode getChild() {  
	    return this.child;
      }
  	
  	public void setChild(HeapNode child) {  
	    this.child=child;
      }
  	
  	public HeapNode getNext() {  
	    return this.next;
      }
  	
	public void setNext(HeapNode next) {  
	    this.next=next;
      }
  	
  	public HeapNode getPrev() {  
	    return this.prev;
      }
  	
	public void setPrev(HeapNode prev) {  
	    this.prev=prev;
      }
  	
  	public HeapNode getParent() {  
	    return this.parent;
      }
  	
	public void setParent(HeapNode parent) {  
	    this.parent=parent;
      }
	
	public HeapNode getInOriginal() {  
	    return this.inOrginial;
      }
  	
	public void setInOriginal(HeapNode inOriginal) {  
	    this.inOrginial=inOriginal;
      }
	


    }
    

    public HeapNode getFirst() {
    	return first;
    }
    
	

	public int getNumberOfTrees() {
		
		return this.trees;
	}
	
	public int getSize() {
		
		return this.size;
	}


	
		
	
	

}



