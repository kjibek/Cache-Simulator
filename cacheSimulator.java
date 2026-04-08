import java.util.Scanner;
import java.util.Arrays;
public class cacheSimulator {
	// create datatype cache w/ attributes valid & dirty bit, slot, tag, and data block
	private static class cache {
		private boolean valid;
		private boolean dirty;
		private int slot;
		private int tag;
		private String [] data;
		
		public cache (boolean valid, boolean dirty, int slot, int tag, String [] data) {
			this.valid = valid;
			this.dirty = dirty;
			this.slot = slot;
			this.tag = tag;
			this.data = data;
		}
	}
	
	// cache array with 16 slots
	public cache [] cacheArray = new cache [16];
	// memory array of size 2k
	public String [] memory = new String [2048];
	
	// initialize empty cache & initialize memory array w/ values respective to their indices (in hex)
	public cacheSimulator() {
		for (int i = 0; i < memory.length; i++) memory[i] = Integer.toHexString(i);
		for (int i = 0; i < 16; i++) {
			cacheArray[i] = new cache(false, false, i, 0, new String [16]);
			for (int j = 0; j < 16; j++) cacheArray[i].data[j] = Integer.toHexString(0);
		}
	}
	
	// deconstruct user input (address) into tag, index, and offset
	public int tag (int address) {
		return (address >>> 8);
	}
	public int index (int address) {
		return ((address >>> 4) & 0xF);
	}
	public int offset (int address) {
		return (address & 0xF);
	}
	
	// print out display of cache at the current moment
	public void display(cache [] cacheArray) {
		System.out.println("VALID | DIRTY | SLOT | TAG | DATA");
		for (int k = 0; k < 16; k++) {
			System.out.print(
				    cacheArray[k].valid + "   " + cacheArray[k].dirty + "     " + cacheArray[k].slot + "     " 
				    + cacheArray[k].tag + "    " + Arrays.toString(cacheArray[k].data) + "\n");
		}
	}
	
	// read cache operation method
	public void read(cache [] cacheArray, int address) {
		// check if slot valid. if not valid => call cache miss method. else check if slot has dirty bit true & diff tag => update memory, then pull 
		if (cacheArray[index(address)].valid == true) {
			// if slot valid and tag matches => cache hit
			if (cacheArray[index(address)].tag == tag(address)) System.out.println("Cache hit! Current value is... " + cacheArray[index(address)].data[offset(address)]);
			else {
				// if slot valid and tag doesnt match and dirty => update memory, then pull from memory
				if (cacheArray[index(address)].dirty == true) updateMemory(cacheArray, address);
				// if slot valid and tag doesnt match => cache miss
				cacheMiss(cacheArray, address);
			}
		}
		// if slot not valid => cache miss
		else cacheMiss(cacheArray, address);		
	}
	
	// write cache operation method
	public void write(cache [] cacheArray, int address) {
		if (cacheArray[index(address)].valid == true) {
			// if slot valid and tag matches => cache hit => writeHelper()
			if (cacheArray[index(address)].tag == tag(address)) {
				System.out.println("Cache hit! Current value is... " + cacheArray[index(address)].data[offset(address)]);
				writeHelper(cacheArray, address);
			}
			else {
				// if slot valid and tag doesnt match and dirty => update memory, pull from memory, writeHelper()
				if (cacheArray[index(address)].dirty == true) updateMemory(cacheArray, address);
				cacheMiss(cacheArray, address);
				writeHelper(cacheArray, address);
			}
		}
		// if slot not valid => cache miss => writeHelper()
		else {
			cacheMiss(cacheArray, address);
			writeHelper(cacheArray, address);
		}
	}
	
	// write helper method to not repeat code
	public void writeHelper(cache[] cacheArray, int address) {
		Scanner scnr = new Scanner(System.in);
		System.out.println("Please enter new value to enter: "); 
		String subMem = scnr.nextLine(); // get user input for new value
		cacheArray[index(address)].data[offset(address)] = subMem; // update old value w/ new value in cache
		cacheArray[index(address)].dirty = true; // set dirty bit to true
		System.out.println("Cache data successfully updated with " + subMem);
	}
	
	// update memory method for when dirty block needs to be replaced
	public void updateMemory(cache[] cacheArray, int address) {
	    int memIndex = address & 0xFF0; // start index at first offset
	    for (int i = 0; i < 16; i++) { // update memory
	        String hex = cacheArray[index(address)].data[i];
	        if (hex.length() == 1) hex = "0" + hex;
	        memory[memIndex + i] = hex;
	    }
	    cacheArray[index(address)].dirty = false; // set dirty to false
	}

	// cache miss method
	public void cacheMiss(cache[] cacheArray, int address) {
	    System.out.println("Cache Miss. Loading from memory...");
	    cacheArray[index(address)].valid = true; // set valid to true
	    cacheArray[index(address)].tag = tag(address); // update the tag for this block
	    int memIndex = address & 0xFF0; // start at 0 offset
	    for (int i = 0; i < 16; i++) { // copy 16 bytes into cache
	        String val = memory[memIndex + i];
	        if (val.length() > 2) val = val.substring(val.length() - 2); // keep last 2 digits
	        cacheArray[index(address)].data[i] = val;
	    }
	    cacheArray[index(address)].dirty = false; // set dirty to false
	    System.out.println("Value is... " + cacheArray[index(address)].data[offset(address)]);
	}

	// get user input method
	public void input() {
	    Scanner scnr = new Scanner(System.in);
	    String operation = "";
	    while (!operation.equals("EXIT") ) { // while user doesnt exit, will continue to ask for cache operation
	    	String hexAddress; int address;
		    System.out.println("Choose operation: Read byte, Write byte, or Display Byte. "
		    		+ "\nType 'R' for Read, 'W' for Write, and 'D' for Display."
		    		+ "\nIf you would like to exit, please enter 'EXIT'.");
		    operation = scnr.nextLine().toUpperCase(); // get user input for operation type
		    if (operation.equals("exit")) break;
		    else if (operation.equals("D")) display(cacheArray); // pass display method if "d"
		    else if (operation.equals("R")) {
		        System.out.println("Please enter an address: ");
		        hexAddress = scnr.nextLine().trim(); // get user address (in string)
		        address = Integer.parseInt(hexAddress, 16); // convert string address to int
		        read(cacheArray, address); // pass read method
		    } 
		    else if (operation.equals("W")) {
		        System.out.println("Please enter an address: ");
		        hexAddress = scnr.nextLine().trim(); // get user address (in string)
		        address = Integer.parseInt(hexAddress, 16); // convert string address to int
		        write(cacheArray, address); // pass write method
		    }
	    }
	    	}
	
	public static void main(String[] args) {
		cacheSimulator sim = new cacheSimulator();
		sim.input();
	}

}
