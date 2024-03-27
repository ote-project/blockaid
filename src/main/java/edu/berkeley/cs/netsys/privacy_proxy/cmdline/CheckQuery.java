package edu.berkeley.cs.netsys.privacy_proxy.cmdline;

import edu.berkeley.cs.netsys.privacy_proxy.jdbc.StandaloneChecker;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import java.sql.SQLException;
import java.util.Optional;

public class CheckQuery {
    private static final LineReader reader = LineReaderBuilder.builder().build();

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: CheckQuery <url> <username> <password>");
            System.exit(1);
        }

        String url = args[0];
        String username = args[1];
        String password = args[2];

        try (StandaloneChecker checker = StandaloneChecker.create(url, username, password)) {
            while (true) {
                Optional<String> o = readQuery();
                if (o.isEmpty()) break;
                try {
                    boolean isCompliant = checker.check(o.get());
                    if (isCompliant) {
                        System.out.println("Query is compliant.");
                    } else {
                        System.out.println("Query is NOT compliant.");
                    }
                } catch (SQLException e) {
                    System.err.println("Failed to check query: " + e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to connect: " + e);
        }

        // Some thread is not quitting, so we force exit.
        // TODO(zhangwen): figure out why this is happening.
        System.exit(0);
    }

    private static Optional<String> readQuery() {
        while (true) {
            try {
                String line = reader.readLine("Query: ");
                if (line == null) break;
                line = line.strip();
                if (line.endsWith(";")) line = line.substring(0, line.length() - 1);
                if (line.isEmpty()) break;
                return Optional.of(line);
            } catch (UserInterruptException e) {
                // Ignore.
            } catch (EndOfFileException e) {
                break;
            }
        }
        return Optional.empty();
    }
}
