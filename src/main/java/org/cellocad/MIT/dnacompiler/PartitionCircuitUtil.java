package org.cellocad.MIT.dnacompiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;



public class PartitionCircuitUtil {

	
	public static List<Integer> getRange(int n){
		// Returns List of integers ranging from 1, n
		List<Integer> range = new ArrayList<>();
		for(int i=1; i<=n ; i++){
			range.add(i);
		}
		return range;
	}
	
	public static HashSet<Gate> Union(HashSet<List<Gate>> list_of_gates_lists){
		//function is basically pointless at this point I think b/c it's already a hashset
		//coming in...
		HashSet<Gate> union = new HashSet<Gate>();
		for(List<Gate> gates_list:list_of_gates_lists){
			for(Gate g:gates_list){
				union.add(g);
			}
		}
		return union;
	}
	
	//Finds 'parents' of given gate, all gates in LC that contain 'gate' as a child
		public static List<Gate> getGateParents(LogicCircuit lc, Gate gate){
			List<Gate> parents = new ArrayList<Gate>();
			for(Gate g:lc.get_Gates()){
				if(g.getChildren().contains(gate)){
					parents.add(g);
				}
			}
			return parents;
		}
		
		
		/**
		 * Iteratively finds all 'k' length combinations of gates in list. Algorithm uses the indexes 
		 * of a list to generate all desired subsets of the list and then grabs the item from the 
		 * original list using those indices. Code was copied (with slight modification) from the 
		 * following answer on stackoverflow:
		 * https://stackoverflow.com/a/29914908/3919605
		 * 
		 * @param arr list of gates to find combinations of
		 * @param k length of subsets to find. the 'k' in "n choose k"
		 * @return list of k length sublists containing all possible combinations of k gates. 
		 */
		public static List<List<Gate>> Combinations(List<Gate> arr, int k){
			List<Gate> input = arr;    // input array

			List<List<Gate>> subsets = new ArrayList<>();

			int[] s = new int[k];                  // here we'll keep indices 
			                                       // pointing to elements in input array

			if (k <= input.size()) {
			    // first index sequence: 0, 1, 2, ...
			    for (int i = 0; (s[i] = i) < k - 1; i++);  
			    	subsets.add(getSubset(input, s));
			    for(;;) {
			        int i;
			        // find position of item that can be incremented
			        for (i = k - 1; i >= 0 && s[i] == input.size() - k + i; i--); 
			        if (i < 0) {
			            break;
			        }
			        s[i]++;                    // increment this item
			        for (++i; i < k; i++) {    // fill up remaining items
			            s[i] = s[i - 1] + 1; 
			        }
			        subsets.add(getSubset(input, s));
			    }
			}
			return subsets;
			
		}
		
		
		/** Helper function for findCombinations(). Generate actual subset by index sequence.
		 * Original code copied from following answer on stackoverflow:
		 * https://stackoverflow.com/a/29914908/3919605
		 */
		private static List<Gate> getSubset(List<Gate> input, int[] subset) {
		    List<Gate> result = new ArrayList<Gate>(subset.length); 
		    for(int i = 0; i < subset.length; i++) 
		    	result.add(input.get(subset[i]));
		    return result;
		}

		
}
