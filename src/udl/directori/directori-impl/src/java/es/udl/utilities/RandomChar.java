package es.udl.utilities.ldap;


import java.util.Random;
 
public class RandomChar {
	private final Random rnd=new Random();
	private static final int maxVal=127;
		
	public char getNextChar(){
		return (char)rnd.nextInt(maxVal); 
	}
 
	public static void main(String[] args) {
		RandomChar chr=new RandomChar();
		for(int i=0;i<100;i++){
			System.out.println(""+chr.getNextChar());
		}
	}
}

