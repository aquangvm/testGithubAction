package com.vz.backend.business.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vz.backend.business.domain.RoomMeeting;
import com.vz.backend.core.repository.IRepository;

@Repository
public interface IRoomMeetingRepository extends IRepository<RoomMeeting>{

	List<RoomMeeting> findByClientIdAndActiveTrue(Long clientId);

	@Query("select r from RoomMeeting r "
			+ "where lower(r.name) like  %:text% or lower(r.description) like %:text% or lower(r.address) like %:text% "
			+ "and r.clientId=:clientId and r.active is true")
	Page<RoomMeeting> getRoomPageByCondition(String text, Long clientId, Pageable page);

	@Query("select r from RoomMeeting r "
			+ "where (:name is null or lower(r.name) like  %:name%) "
			+ "and (:description is null or lower(r.description) like %:description%) "
			+ "and (:address is null or lower(r.address) like %:address%) "
			+ "and (:quantity is null or r.quantity = :quantity) "
			+ "and (:acreage is null or r.acreage = :acreage) "
			+ "and r.clientId=:clientId and r.active is true ")
	Page<RoomMeeting> getRoomPageByCondition(String name, String address, String description, Integer quantity,
			Float acreage, Long clientId, Pageable page);

}
