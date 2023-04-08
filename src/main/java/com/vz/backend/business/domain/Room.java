package com.vz.backend.business.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.vz.backend.core.domain.BaseModel;

import lombok.*;

@Entity
@Table(name = "ROOM", schema = "vz")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Room extends BaseModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "type")
	private Long type;
	@Column(name = "[name]")
	private String name;
	@Column(name = "is_video_call")
	private boolean isVideoCall;
	@Column(name = "is_projector")
	private boolean isProjector;
	@Column(name = "sys_org_id")
	private Long orgId;
	@Column(name = "quantity")
	private Long quantity;

}
