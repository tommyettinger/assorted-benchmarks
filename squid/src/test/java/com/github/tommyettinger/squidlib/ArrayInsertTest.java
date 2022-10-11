package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.utils.Array;

public class ArrayInsertTest {
    public static void main(String[] args) {
        Array<String> array;
        array = Array.with("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        System.out.println("Original array, A through J:");
        System.out.println(array);
        array.insert(0, array.get(array.size - 1));
        System.out.println("Inserting the last item at the start:");
        System.out.println(array);
        array = Array.with("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        array.insert(array.size, array.get(0));
        System.out.println("Inserting the first item at the end:");
        System.out.println(array);
        array = Array.with("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        array.insert(0, array.removeIndex(array.size - 1));
        System.out.println("Moving the last item to the start:");
        System.out.println(array);
        array = Array.with("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
        array.insert(array.size - 1, array.removeIndex(0)); // note that the removeIndex call changes the size!
        System.out.println("Moving the first item to the end:");
        System.out.println(array);
    }
    /*
Original array, A through J:
[A, B, C, D, E, F, G, H, I, J]
Inserting the last item at the start:
[J, A, B, C, D, E, F, G, H, I, J]
Inserting the first item at the end:
[A, B, C, D, E, F, G, H, I, J, A]
Moving the last item to the start:
[J, A, B, C, D, E, F, G, H, I]
Moving the last item to the start:
[B, C, D, E, F, G, H, I, J, A]
     */
}
