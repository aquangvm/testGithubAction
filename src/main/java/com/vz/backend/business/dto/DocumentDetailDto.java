package com.vz.backend.business.dto;

import com.vz.backend.business.domain.Attachment;
import com.vz.backend.business.domain.Documents;
import com.vz.backend.core.config.DocumentStatusEnum;

import java.util.Date;
import java.util.List;

public class DocumentDetailDto {
    private Date deadline;
    private Date dateArrival;
    private Date dateIssued;
    private Date receivedDate;
    private List<Attachment> attachments;
    private DocumentStatusEnum status;
    private String preview;
    private String numberArrivalStr;
    private String placeSend;
    private String numberOrSign;
    private Long id;
    private Boolean confidential;
    private String orgReceiveDocument;

    public void setFromDocuments(Documents document) {
        this.id = document.getId();
        this.deadline = document.getDeadline();
        this.dateArrival = document.getDateArrival();
        this.dateIssued = document.getDateIssued();
        this.receivedDate = document.getReceivedDate();
        this.attachments = document.getAttachments();
        this.status = document.getStatus();
        this.preview = document.getPreview();
        this.numberArrivalStr = document.getNumberArrivalStr();
        this.placeSend = document.getPlaceSend();
        this.numberOrSign = document.getNumberOrSign();
        this.confidential = document.getConfidential();
        this.orgReceiveDocument = document.getOrgReceiveDocument();
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDateArrival() {
        return dateArrival;
    }

    public void setDateArrival(Date dateArrival) {
        this.dateArrival = dateArrival;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public DocumentStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DocumentStatusEnum status) {
        this.status = status;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getNumberArrivalStr() {
        return numberArrivalStr;
    }

    public void setNumberArrivalStr(String numberArrivalStr) {
        this.numberArrivalStr = numberArrivalStr;
    }

    public String getPlaceSend() {
        return placeSend;
    }

    public void setPlaceSend(String placeSend) {
        this.placeSend = placeSend;
    }

    public String getNumberOrSign() {
        return numberOrSign;
    }

    public void setNumberOrSign(String numberOrSign) {
        this.numberOrSign = numberOrSign;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public String getOrgReceiveDocument() {
        return orgReceiveDocument;
    }

    public void setOrgReceiveDocument(String orgReceiveDocument) {
        this.orgReceiveDocument = orgReceiveDocument;
    }


}
