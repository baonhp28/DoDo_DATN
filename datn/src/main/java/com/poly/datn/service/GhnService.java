package com.poly.datn.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GhnService {

    @Value("${ghn.api.key}")
    private String ghnApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String GHN_BASE_URL = "https://online-gateway.ghn.vn/shiip/public-api/master-data";

    // Caching data
    private Map<Integer, List<Map<String, Object>>> districtCache = new HashMap<>();
    private Map<Integer, List<Map<String, Object>>> wardCache = new HashMap<>();
    private List<Map<String, Object>> provinceCache = null;

    // Lấy danh sách các tỉnh
    public List<Map<String, Object>> getProvinces() {
        if (provinceCache == null) {
            provinceCache = callGhnApi(GHN_BASE_URL + "/province");
            System.out.println("Retrieved provinces: " + provinceCache);
        }
        return provinceCache;
    }

    // Lấy danh sách các huyện theo tỉnh
    public List<Map<String, Object>> getDistricts(int provinceId) {
        // Kiểm tra xem danh sách huyện đã được lưu trong cache chưa
        return districtCache.computeIfAbsent(provinceId,
                id -> callGhnApi(GHN_BASE_URL + "/district?province_id=" + id));
    }

    // Lấy danh sách các xã theo huyện
    public List<Map<String, Object>> getWards(int districtId) {
        // Kiểm tra xem danh sách xã đã được lưu trong cache chưa

        return wardCache.computeIfAbsent(districtId, id -> callGhnApi(GHN_BASE_URL + "/ward?district_id=" + id));
    }

    public boolean isValidProvince(int provinceId) {

        List<Map<String, Object>> provinces = getProvinces();
        System.out.println("Checking validity for Province ID: " + provinceId);
        if (provinces == null || provinces.isEmpty()) {
            System.out.println("Danh sách tỉnh trống hoặc không tồn tại.");
            return false;
        }
        // System.out.println("Provinces: " + provinces);
        return provinces.stream()
                .anyMatch(province -> provinceId == (int) province.getOrDefault("ProvinceID", -1));
    }

    public boolean isValidDistrict(int districtId, int provinceId) {
        List<Map<String, Object>> districts = getDistricts(provinceId);
        if (districts == null || districts.isEmpty()) {
            System.out.println("Danh sách huyện trống hoặc không tồn tại.");
            return false;
        }
        // System.out.println("Districts for Province ID " + provinceId + ": " +
        // districts);
        boolean isValid=districts.stream()
                .anyMatch(district -> districtId == (int) district.getOrDefault("DistrictID", -1));
                if (!isValid) {
                    System.out.println(
                            "Huyện với District " + districtId + " không tồn tại trong danh sách xã cho provinceID " + provinceId);
                } else {
                    System.out.println("District " + districtId + " là hợp lệ trong danh sách tỉnh.");
                }
return isValid;

    }

    public boolean isValidWard(String wardId, int districtId) {
        List<Map<String, Object>> wards = getWards(districtId);

        if (wards == null || wards.isEmpty()) {
            System.out.println("Danh sách xã trống hoặc không tồn tại cho districtId " + districtId);
            return false;
        }

        System.out.println("Wards for District ID " + districtId + ": " + wards);

        // Kiểm tra nếu wardId có tồn tại trong danh sách
        boolean isValid = wards.stream()
                .anyMatch(ward -> wardId.equals(String.valueOf(ward.get("WardCode")))); // Sửa thành "WardCode"

        if (!isValid) {
            System.out.println(
                    "Xã với WardID " + wardId + " không tồn tại trong danh sách xã cho districtId " + districtId);
        } else {
            System.out.println("WardID " + wardId + " là hợp lệ trong danh sách xã.");
        }

        return isValid;
    }

    // Gọi API GHN
    private List<Map<String, Object>> callGhnApi(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException(
                        "Failed to fetch data from GHN API, status code: " + response.getStatusCode());
            }

            String responseBody = Optional.ofNullable(response.getBody()).orElse("{}");

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = jsonNode.get("data");

            // Kiểm tra xem dataNode có hợp lệ không
            if (dataNode == null || !dataNode.isArray()) {
                throw new RuntimeException("No valid data returned from GHN API: " + url);
            }

            List<Map<String, Object>> dataList = new ArrayList<>();
            for (JsonNode node : dataNode) {
                dataList.add(objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {
                }));
            }

            return dataList;

        } catch (RestClientException e) {

            throw new RuntimeException("Error while calling GHN API: " + url, e);
        } catch (IOException e) {

            throw new RuntimeException("Failed to parse response", e);
        }
    }

}
