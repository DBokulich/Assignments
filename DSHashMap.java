import java.util.Iterator;

//FIXME Gets stuck in a loop when iterating

/*
 * HashMap class written by the 2022 ADS class
 * 
 * V is the type parameter, the type of the value we will store.
 * For now, all of our keys will be Strings.
 */

class DSHashMap < V > implements Iterable <String> {
  // The backing array - contains the values
  DSArrayList < DSArrayList < KVP >> a; // the backing DSArrayList
  int capacity; // Size of the backing array
  int numItems; // Number of KVPs added to the DSHashMap
  int maxChainLength; // for analysis
  int totalCount;

  // A Java constructor is identified by three properties
  // The same name as the class
  // public
  // no return type, not even void
  public DSHashMap() {
    capacity = 2;
    a = new DSArrayList < > (capacity);
    numItems = 0;
    maxChainLength = 0;
    totalCount = 0;
  }

  /**
   * Add a key,value pair to the DSHashMap.
   * If the key is already there, update the value
   * 
   * @param key
   * @param value
   */
  void put(String key, V value) {

    // Check to see if our DSHashMap is getting crowded
    if (numItems > capacity / 2) {
      rehash();
    }

    // Give us the location of this key's value in the backing array
    int location = hash(key);
    if (a.get(location) == null) {
      a.put(location, new DSArrayList < KVP > (10));
      a.get(location).add(new KVP(key, value));
      numItems++;
    } else { // Already a chain here, with stuff in it
      // search that chain for our key.
      for (int i = 0; i < a.get(location).length(); i++) {
        KVP kvp = a.get(location).get(i);
        // If we find the key, replace the value
        if (kvp.key.equals(key)) {
          a.get(location).replace(i, new KVP(key, value));
          return;
        }
        if (i >= 3) {
          System.out.println("OMG! 3! " + totalCount);
          totalCount++;
        }
      }
      // If we don't find the key, create a new KVP and add it to the chain
      a.get(location).add(new KVP(key, value));
      numItems++;
      int chainLength = a.get(location).length;
      if (chainLength > maxChainLength) {
        maxChainLength = chainLength;
        System.out.println("New max chain length is " + maxChainLength);
      }
    }
  }

  /*
   * Double the size of the backing DSArrayList
   * Move all KVPs to their new locations and chains
   */
  private void rehash() {
    // Save a reference to the current backing array
    DSArrayList < DSArrayList < KVP >> olda = a;

    // Create a new, larger backing array
    int newCapacity = 2 * this.capacity;
    a = new DSArrayList < > (newCapacity);
    this.capacity = newCapacity;
    System.out.println("New capacity is " + this.capacity);

    // Loop over old backing array
    this.numItems = 0;
    this.maxChainLength = 0;
    for (DSArrayList < KVP > chain: olda) {
      if (chain == null) continue;

      for (KVP kvp: chain) {
        if (kvp == null) break;
        this.put(kvp.key, kvp.value);
      }
    }
  }

  /**
   * Return the value associated with this key
   * If the key is not in the DSHashMap, return an exception
   * 
   * @param key
   * @return The value of type V stored with this key
   */
  V get(String key) {
    int location = hash(key);
    if (a.get(location) == null) {
      throw new IndexOutOfBoundsException("Key not found: " + key);
    } else { // Already a chain here, with stuff in it
      // search that chain for our key.
      for (int i = 0; i < a.get(location).length(); i++) {
        KVP kvp = a.get(location).get(i);
        System.out.printf("loc = %d, i = %d: Looking for %s, found %s\n",
          location, i, key, kvp.key);
        // If we find the key, replace the value
        if (kvp.key.equals(key)) {
          return kvp.value;
        }
      }
      // If we don't find the key, create a new KVP and add it to the chain
      throw new IndexOutOfBoundsException("Key not found: " + key);
    }
  }

  // This is the hash function. Tells where a string's value
  // should be stored in the backing array
  int hash(String s) {
    int rv = 0;

    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      rv = (rv * 7 + c) % capacity;
    }
    return rv;
  }

  void fun(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      System.out.println("Character " + c + "'s ASCII value is " + (int) c);
    }
  }

  /**
   * Inner class for Key-Value pairs
   */
  class KVP {
    String key;
    V value;

    public KVP(String s, V v) {
      key = s;
      value = v;
    }
  }
  
  public Iterator < String > iterator() {
    return new Iterator < String > () {
      int index = 0; // current chain
      int index2 = 0; // current item
      int hasRemaining = numItems; // remaining KVP
      

      public boolean hasNext() { // if it has another value to return, return true
        // System.out.println("GOT HERE #7: " + hasRemaining);
        return (hasRemaining > 0);
      }

      public String next() {
        // System.out.println("GOT HERE #2, capacity: " + capacity);
        // System.out.println("index: " + index);

        // for each index in a starting at the last index
        for (int j = index; j < capacity; j++) {
          // System.out.println("GOT HERE #3");
          //  if the chain is not null
          if (a.get(index) != null) {
            // for each index in the current chain, starting at the last index
            for (int i = index2; i < a.get(index).length(); i++) {
              // System.out.println("GOT HERE #4");
              // the current KVP we are looking at
              KVP currItem = a.get(index).get(index2);
              // if the KVP is null
              if (currItem == null) {
                // System.out.println("GOT HERE #5");
                // increment index2 and move on
                index2++; 
              } else {
                // System.out.println("GOT HERE #6");
                // move the index forward so we don't look at the same key again
                index2++;
                // decrease the amount of remaining KVP by 1
                hasRemaining--;
                // return the KVP key
                return currItem.key;
              }
            }
          }
          // when the end of a chain is reached without finding a non null KVP, increment index and look at that chain
          index++;
          index2 = 0;
        }
        System.out.println("SHOULDN'T BE HERE");
        return "";
      }
    };
  }
}
