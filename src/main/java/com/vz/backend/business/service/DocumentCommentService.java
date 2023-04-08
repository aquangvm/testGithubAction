package com.vz.backend.business.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.vz.backend.business.domain.Documents;
import com.vz.backend.business.repository.IDocumentRepository;
import com.vz.backend.core.config.DocumentStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vz.backend.business.config.DocumentCommentTypeEnum;
import com.vz.backend.business.domain.AttachmentComment;
import com.vz.backend.business.domain.DocumentComment;
import com.vz.backend.business.domain.DocumentInProcess;
import com.vz.backend.business.dto.document.DocInCommentDto;
import com.vz.backend.business.repository.IAttachmentCommentRepository;
import com.vz.backend.business.repository.IDocumentCommentRepository;
import com.vz.backend.core.common.BussinessCommon;
import com.vz.backend.core.config.AuthorityEnum;
import com.vz.backend.core.config.HandleTypeEnum;
import com.vz.backend.core.config.Message;
import com.vz.backend.core.domain.Category;
import com.vz.backend.core.domain.User;
import com.vz.backend.core.exception.RestExceptionHandler;
import com.vz.backend.core.repository.ICategoryRepository;
import com.vz.backend.core.repository.IRepository;
import com.vz.backend.core.service.AuthorityUserService;
import com.vz.backend.core.service.BaseService;
import com.vz.backend.core.service.CategoryService;
import com.vz.backend.core.service.OrganizationService;
import com.vz.backend.core.service.UserService;
import com.vz.backend.util.StringUtils;

@Service
public class DocumentCommentService extends BaseService<DocumentComment> {
	@Value("${configs.load-comment-by-org-and-position: false}")
	private boolean loadComment;
	@Value("${configs.load-comment-by-org-and-position-h05: false}")
	private boolean loadCommentH05;
	@Autowired
	public IDocumentCommentRepository commentRepository;

	@Autowired
	public UserService userService;

	@Autowired
	private IAttachmentCommentRepository attachRepository;

	@Autowired
	public ICategoryRepository categoryRepository;
	
	@Autowired
	public CategoryService catService;
	
	@Autowired
	private OrganizationService orgService;
	
	@Autowired
	private DocumentInProcessService docInProcessService;
	
	@Autowired
	private DocumentInManipulationService docInManipulationService;
	
	@Autowired
	private AuthorityUserService authorUserService;

	@Autowired
	private IDocumentRepository documentRepository;

	@Override
	public IRepository<DocumentComment> getRepository() {
		return commentRepository;
	}

	public void validCmt(DocumentComment doc) {

		if (doc == null || doc.getDocId() == null || StringUtils.isNullOrEmpty(doc.getComment())) {
			throw new RestExceptionHandler(Message.NO_INPUT_DATA);
		}
		BussinessCommon.validLengthData(doc.getComment(), "Ý kiến xử lý", 2000);
	}

	public DocumentComment saveCmt(DocumentComment doc) {
		User user = BussinessCommon.getUser();
		Category category = categoryRepository.findByClientIdAndId(user.getClientId(), user.getPosition());
		doc.setUserPosition(category.getName());
		doc.setUserFullName(user.getFullName());
		commentRepository.save(doc);
		return doc;
	}

	public List<DocInCommentDto> getListByDocId(Long docId) {
		User user = BussinessCommon.getUser();
		Documents documentParent = documentRepository.findByClientIdAndId(BussinessCommon.getClientId(), docId);
		List<Long> listId = new ArrayList<>();
		listId.add(docId);
		if (documentParent != null && documentParent.getParentId() != null) {
			docId = documentParent.getParentId();
			listId.add(documentParent.getParentId());
		}
		if (loadCommentH05) {
			List<Long> subOrgs = null;
			if (catService.isLeadership(user.getPosition()))
				subOrgs = orgService.findParentAndSubAndSameOrgByCurrOrg(user.getOrg(), true);
			return commentRepository.findByCurrOrgAndSubOrgInAndDocIdAndClientIdAndActive(user.getOrg(), subOrgs, user.getClientId(), docId, true);
		}
		if (loadComment) {
			// Version 1
//			List<Long> subOrgs = null;
//			Set<HandleTypeEnum> setHandleType = docInProcessService.getHandleTypeByDocId(user.getId(), docId);
//			if (setHandleType == null) return new ArrayList<DocInCommentDto>();
//			if (setHandleType.contains(HandleTypeEnum.MAIN) || setHandleType.contains(HandleTypeEnum.DIRECTION)) {
//				subOrgs = orgService.findParentAndSubAndSameOrgByCurrOrg(user.getOrg(), true);
//				return commentRepository.findByCurrOrgAndSubOrgInAndDocIdAndClientIdAndActive(user.getOrg(), subOrgs, user.getClientId(), docId, true);
//			}
//			if (setHandleType.contains(HandleTypeEnum.SUPPORT)) {
//				if (catService.isLeadership(user.getPosition())) {
//					subOrgs = orgService.findParentAndSubAndSameOrgByCurrOrg(user.getOrg(), true);
//				} else {
//					subOrgs = Arrays.asList(user.getOrgModel().getParentId());
//				}
//				return commentRepository.findByCurrOrgAndSubOrgInAndDocIdAndClientIdAndActive(user.getOrg(), subOrgs, user.getClientId(), docId, true);
//			}
//			if (setHandleType.contains(HandleTypeEnum.SHOW)) {
//				subOrgs = Arrays.asList(user.getOrgModel().getParentId());
//				return commentRepository.findByCurrOrgAndSubOrgInAndDocIdAndClientIdAndActive(user.getOrg(), subOrgs, user.getClientId(), docId, true);
//			}
			//Tab Ý kiến
//			if (docInManipulationService.getHandleStatusByDocId(user.getId(), docId)) {
//				subOrgs = Arrays.asList(user.getOrgModel().getParentId());
//				return commentRepository.findByCurrOrgAndSubOrgInAndDocIdAndClientIdAndActive(user.getOrg(), subOrgs, user.getClientId(), docId, true);
//			}
//			// Version 2
//			Set<Long> listUserId = new HashSet<Long>();
//			List<DocumentInProcess> listProcess = docInProcessService.findProcessByToUserAndDocId(user.getId(), docId);
//			for (DocumentInProcess process : listProcess) {
//				listUserId.add(process.getFrUser()); // Người chuyển xử lý
//				listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStepAndOrgId(process.getFrUser(), docId, process.getStep(), user.getOrg())); // Cùng org
//				// Xử lý chính
//				if (HandleTypeEnum.MAIN.equals(process.getHandleType()) || HandleTypeEnum.DIRECTION.equals(process.getHandleType())) {
//					listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStep(process.getFrUser(), docId, process.getStep())); // Step hiện tại
//					if (authorUserService.isUserHasAuthority(user.getId(), null, AuthorityEnum.LEADERSHIP)) // LĐC
//						listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStepAndHandleTypeIn(user.getId(), docId, process.getStep() + 1, Arrays.asList(HandleTypeEnum.MAIN))); // Người XLC step sau
//					else { // Không phải LĐC
//						listUserId.addAll(docInProcessService.findToUserByFromUserAndDocId(user.getId(), docId)); // Những người được nó CXL
//					}
//				}
//				// Phối hợp
//				if (HandleTypeEnum.SUPPORT.equals(process.getHandleType())) {
//					if (catService.isLeadership(user.getPosition())) { // Có chức vụ lãnh đạo
//						listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStepAndHandleTypeIn(process.getFrUser(), docId, process.getStep(), Arrays.asList(HandleTypeEnum.MAIN))); // Người XLC step hiện tại
//						listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStepAndHandleTypeInAndLeadership(process.getFrUser(), docId, process.getStep(), Arrays.asList(HandleTypeEnum.SUPPORT), true)); // Lãnh đạo là XLPH step hiện tại
//						List<Long> mainUser = docInProcessService.findToUserByFromUserAndDocIdAndStepAndHandleTypeIn(process.getFrUser(), docId, process.getStep(), Arrays.asList(HandleTypeEnum.MAIN)); //Tìm người XLC trong step hiện tại
//						listUserId.addAll(docInProcessService.findToUserByFromUserInAndDocIdAndStepGreaterAndLeadership(mainUser, docId, process.getStep(), true)); // Lấy thêm lãnh đạo ở step sau, được người XLC step hiện tại CXL.
//					} else { // Không có chức vụ lãnh đạo
//					}
//				}
//				// Nhận để biết
//				if (HandleTypeEnum.SHOW.equals(process.getHandleType())) {
//				}
//			}
//			// Add danh sách lãnh đạo đơn vị cấp trên (LĐ Cục)
//			Long parentOrg = user.getOrgModel().getParentId();
//			if (parentOrg != null)
//				listUserId.addAll(userService.findAllLeadershipInParentOrg(parentOrg));
//			//Add danh sách người xin ý kiến
//			listUserId.addAll(docInManipulationService.findFromUserByToUserAndDocId(user.getId(), docId));
//			listUserId.addAll(docInManipulationService.findToUserByFromUserAndDocId(user.getId(), docId));
//			List<DocInCommentDto> result = new ArrayList<DocInCommentDto>();
//			if (!listProcess.isEmpty()) result = commentRepository.findByListUserIdAndDocIdAndClientIdAndActive(listUserId, docId, user.getClientId(), true);
//			System.out.println(result.size());
//			return result;
			// Version 3
			Set<Long> listUserId = new HashSet<Long>();
			List<DocumentInProcess> listProcess = docInProcessService.findProcessByToUserAndDocId(user.getId(), docId);
			for (DocumentInProcess process : listProcess) {
				listUserId.add(process.getFrUser()); // Người chuyển xử lý
				listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndOrgId(process.getFrUser(), docId, user.getOrg())); // Cùng org
				// Xử lý chính
				if (HandleTypeEnum.MAIN.equals(process.getHandleType()) || HandleTypeEnum.DIRECTION.equals(process.getHandleType())) {
					listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStep(process.getFrUser(), docId, process.getStep())); // Step hiện tại
					if (authorUserService.isUserHasAuthority(user.getId(), null, AuthorityEnum.LEADERSHIP)) // LĐC
						listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStepAndHandleTypeIn(user.getId(), docId, process.getStep() + 1, Arrays.asList(HandleTypeEnum.MAIN))); // Người XLC step sau
					else { // Không phải LĐC
						listUserId.addAll(docInProcessService.findToUserByFromUserAndDocId(user.getId(), docId)); // Những người được nó CXL
						listUserId.addAll(docInProcessService.findToUserByDocIdAndAuthority(docId, true)); // Tất cả những người có chức vụ lãnh đạo
					}
				}
				// Phối hợp
				if (HandleTypeEnum.SUPPORT.equals(process.getHandleType())) {
					if (catService.isLeadership(user.getPosition())) { // Có chức vụ lãnh đạo
						boolean currStep = false;
						List<User> mainUser = docInProcessService.findToUserObjByFromUserAndDocIdAndStepAndHandleTypeIn(process.getFrUser(), docId, process.getStep(), Arrays.asList(HandleTypeEnum.MAIN));
						for (User u : mainUser) {
							listUserId.add(u.getId());
							if (u.getOrg().equals(user.getOrg())) {
								listUserId.addAll(docInProcessService.findToUserByFromUserAndDocId(u.getId(), docId)); // Những người được mainUser CXL
								if (!currStep) 
									listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStep(process.getFrUser(), docId, process.getStep())); // Step hiện tại
								currStep = true;
							}
						}
						if (!currStep) 
							listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStepAndHandleTypeInAndLeadership(process.getFrUser(), docId, process.getStep(), Arrays.asList(HandleTypeEnum.SUPPORT), true)); // Lãnh đạo là XLPH step hiện tại
						listUserId.addAll(docInProcessService.findToUserByDocIdAndAuthority(docId, true)); // Tất cả những người có chức vụ lãnh đạo
					} else { // Không có chức vụ lãnh đạo
					}
				}
				// Nhận để biết
				if (HandleTypeEnum.SHOW.equals(process.getHandleType())) {
					if (catService.isLeadership(user.getPosition())) { // Có chức vụ lãnh đạo
					boolean currStep = false;
					List<User> mainUser = docInProcessService.findToUserObjByFromUserAndDocIdAndStepAndHandleTypeIn(process.getFrUser(), docId, process.getStep(), Arrays.asList(HandleTypeEnum.MAIN));
					for (User u : mainUser) {
						listUserId.add(u.getId());
						if (u.getOrg().equals(user.getOrg())) {
							listUserId.addAll(docInProcessService.findToUserByFromUserAndDocId(u.getId(), docId)); // Những người được mainUser CXL
							if (!currStep) {
								listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStep(process.getFrUser(), docId, process.getStep())); // Step hiện tại
								listUserId.addAll(docInProcessService.findToUserByDocIdAndAuthority(docId, true)); // Tất cả những người có chức vụ lãnh đạo
							}
							currStep = true;
						}
						if (!currStep) 
							listUserId.addAll(docInProcessService.findToUserByFromUserAndDocIdAndStepAndHandleTypeInAndLeadership(process.getFrUser(), docId, process.getStep(), Arrays.asList(HandleTypeEnum.SUPPORT), true)); // Lãnh đạo là XLPH step hiện tại
						//listUserId.addAll(docInProcessService.findToUserByDocIdAndAuthority(docId, true)); // Tất cả những người có chức vụ lãnh đạo
					}
				} else { // Không có chức vụ lãnh đạo
				}
				}
			}
			// Add danh sách lãnh đạo đơn vị cấp trên (LĐ Cục)
			Long parentOrg = user.getOrgModel().getParentId();
			if (parentOrg != null)
				listUserId.addAll(userService.findAllLeadershipInParentOrg(parentOrg));
			//Add danh sách người xin ý kiến
			listUserId.addAll(docInManipulationService.findFromUserByToUserAndDocId(user.getId(), docId));
			listUserId.addAll(docInManipulationService.findToUserByFromUserAndDocId(user.getId(), docId));
			List<DocInCommentDto> result = new ArrayList<DocInCommentDto>();
			if (!listProcess.isEmpty()) result = commentRepository.findByListUserIdAndDocIdAndClientIdAndActive(listUserId, docId, user.getClientId(), true);
			System.out.println(result.size());
			return result;
		}
		List<DocInCommentDto> docInCommentDtoList = commentRepository.findByClientIdAndDocIdAndActiveOrderByIdDesc(BussinessCommon.getClientId(), listId, true);
		List<DocInCommentDto> docDuplicate = docInCommentDtoList.stream().filter(i -> i.getUserFullName().equals(BussinessCommon.getUser().getFullName())).collect(Collectors.toList());
		if(docDuplicate.size() > 1){
			for (int i = 0; i < docDuplicate.size(); i++) {
				if (i == docDuplicate.size()-1) {
					docDuplicate.get(i).setEditable(false);
				}
			}
		}
		if (documentParent != null && documentParent.getStatus() == DocumentStatusEnum.DONE) {
			for (DocInCommentDto docInCommentDto : docInCommentDtoList) {
				docInCommentDto.setEditable(false);
			}
		}
		if(documentParent !=null){
			DocumentInProcess documentInProcessToUser= docInProcessService.findByToUserAndDocId(BussinessCommon.getUserId(), documentParent.getId());
			Boolean check = false;
			if (documentInProcessToUser != null) {
				check =docInProcessService.checkDocumentIsStatus(documentInProcessToUser.getStep() + 1, documentParent.getId());
			}
			if(check){
				for (DocInCommentDto docInCommentDto : docInCommentDtoList) {
					if(docInCommentDto.getUserFullName().equals(BussinessCommon.getUser().getFullName())){
						docInCommentDto.setEditable(false);
					}
				}
			}
		}
		return docInCommentDtoList;
	}

	public List<DocumentComment> getListByListDocId(List<Long> docId) {
		return commentRepository.findByClientIdAndListDocIdAndActive(BussinessCommon.getClientId(), docId, true);
	}

	public Boolean deleteComment(Long cmtId) {
		Optional<DocumentComment> dc = commentRepository.findById(cmtId);
		if (dc.isPresent()) {
			dc.get().setActive(false);
			commentRepository.save(dc.get());
			List<AttachmentComment> listAttachment = getListByCommentId(cmtId);
			for (AttachmentComment attach : listAttachment) {
				attach.setActive(false);
				attachRepository.save(attach);
			}
		} else {
			throw new RestExceptionHandler(Message.ACTION_FAILED);
		}
		return true;
	}

	public List<AttachmentComment> getListByCommentId(Long cmtId) {
		return attachRepository.findByClientIdAndCommentId(BussinessCommon.getClientId(), cmtId);
	}

	public DocumentComment findByIdCmt(Long cmtId) {
		DocumentComment cmt = commentRepository.findByClientIdAndIdAndActive(BussinessCommon.getClientId(), cmtId,
				true);
		if (cmt == null) {
			throw new RestExceptionHandler(Message.CMT_NOT_FOUND);
		}
		return cmt;
	}
	
	public List<DocumentComment> getByDocId(Long docId, List<Long> userIds) {
		return commentRepository.findByClientIdAndDocIdAndCreateByInAndActiveTrueOrderByIdAsc(BussinessCommon.getClientId(), docId, userIds);
	}
	
	public DocumentComment save(String comment, String cmtContent, Long docId, DocumentCommentTypeEnum type) {
		if (comment == null) {
			return null;
		}
		
		DocumentComment cmt = new DocumentComment();
		cmt.setDocId(docId);
		cmt.setComment(comment);
		cmt.setType(type);
		if(!StringUtils.isNullOrEmpty(cmtContent)) {
			cmt.setCmtContent(cmtContent);
			cmt.setIsTransfer(true);
		}
		
		return saveCmt(cmt);
	}
	
	public DocumentComment load(Long id) {
		return valid(id, Message.CMT_NOT_FOUND);
	}
	
	public DocumentComment update(Long id, String comment) {
		DocumentComment rs = valid(id, Message.CMT_NOT_FOUND);
		
		if(!rs.getCreateBy().equals(BussinessCommon.getUserId())) {
			throw new RestExceptionHandler(Message.NO_ACTION_PERMISSION);
		}
		BussinessCommon.validLengthData(comment, "Ý kiến xử lý", 2000);
		rs.setComment(comment);
		return commentRepository.save(rs);
	}
}
