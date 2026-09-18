import java.util.ArrayList;
import java.util.List;


// ===============================
// Observer
// ===============================

interface Observer {

    void update(
            double temperature,
            double humidity,
            double pressure
    );
}


// ===============================
// Concrete Observer 1
// ===============================

class CurrentConditionsDisplay
        implements Observer {

    @Override
    public void update(
            double temperature,
            double humidity,
            double pressure) {

        System.out.println(
            "Current Conditions: "
            + temperature + "°C, "
            + humidity + "% humidity"
        );
    }
}


// ===============================
// Concrete Observer 2
// ===============================

class StatisticsDisplay
        implements Observer {

    @Override
    public void update(
            double temperature,
            double humidity,
            double pressure) {

        System.out.println(
            "Statistics Display Updated: "
            + temperature + "°C"
        );
    }
}


// ===============================
// Concrete Observer 3
// ===============================

class ForecastDisplay
        implements Observer {

    @Override
    public void update(
            double temperature,
            double humidity,
            double pressure) {

        System.out.println(
            "Forecast Display Updated"
        );
    }
}


// ===============================
// Subject
// ===============================

class WeatherStation {

    private List<Observer> observers =
            new ArrayList<>();

    private double temperature;
    private double humidity;
    private double pressure;


    public void addObserver(Observer observer) {
        observers.add(observer);
    }


    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }


    private void notifyObservers() {

        for (Observer observer : observers) {

            observer.update(
                    temperature,
                    humidity,
                    pressure
            );
        }
    }


    public void setMeasurements(
            double temperature,
            double humidity,
            double pressure) {

        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;

        notifyObservers();
    }
}


// ===============================
// Client
// ===============================

public class Main {

    public static void main(String[] args) {

        WeatherStation station =
                new WeatherStation();


        Observer current =
                new CurrentConditionsDisplay();

        Observer statistics =
                new StatisticsDisplay();

        Observer forecast =
                new ForecastDisplay();


        station.addObserver(current);
        station.addObserver(statistics);
        station.addObserver(forecast);


        station.setMeasurements(
                30,
                70,
                1012
        );


        System.out.println("\nRemoving forecast display...\n");


        station.removeObserver(forecast);


        station.setMeasurements(
                32,
                65,
                1010
        );
    }
}
