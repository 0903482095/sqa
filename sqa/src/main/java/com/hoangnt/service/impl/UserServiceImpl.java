package com.hoangnt.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hoangnt.entity.Account;
import com.hoangnt.entity.Address;
import com.hoangnt.entity.Area;
import com.hoangnt.entity.Coefficient;
import com.hoangnt.entity.QuanHuyen;
import com.hoangnt.entity.Role;
import com.hoangnt.entity.Salary;
import com.hoangnt.entity.TinhThanhPho;
import com.hoangnt.entity.User;
import com.hoangnt.entity.XaPhuongThiTran;
import com.hoangnt.model.AccountDTO;
import com.hoangnt.model.AddressDTO;
import com.hoangnt.model.AreaDTO;
import com.hoangnt.model.QuanHuyenDTO;
import com.hoangnt.model.SalaryDTO;
import com.hoangnt.model.TinhThanhPhoDTO;
import com.hoangnt.model.UserDTO;
import com.hoangnt.model.XaPhuongThiTranDTO;
import com.hoangnt.repository.AccountRepository;
import com.hoangnt.repository.AddressRepository;
import com.hoangnt.repository.CoefficientRepository;
import com.hoangnt.repository.SalaryRepository;
import com.hoangnt.repository.UserRepository;
import com.hoangnt.service.UserService;

@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AddressRepository addressRepository;

	@Autowired
	SalaryRepository salaryRepository;

	@Autowired
	CoefficientRepository coefficientRepository;

	@Override
	public List<UserDTO> getAllUser() { // get tat ca user
		List<UserDTO> userDTOs = new ArrayList<UserDTO>();
		userRepository.findAll().forEach(user -> {
			userDTOs.add(convertDataFromUserToUserDTO(user)); // goi ham middleware de convert du lieu tu lop user sang
																// lop userDTO
		});
		return userDTOs;
	}

	@Override
	public UserDTO getUserById(int id) { // get user theo id
		User user = userRepository.getOne(id);
		if (user != null) {
			return convertDataFromUserToUserDTO(user); // goi ham middleware de convert du lieu tu lop user sang lop
														// userDTO
		}
		return null;
	}

	@Override
	public void deleteUser(int id) { // xoa user theo id
		userRepository.deleteById(id);
	}

	@Override
	public UserDTO getUserByNameAccount(String name) { // get user theo name
		User user = userRepository.findByNameAccount(name);
		if (user != null) {
			return convertDataFromUserToUserDTO(user); // goi ham middleware de convert du lieu tu lop user sang lop
														// userDTO
		}
		return null;
	}

	@Override
	public UserDTO getUserByEmail(String email) {// get user theo email
		User user = userRepository.findByEmail(email);
		if (user != null) {
			return convertDataFromUserToUserDTO(user); // goi ham middleware de convert du lieu tu lop user sang lop
														// userDTO
		}
		return null;
	}

	@Override
	public UserDTO login(AccountDTO accountDTO) { // ham dang nhap
		UserDTO userDTO = getUserByNameAccount(accountDTO.getUsername());
		if (userDTO != null && BCrypt.checkpw(accountDTO.getPassword(), userDTO.getAccountDTO().getPassword())) {
			return userDTO;
		}
		return null;
	}

	@Override
	public void updatePassword(String password, int id) {// ham update password
		User user = userRepository.getOne(id);
		accountRepository.updatePassword(password, user.getAccount().getId());

	}

	@Override
	public User addUserDTO(UserDTO userDTO) { // them user
		if (getUserByEmail(userDTO.getEmail()) == null) {
			User user = new User();
			Account account = new Account();
			account.setUsername(userDTO.getEmail());
			// password duoc theo vao csdl dua tren ngay thang nam sinh cua user
			account.setPassword(BCrypt.hashpw(chuanHoaDate(userDTO.getDate_of_birth()), BCrypt.gensalt(12)));
			accountRepository.save(account);
			user.setAccount(account);

			convertDataFromUserDTOToUser(user, userDTO); // goi ham middle de convert du lieu tu lop userDTO sang lop
															// user

			return user;
		}
		return null;
	}

	@Override
	public User updateUserDTO(UserDTO userDTO) { // cap nhat user
		User user = userRepository.getOne(userDTO.getId());

		convertDataFromUserDTOToUser(user, userDTO); // goi ham middle de convert du lieu tu lop userDTO sang lop user
		return user;
	}

	public UserDTO convertDataFromUserToUserDTO(User user) { // convert du lieu tu user sang userDTO
		UserDTO userDTO = new UserDTO(0);
		if (user.getId() > 0) {

			userDTO.setId(user.getId());
			userDTO.setFull_name(user.getFull_name());
			userDTO.setEmail(user.getEmail());
			userDTO.setId_person(user.getId_person());
			userDTO.setDate_of_birth(user.getDate_of_birth());
			userDTO.setIs_male(user.isIs_male());

			userDTO.setIs_vol(user.isIs_vol());
			userDTO.setCareer(user.getCareer());
			userDTO.setIs_free(user.isIs_free());
			userDTO.setFree_detail(user.getFree_detail());
			userDTO.setPhone(user.getPhone());

			userDTO.setRole_id(user.getRole().getId());

			AccountDTO accountDTO = new AccountDTO();
			accountDTO.setId(user.getAccount().getId());
			accountDTO.setUsername(user.getAccount().getUsername());
			accountDTO.setPassword(user.getAccount().getPassword());

			userDTO.setAccountDTO(accountDTO);

			AddressDTO addressDTO = new AddressDTO();
			addressDTO.setId(user.getAddress().getId());

			TinhThanhPhoDTO tinhThanhPhoDTO = new TinhThanhPhoDTO();
			TinhThanhPho tinhThanhPho = user.getAddress().getProvince();
			tinhThanhPhoDTO.setMatp(tinhThanhPho.getMatp());
			tinhThanhPhoDTO.setName(tinhThanhPho.getName());
			tinhThanhPhoDTO.setType(tinhThanhPho.getType());

			QuanHuyenDTO quanHuyenDTO = new QuanHuyenDTO();
			QuanHuyen quanHuyen = user.getAddress().getDistrict();
			quanHuyenDTO.setMaqh(quanHuyen.getMaqh());
			quanHuyenDTO.setName(quanHuyen.getName());
			quanHuyenDTO.setType(quanHuyen.getType());

			AreaDTO areaDTO = new AreaDTO();
			Area area = user.getAddress().getDistrict().getArea();
			areaDTO.setId(area.getId());
			areaDTO.setMax_sal(area.getMax_sal());
			areaDTO.setMin_sal(area.getMin_sal());
			areaDTO.setName(area.getName());
			quanHuyenDTO.setAreaDTO(areaDTO);

			XaPhuongThiTranDTO xaPhuongThiTranDTO = new XaPhuongThiTranDTO();
			XaPhuongThiTran xaPhuongThiTran = user.getAddress().getTown();
			xaPhuongThiTranDTO.setXaid(xaPhuongThiTran.getXaid());
			xaPhuongThiTranDTO.setName(xaPhuongThiTran.getName());
			xaPhuongThiTranDTO.setType(xaPhuongThiTran.getType());

			addressDTO.setProvince(tinhThanhPhoDTO);
			addressDTO.setDistrict(quanHuyenDTO);
			addressDTO.setTown(xaPhuongThiTranDTO);

			userDTO.setAddressDTO(addressDTO);

			SalaryDTO salaryDTO = new SalaryDTO();
			salaryDTO.setId(user.getSalary().getId());
			salaryDTO.setMain_sal(user.getSalary().getMain_sal());
			salaryDTO.setPosition_allowrance(user.getSalary().getPosition_allowrance());
			salaryDTO.setRes_allowrance(user.getSalary().getRes_allowrance());

			userDTO.setSalaryDTO(salaryDTO);

			Coefficient coefficient = coefficientRepository.getOne(1);
			userDTO.setInsurance(insurance(coefficient.getCoe(), user));
		}
		return userDTO;
	}

	public void convertDataFromUserDTOToUser(User user, UserDTO userDTO) { // convert du lieu tu userDTO sang user
		user.setFull_name(userDTO.getFull_name());
		user.setEmail(userDTO.getEmail());
		user.setId_person(userDTO.getId_person());
		user.setDate_of_birth(userDTO.getDate_of_birth());
		user.setIs_male(userDTO.isIs_male());

		user.setIs_vol(userDTO.isIs_vol());
		user.setCareer(userDTO.getCareer());
		user.setIs_free(userDTO.isIs_free());
		user.setFree_detail(userDTO.getFree_detail());
		user.setPhone(userDTO.getPhone());

		Address address = new Address();

		TinhThanhPhoDTO tinhThanhPhoDTO = userDTO.getAddressDTO().getProvince();
		QuanHuyenDTO quanHuyenDTO = userDTO.getAddressDTO().getDistrict();
		XaPhuongThiTranDTO xaPhuongThiTranDTO = userDTO.getAddressDTO().getTown();

		address.setProvince(new TinhThanhPho(tinhThanhPhoDTO.getMatp()));
		address.setDistrict(new QuanHuyen(quanHuyenDTO.getMaqh()));
		address.setTown(new XaPhuongThiTran(xaPhuongThiTranDTO.getXaid()));

		addressRepository.save(address);
		user.setAddress(address);

		Salary salary = new Salary();
		salary.setMain_sal(userDTO.getSalaryDTO().getMain_sal());
		salary.setPosition_allowrance(userDTO.getSalaryDTO().getPosition_allowrance());
		salary.setRes_allowrance(userDTO.getSalaryDTO().getRes_allowrance());
		salaryRepository.save(salary);
		user.setSalary(salary);

		user.setRole(new Role(userDTO.getRole_id()));

		userRepository.save(user);
	}

	public static String chuanHoaDate(String s) { // convert ngay sinh cua user sang dinh dang ddmmyy
		String s2;
		String s1[] = s.split("-");
		s2 = s1[2] + s1[1] + s1[0].substring(2, 4);
		return s2;
	}

	public static Double insurance(Double coe, User user) {// ham tinh so tien bao hiem pai dong cua user
		if (user.isIs_free() && !user.isIs_vol()) { // truogn hop user duoc mien giam va khong tu nguyen dong
			return 0.0;
		}
		// tinh tong so luong cua user
		Double total_salary = user.getSalary().getMain_sal() + user.getSalary().getPosition_allowrance()
				+ user.getSalary().getRes_allowrance();

		if (user.isIs_vol()) {// truong user tu nguye dong
			if (total_salary < user.getAddress().getDistrict().getArea().getMin_sal()) { // tong luong nho hon muc luong
																							// toi thieu cua vung
				return (coe * user.getAddress().getDistrict().getArea().getMin_sal());
			}
		}

		if (total_salary > user.getAddress().getDistrict().getArea().getMax_sal()) // tong luong lon hon mua luong toi
																					// da cua vung
			return (coe * user.getAddress().getDistrict().getArea().getMax_sal());

		if (total_salary < user.getAddress().getDistrict().getArea().getMin_sal())// tong luong nho hon muc luong toi
																					// thieu cua vung
			return 0.0;

		return coe * total_salary; // truong hop doi tuong co muc luong trong khoang dong bao hiem va khong duoc
									// mien giam
	}

}
