import java.io.*;
import java.util.*;
// Christopher Garcia Project HW
// Monday September 23, 2024
public class Card {
    private List<String> invalidCards = new ArrayList<>();
    public Map<String, Integer> readDeck(String filename) throws IOException {
        Map<String, Integer> deck = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        int cardCount = 0;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(":");
            if (parts.length != 2 || parts[0].trim().isEmpty() || !isValidCost(parts[1].trim())) {
                invalidCards.add(line);
                continue;
            }
            String cardName = parts[0].trim();
            int cost = Integer.parseInt(parts[1].trim());
            if (cardCount < 1000) {
                deck.put(cardName, cost);
                cardCount++;
            } else {
                break;
            }
        }
        reader.close();
        return deck;
    }
    private boolean isValidCost(String costStr) {
        try {
            int cost = Integer.parseInt(costStr);
            return cost >= 0 && cost <= 6;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public String generateDeckId() {
        Random random = new Random();
        int deckId = 100_000_000 + random.nextInt(900_000_000);
        return String.valueOf(deckId);
    }
    public List<String> getInvalidCards() {
        return invalidCards;
    }
    public int calculateTotalCost(Map<String, Integer> deck) {
        int totalCost = 0;
        for (int cost : deck.values()) {
            totalCost += cost;
        }
        return totalCost;
    }
    public int[] generateHistogram(Map<String, Integer> deck) {
        int[] histogram = new int[7];
        for (int cost : deck.values()) {
            histogram[cost]++;
        }
        return histogram;
    }
    public void generateReport(String deckId, Map<String, Integer> deck, int totalCost, int[] histogram) {
        // Print report to console
        System.out.println("Deck Report");
        System.out.println("Deck ID: " + deckId);
        System.out.println("Total Energy Cost of the Deck: " + totalCost + " energy");
        System.out.println("\nEnergy Cost Histogram:");
        for (int i = 0; i < histogram.length; i++) {
            System.out.println(i + " energy: " + histogram[i] + " cards");
        }
        if (!invalidCards.isEmpty()) {
            System.out.println("\nInvalid Cards:");
            for (String invalidCard : invalidCards) {
                System.out.println(invalidCard);
            }
        }
        if (invalidCards.size() > 10 || deck.size() > 1000) {
            System.out.println("\nVOID REPORT: Too many invalid cards or card count exceeds 1000.");
        }
        // Generate PDF report
        try {
            generatePDFReport(deckId, totalCost, histogram, deck);
        } catch (IOException e) {
            System.out.println("Error generating PDF report: " + e.getMessage());
        }
    }
    private void generatePDFReport(String deckId, int totalCost, int[] histogram, Map<String, Integer> deck) throws IOException {
        String reportFilename;
        if (invalidCards.size() > 10 || deck.size() > 1000) {
            reportFilename = "SpireDeck " + deckId + "(VOID).pdf";
        } else {
            reportFilename = "SpireDeck " + deckId + ".pdf";
        }

    }
    public static void main(String[] args) {
        Card deckTally = new Card(); // Fix the class name here
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the deck file name: ");
            String filename = scanner.nextLine();
            Map<String, Integer> deck = deckTally.readDeck(filename);
            String deckId = deckTally.generateDeckId();
            System.out.println("Generated Deck ID: " + deckId);
            int totalCost = deckTally.calculateTotalCost(deck);
            int[] histogram = deckTally.generateHistogram(deck);
            deckTally.generateReport(deckId, deck, totalCost, histogram);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}