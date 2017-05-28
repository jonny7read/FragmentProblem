package fragment.submissions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class JonathanRead {
	public static void main(String[] args) {
		try (BufferedReader in = new BufferedReader(new FileReader(args[0]))) {
			String fragmentProblem;
			while ((fragmentProblem = in.readLine()) != null) {
				System.out.println(reassemble(fragmentProblem, false));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String reassemble(String line, boolean isTest) throws InterruptedException {
		String input = !isTest ? line : "this is the best;the best i've ever had.";
		ArrayList<String> frags = new ArrayList<String>(Arrays.asList(input.split(";")));
		String output = "";

		System.out.println("input: " + input);

		// while more than one fragment
		while (frags.size() > 1) {
			// for each fragment in the line
			for (int firstWord = 0; firstWord < frags.size(); firstWord++) {
				// for each other fragment in the line
				for (int secondWord = 0; secondWord < frags.size(); secondWord++) {
					// ensure not comparing itself
					if (firstWord != secondWord) {
						// try to merge the two fragments
						tryMerge(frags, firstWord, secondWord);
					}

				}
			}
			System.out.println("------" + frags.size() + " fragments left");
		}
		output = "output: " + frags.get(0);
		return output;
	}

	private static void tryMerge(ArrayList<String> frags, int firstWord, int secondWord) throws InterruptedException {
		int overlap = 0;
		boolean isOverlap = false;

		// for each letter in the first fragment
		int firstLetter = 0;
		// for each letter in the second fragment
		for (int secondLetter = 0; secondLetter < frags.get(secondWord).length(); secondLetter++) {
			char ch1 = frags.get(firstWord).charAt(firstLetter);
			char ch2 = frags.get(secondWord).charAt(secondLetter);
			// compare the letters
			// if match
			if (ch1 == ch2) { // match
				// if first match, set overlap
				if (!isOverlap) { // is first match?
					overlap = secondLetter;
					isOverlap = true;
				} else if (secondLetter == frags.get(secondWord).length() - 1) {
					// last match so full match found
					System.out.println(frags.get(secondWord).substring(0, overlap) + "|" + frags.get(firstWord));
					// merge the two fragments
					frags.set(secondWord, frags.get(secondWord).substring(0, overlap) + frags.get(firstWord));
					// delete the second
					frags.remove(firstWord);
					return;
				} else if (firstLetter == frags.get(firstWord).length() - 1) {
					// match in middle of fragment, abort!
					return;
				}
				firstLetter++;
			} else if (isOverlap) {
				overlap = 0;
				isOverlap = false;
				firstLetter = 0;
			}
		}
	}
}
