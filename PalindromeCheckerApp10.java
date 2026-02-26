
import java.util.Scanner;

class PalindromeCheckerApp10{

    static boolean isPalindrome(String text, int start, int end) {
        if (start >= end)
            return true;
        if (text.charAt(start) != text.charAt(end))
            return false;
        return isPalindrome(text, start + 1, end - 1);
    }

    static boolean checkPalindrome(String input) {
        String normalized = input.replaceAll("\\s+", "").toLowerCase();
        return isPalindrome(normalized, 0, normalized.length() - 1);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        System.out.println(checkPalindrome(input));
        sc.close();
    }
}