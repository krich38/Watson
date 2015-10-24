package org.watson.command.io;

import org.watson.command.CommandManager;
import org.watson.protocol.io.DatabaseAdapter;

import java.io.File;
import java.sql.*;

/**
 * @author Kyle Richards
 * @version 1.0
 *          <p>
 *          Watson has a connection with a database for his Markov machine learning
 *          <p>
 *          This is just the adapter that handles the communication
 */
public class MarkovDatabaseAdapter {
    private static Connection connection;

    public static boolean setup() {
        try {
            connection = DatabaseAdapter.getConnection();
            connection.createStatement().executeUpdate("create table if not exists markov (seed_a TEXT, seed_b TEXT, seed_c TEXT, unique(seed_a, seed_b, seed_c) on conflict ignore)");
            connection.createStatement().executeUpdate("ATTACH DATABASE ':memory:' AS mem");
            connection.createStatement().executeUpdate("create table if not exists mem.markov (seed_a TEXT, seed_b TEXT, seed_c TEXT, unique(seed_a, seed_b, seed_c) on conflict ignore)");
            connection.createStatement().executeUpdate("insert into mem.markov (seed_a, seed_b, seed_c) select seed_a, seed_b, seed_c from markov");
        } catch (Exception e) {
            e.printStackTrace();

        }
        File f = new File("watson.db");
        if (f.exists()) {
            try {
                System.out.println("Importing old markov.db... ");
                connection.setAutoCommit(false);
                Connection c = DriverManager.getConnection("jdbc:sqlite:watson.db");
                ResultSet rs = c.createStatement().executeQuery("select seed_a, seed_b, seed_c from markov");
                while (rs.next()) {
                    markovInsert(rs.getString(1), rs.getString(2), rs.getString(3));
                }
                c.close();
                if (!f.delete()) {
                    f.deleteOnExit();
                }
            } catch (Exception e) {
                System.out.println("Error importing old watson.db");
                e.printStackTrace();
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (Exception ignored) {
                }
            }
        }
        return true;
    }

    public static String markovFind(String seed1, String seed2) {
        try {
            PreparedStatement ps;
            if (seed2 == null) {
                ps = connection.prepareStatement("select seed_a, seed_b from mem.markov where seed_a = ? or seed_b = ? COLLATE NOCASE order by random() limit 1");
                ps.setString(1, seed1);
                ps.setString(2, seed1);
            } else {
                ps = connection.prepareStatement("select seed_a, seed_b from mem.markov where seed_a in (?, ?) or seed_b in (?, ?) COLLATE NOCASE order by random() limit 1");
                ps.setString(1, seed1);
                ps.setString(2, seed2);
                ps.setString(3, seed1);
                ps.setString(4, seed2);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String found1 = rs.getString(1);
                String found2 = rs.getString(2);
                return markovGenerate(found1, found2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void markovLearn(String input) {
        String seed1, seed2;
        seed1 = seed2 = "\n";
        String[] words = input.split(" ");
        for (String seed3 : words) {
            markovInsert(seed1, seed2, seed3);
            seed1 = seed2;
            seed2 = seed3;
        }
    }

    private static void markovInsert(String seed1, String seed2, String seed3) {
        try {
            PreparedStatement ps = connection.prepareStatement("insert into mem.markov (seed_a, seed_b, seed_c) values (?, ?, ?)");
            ps.setString(1, seed1);
            ps.setString(2, seed2);
            ps.setString(3, seed3);
            ps.executeUpdate();
            ps = connection.prepareStatement("insert into markov (seed_a, seed_b, seed_c) values (?, ?, ?)");
            ps.setString(1, seed1);
            ps.setString(2, seed2);
            ps.setString(3, seed3);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String markovGenerate() {
        try {
            PreparedStatement ps = connection.prepareStatement("select seed_a, seed_b from mem.markov order by random() limit 1");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String found1 = rs.getString(1);
                String found2 = rs.getString(2);
                return markovGenerate(found1, found2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int markovFillForwards(StringBuilder result, int wordcount, String seed1, String seed2) {
        int count = 0;
        for (int i = 0; i < wordcount / 2; i++) {
            String seed3 = markovNextSeed(seed1, seed2);
            if (seed3 == null || seed3.equalsIgnoreCase("\n")) {
                break;
            }
            count++;
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(seed3);
            seed1 = seed2;
            seed2 = seed3;
        }
        return count;
    }


    private static String markovNextSeed(String seed1, String seed2) {
        try {
            PreparedStatement ps = connection.prepareStatement("select seed_c from mem.markov where seed_a = ? and seed_b = ? order by random() limit 1");
            ps.setString(1, seed1);
            ps.setString(2, seed2);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String markovPreviousSeed(String seed2, String seed3) {
        try {
            PreparedStatement ps = connection.prepareStatement("select seed_a from mem.markov where seed_b = ? and seed_c = ? order by random() limit 1");
            ps.setString(1, seed2);
            ps.setString(2, seed3);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int markovFillBackwards(StringBuilder result, int wordcount, String seed1, String seed2) {
        int count = 0;
        for (int i = 0; i < wordcount; i++) {
            String seed0 = markovPreviousSeed(seed1, seed2);
            if (seed0 == null || seed0.equalsIgnoreCase("\n")) {
                break;
            }
            count++;
            if (result.length() > 0) {
                result.insert(0, ' ');
            }
            result.insert(0, seed0);
            seed2 = seed1;
            seed1 = seed0;
        }
        return count;
    }

    private static String markovGenerate(String seed1, String seed2) {
        StringBuilder result = new StringBuilder();
        if (seed1 == null) {
            seed1 = "\n";
        }
        if (seed2 == null) {
            seed2 = "\n";
        }
        if (!seed1.equalsIgnoreCase("\n")) {
            result.append(seed1);
            result.append(' ');
        }
        if (!seed2.equalsIgnoreCase("\n")) {
            result.append(seed2);
        }
        int wordcount = CommandManager.RANDOM.nextInt(30) + 10;
        int type = CommandManager.RANDOM.nextInt(3);
        switch (type) {
            case 0:
                markovFillBackwards(result, wordcount, seed1, seed2);
                break;
            case 1:
                markovFillForwards(result, wordcount, seed1, seed2);
                break;
            default:
                int num = markovFillBackwards(result, wordcount / 2, seed1, seed2);
                markovFillForwards(result, wordcount - num, seed1, seed2);
                break;
        }
        if (result.length() > 0) {
            return result.toString().trim();
        } else {
            return null;
        }
    }
}
