import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class main {
    private static String ttyConfig;

    public static void main(String[] args) {
        try {
            setTerminalToCBreak();

            while (true) {
                int c = 0;
                c = System.in.read();
                if (Character.isISOControl(c)) {
                    System.out.print(c);
                    System.out.println("\r");
                } else {
                    System.out.print(c);
                    System.out.print(' ');
                    System.out.print((char) c);
                    System.out.println("\r");
                }
                if (c == 113)
                    break;
            }
        }
        catch (IOException e) {
                System.err.println("IOException");
        }
        catch (InterruptedException e) {
                System.err.println("InterruptedException");
        }
        finally {
            try {
                stty( ttyConfig.trim() );
                }
                catch (Exception e) {
                    System.err.println("Exception restoring tty config");
                }
        }
    }

    private static void setTerminalToCBreak() throws IOException, InterruptedException {
        ttyConfig = stty("-g");
        // set the console to be character-buffered instead of line-buffered
        stty("-icanon min 1");
        // disable character echoing
        stty("-echo");
        // disalbe ctrl + c and ctrl + z
        stty("-isig");
        stty("-iexten");


        stty("-brkint");
        stty("-ixon");
        stty("-icrnl");
        stty("-inpck");
        stty("-istrip");

        stty("-opost");

        stty("cs8");

        stty("min 0");
        stty("min 1");
    }

    /**
     *  Execute the stty command with the specified arguments
     *  against the current active terminal.
     */
    private static String stty(final String args)
                    throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[] {
                    "sh",
                    "-c",
                    cmd
                });
    }

    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    private static String exec(final String[] cmd)
                    throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        String result = new String(bout.toByteArray());
        return result;
    }
}