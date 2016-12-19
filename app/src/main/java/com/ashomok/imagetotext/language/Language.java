package com.ashomok.imagetotext.language;

/**
 * Created by iuliia on 12/11/16.
 */
public class Language {

    private String shortCut;
    private String name;
    private boolean checked;

    public Language(String name, String shortCut) {
       this(name, shortCut, false);
    }

    public Language(String name, String shortCut, boolean isChecked) {
        this.name = name;
        this.shortCut = shortCut;
        checked = isChecked;
    }

    public String getShortCut() {
        return shortCut;
    }

    public void setShortCut(String shortCut) {
        this.shortCut = shortCut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Language language = (Language) o;

        if (shortCut != null ? !shortCut.equals(language.shortCut) : language.shortCut != null)
            return false;
        return name.equals(language.name);

    }

    @Override
    public int hashCode() {
        int result = shortCut != null ? shortCut.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }
}

