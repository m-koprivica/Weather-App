package ui;

import model.WeatherApp;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class WeatherAppGUI extends JFrame {
    private final WeatherApp weatherApp;
    private final JPanel hourlyWeatherInfoPanel;
    private final JPanel currentWeatherInfoPanel;

    public WeatherAppGUI() {
        super("Weather App");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(905, 590);
        setLocationRelativeTo(null); // center the app on startup

        weatherApp = new WeatherApp("Vancouver");

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        initSearchPanel(searchPanel);

        hourlyWeatherInfoPanel = new JPanel();
        hourlyWeatherInfoPanel.setLayout(new FlowLayout());
        initHourlyWeatherInfo();

        currentWeatherInfoPanel = new JPanel();
        currentWeatherInfoPanel.setLayout(new BorderLayout());
        initCurrentWeatherInfo();

        Container contentPane = getContentPane();
        contentPane.add("North", searchPanel);
        contentPane.add("South", hourlyWeatherInfoPanel);
        contentPane.add("Center", currentWeatherInfoPanel);

        validate();
        repaint();
    }

    private void initCurrentWeatherInfo() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // clear previous weather data
                currentWeatherInfoPanel.removeAll();
                currentWeatherInfoPanel.revalidate();
                currentWeatherInfoPanel.repaint();

                // fetch and display new data
                JSONObject currData = weatherApp.findCurrentWeatherData();
                displayCurrentData(currData);
                return null;
            }
        };
        worker.execute();
    }

    private void displayCurrentData(JSONObject currData) {
        SwingUtilities.invokeLater(() -> {
            JSONObject data = (JSONObject) currData.get("current");

            double temp = (double) data.get("temperature_2m");
            long code = (long) data.get("weather_code");
            long isDay = (long) data.get("is_day");
            String time = (String) data.get("time");

            String timeStatus = isDay == 1 ? "day" : "night";
            String currTime = time.substring(time.indexOf("T") + 1);
            JLabel timeLabel = new JLabel("As of " + currTime + " in " + weatherApp.getLocation() + ": ");
            timeLabel.setFont(new Font("Default", Font.BOLD, 15));
            JLabel iconLabel = getWeatherIcon(code, timeStatus);
            JLabel tempLabel = new JLabel(temp + " °C");
            tempLabel.setFont(new Font("Default", Font.BOLD, 15));

            JPanel currentWeatherInfoBox = new JPanel();
            currentWeatherInfoBox.setLayout(new BorderLayout());
            currentWeatherInfoBox.add(timeLabel, BorderLayout.NORTH);
            currentWeatherInfoBox.add(iconLabel, BorderLayout.WEST);
            currentWeatherInfoBox.add(tempLabel, BorderLayout.EAST);

            Border border = BorderFactory.createLineBorder(new Color(90, 90, 90), 5); // Red border with 5px thickness
            currentWeatherInfoBox.setBorder(border);
            currentWeatherInfoBox.setBackground(new Color(200, 200, 200));

            JPanel backgroundImagePanel = new BackgroundForecastPanel(weatherApp.findWeatherBackground((int) code, timeStatus));
            backgroundImagePanel.add(currentWeatherInfoBox);
            currentWeatherInfoPanel.add(backgroundImagePanel, BorderLayout.CENTER);
        });

        currentWeatherInfoPanel.revalidate();
        currentWeatherInfoPanel.repaint();
    }

    private void initHourlyWeatherInfo() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // clear previous weather data
                hourlyWeatherInfoPanel.removeAll();
                hourlyWeatherInfoPanel.revalidate();
                hourlyWeatherInfoPanel.repaint();

                // fetch and display new data
                JSONObject hourlyData = weatherApp.findHourlyWeatherData();
                displayHourlyData(hourlyData);
                return null;
            }
        };
        worker.execute();
    }

    private void displayHourlyData(JSONObject hourlyData) {
        SwingUtilities.invokeLater(() -> {
            JSONObject hourly = (JSONObject) hourlyData.get("hourly");
            JSONArray temps = (JSONArray) hourly.get("temperature_2m");
            JSONArray codes = (JSONArray) hourly.get("weather_code"); // indicates weather
            JSONArray isDay = (JSONArray) hourly.get("is_day");

            // change isDay entries (0, 1) to strings (night, day)
            // used to append to image icon path
            List<String> isDayStrings = new ArrayList<>();
            for (int i = 0; i < isDay.size(); i++) {
                if ((long) isDay.get(i) == 1) {
                    isDayStrings.add("day");
                } else {
                    isDayStrings.add("night");
                }
            }

            // create boxes for every 3rd hour, containing temperature and weather
            for (int i = 0; i < temps.size(); i++) {
                if (i % 3 != 0) continue;

                double d = (Double) temps.get(i);

                JPanel hourlyWeatherBox = new JPanel();
                hourlyWeatherBox.setLayout(new BorderLayout());
                JLabel hourLabel = new JLabel(i + ":00");
                JLabel iconLabel = getWeatherIcon((Long) codes.get(i), isDayStrings.get(i));
                JLabel tempLabel = new JLabel(d + " °C");

                hourlyWeatherBox.add(hourLabel, BorderLayout.NORTH);
                hourlyWeatherBox.add(iconLabel, BorderLayout.WEST);
                hourlyWeatherBox.add(tempLabel, BorderLayout.EAST);

                Border border = BorderFactory.createLineBorder(new Color(90, 90, 90), 5); // Red border with 5px thickness
                hourlyWeatherBox.setBorder(border);
                hourlyWeatherBox.setBackground(new Color(200, 200, 200));

                hourlyWeatherInfoPanel.add(hourlyWeatherBox);
            }
            hourlyWeatherInfoPanel.revalidate();
            hourlyWeatherInfoPanel.repaint();
        });
    }

    private JLabel getWeatherIcon(Long weatherCode, String timeStatus) {
        String weatherIconPath = weatherApp.findWeatherIcon(weatherCode.intValue(), timeStatus);
        ImageIcon icon = new ImageIcon(getClass().getResource(weatherIconPath));

        Image image = icon.getImage();
        Image resizedImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        return new JLabel(resizedIcon);
    }

    private void initSearchPanel(JPanel panel) {
        JTextField searchBar = new JTextField(20);
        searchBar.setFont(new Font("Tahoma", Font.PLAIN, 18));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Tahoma", Font.PLAIN, 16));

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchBar.getText();
                // when search button is clicked -> change location & reload weather info
                weatherApp.changeLocation(searchText);
                initCurrentWeatherInfo();
                initHourlyWeatherInfo();
            }
        });

        panel.add(searchBar);
        panel.add(searchButton);
    }
}