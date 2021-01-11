public class Produs {
    String nume;
    String numeLicitant;
    float pretStart;
    float pretCurent;

    public Produs(String nume, String numeLicitant, float pretStart) {
        this.nume = nume;
        this.numeLicitant = numeLicitant;
        this.pretStart = pretStart;
        this.pretCurent = pretStart;
    }

    @Override
    public String toString() {
        return "Produs{" +
                "nume='" + nume + '\'' +
                ", numeLicitant='" + numeLicitant + '\'' +
                ", pretStart=" + pretStart +
                ", pretCurent=" + pretCurent +
                '}';
    }
}
