package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.dto.UserAccountDTO;
import com.macrotel.zippyworld_test.entity.UserAccountEntity;
import org.springframework.stereotype.Service;

@Service
public class DTOService {
    public UserAccountDTO userAccountDTO(UserAccountEntity userAccountEntity){
        UserAccountDTO userAccountDTO = new UserAccountDTO();
        userAccountDTO.setFirstname(userAccountEntity.getFirstname());
        userAccountDTO.setLastname(userAccountEntity.getLastname());
        userAccountDTO.setPhonenumber(userAccountEntity.getPhonenumber());
        userAccountDTO.setEmail(userAccountEntity.getEmail());
        userAccountDTO.setUser_type(userAccountEntity.getUserType());
        userAccountDTO.setUser_package_id(userAccountEntity.getUserPackageId());
        userAccountDTO.setIs_tax_collector(userAccountEntity.getIsTaxCollector());
        userAccountDTO.setCommission_mode(userAccountEntity.getCommissionMode());
        userAccountDTO.setKyc_level(userAccountEntity.getKycLevel());
        userAccountDTO.setBvn(userAccountEntity.getBvn());
        userAccountDTO.setBvn_verification_status(userAccountEntity.getBvnVerificationStatus());
        userAccountDTO.setIdentityNumber(userAccountEntity.getIdentityNumber());
        userAccountDTO.setIdentity_verify_status(userAccountEntity.getIdentityVerifyStatus());
        userAccountDTO.setAccount_no(userAccountEntity.getAccountNo());
        userAccountDTO.setGtb_account_no(userAccountEntity.getGtbAccountNo().isEmpty() ? "0" : userAccountEntity.getGtbAccountNo());
        userAccountDTO.setWtl_status(userAccountEntity.getWtlStatus().isEmpty() ? "2" :  userAccountEntity.getWtlStatus());
        userAccountDTO.setAddress(userAccountEntity.getAddress());
        userAccountDTO.setWtl_user_type(userAccountEntity.getWtlStatus().isEmpty() ? "2" :  userAccountEntity.getWtlStatus());
        userAccountDTO.setPromo_code(userAccountEntity.getPromoCode());
        userAccountDTO.setReferrer_code(userAccountEntity.getReferrerCode());
        return userAccountDTO;
    }
}
