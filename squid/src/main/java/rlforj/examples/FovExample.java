package rlforj.examples;

import java.util.Random;

import rlforj.los.IFovAlgorithm;
import rlforj.los.PrecisePermissive;
import rlforj.los.ShadowCasting;

public class FovExample
{

	/**
	 * Each time creates a 21x21 area with random obstacles and
	 * runs ShadowCasting and Precise Permissive algoritms 
	 * on it, printing out the results in stdout.
	 * @param args
	 */
	public static void main(String[] args)
	{
		ExampleBoard b = new ExampleBoard(21, 21);
		Random rand=new Random();
		for(int i=0; i<30; i++) {
			b.setObstacle(rand.nextInt(21), rand.nextInt(21));
		}
		
		System.out.println("ShadowCasting");
		ShadowCasting sc =new ShadowCasting();
		sc.visitFieldOfView(b, 10, 10, 9);
		b.print(10, 10);
		
		b.resetVisited();

		System.out.println("Precise Permissive");
		PrecisePermissive pp =new PrecisePermissive();
		pp.visitFieldOfView(b, 10, 10, 9);
		b.print(10, 10);

		b.resetVisited();
		for(int i=0; i<30; i++) {
			b.setObstacle(rand.nextInt(21), rand.nextInt(21));
		}

		System.out.println("Precise Permissive");
		pp.visitFieldOfView(b, 10, 10, 9);
		b.print(10, 10);
	}
}
