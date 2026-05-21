# {팀이름}
https://www.notion.so/Part_Team3-360a0dc3f7b680369764f847a959e9e8?source=copy_link

## 팀원 구성 
 박교현 (팀장) : https://github.com/hyeon2628?tab=repositories  
 여운정 : https://github.com/novafterg1ow  
 이주혜 : https://github.com/Jerong81  
 이태형 : https://github.com/lth0415-tech  
 정우진 : https://github.com/zinzin68  

## 프로젝트 소개
- 가볍고 빠른 외부 API 연동 금융 분석 벡엔드 시스템 구축
- 💹 한눈에 보는 금융 지수 데이터!
Findex는 외부 Open API와 연동하여 금융 지수 데이터를 제공하는 대시보드 서비스입니다.
사용자는 직관적인 UI에서 금융 지수의 흐름을 파악하고, 자동 연동 기능을 통해 최신 데이터를 분석할 수 있습니다. 지수별 성과 분석, 이동평균선 계산, 자동 데이터 업데이트 기능을 통해 가볍고 강력한 금융 분석 도구를 경험해 보세요! 📈📊
- 프로젝트 기간: 2026.05.14 ~ 2026.05.26

## 기술 스택
- Framework: SpringBoot
- DataBase: Spring Data JPA, PostgreSQL, H2
- Documentation: springdoc-openapi (Swagger)
- Scheduling: Spring Scheduler
- Utility: MapStruct, Query DSL
- 배포 및 협업: Railway.io / Git & GitHub, Jira, Discord

## 팀원별 구현 기능 상세
#### 박교현
  - 팀원 역할 분담 및 일정 관리
  - Index Info CRUD API
    - Index Info 등록 API
    - Index Info 수정 API
    - Index Info 조회 API
      - cursor 기반 Index Info 목록 페이지 네이션 구현
    - Index Info 삭제 API
   
#### 여운정
  - 대시보드 API

#### 이주혜
  - Open API 연동 및 CRUD

#### 이태형
  - Open API 자동 연동 및 CRUD

#### 정우진
  - Index Data CRUD API
    - Index Data 등록 API
    - Index Data 수정 API
    - Index Data 조회 API
      - cursor 기반 Index Data 목록 페이지 네이션 구현
    - Index Data 삭제 API

## 파일 구조
```
src
 ├─main
 │ ├─java
 │ │  └─com
 │ │    └─codeit
 │ │      └─findex
 │ │         │  FindexApplication.java
 │ │         │
 │ │         ├─domain
 │ │         │  ├─autosync
 │ │         │  │  ├─controller
 │ │         │  │  │      AutoSyncController.java
 │ │         │  │  │
 │ │         │  │  ├─dto
 │ │         │  │  │      AutoSyncCreateRequest.java
 │ │         │  │  │      AutoSyncResponse.java
 │ │         │  │  │      AutoSyncUpdateRequest.java
 │ │         │  │  │      
 │ │         │  │  ├─entity
 │ │         │  │  │      AutoSync.java
 │ │         │  │  │
 │ │         │  │  ├─repository
 │ │         │  │  │      AutoSyncRepository.java
 │ │         │  │  │
 │ │         │  │  └─service
 │ │         │  │          AutoSyncService.java
 │ │         │  │
 │ │         │  ├─dashboard
 │ │         │  │  │  DashboardMapper.java
 │ │         │  │  │
 │ │         │  │  ├─controller
 │ │         │  │  │      DashboardController.java
 │ │         │  │  │
 │ │         │  │  ├─dto
 │ │         │  │  │      DashboardSummaryResponse.java
 │ │         │  │  │      IndexPerformanceResponse.java
 │ │         │  │  │
 │ │         │  │  └─service
 │ │         │  │          DashboardFavoriteService.java
 │ │         │  │
 │ │         │  ├─indexdata
 │ │         │  │  ├─controller
 │ │         │  │  │      IndexDataController.java
 │ │         │  │  │
 │ │         │  │  ├─dto
 │ │         │  │  │      CursorPageResponseIndexDataDto.java
 │ │         │  │  │      IndexDataCreateRequest.java
 │ │         │  │  │      IndexDataDto.java
 │ │         │  │  │      IndexDataSearchRequest.java
 │ │         │  │  │      IndexDataUpdateRequest.java
 │ │         │  │  │      IndexPerformanceDto.java
 │ │         │  │  │      RankedIndexPerformanceDto.java
 │ │         │  │  │
 │ │         │  │  ├─entity
 │ │         │  │  │      IndexData.java
 │ │         │  │  │      PeriodType.java
 │ │         │  │  │      SourceType.java
 │ │         │  │  │
 │ │         │  │  ├─mapper
 │ │         │  │  │      IndexDataMapper.java
 │ │         │  │  │
 │ │         │  │  ├─repository
 │ │         │  │  │      IndexDataRepository.java
 │ │         │  │  │      IndexDataRepositoryCustom.java
 │ │         │  │  │      IndexDataRepositoryCustomImpl.java
 │ │         │  │  │
 │ │         │  │  └─service
 │ │         │  │          IndexDataService.java
 │ │         │  │
 │ │         │  ├─indexinfo
 │ │         │  │  ├─controller
 │ │         │  │  │      IndexInfoController.java
 │ │         │  │  │
 │ │         │  │  ├─dto
 │ │         │  │  │      IndexInfoCreateRequest.java
 │ │         │  │  │      IndexInfoResponse.java
 │ │         │  │  │      IndexInfoUpdateRequest.java
 │ │         │  │  │
 │ │         │  │  ├─entity
 │ │         │  │  │      IndexInfo.java
 │ │         │  │  │
 │ │         │  │  ├─repository
 │ │         │  │  │      IndexInfoRepository.java
 │ │         │  │  │
 │ │         │  │  └─service
 │ │         │  │          IndexInfoService.java
 │ │         │  │
 │ │         │  └─syncjob
 │ │         │      ├─controller
 │ │         │      │      SyncJobController.java
 │ │         │      │
 │ │         │      ├─dto
 │ │         │      │      SyncJobDetailResponse.java
 │ │         │      │      SyncJobIndexDataSyncRequest.java
 │ │         │      │      SyncJobListResponse.java
 │ │         │      │      SyncJobSearchCondition.java
 │ │         │      │
 │ │         │      ├─entity
 │ │         │      │      SyncJob.java
 │ │         │      │
 │ │         │      ├─repository
 │ │         │      │      SyncJobRepository.java
 │ │         │      │
 │ │         │      ├─service
 │ │         │      │      SyncJobService.java
 │ │         │      │
 │ │         │      └─specification
 │ │         │              SyncJobSpecification.java
 │ │         │
 │ │         ├─global
 │ │         │  ├─common
 │ │         │  │  │  ApiResponse.java
 │ │         │  │  │
 │ │         │  │  └─dto
 │ │         │  │          CursorPageResponse.java
 │ │         │  │
 │ │         │  ├─config
 │ │         │  │      QuerydslConfig.java
 │ │         │  │      RestClientConfig.java
 │ │         │  │
 │ │         │  └─exception
 │ │         │          BusinessException.java
 │ │         │          ErrorCode.java
 │ │         │          ErrorResponse.java
 │ │         │          GlobalRestExceptionHandler.java
 │ │         │
 │ │         └─infra
 │ │             └─openapi
 │ │                 │  OpenApiClient.java
 │ │                 │  OpenApiService.java
 │ │                 │
 │ │                 ├─config
 │ │                 │      OpenApiProperties.java
 │ │                 │
 │ │                 ├─dto
 │ │                 │      OpenApiIndexItemDto.java
 │ │                 │      OpenApiResponseDto.java
 │ │                 │
 │ │                 └─parser
 │ │                         OpenApiResponseParser.java
 │ │
 │ └─resources
 │         application.yaml
 │         schema-h2.sql
 │         schema.sql
 │
 └─test
     └─java
         └─com
             └─codeit
                 └─findex
                     │  FindexApplicationTests.java
                     │
                     └─infra
                         └─openapi
                             └─parser
                                     OpenApiResponseParserTest.java


```

## 구현 홈페이지

## 프로젝트 회고록
