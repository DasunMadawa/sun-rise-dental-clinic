package report.dto;

public class PatientRow {
    private String patientId;
    private String name;
    private String contactNo;
    private String nic;
    private String dateOfBirth;
    private int age;
    private String gender;
    private String registeredDate;

    public PatientRow(String patientId, String name, String contactNo, String nic, String dateOfBirth, int age, String gender, String registeredDate) {
        this.patientId = patientId;
        this.name = name;
        this.contactNo = contactNo;
        this.nic = nic;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.gender = gender;
        this.registeredDate = registeredDate;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getNic() {
        return nic;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

}
