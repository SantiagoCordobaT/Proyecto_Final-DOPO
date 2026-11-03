public class Settings {

    private boolean soundOn;
    private boolean musicOn;
    private String language;

    public Settings() {
        this.soundOn = true;
        this.musicOn = true;
        this.language = "Español";
    }

    public Settings(boolean soundOn, boolean musicOn, String language) {
        this.soundOn = soundOn;
        this.musicOn = musicOn;
        this.language = language;
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public String getLanguage() {
        return language;
    }

    // Setter del lenguaje (posible vacio entonces manejo con expeciones)
    public void setLanguage(String language) {
        this.language = language;
    }
}
