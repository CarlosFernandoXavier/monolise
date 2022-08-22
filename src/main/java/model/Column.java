package model;

public class Column {
    private String nomeFuncionalidade;
    private Double threshold;

    public Column(String nomeFuncionalidade, Double threshold) {
        this.nomeFuncionalidade = nomeFuncionalidade;
        this.threshold = threshold;
    }

    public String getNomeFuncionalidade() {
        return nomeFuncionalidade;
    }

    public Double getThreshold() {
        return threshold;
    }
}
