package com.example.demo;

public class Test {
	public static void getPermutation(int num) {
		int permuteLen = (int) Math.pow(2, num);
		boolean b[] = new boolean[num];
		for (int i = 0; i < b.length; i++) {
			b[i] = true;
		}

		for (int j = 0; j < permuteLen; j++) {
			for (int i = 0; i < num; i++) {
				System.out.print("  " + b[i] + "  ");
			}
			System.out.println(" ");

			for (int i = num - 1; i >= 0; i--) {
				if (b[i] == true) {
					b[i] = false;
					break;
				} else
					b[i] = true;
			}
		}
	}

	public static void main(String[] args) {
		getPermutation(4);
	}
}
