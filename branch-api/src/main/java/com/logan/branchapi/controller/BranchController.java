package com.logan.branchapi.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.logan.branchapi.dto.BranchCreateRequest;
import com.logan.branchapi.dto.BranchResponse;
import com.logan.branchapi.dto.BranchUpdateRequest;
import com.logan.branchapi.dto.PageResponse;
import com.logan.branchapi.service.BranchService;
import com.logan.branchapi.service.BranchWeatherService;
import com.logan.branchapi.dto.BranchWeatherResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchService branchService;
    private final BranchWeatherService branchWeatherService;

    public BranchController(
            BranchService branchService,
            BranchWeatherService branchWeatherService) {

        this.branchService = branchService;
        this.branchWeatherService =
                branchWeatherService;
    }

    @PostMapping
    public ResponseEntity<BranchResponse> createBranch(
            @Valid
            @RequestBody
            BranchCreateRequest request) {

        BranchResponse created =
                branchService.createBranch(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> updateBranch(
            @PathVariable Long id,
            @Valid
            @RequestBody
            BranchUpdateRequest request) {

        return ResponseEntity.ok(
                branchService.updateBranch(id, request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getBranch(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                branchService.getBranch(id)
        );
    }

    @GetMapping
    public ResponseEntity<PageResponse<BranchResponse>>
            getBranches(

            @RequestParam(defaultValue = "0")
            int page) {

        return ResponseEntity.ok(
                branchService.getBranches(page)
        );
    }

    @GetMapping("/code/{branchCode}")
    public ResponseEntity<BranchResponse> getBranchByCode(
            @PathVariable String branchCode) {

        return ResponseEntity.ok(
                branchService.getBranchByCode(branchCode)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(
            @PathVariable Long id) {

        branchService.deleteBranch(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/{id}/weather")
    public ResponseEntity<BranchWeatherResponse>
            getBranchWeather(@PathVariable Long id) {

        return ResponseEntity.ok(
                branchWeatherService.getBranchWeather(id)
        );
    }
}