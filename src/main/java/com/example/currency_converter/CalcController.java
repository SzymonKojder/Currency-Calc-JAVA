package com.example.currency_converter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CalcController implements Initializable {
    @FXML
    private TextField amount;
    @FXML
    private ComboBox<String> convert_from;
    @FXML
    private ComboBox<String> convert_to;
    @FXML
    private Label lbl_result;

    ObservableList<String> codesList = FXCollections.observableArrayList();

    double amount_result;
    JSONParser jp = new JSONParser();
    JSONObject jsonObject = new JSONObject();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getAPI("EUR", "PLN");
            convert_from.getItems().addAll(codesList);
            convert_from.getSelectionModel().select("EUR (Euro)");
            convert_to.getItems().addAll(codesList);
            convert_to.getSelectionModel().select("PLN (Polski złoty)");
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void enterKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            result();
        }
    }

    @FXML
    public void exit() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void setTextLimit() {
        amount.setOnKeyTyped(keyEvent -> {
            String str = amount.getText();
            if (str.length() > 10) {
                amount.setText(str.substring(0, 10));
                amount.positionCaret(str.length());
            }
        });
    }

    @FXML
    private void result() {
        try {
            String str_from = convert_from.getSelectionModel().getSelectedItem().substring(0, 3);
            String str_to = convert_to.getSelectionModel().getSelectedItem().substring(0, 3);
            getAPI(str_from, str_to);

            try {
                amount_result = Double.parseDouble(amount.getText().replace(",", "."));

                if (amount_result <= 0.0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Nieprawidłowa kwota!");
                    alert.setContentText("Proszę wprowadzić kwotę większą od 0!");
                    alert.showAndWait();
                    amount.clear();
                }
            }
            catch (Exception exception) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Nieprawidłowa kwota!");
                alert.setContentText("Proszę wprowadzić prawidłową kwotę!");
                alert.showAndWait();
                amount.clear();
            }

            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);

            double base = amount_result * Double.parseDouble(jsonObject.get("result").toString());

            if (amount.getText().isEmpty() || amount_result <= 0.0) {
                lbl_result.setText("");
            }
            else {
                lbl_result.setText(amount.getText().replace(".", ",") + " " + str_from + " = " + df.format(base) + " " + str_to);
            }
        }
        catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Nieprawidłowy kod waluty!");
            alert.setContentText("Proszę wybrać poprawny kod waluty!");
            alert.showAndWait();
        }
    }

    @FXML
    public void getAPI(String from, String to) throws IOException, ParseException {
        String url_str = "https://api.exchangerate.host/convert?from="+from+"&to="+to+"";

        URL url;
        HttpURLConnection request = null;
        try {
            url = new URL(url_str);
            request = (HttpURLConnection) url.openConnection();
            request.connect();
        }
        catch (MalformedURLException malformedURLException) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Przepraszamy, brak połączenia!");
            alert.setContentText("Proszę sprawdzić swoje połączenie internetowe!");
            alert.showAndWait();
            System.exit(0);
        }
        catch (IOException ioException) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Nie znaleziono danych!");
            alert.setContentText("Proszę sprawdzić swoje połączenie internetowe!");
            alert.showAndWait();
            System.exit(0);
        }

        try {
            jsonObject = (JSONObject) jp.parse(new InputStreamReader((InputStream) request.getContent()));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Przepraszamy, nie znaleziono plików biblioteki!");
            alert.setContentText("Proszę zaimportować plik biblioteki 'json.simple-1.1.jar'!");
            alert.showAndWait();
        }
        catch (IOException ioException) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Przepraszamy, nie można zaimportować plików biblioteki!");
            alert.setContentText("Proszę sprawdzić dostępność plików biblioteki!");
            alert.showAndWait();
        }
        catch (ParseException parseException) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Przepraszamy, nie można konwertować w formacie JSON!");
            alert.setContentText("Proszę sprawdzić dostępność plików biblioteki!");
            alert.showAndWait();
        }

        setCountryCodes();
    }

    @FXML
    public void setCountryCodes() {
        codesList.add("AED (Dirham Zjednoczonych Emiratów Arabskich)");
        codesList.add("AUD (Dolar australijski)");
        codesList.add("BGN (Lew bułgarski)");
        codesList.add("BTC (Bitcoin)");
        codesList.add("CAD (Dolar kanadyjski)");
        codesList.add("CHF (Frank szwajcarski)");
        codesList.add("CZK (Korona czeska)");
        codesList.add("DKK (Korona duńska)");
        codesList.add("EUR (Euro)");
        codesList.add("GBP (Brytyjski funt szterling)");
        codesList.add("HKD (Dolar Hongkongu)");
        codesList.add("JPY (Jen japoński)");
        codesList.add("NOK (Korona norweska)");
        codesList.add("NZD (Dolar nowozelandzki)");
        codesList.add("PLN (Polski złoty)");
        codesList.add("SEK (Korona szwedzka)");
        codesList.add("SGD (Dolar singapurski)");
        codesList.add("THB (Bat tajlandzki)");
        codesList.add("TRY (Lira turecka)");
        codesList.add("UAH (Hrywna ukraińska)");
        codesList.add("USD (Dolar amerykański)");
        codesList.add("XAU (Uncja złota)");
    }
}
