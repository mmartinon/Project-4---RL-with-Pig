import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PigSolver {
    int goal;
    double epsilon;
    double[][][] p;
    boolean[][][] roll; // true = roll, false = hold

    PigSolver(int goal, double epsilon) {
        this.goal = goal;
        this.epsilon = epsilon;
        p = new double[goal][goal][goal];
        roll = new boolean[goal][goal][goal];

        valueIterate();
    }

    void valueIterate() {
        double maxChange;
        do {
            maxChange = 0.0;
            for (int i = 0; i < goal; i++) { // for all player scores
                for (int j = 0; j < goal; j++) { // for all opponent scores
                    for (int k = 0; k < goal - i; k++) { // for all turn totals
                        double oldProb = p[i][j][k];

                        // Calculate probability of winning if rolling
                        double pRoll = (1.0/6.0) * (1.0 - pWin(j, i, 0)); // roll a 1
                        for (int d = 2; d <= 6; d++) {
                            pRoll += (1.0/6.0) * pWin(i, j, k + d); // roll 2–6
                        }

                        // Calculate probability of winning if holding
                        double pHold = 1.0 - pWin(j, i + k, 0);

                        // Choose better action
                        p[i][j][k] = Math.max(pRoll, pHold);
                        roll[i][j][k] = pRoll > pHold;

                        double change = Math.abs(p[i][j][k] - oldProb);
                        maxChange = Math.max(maxChange, change);
                    }
                }
            }
        } while (maxChange >= epsilon);
    }

    public double pWin(int i, int j, int k) {
        if (i + k >= goal)
            return 1.0;
        else if (j >= goal)
            return 0.0;
        else
            return p[i][j][k];
    }

    public void outputHoldValues() {
        for (int i = 0; i < goal; i++) {
            for (int j = 0; j < goal; j++) {
                int k = 0;
                while (k < goal - i && roll[i][j][k])
                    k++;
                System.out.print(k + " ");
            }
            System.out.println();
        }
    }

    // Output the policy in a summary format
    public void outputSummaryPolicy() {
        System.out.println("Player Scores Opponent Score HoldThreshold");
        for (int i = 90; i < 100; i++) {
            for (int j = 90; j < 100; j++) {
                int k = 0;
                while (k < goal - i && roll[i][j][k])
                    k++;
                System.out.printf("%d\t\t%d\t\t%d\n", i, j, k);
            }
        }

    }

    // Output the policy to a CSV file
    // Each row corresponds to a player score, and each column corresponds to an opponent score.
    public void outputPolicyToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (int i = 0; i < goal; i++) {
                for (int j = 0; j < goal; j++) {
                    int k = 0;
                    while (k < goal - i && roll[i][j][k]) {
                        k++;
                    }
                    writer.print(k);
                    if (j < goal - 1) writer.print(",");
                }
                writer.println();
            }
            System.out.println("Policy written to: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to write policy to CSV: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        PigSolver ps = new PigSolver(100, 1e-9);

        System.out.println("First player win possibility: " + ps.p[0][0][0]);

        int firstHold = -1;
        for (int i = 0; i < ps.goal; i++) {
            if (!ps.roll[0][0][i]) {
                firstHold = i;
                break;
            }
        }
        System.out.println("Lowest turn where player should hold: " + firstHold);

        double player2Win = 1.0 - ps.p[0][0][0];
        System.out.println("Player 2 win possibility if player 1 scores 0: " + player2Win);
        System.out.println("Player 2 should also hold at: " + firstHold);

        ps.outputPolicyToCSV("policy_output.csv");
        ps.outputHoldValues();
        //ps.outputSummaryPolicy();

    }
}
