package ru.travelplanner.TGbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.travelplanner.model.BookingForm;
import ru.travelplanner.model.TourPackage;
import ru.travelplanner.model.User;
import ru.travelplanner.service.TourPackageService;
import ru.travelplanner.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;


@org.springframework.stereotype.Service
public class TelegramBotService {

    private static final int MAX_MESSAGE_LENGTH = 4096;
    private final TelegramBot telegramBot;
    private final TourPackageService tourPackageService;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionManager userSessionManager;

    @Autowired
    public TelegramBotService(TelegramBot telegramBot, TourPackageService tourPackageService, UserServiceImpl userService, PasswordEncoder passwordEncoder, UserSessionManager userSessionManager) {
        this.telegramBot = telegramBot;
        this.tourPackageService = tourPackageService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userSessionManager = userSessionManager;
        this.telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    private void processUpdate(Update update) {
        String messageText = update.message().text();
        Long chatId = update.message().chat().id();

        if (!messageText.startsWith("/") || messageText.length() == 1) {
            handleUnknownCommand(chatId);
            return;
        }

        if (messageText.startsWith("/login")) {
            handleLogin(chatId, messageText.replace("/login ", ""));
        } else if (messageText.startsWith("/getTour")) {
            handleGetTourById(chatId, messageText.replace("/getTour ", ""));
        } else if (messageText.startsWith("/addTour")) {
            handleAddTour(chatId, messageText.replace("/addTour ", ""));
        } else if (messageText.startsWith("/deleteTour")) {
            handleDeleteTour(chatId, messageText.replace("/deleteTour ", ""));
        } else if (messageText.startsWith("/start")) {
            handleStart(chatId);
        } else if(messageText.startsWith("/suitable")){
            handleSuitable(chatId, messageText.replace("/suitable ", ""));
        } else if (messageText.startsWith("/form")){
            handleForm(chatId, messageText.replace("/form ", ""));
        } else if (messageText.startsWith("/booking")) {
            handleBooking(chatId);
        } else if(messageText.startsWith("/help")){
            handleHelp(chatId);
        }
    }

    public void handleLogin(Long chatId, String credentials) {
        String[] parts = credentials.split(" ");
        if (parts.length != 2) {
            sendMessage(chatId, "Использование: /login <Логин> <Пароль>");
            return;
        }
        String username = parts[0];
        String password = parts[1];
        try {
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (passwordEncoder.matches(password, userDetails.getPassword())) {
                userSessionManager.loginUser(chatId, username);
                sendMessage(chatId, "Авторизация успешна!");
            } else {
                sendMessage(chatId, "Неверный пароль.");
            }
        } catch (Exception e) {
            sendMessage(chatId, "Пользователь не найден.");
        }
    }

    public void handleGetTourById(Long chatId, String idText) {
        try {
            Long id = Long.parseLong(idText);
            Optional<TourPackage> tourPackage = tourPackageService.getTourPackageById(id);
            if (tourPackage.isPresent()) {
                TourPackage tour = tourPackage.get();
                String message = String.format(
                        "Страна: %s\nГород: %s\nКол-во недель: %d\nКол-во людей: %d\nИнформация об отеле: %s\nЦена: %d",
                        tour.getCountry(),
                        tour.getCity(),
                        tour.getDurationInWeeks(),
                        tour.getCntPeople(),
                        tour.getHotelDescription(),
                        tour.getPrice()
                );
                sendMessage(chatId, message);
            } else {
                sendMessage(chatId, "Тур не найден.");
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Неверный формат ID.");
        }
    }


    public void handleAddTour(Long chatId, String credentials) {
        if (!userSessionManager.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "У вас не достаточно прав. Пожалуйста авторизуйтесь.");
            return;
        }
        String[] parts = credentials.split(" ");
        if (parts.length != 6) {
            sendMessage(chatId, "Использование: /addTour <Страна> <Город> <Кол-во недель> <Кол-во людей> <Информация об отеле> <Цена>");
            return;
        }
        String country = parts[0];
        String city = parts[1];
        int cntWeeks = Integer.parseInt(parts[2]);
        int cntPeople = Integer.parseInt(parts[3]);
        String hotelInfo = parts[4];
        int price = Integer.parseInt(parts[5]);
        TourPackage newTour = new TourPackage();
        newTour.setCountry(country);
        newTour.setCity(city);
        newTour.setDurationInWeeks(cntWeeks);
        newTour.setCntPeople(cntPeople);
        newTour.setHotelDescription(hotelInfo);
        newTour.setPrice(price);
        TourPackage savedTour = tourPackageService.saveTourPackage(newTour);
        sendMessage(chatId, "Тур добавлен с ID: " + savedTour.getId());
    }

    public void handleDeleteTour(Long chatId, String idText) {
        if (!userSessionManager.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "У вас не достаточно прав. Пожалуйста авторизуйтесь.");
            return;
        }
        try {
            Long id = Long.parseLong(idText);
            tourPackageService.deleteTourPackageById(id);
            sendMessage(chatId, "Тур удалён.");
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Неверный формат ID.");
        }
    }

    private void handleUnknownCommand(Long chatId) {
        sendMessage(chatId, "Неизвестная команда.");
    }



    public String escapeHtml(String text) {
        // Заменяем неподдерживаемые символы на HTML-энтити
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public void sendMessage(Long chatId, String text) {
        // Экранирование неподдерживаемых символов
        text = escapeHtml(text);

        int length = text.length();
        for (int i = 0; i < length; i += MAX_MESSAGE_LENGTH) {
            String part = text.substring(i, Math.min(length, i + MAX_MESSAGE_LENGTH));
            SendMessage request = new SendMessage(chatId, part)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true);

            try {
                SendResponse response = telegramBot.execute(request);
                if (!response.isOk()) {
                    System.err.println("Error sending message: " + response.description());
                }
                // Pause between messages to avoid hitting rate limits
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void handleStart(Long chatId) {
        sendMessage(chatId, "Добро пожаловать в TravelPlaner!\nЧтобы подобрать подходящий тур введите команду:\n/suitable <Бюджет> <Кол-во людей> <Время(недели)>\nЛибо же введите /help для получения списка доступных команд");
    }

    public void handleSuitable(Long chatId, String credentials){
        String[] parts = credentials.split(" ");
        if (parts.length != 3) {
            sendMessage(chatId, "Использование: /suitable <Бюджет> <Кол-во людей> <Кол-во недель>");
            return;
        }
        int budjet = Integer.parseInt(parts[0]);
        int cntPeople = Integer.parseInt(parts[1]);
        int cntWeeks = Integer.parseInt(parts[2]);

        List<TourPackage> tours = tourPackageService.getSuitableTourPackages(budjet, cntPeople, cntWeeks);

        StringBuilder response = new StringBuilder("Вот все подходящие вам туры:\n");
        for (TourPackage tour : tours) {

            String message = String.format(
                    "Страна: %s\nГород: %s\nКол-во недель: %d\nКол-во людей: %d\nИнформация об отеле: %s\nЦена: %d\n",
                    tour.getCountry(),
                    tour.getCity(),
                    tour.getDurationInWeeks(),
                    tour.getCntPeople(),
                    tour.getHotelDescription(),
                    tour.getPrice()
            );

            response.append(tour.getId()).append(": \n").append(message).append("\n");

        }
        sendMessage(chatId, response.toString());
        sendMessage(chatId, "Если вы выбрали что-то из предложенного и хотите перейти к оформлению заявки, пожалуйста заполните форму.\nПример: /form <Имя> <Фамилия> <Номер> <Почта> <ID путёвки>");
    }

    private void handleForm(Long chatId, String credentials){
        String[] parts = credentials.split(" ");
        if (parts.length != 5) {
            sendMessage(chatId, "Использование: /form <Имя> <Фамилия> <Номер> <Почта> <ID путёвки>");
            return;
        }
        String firstName = parts[0];
        String lastName = parts[1];
        String numberOfPhone = parts[2];
        String email = parts[3];
        Long id = Long.parseLong(parts[4]);
        TourPackage test = new TourPackage();
        if(tourPackageService.getTourPackageById(id).isPresent()){
            test = tourPackageService.getTourPackageById(id).get();
        }
        else{
            sendMessage(chatId, "Вы ввели несуществующее ID.");
            return;
        }
        TourPackage tourId = test;

        BookingForm newForm = new BookingForm();
        newForm.setFirstName(firstName);
        newForm.setLastName(lastName);
        newForm.setPhoneNumber(numberOfPhone);
        newForm.setEmail(email);
        newForm.setTourPackage(tourId);
        tourPackageService.saveBookingForm(newForm);

        sendMessage(chatId, "Ваша заявка отправлена. Скоро с вами свяжется специалист");
    }

    public void handleBooking(Long chatId){

        List<BookingForm> booking = tourPackageService.getBookingForms();

        StringBuilder response = new StringBuilder("Вот все оставленные заявки:\n");
        for (BookingForm book : booking) {

            String message = String.format(
                    "Имя: %s\nФамилия: %s\nНомер: %s\nEmail: %s\nID тура: %s",
                    book.getFirstName(),
                    book.getLastName(),
                    book.getPhoneNumber(),
                    book.getEmail(),
                    book.getTourPackage()

            );

            response.append(book.getId()).append(": \n").append(message).append("\n");

        }
        sendMessage(chatId, response.toString());
    }

    private void handleHelp(Long chatId){
        if (!userSessionManager.isUserLoggedIn(chatId)) {
            sendMessage(chatId, "Вот все доступные команды:\n" +
                                    "/suitable <Бюджет> <Кол-во людей> <Кол-во недель> - подбор подходящего тура\n" +
                                    "/form <Имя> <Фамилия> <Номер> <Почта> <ID путёвки> - оставить заявку на покупку тура\n" +
                                    "/getTour <ID> - выводит тур по его ID\n" +
                                    "/login <Username> <Password> - авторизация\n" +
                                    "/start - запуск бота\n" +
                                    "/help - доступные команды");
            return;
        }
        else {
            sendMessage(chatId, "Вот все доступные команды:\n" +
                    "/addTour <Страна> <Город> <Кол-во недель> <Кол-во людей> <Информация об отеле> <Цена> - добавление нового тура\n" +
                    "/deleteTour <ID> - Удаление тура по его ID\n" +
                    "/booking - вывод всех оставленных заявок\n" +
                    "/suitable <Бюджет> <Кол-во людей> <Кол-во недель> - подбор подходящего тура\n" +
                    "/form <Имя> <Фамилия> <Номер> <Почта> <ID путёвки> - оставить заявку на покупку тура\n" +
                    "/getTour <ID> - выводит тур по его ID\n" +
                    "/login <Username> <Password> - авторизация\n" +
                    "/start - запуск бота\n" +
                    "/help - доступные команды");
        }

    }
}

