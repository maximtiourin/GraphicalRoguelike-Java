package com.fizzikgames.roguelike.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Can be given a list of objects with probability weights, and will return an object at random
 * using those weights rolling for a random integer out of highestRoll. Lowest weight item types will roll first against their weight, 
 * and then go down the list with higher weight item types, the highest weight item type will return if all other rolls fail.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class RandomBag<T> {
    private class Pairing<C> implements Comparable<Pairing<C>> {
        private C type;
        private int weight;
        
        public Pairing(C type, int weight) {
            this.type = type;
            this.weight = weight;
        }
        
        public C getType() {
            return type;
        }
        
        public int getWeight() {
            return weight;
        }

        @Override
        public int compareTo(Pairing<C> other) {
            return getWeight() - other.getWeight();
        }
    }
    
    private Random rng;
    private int highestRoll;
    private ArrayList<Pairing<T>> pairings;
    
    public RandomBag(Random rng, int highestRoll) {
        this.rng = rng;
        this.highestRoll = highestRoll;
        this.pairings = new ArrayList<Pairing<T>>();
    }
    
    public void addPairing(T type, int weight) {
        pairings.add(new Pairing<T>(type, weight));
        
        Collections.sort(pairings);
    }
    
    @SuppressWarnings("unchecked")
    public T getRandomObject() {
        ArrayList<Pairing<T>> bag = (ArrayList<Pairing<T>>) pairings.clone();
        Pairing<T> lastPairing = bag.remove(bag.size() - 1); //Get and remove last pairing since it will return no matter what
        
        for (Pairing<T> pairing : bag) {
            int weight = pairing.getWeight();
            int roll = rng.nextInt(highestRoll + 1);
            
            if (roll <= weight) {
                return pairing.getType();
            }
        }
        
        return lastPairing.getType();
    }
}
