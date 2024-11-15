package es.codegym.telegrambot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyFirstTelegramBot extends TelegramLongPollingBot {

    public static final String NAME = "CodeGymBotty_bot";
    public static final String TOKEN = "7885667456:AAE-TVraFJKIQcbWsWlXufCIB-l7YFaGX3g"; // Reemplaza con tu token

    // Variables para rastrear el progreso del juego
    private int currentStep = 1;

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackData = update.getCallbackQuery().getData();

            // Lógica del juego con los pasos
            if (callbackData.equals("next_step") && currentStep < 8) {
                currentStep++;  // Avanzar al siguiente paso
                sendStep(chatId, currentStep);
            } else if (currentStep == 8) {
                sendFinalText(chatId);  // Cuando llega al último paso, mostrar el final
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                currentStep = 1;  // Comienza el juego desde el primer paso
                sendStep(chatId, currentStep);
            }
        }
    }

    // Método para enviar los pasos del juego
    private void sendStep(Long chatId, int step) {
        String imageName = "step_" + step + "_pic";  // Nombre de la imagen correspondiente al paso
        String text = getStepText(step);  // Obtener el texto correspondiente al paso

        // Primero, enviar la imagen
        String resourcePath = "images/" + imageName + ".jpg";  // Ruta relativa al recurso en el classpath
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream != null) {
            // Crear el mensaje con la imagen
            SendPhoto photoMessage = new SendPhoto();
            photoMessage.setChatId(chatId.toString());
            photoMessage.setPhoto(new InputFile(inputStream, imageName + ".jpg"));

            try {
                execute(photoMessage);  // Enviar la imagen
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            // Si la imagen no se encuentra, imprimir mensaje de error
            System.out.println("No se pudo encontrar la imagen en la ruta: " + resourcePath);
        }

        // Después, enviar el mensaje de texto con el botón
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("Markdown");

        // Crear el botón para avanzar al siguiente paso
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton nextStepButton = new InlineKeyboardButton();
        nextStepButton.setText("Siguiente paso");
        nextStepButton.setCallbackData("next_step");

        row.add(nextStepButton);
        rows.add(row);
        keyboardMarkup.setKeyboard(rows);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);  // Enviar el mensaje con el botón
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Método para obtener el texto del paso actual
    private String getStepText(int step) {
        switch (step) {
            case 1:
                return TelegramBotContent.STEP_1_TEXT;
            case 2:
                return TelegramBotContent.STEP_2_TEXT;
            case 3:
                return TelegramBotContent.STEP_3_TEXT;
            case 4:
                return TelegramBotContent.STEP_4_TEXT;
            case 5:
                return TelegramBotContent.STEP_5_TEXT;
            case 6:
                return TelegramBotContent.STEP_6_TEXT;
            case 7:
                return TelegramBotContent.STEP_7_TEXT;
            case 8:
                return TelegramBotContent.STEP_8_TEXT;
            default:
                return TelegramBotContent.FINAL_TEXT;
        }
    }

    // Método para enviar el texto final del juego
    private void sendFinalText(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(TelegramBotContent.FINAL_TEXT);
        message.setParseMode("Markdown");

        try {
            execute(message);  // Enviar el mensaje final
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new MyFirstTelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
