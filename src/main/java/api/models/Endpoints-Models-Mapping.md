# TeamCity Happy Path: endpoints ↔ models

База: `http://localhost:8111`  
Имена: `*Request` — тело запроса, `*Response` — тело ответа.  
Auth для REST: HTTP Basic / Bearer (заголовок), не отдельная login-модель.

---

## Почему не везде есть пара Request + Response

| Ситуация | Пример | Почему |
|----------|--------|--------|
| Только GET | `GET /agents` → `AgentsResponse` | Нет body в запросе |
| Только DELETE | `DELETE /projects/{id}` | Нет body |
| Auth header | Basic / Bearer | Credentials не JSON-модель |
| Вложенный объект | `Properties`, `Property` | Не endpoint, а часть Step |
| Лог билда | `/downloadBuildLog.html` | plain text, не DTO |
| Swagger одна schema на вход/выход | BuildType, Step, Build | В коде всё равно **две** модели (`Create*Request` / `*Response`) |

---

## Authorization / User

| Method | Endpoint | Request model | Response model |
|--------|----------|---------------|----------------|
| `GET` | `/app/rest/users/id:current` | — | `UserResponse` |
| `GET` | `/app/rest/users/{userLocator}` | — | `UserResponse` |
| `GET` | `/app/rest/users` | — | `UsersResponse` |
| `POST` | `/app/rest/users` | `CreateUserRequest` | `UserResponse` |
| `DELETE` | `/app/rest/users/{userLocator}` | — | — (cleanup) |
| `POST` | `/app/rest/users/{userLocator}/tokens` | `CreateTokenRequest` | `TokenResponse` |
| `GET` | `/app/rest/users/{userLocator}/tokens` | — | `TokensResponse` |
| `GET` | `/app/rest/server` | — | — (проверка auth / 401) |

`password` только в `CreateUserRequest` (в response обычно не возвращается).

---

## Agent

| Method | Endpoint | Request model | Response model |
|--------|----------|---------------|----------------|
| `GET` | `/app/rest/agents` | — | `AgentsResponse` |
| `GET` | `/app/rest/agents/{agentLocator}` | — | `AgentResponse` |
| `GET` | `/app/rest/agents/{agentLocator}/authorizedInfo` | — | `AuthorizeAgentResponse` |
| `PUT` | `/app/rest/agents/{agentLocator}/authorizedInfo` | `AuthorizeAgentRequest` | `AuthorizeAgentResponse` |
| `GET` | `/app/rest/agents/{agentLocator}/enabledInfo` | — | status body |
| `PUT` | `/app/rest/agents/{agentLocator}/enabledInfo` | status body | status body |

После authorize:  
`ModelAssertions.assertThatModels(authorizeRequest, agentResponse)` → `status` ↔ `authorized`.

---

## Project

| Method | Endpoint | Request model | Response model |
|--------|----------|---------------|----------------|
| `POST` | `/app/rest/projects` | `CreateProjectRequest` | `ProjectResponse` |
| `GET` | `/app/rest/projects` | — | — (список; при необходимости отдельная модель) |
| `GET` | `/app/rest/projects/{projectLocator}` | — | `ProjectResponse` |
| `DELETE` | `/app/rest/projects/{projectLocator}` | — | — |

`CreateProjectRequest.parentProject` — stub через `ProjectResponse` (`locator` / `id`, напр. `_Root`).

---

## Build Configuration + Step

| Method | Endpoint | Request model | Response model |
|--------|----------|---------------|----------------|
| `POST` | `/app/rest/buildTypes` | `CreateBuildTypeRequest` | `BuildTypeResponse` |
| `POST` | `/app/rest/projects/{projectLocator}/buildTypes` | `CreateBuildTypeRequest` | `BuildTypeResponse` |
| `GET` | `/app/rest/buildTypes/{btLocator}` | — | `BuildTypeResponse` |
| `GET` | `/app/rest/buildTypes` | — | `BuildTypesResponse` |
| `DELETE` | `/app/rest/buildTypes/{btLocator}` | — | — |
| `POST` | `/app/rest/buildTypes/{btLocator}/steps` | `CreateBuildStepRequest` (+ `Properties` / `Property`) | `BuildStepResponse` |
| `GET` | `/app/rest/buildTypes/{btLocator}/steps` | — | `BuildStepsResponse` |
| `GET` | `/app/rest/buildTypes/{btLocator}/steps/{stepId}` | — | `BuildStepResponse` |

Вложенные (не endpoint-модели): `Properties`, `Property`.

---

## Run / Wait / Result

| Method | Endpoint | Request model | Response model |
|--------|----------|---------------|----------------|
| `POST` | `/app/rest/buildQueue` | `RunBuildRequest` | `BuildResponse` |
| `GET` | `/app/rest/buildQueue` | — | `BuildsResponse` |
| `GET` | `/app/rest/buildQueue/{queuedBuildLocator}` | — | `BuildResponse` |
| `POST` | `/app/rest/buildQueue/{queuedBuildLocator}` | — | cancel queued |
| `POST` | `/app/rest/builds/{buildLocator}` | — | cancel running |
| `GET` | `/app/rest/builds/{buildLocator}` | — | `BuildResponse` (poll `state` / `status`) |
| `GET` | `/app/rest/builds/{buildLocator}/status` | — | text / status |
| `GET` | `/app/rest/builds` | — | `BuildsResponse` |
| `GET` | `/downloadBuildLog.html?buildId={id}` | — | **plain text** (не DTO) |

---

## Минимальный Happy Path ↔ модели

```
GET  /app/rest/users/id:current                          → UserResponse
GET  /app/rest/agents                                    → AgentsResponse / AgentResponse
PUT  /app/rest/agents/{agentLocator}/authorizedInfo      ← AuthorizeAgentRequest → AuthorizeAgentResponse
POST /app/rest/projects                                  ← CreateProjectRequest → ProjectResponse
POST /app/rest/buildTypes                                ← CreateBuildTypeRequest → BuildTypeResponse
POST /app/rest/buildTypes/{btLocator}/steps              ← CreateBuildStepRequest → BuildStepResponse
POST /app/rest/buildQueue                                ← RunBuildRequest → BuildResponse
GET  /app/rest/builds/{buildLocator}                     → BuildResponse
GET  /downloadBuildLog.html?buildId={id}                 → String (log)
```
