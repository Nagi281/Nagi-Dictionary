package model;

import java.io.Serializable;

public class Word implements Serializable {
    private int id;
    private String name;
    private String content;

    public Word() {

    }

    public Word(int id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
        insertScriptForHref();
    }

    public static Word Copy(Word word) {
        return new Word(word.getId(), word.getName(), word.getContent());
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void insertScriptForHref() {
        this.content = this.content.replace("class=\"aexample\"",
                "onclick=\"return false;\" class=\"aexample\"");
    }
}
