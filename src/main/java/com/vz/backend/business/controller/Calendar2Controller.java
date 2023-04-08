package com.vz.backend.business.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vz.backend.business.repository.ICalendar2Repository;
import com.vz.backend.core.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.vz.backend.business.config.ecabinet.FileTypeEnum;
import com.vz.backend.business.domain.AttachmentComment;
import com.vz.backend.business.domain.Calendar2;
import com.vz.backend.business.domain.DocumentCalendar;
import com.vz.backend.business.dto.Calendar2ExportDto;
import com.vz.backend.business.dto.CalendarWrapperDto;
import com.vz.backend.business.dto.MeetingCalendarDto;
import com.vz.backend.business.service.Calendar2Service;
import com.vz.backend.business.service.CalendarHistoryService;
import com.vz.backend.business.service.NotificationService;
import com.vz.backend.core.common.BussinessCommon;
import com.vz.backend.core.config.CalendarActionEnum;
import com.vz.backend.core.config.CalendarStatusEnum;
import com.vz.backend.core.config.Constant;
import com.vz.backend.core.config.DocumentTypeEnum;
import com.vz.backend.core.config.Message;
import com.vz.backend.core.config.ModuleCodeEnum;
import com.vz.backend.core.config.NotificationHandleStatusEnum;
import com.vz.backend.core.controller.BaseController;
import com.vz.backend.core.domain.User;
import com.vz.backend.core.exception.RestExceptionHandler;
import com.vz.backend.core.service.FilesStorageService;
import com.vz.backend.core.service.IService;
import com.vz.backend.core.service.OrganizationService;
import com.vz.backend.core.service.RoleService;
import com.vz.backend.util.DateTimeUtils;
import com.vz.backend.util.StringUtils;

@RestController
@RequestMapping("/calendar2")
public class Calendar2Controller extends BaseController<Calendar2> {

	enum SortBy {
		UPDATEDATE("updateDate"), // Ngày cập nhật
		CREATEDATE("createDate"), // Ngày tạo
		TITLE("title"), ADDRESS("address"), DESCRIPTION("description"), START_TIME("startTime"), END_TIME("endTime"),
		STATUS("status");

		private String field;

		private SortBy(String field) {
			this.field = field;
		}

		public static String getEnum(String name) {
			for (SortBy v : values()) {
				if (v.name().equals(name)) {
					return v.field;
				}
			}
			return SortBy.CREATEDATE.field;
		}
	}

	@Override
	public IService<Calendar2> getService() {
		return null;
	}

	@Autowired
	private Calendar2Service calendar2Service;

	@Autowired
	private RoleService rService;

	@Autowired
	private OrganizationService orgService;

	@Autowired
	private NotificationService notiService;

	@Autowired
	CalendarHistoryService calendarHistoryservice;

	@Autowired
	private FilesStorageService storageService;

	@Autowired
	ICalendar2Repository calendar2Repository;

	@PostMapping(value = "/addCalendar2/{orgType}") //1-Ban /2-CucVuVien /3-Phong
	public ResponseEntity<Calendar2> addCalendar2(@PathVariable Long orgType, @RequestBody Calendar2 calendar) {
		User user = BussinessCommon.getUser();

//		if(!calendar.isMeetingCalendar()) { // lịch họp
//			if (!rService.isAllowModule(user, ModuleCodeEnum.CAL_MEETING.getName(), ModuleCodeEnum.CAL_BUSINESS.getName())) {
//				throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);
//			}
//			if(user != null && !user.getOrgModel().getName().toLowerCase().equals("phòng tổng hợp - văn phòng ban")){
//				if (orgType == 1 && !orgService.isUserOfOrgType(user, Constant.BAN)
//						|| orgType != 1 && orgService.isUserOfOrgType(user, Constant.BAN)) {
//					throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);
//				}
//			}
//
//		}

		calendar.validCalender();
		Boolean isRegisterBan=calendar.getRegisterBan();

		Calendar2 sCalendar = calendar2Service.addCalendar(calendar,orgType);
//		sCalendar.setRegisterBan(isRegisterBan);
		calendarHistoryservice.save(isRegisterBan, sCalendar, CalendarActionEnum.ADD);

		return new ResponseEntity<>(sCalendar, HttpStatus.OK);
	}

	@GetMapping(value = "/findByOrg/{orgId}")
	public ResponseEntity<CalendarWrapperDto> findByOrg(@PathVariable Long orgId,
			@RequestParam(defaultValue = "1") int statusType, // 1- register /2-approve /3-publish
			@RequestParam(defaultValue = "1") int orgType, // 1-Ban /2-CucVuVien /3-Phong
			@RequestParam(defaultValue = Constant.DEFAULT_SORT_BY) String sortBy,
			@RequestParam(defaultValue = Constant.DEFAULT_DIRECTION) Direction direction,
			@RequestParam(defaultValue = Constant.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(defaultValue = Constant.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = "0", required = false) int week,
			@RequestParam(defaultValue = "0", required = false) int year) {
		User user = BussinessCommon.getUser();

		if (!rService.isAllowModule(user, ModuleCodeEnum.CAL_MEETING.getName(), ModuleCodeEnum.CAL_BUSINESS.getName())) {
			throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);
		}

		if (!user.getOrg().equals(orgId)) {
			throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);
		}

		Date start = DateTimeUtils.getDateByWeek(week, year, DateTimeUtils.TYPE_START_DATE);
		Date end = DateTimeUtils.getDateByWeek(week, year, DateTimeUtils.TYPE_END_DATE);

		//Pageable pageable = BussinessCommon.castToPageable(page, Sort.by(direction, SortBy.getEnum(sortBy)), size);
		return new ResponseEntity<>(calendar2Service.findByListCondition(start, end, statusType, orgType,
				calendar2Service.getStatus(statusType), week, year), HttpStatus.OK);
	}

	@GetMapping("/getByMonth/{orgType}") // 1-Ban /2-CucVuVien /3-Phong
	public ResponseEntity<List<Calendar2>> getAllCalendar(@PathVariable Long orgType,
			@RequestParam(required = false, defaultValue = "0") int week,
			@RequestParam(required = false, defaultValue = "0") int year) {
		Date start = DateTimeUtils.getDateByWeek(week, year, DateTimeUtils.TYPE_START_DATE);
		Date end = DateTimeUtils.getDateByWeek(week, year, DateTimeUtils.TYPE_END_DATE);
		return new ResponseEntity<>(calendar2Service.findByMonth(orgType, start, end), HttpStatus.OK);
	}

	@GetMapping("/getByWeek/{orgType}") // 1-Ban /2-CucVuVien /3-Phong
	public ResponseEntity<CalendarWrapperDto> getAllCalendar1(@PathVariable Long orgType,
			@RequestParam(required = false, defaultValue = "0") int year,
			@RequestParam(required = false, defaultValue = "0") int week) {
		Date start = DateTimeUtils.getDateByWeek(week, year, DateTimeUtils.TYPE_START_DATE);
		Date end = DateTimeUtils.getDateByWeek(week, year, DateTimeUtils.TYPE_END_DATE);
		return new ResponseEntity<>(calendar2Service.getByWeek(orgType, start, end, week, year), HttpStatus.OK);
	}

	/**
	 * for approve calendar
	 * @param id
	 * @param status
	 * @param comment
	 * @return
	 */
	@PostMapping(value = "/updateCalendar/{id}")
	public ResponseEntity<Calendar2> updateStatusCalendar(@PathVariable Long id, @RequestParam CalendarStatusEnum status,
			@RequestParam(required = false) String comment) {
		User user = BussinessCommon.getUser();

		BussinessCommon.validLengthData(comment, "Ý kiến xử lý", 200);

		if (!rService.isAllowModule(user, ModuleCodeEnum.CAL_BUSINESS.getName(), ModuleCodeEnum.CAL_MEETING.getName() )) throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);

		Calendar2 c = calendar2Service.getCalendar2(id);
		boolean canApporve = c.isMeetingCalendar() ? calendar2Service.checkUserCanApproveMeetingCalendar(user) : calendar2Service.canApprove(user, c);
		if (!canApporve) throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);

		c.setStatus(status);
		c.setComment(comment);
		Calendar2 sCalendar = calendar2Service.save(c);

		//Xóa tất cả thông báo
		notiService.deactiveAllByDocIdAndDocType(sCalendar.getId(), DocumentTypeEnum.TAO_LICH);
		//Add thông báo cho người tạo.
		NotificationHandleStatusEnum notiEnum = NotificationHandleStatusEnum.CAL_DONG_Y_CVV;
		if(CalendarStatusEnum.APPROVE.equals(status)) {
			notiEnum = NotificationHandleStatusEnum.CAL_DONG_Y_CVV;
			if (Boolean.TRUE.equals(sCalendar.getRegisterBan())) notiEnum = NotificationHandleStatusEnum.CAL_DONG_Y_BAN;
		} else if (CalendarStatusEnum.RETURN.equals(status)) {
			notiEnum = NotificationHandleStatusEnum.CAL_TU_CHOI_CVV;
			if (Boolean.TRUE.equals(sCalendar.getRegisterBan())) notiEnum = NotificationHandleStatusEnum.CAL_TU_CHOI_BAN;
		} else if (CalendarStatusEnum.CANCEL.equals(status)) {
			notiEnum = NotificationHandleStatusEnum.CAL_HUY_DUYET_CVV;
			if (Boolean.TRUE.equals(sCalendar.getRegisterBan())) notiEnum = NotificationHandleStatusEnum.CAL_HUY_DUYET_BAN;
		}
		notiService.add(sCalendar.getCreateBy(), sCalendar.getId(), sCalendar.getTitle(), DocumentTypeEnum.TAO_LICH, notiEnum,  sCalendar.isMeetingCalendar() ? ModuleCodeEnum.CAL_MEETING : ModuleCodeEnum.CAL_BUSINESS);

		calendarHistoryservice.save(null ,sCalendar, CalendarActionEnum.APPROVE);
		return new ResponseEntity<>(sCalendar, HttpStatus.OK);
	}

	@PostMapping(value = "/updateCalendarBody/{id}")
	public ResponseEntity<Calendar2> updateBodyCalendar(@PathVariable Long id, @RequestBody Calendar2 news) {
		User user = BussinessCommon.getUser();
		boolean isChangeStatus = true;

		Calendar2 old = calendar2Service.findByIdAndClientIdAndActive(id, true);
		if (old == null || news == null
				// CR: 14/1/2022 Cho phép chỉnh sửa lịch quá khứ
//				|| old.getStartTime().getTime() <= new Date().getTime()
				) {
			throw new RestExceptionHandler(Message.CALENDAR_INVALD);
		}

		if (!rService.isAllowModule(user, ModuleCodeEnum.CAL_BUSINESS.getName(), ModuleCodeEnum.CAL_MEETING.getName() ))
			throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);

		boolean canApprove = old.isMeetingCalendar() ? calendar2Service.checkUserCanApproveMeetingCalendar(user)
				: calendar2Service.checkUserCanApprove(user, old);

		if (!canApprove && !user.getId().equals(old.getCreateBy())) {
			throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);
		}

		news.validCalender();
		List<DocumentCalendar> docInList = news.getDInList();
		List<DocumentCalendar> docOutList = news.getDOutList();
		List<DocumentCalendar> taskList = news.getTaskList();
		calendar2Service.checkFullNameNotFound(news.getParticipants());
		calendar2Service.saveIngredient(news, true);
		if (old.isMeetingCalendar()) {
			old.setCalendater(true, news);
			Calendar2 sCalendar1 = calendar2Service.saveDocRelated(old, docInList, docOutList, taskList);
					//calendar2Service.save(old);

			calendar2Service.addNotification(sCalendar1);
			calendarHistoryservice.save(null, sCalendar1, CalendarActionEnum.UPDATE);
			return new ResponseEntity<>(sCalendar1, HttpStatus.OK);
		}

		if (CalendarStatusEnum.APPROVE.equals(old.getStatus())) {
			if (canApprove) isChangeStatus = false;
			if (Boolean.TRUE.equals(news.getRegisterBan())) {
				news.setId(null);
				news.setCalendater(true, news);
				Calendar2 sCalendar = calendar2Service.save(news);
				calendar2Service.saveDocRelated(sCalendar, docInList, docOutList, taskList);
				// Add thông báo
				calendar2Service.addNotiRequestApproveCalendar(sCalendar);
				calendarHistoryservice.save(null, sCalendar, CalendarActionEnum.REGISTER_TOP_LEVEL);

				return new ResponseEntity<>(sCalendar, HttpStatus.OK);
			}
		}

		old.setCalendater(isChangeStatus, news);
		if (Boolean.TRUE.equals(old.getRegisterBan())) {
			old.setRegisterBan(false);
			Calendar2 calendarForBan = new Calendar2();
			calendarForBan.setCalendater(false, old);
			calendarForBan.setMeetingCalendar(false);
			calendarForBan.setOrgId(orgService.getRootOrgId(user.getOrg()));
			calendarForBan = calendar2Repository.save(calendarForBan);
			calendar2Service.addNotiRequestApproveCalendar(calendarForBan);
		}
		Calendar2 sCalendar = calendar2Service.saveDocRelated(old, docInList, docOutList, taskList);
//		Calendar2 sCalendar = calendar2Service.save(old);

		calendar2Service.addNotification(sCalendar);
		calendarHistoryservice.save(null, sCalendar, CalendarActionEnum.UPDATE);
		return new ResponseEntity<>(sCalendar, HttpStatus.OK);
	}

	@GetMapping("/getCalendar/{calenderId}")
	public ResponseEntity<Calendar2> getCalendar(@PathVariable Long calenderId) {
		return new ResponseEntity<>(calendar2Service.getCalendar2(calenderId), HttpStatus.OK);
	}

	@GetMapping("/getCalendar2toDate")
	public ResponseEntity<?> getCalendar2toDate(@RequestParam String date) {
		return new ResponseEntity<>(calendar2Service.getCalendar2toDate(date), HttpStatus.OK);
	}

	@GetMapping("/getMeetingCalendar/{meetingId}")
	public ResponseEntity<Calendar2> getMeetingCalendar(@PathVariable Long meetingId) {
		return new ResponseEntity<>(calendar2Service.getByMeetingId(meetingId), HttpStatus.OK);
	}

	@PostMapping("/export")
	public ResponseEntity<StreamingResponseBody> export(@RequestBody Calendar2ExportDto dto) {
		if (dto.getOrgType() == null) {
			throw new RestExceptionHandler("orgType must be number");
		}
		StreamingResponseBody stream = outputStream -> {
			try {
				calendar2Service.export(outputStream, dto);
			} finally {
				StreamUtils.closeOutputStream(outputStream);
			}
		};
		return new ResponseEntity<>(stream, HttpStatus.OK);
	}

	@PostMapping("/del/{calendarId}")
	public ResponseEntity<Boolean> del(@PathVariable Long calendarId) {
		return new ResponseEntity<>(calendar2Service.del(calendarId), HttpStatus.OK);
	}

	@GetMapping(value = "/findMeetingCalendar")
	public ResponseEntity<CalendarWrapperDto> findMeetingCalendar(
			@RequestParam(defaultValue = "1") int statusType, // 1- register /2-approve /3-publish
			@RequestParam(defaultValue = Constant.DEFAULT_SORT_BY) String sortBy,
			@RequestParam(defaultValue = Constant.DEFAULT_DIRECTION) Direction direction,
			@RequestParam(defaultValue = Constant.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(defaultValue = Constant.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = "0", required = false) int week,
			@RequestParam(defaultValue = "0", required = false) int year,
			@RequestParam(required = false) Long roomId,
			@RequestParam(required = false) Long userId) {
		User user = BussinessCommon.getUser();

		if (!rService.isAllowModule(user, ModuleCodeEnum.CAL_MEETING.getName())) {
			throw new RestExceptionHandler(Message.CALENDER_NOT_ALLOW);
		}

		Date start = DateTimeUtils.getDateByWeek(week, year, DateTimeUtils.TYPE_START_DATE);
		Date end = DateTimeUtils.getDateByWeek(week, year, DateTimeUtils.TYPE_END_DATE);

		return new ResponseEntity<>(calendar2Service.findMeetingCalendar(start, end, week, year, roomId, userId), HttpStatus.OK);
	}

	@PostMapping("/meeting/update/{id}")
	public ResponseEntity<?> updateMeetingCalendar(@PathVariable Long id, @RequestBody MeetingCalendarDto dto) {
		return new ResponseEntity<>(calendar2Service.updateMeetingCalendar(id, dto), HttpStatus.OK);
	}

	@PostMapping("/meeting/attachment/{meetingId}")
	public ResponseEntity<?> uploadMeetingAttachment(@RequestParam MultipartFile[] files, @PathVariable Long meetingId) {
		return new ResponseEntity<>(calendar2Service.addListAttachment(files, meetingId), HttpStatus.OK);
	}

	@PostMapping(value = "/meeting/attachment/deleteBy/{id}/{meetingId}")
	public ResponseEntity<?> deleteAttachmentById(@PathVariable Long id, @PathVariable Long meetingId) {
		return new ResponseEntity<>(calendar2Service.deleteMeetingCalendarAttachmentById(id, meetingId),HttpStatus.OK);
	}

	/**
	 * Tải file đính kèm
	 * @param fileName
	 * @return
	 */
	@GetMapping("/download/{fileName:.+}")
	public ResponseEntity<Resource> download(@PathVariable String fileName) {
		String name = StringUtils.decodeFromUrl(fileName);
		Resource file = storageService.load(name);
		if (file == null) {
			throw new RestExceptionHandler(Message.NOT_FOUND_FILE);
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PostMapping(value = "/meeting/deleteBy/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Long id) {
		return new ResponseEntity<>(calendar2Service.deleteMeetingCalendarById(id),HttpStatus.OK);
	}

	@GetMapping(value = "/ban/create")
	public ResponseEntity<?> deleteById() {
		Map<String, Boolean> map = new HashMap<>();
		map.put("isPermission", calendar2Service.createCalendarBan());
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
}
