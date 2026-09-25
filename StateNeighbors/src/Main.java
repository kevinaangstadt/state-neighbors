public class Main {

    public static void main(String[] args) {
        UnitedStates us = new UnitedStates();

        // print out all of the states
        us.printStates();

        String start = "MT";
        String finish = "SC";

        boolean result = us.search(start, finish);
        if (result) {
            System.out.println("Solution:");
            for (State s : us.buildSolution(finish)) {
                System.out.println(s);
            }
        } else {
            System.out.println("No Solution :/");
        }
    }
}
