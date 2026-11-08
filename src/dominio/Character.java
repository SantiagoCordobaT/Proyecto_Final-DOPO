package dominio;

public class Character {
    private String name;
    private int speed;
    private int power;

    public Character(String name, int speed, int power) {
        this.name = name;
        this.speed = speed;
        this.power = power;
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public int getPower() {
        return power;
    }
}