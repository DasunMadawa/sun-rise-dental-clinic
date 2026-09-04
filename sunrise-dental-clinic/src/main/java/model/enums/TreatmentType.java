package model.enums;

public enum TreatmentType {
    CHECKING("CHECKING", 0.00, false, 15),
    SCALING("SCALING", 3500.00, false, 30),
    FILLING("FILLING", 6000.00, true, 45),
    EXTRACTION("EXTRACTION", 5000.00, true, 30),
    ROOT_CANAL("ROOT_CANAL", 15000.00, true, 60),
    CROWN("CROWN", 20000.00, true, 60),
    DENTURE("DENTURE", 25000.00, false, 90),
    WHITENING("WHITENING", 12000.00, false, 45),
    BRACES_REVIEW("BRACES_REVIEW", 2500.00, false, 20);

    private final String code;
    private final double unitCost;
    private final boolean isPerTooth;
    private final int durationMinutes;

    TreatmentType(String code, double unitCost, boolean isPerTooth, int durationMinutes) {
        this.code = code;
        this.unitCost = unitCost;
        this.isPerTooth = isPerTooth;
        this.durationMinutes = durationMinutes;
    }

    public String getCode() {
        return code;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public boolean isPerTooth() {
        return isPerTooth;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public static TreatmentType fromCode(String code) {
        for (TreatmentType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return CHECKING;
    }

}
