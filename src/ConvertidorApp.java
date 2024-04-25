import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import java.util.Properties;
import java.io.FileInputStream;

public class ConvertidorApp {

    private static final String CLP = "CLP";
    private static final String USD = "USD";
    private static final String COP = "COP";

    //Aqui carga el archivo que contiene la API
    private static String loadApiKeyFromProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/config.properties"));
        return properties.getProperty("API_KEY");
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String apiKey = loadApiKeyFromProperties(); // Carga la clave API desde el archivo propiedades


        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Convertir CLP a USD");
            System.out.println("2. Convertir COP a USD");
            System.out.println("3. Convertir USD a CLP");
            System.out.println("4. Convertir USD a COP");
            System.out.println("5. Salir");

            int choice = Integer.parseInt(reader.readLine());

            switch (choice) {
                case 1:
                    exchangeCLPtoUSD(reader, apiKey);
                    break;
                case 2:
                    exchangeCOPtoUSD(reader, apiKey);
                    break;
                case 3:
                    exchangeUSDtoCLP(reader, apiKey);
                    break;
                case 4:
                    exchangeUSDtoCOP(reader, apiKey);
                    break;
                case 5:
                    System.out.println("Saliendo de la aplicación...");
                    return;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private static void exchangeCLPtoUSD(BufferedReader reader, String apiKey) throws IOException {
        double amount = getAmountFromUser(reader);
        double rate = getExchangeRate(apiKey, CLP, USD);
        double convertedAmount = amount / rate;
        printConversionResult(amount, CLP, convertedAmount, USD);
    }

    private static void exchangeCOPtoUSD(BufferedReader reader, String apiKey) throws IOException {
        double amount = getAmountFromUser(reader);
        double rate = getExchangeRate(apiKey, COP, USD);
        double convertedAmount = amount / rate;
        printConversionResult(amount, COP, convertedAmount, USD);
    }

    private static void exchangeUSDtoCLP(BufferedReader reader, String apiKey) throws IOException {
        double amount = getAmountFromUser(reader);
        double rate = getExchangeRate(apiKey, USD, CLP);
        double convertedAmount = amount * rate;
        printConversionResult(amount, USD, convertedAmount, CLP);
    }

    private static void exchangeUSDtoCOP(BufferedReader reader, String apiKey) throws IOException {
        double amount = getAmountFromUser(reader);
        double rate = getExchangeRate(apiKey, USD, COP);
        double convertedAmount = amount * rate;
        printConversionResult(amount, USD, convertedAmount, COP);
    }

    private static double getExchangeRate(String apiKey, String baseCurrency, String targetCurrency) throws IOException {
        String url = String.format("https://v6.exchangerate-api.com/v6/%s/latest/%s", apiKey, baseCurrency);

        try (var response = new OkHttpClient().newCall(new okhttp3.Request.Builder().url(url).build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error: API request failed with status code " + response.code());
            }

            JSONObject data = new JSONObject(response.body().string());

            if (!data.getString("result").equals("success")) {
                throw new IOException("Error: API response indicates an error (" + data.getString("error-type") + ")");
            }

            JSONObject conversionRates = data.getJSONObject("conversion_rates");
            double rate = conversionRates.getDouble(targetCurrency);

            return rate;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static double getAmountFromUser(BufferedReader reader) throws IOException {
        System.out.print("Ingrese la cantidad a convertir: ");
        return Double.parseDouble(reader.readLine());
    }

    private static void printConversionResult(double originalAmount, String originalCurrency, double convertedAmount, String convertedCurrency) {
        System.out.printf("%.2f %s equivale a %.2f %s\n", originalAmount, originalCurrency, convertedAmount, convertedCurrency);
        }
    }