import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class UnitedStates {
    private ArrayList<State> states;

    public UnitedStates() {
        // 50 states + DC
        states = new ArrayList<>(51);

        // load in code file and fill up the states ArrayList
        loadCodesFile();
        loadNeighborsFile();
    }

    private void loadNeighborsFile(){
        Scanner neighbors;

        try {
            // Create a scanner object from a file
            neighbors = new Scanner(new File("contiguous-usa.txt"));
            // NOTE: if we wanted to create a Scanner from user input:
            // new Scanner(System.in);
        } catch (FileNotFoundException e) {
            System.err.println("Could not find the adjacent states files");
            e.printStackTrace();
            System.exit(1);
            return;
        }

        // Loop over all of the lines in the file
        while(neighbors.hasNextLine()) {
            String line = neighbors.nextLine();
            String[] values = line.split(" ");

            /*
             * values[0] is one state code (e.g., PA)
             * values[1] is another state code (e.g., NY)
             *
             *
             * (1) Find the state in states ArrayList that has the code
             * values[0]
             * (2) Add a neighbor to that state
             *     - values[1]
             *     - look up the state object for values[1]
             *
             * Do this in the opposite direction as well.
             */
            findState(values[0]).addNeighbor(findState(values[1]));
            findState(values[1]).addNeighbor(findState(values[0]));

        }

        return;
    }

    /**
     * Return the State object from the states ArrayList that
     * has the correct two-letter code.
     * preconditions: code must exist in the states array
     *
     * @param code Two-letter abbreviation for a state
     * @return the State from the states ArrayList that is associated
     * with the code provided
     */
    private State findState(String code) {
        for(int i = 0; i < states.size(); i++) {
            if(states.get(i).getCode().equals(code)) {
                // found it!
                return states.get(i);
            }
        }
        // not found --- should never be reached
        return null;
    }


    /**
     * load in the information stored in codes.csv
     * create each state and add it to the states ArrayList
     */
    private void loadCodesFile() {
        // scanner can be used to read a file
        Scanner codes;

        try {
            // opened the file for reading with the scanner
            codes = new Scanner(new File("codes.csv"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not find the codes file");
            e.printStackTrace();
            System.exit(1);
            return; // this line should never be reached
        }

        // if we get here, we have successfully opened the file

        // loop through all lines of the file to add states
        while(codes.hasNextLine()) {
            String line = codes.nextLine();
            String[] values = line.split(",");
            // name, code, number
            // create state with name and code
            State s = new State(values[0], values[1]);
            states.add(s);
        }

        return;
    }

    public void printStates() {
        // Print out each state and all its neighbors
        for (State s : states) {
            System.out.print(s);
            System.out.print(": ");
            for (int i = 0; i < s.getNeighborCount(); i++)  {
                System.out.printf("%s, ", s.getNeighbor(i).getName());
            } // for i
            System.out.println();
        } // for State s
    }

    /**
     * Search to see if there is a path from start to end in the US
     * @param start starting code for the state (e.g., NY)
     * @param finish ending code for the state (e.g., CA)
     * @return true if there is a path, false otherwise
     */
    public boolean search(String start, String finish) {
        State begin = findState(start);
        State end = findState(finish);

        // make a reachable collection (e.g., stack, queue)
        ArrayDeque<State> reachable = new ArrayDeque<>();
        // seed the collection with our begin state
        begin.visit();
        reachable.addFirst(begin);

        // repeat as long as there are reachable states
        while(!reachable.isEmpty()) {
            // get a state from the collection
            // removeFirst => LIFO (stack)
            // removeLast => FIFO (queue)
            State curr = reachable.removeFirst();

            // TODO: go through all neighboring states and add them
            for (int i = 0; i < curr.getNeighborCount(); i++) {
                // grab a neighbor
                State neighbor = curr.getNeighbor(i);
                if (!neighbor.isVisited()) {
                    // if not visited yet, visit it and add it to the reachable collection
                    neighbor.visit();
                    // save the path from curr to neighbor
                    neighbor.setPrev(curr);
                    reachable.addFirst(neighbor);
                }
                if (neighbor.equals(end)) {
                    // We have found the end state through our search
                    return true;
                }
            } // for
        } // while

        // we processed all reachable states, but didn't find target
        return false;
    }

    /**
     * After conducting a search, build a list of the states visited
     * PRECONDITION: search must be called before this method
     * @param finish code for the end state
     * @return list of the states visited
     */
    public List<State> buildSolution(String finish) {
        State curr = findState(finish);
        // make an empty list for the solution
        LinkedList<State> solution = new LinkedList<>();
        // loop to add states to solution and move on to curr.prev
        // when the state is null, have reached the end of our backtrack
        while(curr != null) {
            // add state to solution
            // because we are backtracking, add to beginning of list
            solution.addFirst(curr);
            // update curr to the prev
            curr = curr.getPrev();
        } // while

        // return the solution
        return solution;
    }

}
