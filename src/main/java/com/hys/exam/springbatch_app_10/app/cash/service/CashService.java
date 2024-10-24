package com.hys.exam.springbatch_app_10.app.cash.service;

import com.hys.exam.springbatch_app_10.app.cash.entity.CashLog;
import com.hys.exam.springbatch_app_10.app.cash.repository.CashRepository;
import com.hys.exam.springbatch_app_10.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CashService {
    private final CashRepository cashRepository;
    public CashLog addCash(Member member, long price, String eventType) {
        CashLog cashLog = CashLog.builder()
                .member(member)
                .price(price)
                .eventType(eventType)
                .build();
        cashRepository.save(cashLog);
        return cashLog;
    }
}