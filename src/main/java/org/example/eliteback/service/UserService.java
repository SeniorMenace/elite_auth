package org.example.eliteback.service;

import org.example.eliteback.dto.profile.*;
import org.example.eliteback.entity.User;
import org.example.eliteback.entity.UserPhoto;
import org.example.eliteback.repository.UserPhotoRepository;
import org.example.eliteback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final FileStorageService fileStorageService;

    private static final String NEXT_PERSONAL = "personal";
    private static final String NEXT_CHARACTER = "character";
    private static final String NEXT_PHOTOS = "photos";

    public UserService(UserRepository userRepository, UserPhotoRepository userPhotoRepository,
                       FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.userPhotoRepository = userPhotoRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public OnboardingStepResponse updateGender(Long userId, OnboardingGenderRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setGender(request.getGender());
        user.setRegistrationStep(Math.max(user.getRegistrationStep(), 1));
        userRepository.save(user);
        OnboardingStepResponse resp = new OnboardingStepResponse();
        resp.setMessage("Gender updated");
        resp.setNextStep(NEXT_PERSONAL);
        return resp;
    }

    @Transactional
    public OnboardingStepResponse updatePersonal(Long userId, OnboardingPersonalRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        user.setRegistrationStep(Math.max(user.getRegistrationStep(), 2));
        userRepository.save(user);
        OnboardingStepResponse resp = new OnboardingStepResponse();
        resp.setMessage("Personal info updated");
        resp.setNextStep(NEXT_CHARACTER);
        return resp;
    }

    @Transactional
    public OnboardingStepResponse updateCharacter(Long userId, OnboardingCharacterRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getCharacterTraits() != null) user.setCharacterTraits(request.getCharacterTraits());
        user.setRegistrationStep(Math.max(user.getRegistrationStep(), 3));
        userRepository.save(user);
        OnboardingStepResponse resp = new OnboardingStepResponse();
        resp.setMessage("Character updated");
        resp.setNextStep(NEXT_PHOTOS);
        return resp;
    }

    @Transactional
    public PhotoUploadResponse uploadPhotos(Long userId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("At least one file is required");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<String> urls = new ArrayList<>();
        int order = userPhotoRepository.findByUserIdOrderBySortOrderAsc(userId).size();
        for (MultipartFile file : files) {
            String url = fileStorageService.store(userId, file);
            UserPhoto photo = new UserPhoto();
            photo.setUser(user);
            photo.setUrl(url);
            photo.setSortOrder(order++);
            userPhotoRepository.save(photo);
            urls.add(url);
        }
        user.setRegistrationStep(Math.max(user.getRegistrationStep(), 4));
        userRepository.save(user);
        PhotoUploadResponse resp = new PhotoUploadResponse();
        resp.setMessage("Photos uploaded");
        resp.setUrls(urls);
        return resp;
    }
}
