package model;

import dao.DAOFactory;
import dao.custom.PatientDAO;
import dao.custom.QueryDAO;
import model.enums.Gender;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class PatientModel {
    private static final PatientDAO patientDAO = (PatientDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.PATIENT);
    private static final QueryDAO queryDAO = (QueryDAO) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOTypes.QUERY);

    private String patientID;
    private String patientName;
    private String address;
    private String contactNo;
    private String nic;
    private LocalDate dateOfBirth;
    private Gender gender;
    private LocalDate registeredDate;
    private String email;

    public PatientModel() {

    }

    public PatientModel(String patientID, String patientName, String address, String contactNo, String nic, LocalDate dateOfBirth, Gender gender, LocalDate registeredDate, String email) {
        this.patientID = patientID;
        this.patientName = patientName;
        this.address = address;
        this.contactNo = contactNo;
        this.nic = nic;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.registeredDate = registeredDate;
        this.email = email;
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public static PatientModel search(String id) throws Exception {
        return patientDAO.search(id);
    }

    public static PatientModel searchByNic(String nic) throws Exception {
        return patientDAO.searchByNic(nic);
    }

    public static List<PatientModel> getAll() throws Exception {
        return patientDAO.getAll();
    }

    public static String generateNextId() throws Exception {
        return patientDAO.generateNextId();
    }

    public static int getTotalCount() throws Exception {
        return queryDAO.getTotalPatients();
    }

    public static boolean delete(String id) throws Exception {
        return patientDAO.delete(id);
    }

    public boolean save() throws Exception {
        return patientDAO.add(this);
    }

    public boolean update() throws Exception {
        return patientDAO.update(this);
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
