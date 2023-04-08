package com.vz.backend.business.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vz.backend.business.domain.DocumentInProcess;
import com.vz.backend.business.domain.Documents;
import com.vz.backend.business.dto.document.ButtonDto;
import com.vz.backend.business.service.DocumentInProcessService;
import com.vz.backend.core.common.BussinessCommon;
import com.vz.backend.core.config.Constant;
import com.vz.backend.core.config.DocumentInHandleStatusEnum;
import com.vz.backend.core.config.HandleTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DocumentDto {
	private Documents doc;
	private String orgExe;
	
	@JsonIgnore
	private Date date;
	@JsonIgnore
	private Long docId;
	private String nextHandle;
	private String delegater;
	private boolean isDelegatedDoc;

	private boolean isDelegatingdDoc;
	private Integer progress;
	private String comment;
	private Long processId;
	private Boolean important;
	private DocumentInHandleStatusEnum pStatus;
	private Date deadline;
	private Long processNode;
	private int processStep;
	private Long bpmnId;
	private String bpmnName;
	private Long node;
	@JsonIgnore
	private HandleTypeEnum handleType;
	
	private Boolean read;
	private ButtonDto button;
	
	@JsonIgnore
	private DocumentInProcess p;
	
	private Long processNextNode;

	public String getPStatusName() {
		if (this.pStatus == null) {
			return "";
		}
		return this.pStatus.getName();
	}

	public DocumentDto(Documents doc) {
		this.doc = doc;
		this.docId = doc.getId();
	}

	public DocumentDto(Documents doc, String orgExe) {
		this(doc);
		this.orgExe = orgExe;
	}

	public DocumentDto(Documents doc, String orgExe, Boolean important) {
		this(doc, orgExe);
		this.important = important;
	}

	public DocumentDto(Documents doc, Long processId, Boolean important, String orgExe, Date date, Integer progress,
			String comment) {
		this(doc, orgExe, important);
		this.processId = processId;
		this.date = date;
		this.progress = progress;
		this.comment = comment;
	}

	public DocumentDto(Documents doc, Long processId, Boolean important, String orgExe, Date date, Integer progress,
			String comment, DocumentInHandleStatusEnum pStatus, Date deadline, Long processNode, Boolean read, int step) {
		this(doc, processId, important, orgExe, date, progress, comment, pStatus);
		this.deadline = deadline;
		this.processNode = processNode;
		this.read = read;
		this.processStep = step;
	}
	
	public DocumentDto(Documents doc, Long processId, Boolean important, String orgExe, Date date, Integer progress,
			String comment, DocumentInHandleStatusEnum pStatus, Date deadline, Long processNode, Boolean read, int step, HandleTypeEnum handleType) {
		this(doc, processId, important, orgExe, date, progress, comment, pStatus, deadline, processNode, read, step);
		this.handleType = handleType;
	}

	public DocumentDto(Documents doc, Long processId, Boolean important, String orgExe, Date date, Integer progress,
			String comment, DocumentInHandleStatusEnum pStatus) {
		this(doc, processId, important, orgExe, date, progress, comment);
		this.pStatus = pStatus;
	}

	public DocumentDto(Documents doc, Long processId, Boolean important, Date date, String delegater, Integer progress,
			String comment, Date deadline) {
		this.doc = doc;
		this.processId = processId;
		this.important = important;
		this.delegater = delegater;
		this.date = date;
		this.progress = progress;
		this.comment = comment;
		this.deadline = deadline;
	}

	public DocumentDto(Documents doc, Long processId, Boolean important, Date date, String delegater, Integer progress,
			String comment, DocumentInHandleStatusEnum pStatus, Date deadline) {
//		this(doc, processId, important, date, delegater, progress, comment, deadline);
		this.doc =doc;
		this.processId =processId;
		this.important =important;
		this.date =date;
		this.delegater =delegater;
		this.progress =progress;
		this.comment =comment;
		this.deadline =deadline;
		this.pStatus = pStatus;
	}
	
	public boolean getRead() {
		return this.read == null ? false : this.read;
	}
	
	public DocumentDto(Documents doc, DocumentInProcess p, Boolean read, Boolean important) {
		this.doc = doc;
		this.processId = p.getId();
		this.important = important;
		this.orgExe = p.getOrgName();
		this.progress = p.getProgress();
		this.comment = p.getComment();
		this.deadline = p.getDeadline();
		this.pStatus = p.getHandleStatus();
		this.processNode = p.getNode();
		this.processStep = p.getStep();
		this.read = read;
		this.date = p.getUpdateDate();
		this.setDelegatedDoc(p.getDelegaterId() != null && p.getDelegaterId().equals(BussinessCommon.getUserId()) );
		this.setDelegatingdDoc(p.getDelegaterId() != null && !p.getDelegaterId().equals(BussinessCommon.getUserId()) );
		this.p = p;
		this.processNextNode = p.getNextNode();
		this.node =  p.getNode();
//		if (Constant.START_STEP.equals(p.getStep()) && Constant.START_NODE.equals(this.processNode)) {
//			this.processNode = this.processNextNode;
//		}
	}
	
	public String getHandleTypeStr() {
		return this.handleType != null ? this.handleType.name() : "";
	}
	
	/**
	 * Hoàn thanh đối với văn bản nội bộ chuyển luồng
	 */
	public boolean getCanDoneInternal() {
		return HandleTypeEnum.MAIN.equals(this.handleType) && Boolean.TRUE.equals(doc.getMergedLines())
				&& DocumentInProcessService.isStatus(this.pStatus, DocumentInProcessService.HANDLE_STATUS_NOT_YET);
	}
}
