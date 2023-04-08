package com.vz.backend.business.dto;

import java.util.List;

import com.vz.backend.business.domain.AttachmentCalendar;
import com.vz.backend.business.domain.Calendar2;
import com.vz.backend.core.dto.OrgGroupDto;
import com.vz.backend.util.DateTimeUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Calendar2DetailDto {
	private Long id;
	private String title;
	private String address;
	private String status;
	private String participants;
	private String participantsGuest;
	private List<OrgGroupDto> participantsOrg;
	private List<OrgGroupDto> participantsGroup;
	private String time;
	private String description;
	private String createUserName;
	private String note;
	private boolean showApproveBt;
	private boolean showRejectBt;
	private boolean showEditBt;
	private boolean showCancelBt;
	private boolean showDelBt;
	private boolean isCabinet;
	private String scheduleFileName;
	private Boolean isScheduleAFile;
	private List<AttachmentCalendar> attachments;
	private Long createBy;
	private boolean isShowAttachments;
	
	public void set(Calendar2 c) {
		this.id = c.getId();
		this.title = c.getTitle();
		this.address = c.getAddress();
		this.status = c.getStatusName() == null ? null : c.getStatusName();
		this.participants = c.getParticipants();
		this.participantsGuest = c.getParticipantsGuest();
		this.participantsOrg = c.getParticipantsOrg();
		this.participantsGroup = c.getParticipantsGroup();
		this.time = DateTimeUtils.getHourMinutes(c.getStartTime());
		this.description = c.getDescription();
		this.createUserName = c.getCreateUserName();
		this.showApproveBt = c.isShowApproveBt();
		this.showRejectBt = c.isShowRejectBt();
		this.showCancelBt = c.isShowCancelBt();
		this.showDelBt = c.isShowDelBt();
		this.showEditBt = c.isShowEditBt();
		this.attachments = c.getAttachments();
		this.note = c.getNote();
		this.isCabinet = c.getIsCabinet() == null ? false : c.getIsCabinet();
		this.scheduleFileName = c.getScheduleFileName() == null ? null : c.getScheduleFileName();
		this.isScheduleAFile = c.getIsScheduleAFile() == null ? null : c.getIsScheduleAFile();
		this.createBy = c.getCreateBy();
		this.isShowAttachments = c.getIsShowAttachments() != null ? c.getIsShowAttachments() : false;
	}
}
