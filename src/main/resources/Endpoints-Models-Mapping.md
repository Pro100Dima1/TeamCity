# TeamCity: endpoints ↔ Request / Response models

База: `http://localhost:8111`  
Имена: `*Request` — тело запроса, `*Response` — тело ответа.

---

## Почему не везде есть пара Request + Response

| Ситуация | Пример | Почему |
|----------|--------|--------|
| Только GET | `GET /agents` → `AgentsResponse` | Нет body в запросе |
| Только DELETE | `DELETE /projects/{id}` | Нет body |
| Auth header | Basic / Bearer | Credentials не JSON-модель |
| Вложенный объект | `Properties`, `Property` | Не endpoint, а часть Step |
| Лог билда | `/downloadBuildLog.html` | plain text, не DTO |
| Swagger одна schema на вход/выход | BuildType, Step, Build | В коде всё равно **две** модели (`Create*Request` / `*Response`) — как в NBank, чтобы в тестах было ясно |

---

## Authorization / User

| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| `GET` | `/app/rest/users/id:current` | — | `UserResponse` |
| `GET` | `/app/rest/users/{userLocator}` | — | `UserResponse` |
| `GET` | `/app/rest/users` | — | `UsersResponse` |
| `POST` | `/app/rest/users` | `CreateUserRequest` | `UserResponse` |
| `POST` | `/app/rest/users/{userLocator}/tokens` | `CreateTokenRequest` | `TokenResponse` |
| `GET` | `/app/rest/users/{userLocator}/tokens` | — | `TokensResponse` |
| `GET` | `/app/rest/server` | — | — (проверка 200/401) |

---

## Agent

| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| `GET` | `/app/rest/agents` | — | `AgentsResponse` |
| `GET` | `/app/rest/agents/{agentLocator}` | — | `AgentResponse` |
| `PUT` | `/app/rest/agents/{agentLocator}/authorizedInfo` | `AuthorizeAgentRequest` | `AuthorizeAgentResponse` |

После authorize удобно сверить с агентом:  
`ModelAssertions.assertThatModels(authorizeRequest, agentResponse)` → `status` ↔ `authorized`.

---

## Project

| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| `POST` | `/app/rest/projects` | `CreateProjectRequest` | `ProjectResponse` |
| `GET` | `/app/rest/projects/{projectLocator}` | — | `ProjectResponse` |
| `DELETE` | `/app/rest/projects/{projectLocator}` | — | — |

`CreateProjectRequest.parentProject` — stub через `ProjectResponse` (`locator` / `id`).

---

## Build Configuration + Step

| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| `POST` | `/app/rest/buildTypes` | `CreateBuildTypeRequest` | `BuildTypeResponse` |
| `POST` | `/app/rest/projects/{projectLocator}/buildTypes` | `CreateBuildTypeRequest` | `BuildTypeResponse` |
| `GET` | `/app/rest/buildTypes/{btLocator}` | — | `BuildTypeResponse` |
| `GET` | `/app/rest/buildTypes` | — | `BuildTypesResponse` |
| `POST` | `/app/rest/buildTypes/{btLocator}/steps` | `CreateBuildStepRequest` | `BuildStepResponse` |
| `GET` | `/app/rest/buildTypes/{btLocator}/steps` | — | `BuildStepsResponse` |

Вложенные (не endpoint-модели): `Properties`, `Property`.

---

## Run / Wait / Result

| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| `POST` | `/app/rest/buildQueue` | `RunBuildRequest` | `BuildResponse` |
| `GET` | `/app/rest/buildQueue` | — | `BuildsResponse` |
| `GET` | `/app/rest/builds/{buildLocator}` | — | `BuildResponse` |
| `GET` | `/app/rest/builds` | — | `BuildsResponse` |
| `GET` | `/downloadBuildLog.html?buildId={id}` | — | `String` (log) |

---

## Happy Path — цепочка моделей

```
GET  …/users/id:current                    → UserResponse
GET  …/agents                              → AgentsResponse / AgentResponse
PUT  …/authorizedInfo                      ← AuthorizeAgentRequest → AuthorizeAgentResponse
POST …/projects                            ← CreateProjectRequest → ProjectResponse
POST …/buildTypes                          ← CreateBuildTypeRequest → BuildTypeResponse
POST …/steps                               ← CreateBuildStepRequest → BuildStepResponse
POST …/buildQueue                          ← RunBuildRequest → BuildResponse
GET  …/builds/{id}                         → BuildResponse
GET  /downloadBuildLog.html?buildId=…      → String
```
