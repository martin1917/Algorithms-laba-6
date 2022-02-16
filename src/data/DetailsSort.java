package data;

public class DetailsSort {
    private String name;
    private Double time;
    private Double countRead;
    private Double countWrite;
    private Double countCompare;

    private DetailsSort(String name, Double time, Double countRead, Double countWrite, Double countCompare) {
        this.name = name;
        this.time = time;
        this.countRead = countRead;
        this.countWrite = countWrite;
        this.countCompare = countCompare;
    }

    public static DetailsSort createByDetails(String name, double[] details) {
        return new DetailsSort(name, details[0], details[1], details[2], details[3]);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTime() {
        return this.time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Double getCountRead() {
        return this.countRead;
    }

    public void setCountRead(Double countRead) {
        this.countRead = countRead;
    }

    public Double getCountWrite() {
        return this.countWrite;
    }

    public void setCountWrite(Double countWrite) {
        this.countWrite = countWrite;
    }

    public Double getCountCompare() {
        return this.countCompare;
    }

    public void setCountCompare(Double countCompare) {
        this.countCompare = countCompare;
    }


    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", time='" + getTime() + "'" +
            ", countRead='" + getCountRead() + "'" +
            ", countWrite='" + getCountWrite() + "'" +
            ", countCompare='" + getCountCompare() + "'" +
            "}";
    }
}
