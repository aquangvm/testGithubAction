package com.vz.backend.business.dto.document;

import java.util.Date;
import java.util.List;

import com.vz.backend.business.domain.documentInternal.DocumentInternal;
import com.vz.backend.core.config.DocumentStatusEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DocInternalDetailDto {
	private Long docId;
	private String numberOrSign;
	private String preview;
	private Long createBy;
	private String userCreateName;
	private String orgCreateName;
	private Date createDate;
	private DocumentStatusEnum docStatus;
	private List<ApproverDto> listApprover; // Danh sách thông tin phê duyệt
	private List<ApproverDto> listReturn; // Danh sách thông tin trả lại
	private List<DocInternalReceiverDto> listReceiver; // Danh sách nơi nhận
	private List<DocInternalAttachDto> listAttachment; // Danh sách đính kèm
	private boolean canRetake;
	
	public DocInternalDetailDto(DocumentInternal doc) {
		super();
		this.docId = doc.getId();
		this.numberOrSign = doc.getNumberOrSign();
		this.preview = doc.getPreview();
		this.createBy = doc.getCreateBy();
		this.userCreateName = doc.getCreateUser().getFullName();
		this.orgCreateName = doc.getOrg().getName();
		this.createDate = doc.getCreateDate();
		this.docStatus = doc.getStatus();
	}
}
