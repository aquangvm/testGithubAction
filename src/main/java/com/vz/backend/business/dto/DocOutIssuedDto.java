package com.vz.backend.business.dto;

import java.util.Date;
import java.util.List;

import com.vz.backend.business.domain.DocumentOut;
import com.vz.backend.business.domain.DocumentReceive;
import com.vz.backend.business.domain.OutsideReceiveDocument;
import com.vz.backend.core.config.DocumentOutHandleStatusEnum;
import com.vz.backend.core.config.DocumentStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DocOutIssuedDto extends DocOutSignDto {
	private Date dateIssued;
	private String securityName;
	private List<String> showToKnow;
	private String personHandle;
	private DocumentOutHandleStatusEnum statusHandleEnum;
	private DocumentStatusEnum docStatusEnum;
	private boolean read;
	private List<OutsideReceiveDocument> outsideReceives;
	private List<DocumentReceive> listReceive;

	public DocOutIssuedDto(DocumentOut d) {
		this.setDocOutId(d.getId());
		this.setNodeId(d.getNodeId());
		this.setCreateDate(d.getCreateDate());
		this.setNumberOrSign(d.getNumberOrSign());
		this.setNumberInBook(d.getNumberInBook()+"");
		this.setPersonEnter(d.getUserEnter().getFullName());
		this.setPreview(d.getPreview());
		this.setAttachments(d.getAttachments());
		this.setStatus(d.getStatus() != null ? d.getStatus().getName() : "");
		this.setDateIssued(d.getDateIssued());
		this.setDocTypeName(d.getDocType().getName());
		this.setSecurityName(d.getSecurity().getName());
		this.setSignerName(d.getSignerName());
		this.setOrgCreateName(d.getOrgCreateName());
		this.setDocStatusEnum(d.getStatus());
		this.setOutsideReceives(d.getOutsideReceives());
		this.setListReceive(d.getListReceive());
	}
}
