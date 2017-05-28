package fragment.submissions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class JonathanRead {
	private static ArrayList<String> fragments;

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

	public static String reassemble(String input, boolean isTest) throws InterruptedException {
		HashMap<Integer, FragmentMerge> overlappingFragments;
		String output = "";

		fragments = new ArrayList<String>(Arrays.asList(input.split(";")));

		// while more than one fragment exists
		while (fragments.size() > 1) {
			// get all overlapping fragments
			overlappingFragments = getOverlappingFragments();

			// merge the best match on the line
			mergeBiggestOverlap(overlappingFragments);
		}

		output = fragments.get(0);
		return output;
	}

	private static void mergeBiggestOverlap(HashMap<Integer, FragmentMerge> merges) {
		int maxOverlapAmount = 0;
		int maxOverlapKey = 0;
		int currentOverlapAmount = 0;

		// check there is something to merge
		if (merges == null || merges.size() == 0) {
			System.out.println("Nothing to merge");
			return;
		}

		// for each match that was stored
		for (Integer key : merges.keySet()) {
			// get the overlap amount
			currentOverlapAmount = merges.get(key).getOverlapAmount();

			// if it's the best match on the line (so far) then store the key
			if (currentOverlapAmount > maxOverlapAmount) {
				maxOverlapAmount = currentOverlapAmount;
				maxOverlapKey = key;
			}
		}

		// merge the best match
		merges.get(maxOverlapKey).mergeFragments();
	}

	private static HashMap<Integer, FragmentMerge> getOverlappingFragments() {
		HashMap<Integer, FragmentMerge> overlappingFragments = new HashMap<Integer, FragmentMerge>();

		// for each fragment in the line
		for (int endingFragment = 0; endingFragment < fragments.size(); endingFragment++) {
			// for each other fragment in the line
			for (int startingFragment = 0; startingFragment < fragments.size(); startingFragment++) {
				// ensure not comparing itself
				if (endingFragment != startingFragment) {
					// see if there is an overlap
					int overlap = getOverlap(fragments, startingFragment, endingFragment);

					// if the fragment was removed, skip to the next one
					if (overlap == -1) {
						break;
					} else if (overlap > 0) {
						// otherwise check if this fragment already has a match
						FragmentMerge existingOverlap = overlappingFragments.get(startingFragment);

						// if it doesn't, or this one is better, then store the merge
						if (existingOverlap == null || overlap > existingOverlap.getOverlapAmount()) {
							FragmentMerge merge = new FragmentMerge(startingFragment, startingFragment, endingFragment, overlap);
							overlappingFragments.put(startingFragment, merge);
						}
					}
				}
			}
		}

		return overlappingFragments;
	}

	private static int getOverlap(ArrayList<String> fragments, int startingFragment, int endingFragment) {
		int overlap = 0;
		boolean isOverlap = false;
		int matchLetterIndex = 0;

		// for each letter in the second fragment
		for (int checkLetterIndex = 0; checkLetterIndex < fragments.get(startingFragment).length(); checkLetterIndex++) {
			char ch1 = fragments.get(endingFragment).charAt(matchLetterIndex);
			char ch2 = fragments.get(startingFragment).charAt(checkLetterIndex);

			// if the letters match
			if (ch1 == ch2) { // match
				// if this is the first match, set overlap
				if (!isOverlap) { // is first match?
					overlap = checkLetterIndex;
					isOverlap = true;
				} else if (matchLetterIndex == fragments.get(endingFragment).length() - 1) {
					// match in middle of fragment, abort!
					fragments.remove(endingFragment);
					return -1;
				} else if (checkLetterIndex == fragments.get(startingFragment).length() - 1) {
					// last match so full match found
					return overlap;
				}
				matchLetterIndex++;
			} else if (isOverlap) {
				// letters don't match - reset overlap
				overlap = 0;
				isOverlap = false;
				matchLetterIndex = 0;
			}
		}
		return 0;
	}

	static class FragmentMerge {

		private int	mId;
		private int	mStartingFragmentIndex;
		private int	mEndingFragmentIndex;
		private int	mOverlapIndex;
		private int	mOverlapAmount;

		FragmentMerge(int id, int startingFragment, int endingFragment, int overlapIndex) {
			mId = id;
			mStartingFragmentIndex = startingFragment;
			mEndingFragmentIndex = endingFragment;
			mOverlapIndex = overlapIndex;
			mOverlapAmount = fragments.get(startingFragment).length() - overlapIndex;
		}

		public int getmId() {
			return mId;
		}

		public int getStartingFragmentIndex() {
			return mStartingFragmentIndex;
		}

		public int getEndingFragmentIndex() {
			return mEndingFragmentIndex;
		}

		public int getOverlapIndex() {
			return mOverlapIndex;
		}

		public int getOverlapAmount() {
			return mOverlapAmount;
		}

		public String getStartingFragment() {
			return fragments.get(mStartingFragmentIndex);
		}

		public String getEndingFragment() {
			return fragments.get(mEndingFragmentIndex);
		}

		public void mergeFragments() {
			fragments.set(mId, getStartingFragment().substring(0, getOverlapIndex()) + getEndingFragment());
			fragments.remove(mEndingFragmentIndex);
		}

	}
}
