package be.noselus.model;

public enum AssemblyEnum {

    UN(0),
    WAL(1),
    FED(2),
    BXL(3),
    FWB(4),
    SEN(5),
    GVT_WAL(6),
    GVT_FED(7)
    ;

    private final int id;

    AssemblyEnum(final int assemblyId) {
        this.id = assemblyId;
    }

    public static AssemblyEnum getFromAssemblyId(int id) {
        switch (id) {
            case 1:
            case 6:
                return WAL;
            default:
                return FED;
        }
    }

    public int getId() {
        return id;
    }
}
