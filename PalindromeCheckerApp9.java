
import java.util.Scanner;

class PalindromeCheckerApp9{

    static boolean isPalindrome(String text, int start, int end) {
        if (start >= end)
            return true;

        if (text.charAt(start) != text.charAt(end))
            return false;

        return isPalindrome(text, start + 1, end - 1);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        System.out.println(isPalindrome(input, 0, input.length() - 1));
        sc.close();
    }
}