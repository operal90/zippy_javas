package com.macrotel.zippyworld_test.service;

import com.macrotel.zippyworld_test.dto.KycDTO;
import com.macrotel.zippyworld_test.dto.MessageServiceDTO;
import com.macrotel.zippyworld_test.dto.UserAccountDTO;
import com.macrotel.zippyworld_test.entity.CustomerIdentityRecordEntity;
import com.macrotel.zippyworld_test.entity.IdentityEntity;
import com.macrotel.zippyworld_test.entity.MessageServiceEntity;
import com.macrotel.zippyworld_test.entity.UserAccountEntity;
import com.macrotel.zippyworld_test.repo.IdentityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DTOService {
    @Autowired
    IdentityRepo identityRepo;
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
        userAccountDTO.setGtb_account_no(userAccountEntity.getGtbAccountNo()==null ? "0" : userAccountEntity.getGtbAccountNo());
        userAccountDTO.setWtl_status(userAccountEntity.getWtlStatus()==null ? "2" :  userAccountEntity.getWtlStatus());
        userAccountDTO.setAddress(userAccountEntity.getAddress());
        userAccountDTO.setWtl_user_type(userAccountEntity.getWtlStatus()==null ? "2" :  userAccountEntity.getWtlStatus());
        userAccountDTO.setPromo_code(userAccountEntity.getPromoCode());
        userAccountDTO.setReferrer_code(userAccountEntity.getReferrerCode());
        return userAccountDTO;
    }

    public MessageServiceDTO messageServiceDTO(MessageServiceEntity messageServiceEntity){
        MessageServiceDTO messageServiceDTO = new MessageServiceDTO();
        messageServiceDTO.setEmail(messageServiceEntity.getEmail() ==null ? "1" :messageServiceEntity.getEmail());
        messageServiceDTO.setSms(messageServiceEntity.getSms() ==null? "1" : messageServiceEntity.getSms());
        messageServiceDTO.setWhatsapp(messageServiceEntity.getWhatsapp() ==null ?"1" :messageServiceEntity.getWhatsapp());
        return messageServiceDTO;
    }

    public KycDTO kycDTO(CustomerIdentityRecordEntity customerIdentityRecordEntity){
        KycDTO kycDTO = new KycDTO();
        Long kycId = Long.parseLong(customerIdentityRecordEntity.getKycId());
        Optional<IdentityEntity> getIdentity = identityRepo.findById(kycId);
        IdentityEntity identityEntity = getIdentity.get();
        kycDTO.setCustomerId(customerIdentityRecordEntity.getCustomerId());
        kycDTO.setCustomerName(customerIdentityRecordEntity.getIdentityName());
        kycDTO.setIdentityType(identityEntity.getIdentityType());
        kycDTO.setIdentityNumber(customerIdentityRecordEntity.getIdentityNumber());
        kycDTO.setDateCreated(customerIdentityRecordEntity.getInsertedDt());
        kycDTO.setStatus((customerIdentityRecordEntity.getStatus().equals("0") ?"Approved" :customerIdentityRecordEntity.getStatus().equals("2") ? "Rejected":"Pending Approval"));
        return kycDTO;
    }
}
