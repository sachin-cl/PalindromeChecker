
import java.util.Scanner;
import java.util.Deque;
import java.util.ArrayDeque;

public class PalindromeCheckerApp7{

    public static boolean isPalindrome(String input) {
        Deque<Character> deque = new ArrayDeque<>();

        for (int i = 0; i < input.length(); i++) {
            deque.addLast(input.charAt(i));
        }

        while (deque.size() > 1) {
            if (deque.removeFirst() != deque.removeLast()) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();

        if (isPalindrome(text)) {
            System.out.println("Palindrome");
        } else {
            System.out.println("Not Palindrome");
        }

        scanner.close();
    }
}