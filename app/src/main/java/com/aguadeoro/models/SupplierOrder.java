package com.aguadeoro.models;

public class SupplierOrder {
    int id = 0;
    int OrderComponentID = 0;
    String instruction = "";
    String deadline = "";
    String recipient = "";
    String status = "";
    String createdDate = "";
    String step = "";
    String instructionCode = "";
    String remark = "";
    String supplierOrderNumber="";
    String approval = "";
    String shippedDate = "";
    String shippedBy = "";

    public SupplierOrder(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderComponentID() {
        return OrderComponentID;
    }

    public void setOrderComponentID(int orderComponentID) {
        OrderComponentID = orderComponentID;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getInstructionCode() {
        return instructionCode;
    }

    public void setInstructionCode(String instructionCode) {
        this.instructionCode = instructionCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSupplierOrderNumber() {
        return supplierOrderNumber;
    }

    public void setSupplierOrderNumber(String supplierOrderNumber) {
        this.supplierOrderNumber = supplierOrderNumber;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getShippedBy() {
        return shippedBy;
    }

    public void setShippedBy(String shippedBy) {
        this.shippedBy = shippedBy;
    }

    @Override
    public String toString() {
        return "SupplierOrder{" +
                "id=" + id +
                ", OrderComponentID=" + OrderComponentID +
                ", instruction='" + instruction + '\'' +
                ", deadline='" + deadline + '\'' +
                ", recipient='" + recipient + '\'' +
                ", status='" + status + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", step='" + step + '\'' +
                ", instructionCode='" + instructionCode + '\'' +
                ", remark='" + remark + '\'' +
                ", supplierOrderNumber='" + supplierOrderNumber + '\'' +
                ", approval='" + approval + '\'' +
                ", shippedDate='" + shippedDate + '\'' +
                ", shippedBy='" + shippedBy + '\'' +
                '}';
    }
}
