package model;

import dao.DAOFactory;
import dao.custom.AppointmentDAO;
import dao.custom.QueryDAO;
import model.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentModel {
    private static final AppointmentDAO appointmentDAO = (AppointmentDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.APPOINTMENT);
    private static final QueryDAO queryDAO = (QueryDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.QUERY);

    private String appointmentNo;
    private PatientModel patient;
    private DentistModel dentist;
    private String bookedByStaffID;
    private TreatmentTypeModel treatment;
    private int noTooth;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private AppointmentStatus status;
    private String remarks;

    public AppointmentModel() {

    }

    public AppointmentModel(String appointmentNo, PatientModel patient, DentistModel dentist, String bookedByStaffID, TreatmentTypeModel treatment, int noTooth, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatus status, String remarks) {
        this.appointmentNo = appointmentNo;
        this.patient = patient;
        this.dentist = dentist;
        this.bookedByStaffID = bookedByStaffID;
        this.treatment = treatment;
        this.noTooth = noTooth;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.remarks = remarks;
    }

    public int getDurationMinutes() {
        return treatment.getDurationMinutes() * noTooth;
    }

    public double getTreatmentCost() {
        return treatment.isPerTooth() ? treatment.getUnitCost() * noTooth : treatment.getUnitCost();
    }

    public double getConsultationFee() {
        return dentist.getConsultationFee();
    }

    public static AppointmentModel search(String appointmentNo) throws Exception {
        return appointmentDAO.search(appointmentNo);
    }

    public static List<AppointmentModel> getAll() throws Exception {
        return appointmentDAO.getAll();
    }

    public static List<AppointmentModel> getByDentistAndDate(String dentistId, LocalDate date) throws Exception {
        return appointmentDAO.getByDentistAndDate(dentistId, date);
    }

    public static AppointmentModel getLatestForPatient(String patientId) throws Exception {
        return appointmentDAO.getLatestForPatient(patientId);
    }

    public static String generateNextId() throws Exception {
        return appointmentDAO.generateNextId();
    }

    public static int getTodayCount() throws Exception {
        return queryDAO.getTodayAppointmentCount();
    }

    public static boolean delete(String appointmentNo) throws Exception {
        return appointmentDAO.delete(appointmentNo);
    }

    public boolean save() throws Exception {
        return appointmentDAO.add(this);
    }

    public boolean update() throws Exception {
        return appointmentDAO.update(this);
    }

    public LocalTime getEndTime() {
        return appointmentTime.plusMinutes(getDurationMinutes());
    }

    public boolean overlaps(AppointmentModel other) {
        if (!status.blocksSlot() || !other.status.blocksSlot()) {
            return false;
        }
        if (!appointmentDate.equals(other.appointmentDate)) {
            return false;
        }
        return appointmentTime.isBefore(other.getEndTime()) && other.appointmentTime.isBefore(getEndTime());
    }

    public String getAppointmentNo() {
        return appointmentNo;
    }

    public void setAppointmentNo(String appointmentNo) {
        this.appointmentNo = appointmentNo;
    }

    public PatientModel getPatient() {
        return patient;
    }

    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }

    public DentistModel getDentist() {
        return dentist;
    }

    public void setDentist(DentistModel dentist) {
        this.dentist = dentist;
    }

    public String getBookedByStaffID() {
        return bookedByStaffID;
    }

    public void setBookedByStaffID(String bookedByStaffID) {
        this.bookedByStaffID = bookedByStaffID;
    }

    public TreatmentTypeModel getTreatment() {
        return treatment;
    }

    public void setTreatment(TreatmentTypeModel treatment) {
        this.treatment = treatment;
    }

    public int getNoTooth() {
        return noTooth;
    }

    public void setNoTooth(int noTooth) {
        this.noTooth = noTooth;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}
