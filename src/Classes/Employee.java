package Classes;

public class Employee {
    private String empSn;
    private String empName;
    private String empId;
    private String empTel;
    private String empBank;
    private String empBranch;
    private String empType;
    private String empSecurePass;
    private String empPassSalt;
    private String empStatus;
    private String empPhoto;


    public Employee(Employee employee) {

        this.empSn = employee.empSn;
        this.empName = employee.empName;
        this.empId = employee.empId;
        this.empTel = employee.empTel;
        this.empBank = employee.empBank;
        this.empBranch = employee.empBranch;
        this.empType = employee.empType;
        this.empSecurePass = employee.empSecurePass;
        this.empPassSalt = employee.empPassSalt;
        this.empStatus = employee.empStatus;
        this.empPhoto = employee.empPhoto;
    }

    public Employee(String empId, String empSn, String empName, String empTel, String empBank, String empBranch, String empType, String empPhoto){

        this.empSn = empSn;
        this.empName = empName;
        this.empId = empId;
        this.empTel = empTel;
        this.empBank = empBank;
        this.empBranch = empBranch;
        this.empType = empType;
        this.empSecurePass = "";
        this.empPassSalt = "";
        this.empStatus = "OUT";
        this.empPhoto = empPhoto;
    }

    public Employee(){

        this.empSn = "";
        this.empName = "";
        this.empId = "";
        this.empTel = "";
        this.empBank = "";
        this.empBranch = "";
        this.empType = "";
        this.empSecurePass = "";
        this.empPassSalt = "";
        this.empStatus = "OUT";
    }

    public String getEmpSn() {
        return empSn;
    }

    public void setEmpSn(String empSn) {
        this.empSn = empSn;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpTel() {
        return empTel;
    }

    public void setEmpTel(String empTel) {
        this.empTel = empTel;
    }

    public String getEmpBank() {
        return empBank;
    }

    public void setEmpBank(String empBank) {
        this.empBank = empBank;
    }

    public String getEmpBranch() {
        return empBranch;
    }

    public void setEmpBranch(String empBranch) {
        this.empBranch = empBranch;
    }

    public String getEmpType() {
        return empType;
    }

    public void setEmpType(String empType) {
        this.empType = empType;
    }

    public String getEmpPass() {
        return empSecurePass;
    }

    public void setEmpPass(String empSecurePass) {
        this.empSecurePass = empSecurePass;
    }

    public String getEmpPassSalt() {
        return empPassSalt;
    }

    public void setEmpPassSalt(String empPassSalt) {
        this.empPassSalt = empPassSalt;
    }

    public String getEmpStatus() {
        return empStatus;
    }

    public void setEmpStatus(String empStatus) {
        this.empStatus = empStatus;
    }

    public String getEmpPhoto() {
        return empPhoto;
    }

    public void setEmpPhoto(String empPhoto) {
        this.empPhoto = empPhoto;
    }
}