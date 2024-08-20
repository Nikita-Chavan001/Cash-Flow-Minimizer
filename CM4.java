import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Person {
    String name;
    int netAmount;

    public Person(String name, int netAmount) {
        this.name = name;
        this.netAmount = netAmount;
    }
}

public class CM4 extends JFrame implements ActionListener {
    private JTextField numPeopleField;
    private JTextField numTransactionsField;
    private JButton calculateButton;
    private JTextArea outputArea;

    // Declaring graph variable
    private static int[][] graph;

    public CM4() {
        setTitle("Cash Flow Minimizer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the window on the screen

        // Custom colors
        Color startColor = new Color(230, 230, 230);
        Color endColor = new Color(200, 200, 255);
        Color buttonColor = new Color(50, 150, 250);

        JPanel inputPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setPaint(new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        inputPanel.setLayout(new GridLayout(0, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        JLabel numPeopleLabel = new JLabel("Number of People:");
        numPeopleLabel.setFont(labelFont);
        numPeopleLabel.setForeground(Color.BLUE);
        numPeopleField = new JTextField();
        numPeopleField.setFont(labelFont);
        JLabel numTransactionsLabel = new JLabel("Number of Transactions:");
        numTransactionsLabel.setFont(labelFont);
        numTransactionsLabel.setForeground(Color.BLUE);
        numTransactionsField = new JTextField();
        numTransactionsField.setFont(labelFont);
        inputPanel.add(numPeopleLabel);
        inputPanel.add(numPeopleField);
        inputPanel.add(numTransactionsLabel);
        inputPanel.add(numTransactionsField);

        calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(this);
        calculateButton.setPreferredSize(new Dimension(120, 40)); // Set button size
        calculateButton.setBackground(buttonColor);
        calculateButton.setForeground(Color.WHITE);
        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(calculateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == calculateButton) {
            int numPeople = Integer.parseInt(numPeopleField.getText());
            Person[] input = new Person[numPeople];

            for (int i = 0; i < numPeople; i++) {
                input[i] = new Person("", 0);
            }

            for (int i = 0; i < numPeople; i++) {
                String personName = JOptionPane.showInputDialog("Enter name for person " + (i + 1));
                input[i].name = personName;
            }

            int numTransactions = Integer.parseInt(numTransactionsField.getText());
            graph = new int[numPeople][numPeople]; // Initialize graph here

            for (int i = 0; i < numTransactions; i++) {
                String debtor = JOptionPane.showInputDialog("Enter debtor for transaction " + (i + 1));
                String creditor = JOptionPane.showInputDialog("Enter creditor for transaction " + (i + 1));
                int amount = Integer.parseInt(JOptionPane.showInputDialog("Enter amount for transaction " + (i + 1)));

                for (int j = 0; j < numPeople; j++) {
                    for (int k = 0; k < numPeople; k++) {
                        if (input[j].name.equals(debtor) && input[k].name.equals(creditor)) {
                            graph[j][k] = amount;
                            break;
                        }
                    }
                }
            }

            String output = minimizeCashFlow(numPeople, input, numTransactions);
            outputArea.setText(output);
            drawGraph(input, graph);
        }
    }

    static int getMinIndex(Person[] listOfNetAmounts, int numPeople) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < numPeople; i++) {
            if (listOfNetAmounts[i].netAmount == 0)
                continue;

            if (listOfNetAmounts[i].netAmount < min) {
                minIndex = i;
                min = listOfNetAmounts[i].netAmount;
            }
        }
        return minIndex;
    }

    static int getSimpleMaxIndex(Person[] listOfNetAmounts, int numPeople) {
        int max = Integer.MIN_VALUE;
        int maxIndex = -1;
        for (int i = 0; i < numPeople; i++) {
            if (listOfNetAmounts[i].netAmount == 0)
                continue;

            if (listOfNetAmounts[i].netAmount > max) {
                maxIndex = i;
                max = listOfNetAmounts[i].netAmount;
            }
        }
        return maxIndex;
    }

    static String printAns(int[][] ansGraph, int numPeople, Person[] input) {
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("\nThe transactions for minimum cash flow are as follows :\n");
    
        // Store final graph data
        int[][] finalGraph = new int[numPeople][numPeople];
    
        for (int i = 0; i < numPeople; i++) {
            for (int j = 0; j < numPeople; j++) {
                if (i == j)
                    continue;
    
                if (ansGraph[i][j] != 0 && ansGraph[j][i] != 0) {
    
                    if (ansGraph[i][j] == ansGraph[j][i]) {
                        ansGraph[i][j] = 0;
                        ansGraph[j][i] = 0;
                    } else if (ansGraph[i][j] > ansGraph[j][i]) {
                        ansGraph[i][j] -= ansGraph[j][i];
                        ansGraph[j][i] = 0;
    
                        outputBuilder.append(
                                String.format("%s pays Rs %d to %s\n", input[i].name, ansGraph[i][j], input[j].name));
    
                        // Update final graph
                        finalGraph[i][j] = ansGraph[i][j];
                    } else {
                        ansGraph[j][i] -= ansGraph[i][j];
                        ansGraph[i][j] = 0;
    
                        outputBuilder.append(
                                String.format("%s pays Rs %d to %s\n", input[j].name, ansGraph[j][i], input[i].name));
    
                        // Update final graph
                        finalGraph[j][i] = ansGraph[j][i];
                    }
                } else if (ansGraph[i][j] != 0) {
                    outputBuilder.append(
                            String.format("%s pays Rs %d to %s\n", input[i].name, ansGraph[i][j], input[j].name));
    
                    // Update final graph
                    finalGraph[i][j] = ansGraph[i][j];
                } else if (ansGraph[j][i] != 0) {
                    outputBuilder.append(
                            String.format("%s pays Rs %d to %s\n", input[j].name, ansGraph[j][i], input[i].name));
    
                    // Update final graph
                    finalGraph[j][i] = ansGraph[j][i];
                }
    
                ansGraph[i][j] = 0;
                ansGraph[j][i] = 0;
            }
        }
    
        // Call drawGraph with the final graph
        drawGraph(input, finalGraph);
    
        return outputBuilder.toString();
    }
    

    static String minimizeCashFlow(int numPeople, Person[] input, int numTransactions) {
        Person[] listOfNetAmounts = new Person[numPeople];

        for (int p = 0; p < numPeople; p++) {
            listOfNetAmounts[p] = new Person(input[p].name, 0);

            int amount = 0;

            for (int i = 0; i < numPeople; i++) {
                amount += graph[i][p];
            }

            for (int j = 0; j < numPeople; j++) {
                amount -= graph[p][j];
            }

            listOfNetAmounts[p].netAmount = amount;
        }

        int[][] ansGraph = new int[numPeople][numPeople];

        int numZeroNetAmounts = 0;

        for (int i = 0; i < numPeople; i++) {
            if (listOfNetAmounts[i].netAmount == 0)
                numZeroNetAmounts++;
        }
        while (numZeroNetAmounts != numPeople) {

            int minIndex = getMinIndex(listOfNetAmounts, numPeople);
            int maxIndex = getSimpleMaxIndex(listOfNetAmounts, numPeople);

            if (maxIndex == -1) {

                ansGraph[minIndex][0] += Math.abs(listOfNetAmounts[minIndex].netAmount);

                int simpleMaxIndex = getSimpleMaxIndex(listOfNetAmounts, numPeople);
                ansGraph[0][simpleMaxIndex] += Math.abs(listOfNetAmounts[minIndex].netAmount);

                listOfNetAmounts[simpleMaxIndex].netAmount += listOfNetAmounts[minIndex].netAmount;
                listOfNetAmounts[minIndex].netAmount = 0;

                if (listOfNetAmounts[minIndex].netAmount == 0)
                    numZeroNetAmounts++;

                if (listOfNetAmounts[simpleMaxIndex].netAmount == 0)
                    numZeroNetAmounts++;
            } else {
                int transactionAmount;
                if (Math.abs(listOfNetAmounts[minIndex].netAmount) < listOfNetAmounts[maxIndex].netAmount) {
                    transactionAmount = Math.abs(listOfNetAmounts[minIndex].netAmount);
                } else {
                    transactionAmount = listOfNetAmounts[maxIndex].netAmount;
                }
                ansGraph[minIndex][maxIndex] += transactionAmount;

                listOfNetAmounts[minIndex].netAmount += transactionAmount;
                listOfNetAmounts[maxIndex].netAmount -= transactionAmount;

                if (listOfNetAmounts[minIndex].netAmount == 0)
                    numZeroNetAmounts++;

                if (listOfNetAmounts[maxIndex].netAmount == 0)
                    numZeroNetAmounts++;
            }
        }

        return printAns(ansGraph, numPeople, input);
    }

    public static void drawGraph(Person[] input, int[][] graph) {
        JFrame frame = new JFrame("Graph Visualization");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLUE);

                int numPeople = input.length;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.min(getWidth(), getHeight()) / 3;

                // Draw nodes in red
                g2d.setColor(Color.BLUE);
                for (int i = 0; i < numPeople; i++) {
                    double angle = 2 * Math.PI * i / numPeople;
                    int x = (int) (centerX + radius * Math.cos(angle));
                    int y = (int) (centerY + radius * Math.sin(angle));
                    g2d.fillOval(x - 10, y - 10, 20, 20);
                    g2d.drawString(input[i].name, x - 10, y - 15);
                }

                // Draw directed edges with triangles
                for (int i = 0; i < numPeople; i++) {
                    for (int j = 0; j < numPeople; j++) {
                        if (graph[i][j] != 0) {
                            double angle1 = 2 * Math.PI * i / numPeople;
                            double angle2 = 2 * Math.PI * j / numPeople;
                            int x1 = (int) (centerX + radius * Math.cos(angle1));
                            int y1 = (int) (centerY + radius * Math.sin(angle1));
                            int x2 = (int) (centerX + radius * Math.cos(angle2));
                            int y2 = (int) (centerY + radius * Math.sin(angle2));

                            // Calculate midpoint
                            int midX = (x1 + x2) / 2;
                            int midY = (y1 + y2) / 2;

                            // Draw line
                            g2d.drawLine(x1, y1, x2, y2);

                            // Draw triangle at the end of the edge
                            int arrowLength = 20;
                            double arrowAngle = Math.PI / 6; // 30 degrees

                            // Calculate angle of the link
                            double linkAngle = Math.atan2(y2 - y1, x2 - x1);

                            // Calculate coordinates of triangle points
                            int x3 = (int) (midX - arrowLength * Math.cos(linkAngle - arrowAngle));
                            int y3 = (int) (midY - arrowLength * Math.sin(linkAngle - arrowAngle));
                            int x4 = (int) (midX - arrowLength * Math.cos(linkAngle + arrowAngle));
                            int y4 = (int) (midY - arrowLength * Math.sin(linkAngle + arrowAngle));

                            // Draw triangle
                            g2d.setColor(Color.RED); // Change color to make it visible
                            g2d.fillPolygon(new int[] { midX, x3, x4 }, new int[] { midY, y3, y4 }, 3);

                            // Label the amount
                            g2d.setColor(Color.BLACK); // Reset color for labeling
                            g2d.drawString(String.valueOf(graph[i][j]), midX, midY);
                        }
                    }
                }

            }
        };

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new CM4();
}
}