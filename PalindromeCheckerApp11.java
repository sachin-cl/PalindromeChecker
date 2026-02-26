
import java.util.Scanner;

class PalindromeCheckerApp11{

    public boolean checkPalindrome(String input) {
        String text = input.replaceAll("\\s+", "").toLowerCase();
        int start = 0;
        int end = text.length() - 1;

        while (start < end) {
            if (text.charAt(start) != text.charAt(end))
                return false;
            start++;
            end--;
        }
        return true;
    }
}

class PalindromeApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        PalindromeCheckerApp11 checker = new PalindromeCheckerApp11();
        System.out.println(checker.checkPalindrome(input));
        sc.close();
    }
}