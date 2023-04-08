package com.vz.backend.business.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vz.backend.business.domain.DocumentOutAttachment;
import com.vz.backend.core.config.DocumentOutHandleStatusEnum;
import com.vz.backend.core.config.DocumentStatusEnum;
import com.vz.backend.core.dto.UserDto;

import lombok.Data;

@Data

//@NoArgsConstructor
//@AllArgsConstructor
public class DocumentProcessDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long docId;
	private Boolean important;
	private Long processId;
	private String numberOrSign;
	private CategoryDto docType;
	private Long docFieldId;
	private String preview;
	private UserDto userEnter;
	private Date createDate;
	private UserDto handleUser;
	private Date handleDate;
	private CategoryDto security;
	private String status;
	private Long nodeId;
	private List<DocumentOutAttachment> attachments;
	private List<Long> signerIds;
	private UserDto delegateUser; // Người ủy quyền
	private UserDto delegatedUser; // Người được ủy quyền
	private DocumentStatusEnum docStatus;
	private Long numberInBook;
	private boolean read;

	public DocumentProcessDto(Long docId, Boolean important, Long processId, String numberOrSign, Long docTypeId,
			String docTypeName, Long docTypeTypeId, Long docFieldId, String preview, Long enterUserId,
			String enterUserName, String enterFullName, Date createDate, Long handleId, String handleUserName,
			String handleFullName, Date handleDate, Long securityId, String securityName, Long securityTypeId,
			DocumentOutHandleStatusEnum status, Long nodeId, String listSignerIds, Long numberInBook, Boolean read) {
		this(docId, important, processId, numberOrSign, docTypeId, docTypeName, docTypeTypeId, docFieldId, preview,
				enterUserId, enterUserName, enterFullName, createDate, handleId, handleUserName, handleFullName,
				handleDate, securityId, securityName, securityTypeId, status, nodeId, listSignerIds, null, null, null,
				null, null, null, null, numberInBook, read);
	}

	public DocumentProcessDto(Long docId, Boolean important, Long processId, String numberOrSign, Long docTypeId,
			String docTypeName, Long docTypeTypeId, Long docFieldId, String preview, Long enterUserId,
			String enterUserName, String enterFullName, Date createDate, Long handleId, String handleUserName,
			String handleFullName, Date handleDate, Long securityId, String securityName, Long securityTypeId,
			DocumentOutHandleStatusEnum status, Long nodeId, String listSignerIds, Long delegateUserId,
			String delegateUserName, String delegateFullName, Long delegatedUserId, String delegatedUserName,
			String delegatedFullName, DocumentStatusEnum docStatus, Long numberInBook, Boolean read) {
		super();
		this.docId = docId;
		this.important = important;
		this.processId = processId;
		this.numberOrSign = numberOrSign;
		this.docType = new CategoryDto(docTypeId, docTypeName, docTypeTypeId);
		this.docFieldId = docFieldId;
		this.preview = preview;
		this.userEnter = new UserDto(enterUserId, enterUserName, enterFullName);
		this.createDate = createDate;
		this.handleUser = new UserDto(handleId, handleUserName, handleFullName);
		this.handleDate = handleDate;
		this.security = new CategoryDto(securityId, securityName, securityTypeId);
		this.status = status.getName();
		this.nodeId = nodeId;
		this.delegateUser = new UserDto(delegateUserId, delegateUserName, delegateFullName);
		this.delegatedUser = new UserDto(delegatedUserId, delegatedUserName, delegatedFullName);
		this.docStatus = docStatus;
		this.numberInBook = numberInBook;
		this.signerIds = new ArrayList<>();
		if (listSignerIds != null) {
			for (String tmp : listSignerIds.split(",")) {
				try {
					this.signerIds.add(Long.parseLong(tmp));
				} catch (NumberFormatException e) {
				}
			}
		}

		this.read = DocumentOutHandleStatusEnum.CHO_XU_LY.equals(status) && read != null ? read : false;
	}
}
