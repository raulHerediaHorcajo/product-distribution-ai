package com.productdistribution.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.productdistribution.backend.entities.UnfulfilledDemand;
import com.productdistribution.backend.repositories.UnfulfilledDemandRepository;

import java.util.List;

@Service
public class UnfulfilledDemandService {

    private final UnfulfilledDemandRepository unfulfilledDemandRepository;

    @Autowired
    public UnfulfilledDemandService(UnfulfilledDemandRepository unfulfilledDemandRepository) {
        this.unfulfilledDemandRepository = unfulfilledDemandRepository;
    }

    public void add(UnfulfilledDemand unfulfilledDemand) {
        unfulfilledDemandRepository.save(unfulfilledDemand);
    }

    public void addAll(List<UnfulfilledDemand> unfulfilledDemands) {
        unfulfilledDemandRepository.saveAll(unfulfilledDemands);
    }

    public List<UnfulfilledDemand> getAll() {
        return unfulfilledDemandRepository.findAll();
    }

    public void deleteAll() {
        unfulfilledDemandRepository.truncateAll();
    }
}