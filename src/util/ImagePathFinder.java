package util;

public class ImagePathFinder {

    // Returns path of icon of weather given weather code
    // timeStatus is one of: day, night
    public static String findWeatherIcon(int weatherCode, String timeStatus) {
        String weatherIconPath = "/assets/icons/";

        weatherIconPath = appendWeatherNameToPath(weatherCode, weatherIconPath);

        weatherIconPath += "_" + timeStatus + ".png";
        return weatherIconPath;
    }

    // Returns path of background image of weather given weather code
    // timeStatus is one of: day, night
    public static String findWeatherBackground(int weatherCode, String timeStatus) {
        String weatherIconPath = "/assets/backgrounds/";

        weatherIconPath = appendWeatherNameToPath(weatherCode, weatherIconPath);

        weatherIconPath += "_" + timeStatus + ".jpeg";
        return weatherIconPath;
    }

    // Adds weather name to imagePath given the weatherCode.
    private static String appendWeatherNameToPath(int weatherCode, String imagePath) {
        if (isClear(weatherCode)) {
            imagePath += "clear";
        } else if (isCloudy(weatherCode)) {
            imagePath += "cloudy";
        } else if (isFoggy(weatherCode)) {
            imagePath += "foggy";
        } else if (isRainy(weatherCode)) {
            imagePath += "rainy";
        } else if (isSnowy(weatherCode)) {
            imagePath += "snowy";
        } else if (isThunderStorm(weatherCode)) {
            imagePath += "thunderstorm";
        } else {
            imagePath += "sandstorm";
        }
        return imagePath;
    }

    // Returns true if the weather code corresponds to the following types of weather
    // Used to find appropriate weather icon.

    private static Boolean isClear(int code) {
        return code == 0 || code == 1;
    }

    private static Boolean isCloudy(int code) {
        return code == 2 || code == 3;
    }

    private static Boolean isFoggy(int code) {
        return code == 45 || code == 48;
    }

    private static Boolean isRainy(int code) {
        return (code >= 51 && code <= 67) || (code >= 80 && code <= 82);
    }

    private static Boolean isSnowy(int code) {
        return (code >= 71 && code <= 77) || code == 85 || code == 86;
    }

    private static Boolean isThunderStorm(int code) {
        return code == 95 || code == 96 || code == 99;
    }
}
