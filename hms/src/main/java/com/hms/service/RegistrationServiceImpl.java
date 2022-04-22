package com.hms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.dao.RegistrationDAO;
import com.hms.entities.Admin;

@Service
@Transactional(readOnly = true)
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private RegistrationDAO registerationdao;

	@Override
	@Transactional
	public void addAdmin(Admin admin) {
		registerationdao.addAdmin(admin);
	}

	@Override
	public List<Admin> findAllAdmin() {
		return registerationdao.findAllAdmin();
	}

}