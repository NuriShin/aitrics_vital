#### 기술스택
* Java 21, Spring Boot 3.x
* 빌드: Gradle (Groovy 스크립트)
* 영속성: JPA
* DB: H2 database 
* 검증: Jakarta Validation
* 테스트: JUnit 5

#### H2 database scheme 
* 어플리케이션 실행 시 scheme.sql 내용으로 스키마 구성 및 테스트 데이터 입력 

#### 패키지 레이아웃 
* layered 구조
com.aitrics.vital
├─ VitalApplication.java
├─ api
│  ├─ controller
│  │  ├─ PatientController.java 
│  │  ├─ VitalController.java
│  │  └─ InferenceController.java
│  ├─ dto
│  │  ├─ request
│  │  │  ├─ PatientCreateRequest.java
│  │  │  ├─ PatientUpdateRequest.java
│  │  │  ├─ VitalCreateRequest.java
│  │  │  ├─ VitalUpdateRequest.java
│  │  │  └─ InferenceRequest.java
│  │  └─ response
│  │     ├─ PatientResponse.java
│  │     ├─ VitalResponse.java
│  │     └─ InferenceResponse.java
│  └─ error
│     ├─ ApiExceptionHandler.java
│     ├─ ErrorResponse.java
│     └─ ErrorCode.java
├─ application
│  └─ service
│    ├─ PatientService.java
│    ├─ VitalService.java
│    └─ InferenceService.java
├─ domain
│  ├─ model
│  │  ├─ Patient.java
│  │  └─ Vital.java
│  ├─ enumtype
│  │  ├─ Gender.java
│  │  └─ VitalType.java
│  └─ exception
│     ├─ NotFoundException.java
│     ├─ ConflictException.java
│     └─ UnauthorizedException.java
├─ infra
│  ├─ repository
│  │  ├─ PatientRepository.java
│  │  └─ VitalRepository.java
│  └─ security
│     ├─ BearerTokenFilter.java
│     └─ SecurityConfig.java

###### API 기능 

## 환자등록 API 
* controller : PatientController
* 유즈케이스 service : PatientService
* API 요청 및 응답 dtos:  PatientCreateRequest, PatientResponse
* 연결 도메인 : Patient

* Method - `POST`
* Endpoint : `/api/v1/patients`
* Request 예시 
{
  "patient_id": "P00001234",
  "name": "홍길동",
  "gender": "M",
  "birth_date": "1975-03-01"
  }
## 환자정보 수정 API 
* controller : PatientController
* 유즈케이스 service : PatientService
* API 요청 및 응답 dtos:  PatientUpdateRequest, PatientResponse
* 연결도메인 : Patient 
* Method : `PUT`
* Endpoint : `/api/v1/patients/{patient_id}`
* 제약사항 : Request Body에 `version` 필수, DB version과 다르면 → `409 Conflict` 반환(optimistic lock)
* Request 예시
{
  "name": "홍길동",
  "gender": "M",
  "birth_date": "1975-03-01",
  "version": 3
}
#### Vital 데이터 API 
* controller : vitalController
* 유즈케이스 service : VitalService
* API 요청 및 응답 dtos:  VitalCreateRequest, VitalQueryResponse
* 연결도메인 : Vital 
* 제약사항 : 등록된 patient_id만 데이터 저장 가능
* enum/VitalType에 다음의 요소를 등록 ["HR", "RR", "SBP", "DBP", "SpO2", "BT"]
* Request 예시 {
  "patient_id": "P00001234",
  "recorded_at": "2025-12-01T10:15:00Z",
  "vital_type": "HR",
  "value": 110.0
  }

#### Vital 데이터 조회 API
* controller : VitalController
* 유즈케이스 service : VitalService
* API 요청 및 응답 dtos : VitalQueryResponse 
* 연결 도메인 : Vital 
* 제약사항 : 등록된 patient_id만 조회 가능 / from, to 파라미터 필수 / vital_type은 선택값 
* Method : GET 
* Endpoint : /api/v1/patients/{patient_id}/vitals 
* Query Parameters: from=조회 시작 시각 (ISO-8601), to=조회 종료 시각 (ISO-8601), vital_type=조회할 Vital 타입 (선택)
* Response 예시 {
"patient_id": "P00001234",
"vital_type": "HR",
"items": [{
"recorded_at": "2025-12-01T10:15:00Z",
"value": 110.0
}
]
}

#### Vital 데이터 교정 API (Optimistic Lock 적용)
* controller : VitalController
* 유즈케이스 service : VitalService
* API 요청 및 응답 dtos : VitalUpdateRequest, VitalResponse
* 연결 도메인 : Vital
* 제약사항 : Request Body에 version 필수, 요청 version과 DB version 불일치 시 409 Conflict 반환(optimistic lock)
* Method : PUT
* Endpoint : /api/v1/vitals/{vital_id}
* Request 예시 {
"value": 105.0,
"version": 1
}
* 설명 : request version이 현재 version과 비교하여 불일치할 경우 409 Conflict를 반환한다. 해당 에러는 ConflictException으로 전역적으로 관리한다 

#### Inference API (Rule 기반 Vital Risk 평가)
* controller : InferenceController
* 유즈케이스 service : InferenceService
* API 요청 및 응답 dtos : VitalRiskInferenceRequest, VitalRiskInferenceResponse
* Method : POST
* Endpoint : /api/v1/inference/vital-risk
* Request 예시 {
"patient_id": "P00001234",
"records": [
{
"recorded_at": "2025-12-01T10:15:00Z",
"vitals": {
"HR": 130.0,
"SBP": 85.0,
"SpO2": 89.0
}}]}
* Response 예시
{
"risk_score": 0.9,
"risk_level": "HIGH"
}
* 설명: request에 따른 평가 규칙에 따라 risk_score를 반환한다. 평가규칙 및 risk_score은 아래와 같다. 
* 평가 규칙
| 조건 | 의미 |
| --- | --- |
| HR > 120 | 위험 증가 |
| SBP < 90 | 위험 증가 |
| SpO2 < 90 | 위험 증가 |
* 충족 개수에 따른 risk score
  충족 개수	risk_score	risk_level
  0	≤0.3	LOW
  1–2	0.4–0.7	MEDIUM
  ≥3	≥0.8	HIGH

#### 인증 요구사항 
* Bearer Token 기반 인증 적용 
* Header 예시
Authorization: Bearer <token>
* 토큰은 환경별 프로파일 설정파일에 추가하여 관리한다. 

#### 설정
* application-local.yml, application-staging.yml, application-prod.yml 프로파일 설정
* 로컬에서 테스트 하거나 배포할 경우 각 환경에 맞춰 각 설정파일을 참조하도록 실행
* 로컬에서 active profiles에 local을 설정하면 로컬 환경 실행, staging을 설정하면 스테이징 환경 실행, prod를 설정하면 프로드 환경 실행하도록 함
* 각 유닛 테스트를 필수로 작성한다 
* 각 api는 swagger 설정을 추가한다 
* 로컬 실행 가능하도록 dockerfile을 작성한다

#### 로그
* logback-spring.xml에 프로파일별 appenders

#### 실행 및 스웨거 경로
* swagger-ui : http://localhost:8080/swagger-ui/index.html 
* 인증 토큰은 각 설정파일에 기재되어 있습니다. 로컬에서 실행 시 local-test-token를 입력합니다. 
* dockerfile 사용시 
  * 프로젝트 루트 폴더로 접근
  * docker build -t aitrics-vital -f Dockerfile . 실행
  * docker run -d -p 8080:8080 --name aitrics-vital aitrics-vital 실행
  * 스웨거 ui로 접근하여 실행 확인 
