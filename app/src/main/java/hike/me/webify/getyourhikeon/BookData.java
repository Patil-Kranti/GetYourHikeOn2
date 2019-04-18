package hike.me.webify.getyourhikeon;

public class BookData {
    String location, date, time, destination;

    public BookData(String location, String date, String time) {
        this.location = location;
        this.date = date;
        this.time = time;

    }

    public BookData(String destination) {
        this.destination = destination;
    }

    public BookData() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
