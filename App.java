import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class App {
    public static void main(String[] args) {
        try {
            URL url = new URL("https://ghibliapi.vercel.app/films");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setInstanceFollowRedirects(true);

            int responseCode = httpConn.getResponseCode();

            if (responseCode != 200) {
                System.out.println("❌ Failed to fetch data. Response Code: " + responseCode);
                return;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            Gson gson = new Gson();
            JsonArray movieArray = gson.fromJson(response.toString(), JsonArray.class);

            // ─── HEADER ───────────────────────────────────────────────
            System.out.println("\n============================================================");
            System.out.println("  🎬  STUDIO GHIBLI FILM CATALOG");
            System.out.println("============================================================\n");

            // ─── FILM LIST ─────────────────────────────────────────────
            for (JsonElement movieElement : movieArray) {
                JsonObject movie = movieElement.getAsJsonObject();
                String title = movie.get("title").getAsString();
                String description = movie.get("description").getAsString();

                System.out.println(title);
                
                // Wrap description at 80 characters, breaking at word boundaries
                while (description.length() > 80) {
                    int space = description.lastIndexOf(' ', 80);
                    if (space == -1) {
                        space = 80;
                    }
                    System.out.println("  " + description.substring(0, space));
                    description = description.substring(space + 1);
                }
                System.out.println("  " + description);
                System.out.println();  // Blank line between entries
                System.out.println("------------------------------------------------------------");
                System.out.println(); 
            }

            // ─── SEARCH PROMPT ─────────────────────────────────────────
            System.out.println("------------------------------------------------------------");
            System.out.println("  🔍  Enter a film title to see full details");
            System.out.println("  💡  Type 'quit' to exit");
            System.out.println("------------------------------------------------------------");

            Scanner scan = new Scanner(System.in);
            boolean done = false;

            while (!done) {
                System.out.print("\n  ▶  Search: ");
                String input = scan.nextLine().trim();

                if (input.equalsIgnoreCase("quit")) {
                    done = true;
                    System.out.println("\n  👋  Goodbye!");
                    break;
                }

                boolean found = false;
                for (JsonElement movieElement : movieArray) {
                    JsonObject movie = movieElement.getAsJsonObject();
                    String title = movie.get("title").getAsString();
                    if (title.equalsIgnoreCase(input)) {
                        System.out.println("\n============================================================");
                        System.out.println("  📽️  " + title.toUpperCase());
                        System.out.println("============================================================");
                        
                        String desc = movie.get("description").getAsString();
                        while (desc.length() > 80) {
                            int space = desc.lastIndexOf(' ', 80);
                            if (space == -1) {
                                space = 80;
                            }
                            System.out.println("  " + desc.substring(0, space));
                            desc = desc.substring(space + 1);
                        }
                        System.out.println("  " + desc);
                        
                        System.out.println("  📅  Release Date: " + movie.get("release_date").getAsString());
                        System.out.println("  🎨  Producer: " + movie.get("producer").getAsString());
                        System.out.println("============================================================");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println("  ❌  Movie not found. Please try again.");
                }
            }

            scan.close();

        } catch (Exception e) {
            System.err.println("\n⚠️  An error occurred:");
            e.printStackTrace();
        }
    }
}