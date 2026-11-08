package dominio;

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

    public void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
    }

    public String getLanguage() {
        return language;
    }

    // Setter del lenguaje con manejo de excepciones por si llega vacío o nulo
    public void setLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            throw new IllegalArgumentException("El lenguaje no puede estar vacío.");
        }
        this.language = language;
    }
}