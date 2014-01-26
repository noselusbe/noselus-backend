package be.noselus.model;

public enum AssemblyEnum {

    UN,
    WAL,
    FED;

    public static AssemblyEnum getFromAssemblyId(int id) {
        switch (id) {
            case 1:
            case 6:
                return WAL;
            default:
                return FED;
        }
    }
}
