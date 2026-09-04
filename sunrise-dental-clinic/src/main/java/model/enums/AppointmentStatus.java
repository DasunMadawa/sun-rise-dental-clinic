package model.enums;

public enum AppointmentStatus {
    SCHEDULED, COMPLETED, CANCELLED, NO_SHOW;

    public boolean blocksSlot() {
        return this == SCHEDULED;
    }

}
