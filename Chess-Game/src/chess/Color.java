package chess;

public enum Color {
    WHITE,
    BLACK;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
