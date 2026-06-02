package utils;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;
import java.util.Scanner;

/**
 * Interactive helper used when the test needs the tester to act mid-run:
 *  - paste the password-reset link that arrives in Gmail (step 4), and
 *  - confirm the "congratulations" registration email was received (step 1).
 *
 * <p>A blocking, always-on-top GUI dialog is used by default so it stays visible
 * while the tester switches to Gmail. Falls back to stdin when headless.
 */
public final class ConsoleHelper {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleHelper() {}

    /** Free-text prompt — returns the trimmed value the tester typed (may be empty). */
    public static String prompt(String message) {
        if (!GraphicsEnvironment.isHeadless()) {
            JOptionPane pane = new JOptionPane(
                message, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            pane.setWantsInput(true);

            JDialog dialog = pane.createDialog(null, "Action required");
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);   // blocks until OK/Cancel
            dialog.dispose();

            Object value = pane.getInputValue();
            return value == JOptionPane.UNINITIALIZED_VALUE || value == null
                ? "" : value.toString().trim();
        }

        System.out.println();
        System.out.println("==================================================");
        System.out.println(">>> " + message);
        System.out.println("==================================================");
        System.out.print("Input: ");
        return SCANNER.nextLine().trim();
    }

    /** Yes/No confirmation — returns true only if the tester explicitly clicks YES. */
    public static boolean confirm(String message) {
        if (!GraphicsEnvironment.isHeadless()) {
            JOptionPane pane = new JOptionPane(
                message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

            JDialog dialog = pane.createDialog(null, "Confirm");
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);   // blocks until Yes/No
            dialog.dispose();

            Object value = pane.getValue();
            return value instanceof Integer && (Integer) value == JOptionPane.YES_OPTION;
        }

        System.out.print(message + " [y/N]: ");
        return SCANNER.nextLine().trim().equalsIgnoreCase("y");
    }

    public static String promptForResetLink() {
        return prompt(
            "Open the Gmail account you just registered with, find the password " +
            "reset email, and paste the full reset link here, then click OK."
        );
    }

    public static boolean confirmCongratsEmail() {
        return confirm(
            "Check the registered Gmail inbox. Did you receive the registration / " +
            "\"Congratulations\" email?  Click YES if it has arrived."
        );
    }
}
