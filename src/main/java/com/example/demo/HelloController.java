package com.example.demo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

public class HelloController {

    @FXML
    private TextField ipAddressField;

    @FXML
    private TextField portField;

    @FXML
    private Button button1;

    @FXML
    private Button button2;

    @FXML
    private Button button3;

    @FXML
    private Button button4;

    @FXML
    private Button button5;

    @FXML
    private Button button6;

    @FXML
    private Button button7;

    @FXML
    private Button button8;

    @FXML
    private Button button9;

    @FXML
    private Button startButton;


    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private boolean isPlayer1Turn = true;
    private boolean isServer = false;
    private EventObject event;

    @FXML
    private void startServer() {
        int port = Integer.parseInt(portField.getText());

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port " + port);
            // Accept client connections in a separate thread
            Thread acceptConnectionsThread = new Thread(() -> {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Handle the client connection
                    inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    Thread receiverThread = new Thread(this::receiveMoves);
                    receiverThread.start();
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            });
            acceptConnectionsThread.start();

            startButton.setDisable(true);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
        isServer = true;
    }



    @FXML
    private void connectToServer() {
        try {
            String ipAddress = ipAddressField.getText();
            String Port = portField.getText();
            socket = new Socket(ipAddress, Integer.parseInt(Port));
            System.out.println("Connected to server at " + ipAddress + ":" + Port);

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            Thread receiverThread = new Thread(this::receiveMoves);
            receiverThread.start();
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        }
    }

    @FXML
    private void Move(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        clickedButton.setDisable(true);

        String symbol = isPlayer1Turn ? "X" : "O";
        clickedButton.setText(symbol);

        sendMove(clickedButton.getId(), symbol);

        if (checkWin(symbol)) {
            String winner = isPlayer1Turn ? "Player 1" : "Player 2";
            System.out.println("Game Over! " + winner + " wins!");
            disableAllButtons();
        } else if (checkDraw()) {
            System.out.println("Game Over! It's a draw!");
            disableAllButtons();
        } else {
            isPlayer1Turn = !isPlayer1Turn;
            updateTurnLabel();
        }
    }

    private void sendMove(String buttonId, String symbol) {
        try {
            outputStream.writeObject(new Move(buttonId, symbol));

            outputStream.flush();
            disableAllButtons();
        } catch (IOException e) {
            System.out.println("Error sending move: " + e.getMessage());
        }
    }

    private void receiveMoves() {
        try {
            while (true) {
                Move move = (Move) inputStream.readObject();

                Platform.runLater(() -> applyMove(move));
                enableButtonsWithoutText();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving move: " + e.getMessage());
        }
    }

    private void applyMove(Move move) {
        Button button = getButtonById(move.getButtonId());
        button.setDisable(true);
        button.setText(move.getSymbol());
        if (checkWin(move.getSymbol())) {
            String winner = isPlayer1Turn ? "Player 1" : "Player 2";
            System.out.println("Game Over! " + winner + " wins!");
            disableAllButtons();
        } else if (checkDraw()) {
            System.out.println("Game Over! It's a draw!");
            disableAllButtons();
        } else {
            isPlayer1Turn = !isPlayer1Turn;
            updateTurnLabel();
        }
    }

    private Button getButtonById(String buttonId) {
        switch (buttonId) {
            case "button1":
                return button1;
            case "button2":
                return button2;
            case "button3":
                return button3;
            case "button4":
                return button4;
            case "button5":
                return button5;
            case "button6":
                return button6;
            case "button7":
                return button7;
            case "button8":
                return button8;
            case "button9":
                return button9;
            default:
                throw new IllegalArgumentException("Invalid button ID");
        }
    }



    private boolean checkWin(String symbol) {
        String[][] board = new String[3][3];

        // Заповнюємо двовимірний масив `board` символами з кнопок
        board[0][0] = button1.getText();
        board[0][1] = button2.getText();
        board[0][2] = button3.getText();
        board[1][0] = button4.getText();
        board[1][1] = button5.getText();
        board[1][2] = button6.getText();
        board[2][0] = button7.getText();
        board[2][1] = button8.getText();
        board[2][2] = button9.getText();

        // Перевіряємо горизонтальні, вертикальні та діагональні комбінації
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(symbol) && board[i][1].equals(symbol) && board[i][2].equals(symbol)) {
                return true; // Горизонтальна комбінація
            }
            if (board[0][i].equals(symbol) && board[1][i].equals(symbol) && board[2][i].equals(symbol)) {
                return true; // Вертикальна комбінація
            }
        }
        if (board[0][0].equals(symbol) && board[1][1].equals(symbol) && board[2][2].equals(symbol)) {
            return true; // Діагональна комбінація (ліва верхня - права нижня)
        }
        if (board[0][2].equals(symbol) && board[1][1].equals(symbol) && board[2][0].equals(symbol)) {
            return true; // Діагональна комбінація (права верхня - ліва нижня)
        }

        return false; // Немає перемоги
    }


    private boolean checkDraw() {
        return !button1.getText().isEmpty() && !button2.getText().isEmpty() && !button3.getText().isEmpty() &&
                !button4.getText().isEmpty() && !button5.getText().isEmpty() && !button6.getText().isEmpty() &&
                !button7.getText().isEmpty() && !button8.getText().isEmpty() && !button9.getText().isEmpty();
    }

    private void disableAllButtons() {
        button1.setDisable(true);
        button2.setDisable(true);
        button3.setDisable(true);
        button4.setDisable(true);
        button5.setDisable(true);
        button6.setDisable(true);
        button7.setDisable(true);
        button8.setDisable(true);
        button9.setDisable(true);
    }
    public void enableButtonsWithoutText() {
        List<Button> buttons = Arrays.asList(button1, button2, button3, button4, button5, button6, button7, button8, button9);

        for (Button button : buttons) {
            if (button.getText().isEmpty()) {
                button.setDisable(false);
            }
        }
    }

    private void updateTurnLabel() {
        String player = isPlayer1Turn ? "Player 1" : "Player 2";
        System.out.println(player + "'s turn");
    }

}
