# Requirements – person-crud

## Entity "Person"
| Field       | Type        | Required | Rules                                        |
|-------------|-------------|----------|----------------------------------------------|
| `firstName` | String      | yes      | 2–50 characters                              |
| `lastName`  | String      | yes      | 2–50 characters                              |
| `email`     | String      | yes      | valid e-mail format, **unique across project**|
| `birthDate` | LocalDate   | yes      | must be in the past                          |

## Entity "Address"
| Field         | Type   | Required | Rules              |
|---------------|--------|----------|--------------------|
| `street`      | String | yes      | max 100 characters |
| `houseNumber` | String | yes      | max 10 characters  |
| `postalCode`  | String | yes      | max 10 characters  |
| `city`        | String | yes      | max 100 characters |
| `country`     | String | yes      | max 50 characters  |

Relationship: A Person has many Addresses (`@OneToMany`). An Address belongs to exactly one Person (`@ManyToOne`).

## CRUD Operations — Person
- **List** (`GET /persons`) — show all persons; optional full-text search over `firstName`, `lastName`, `email`
- **Create** (`GET/POST /persons/new`) — create a new person
- **Detail** (`GET /persons/{id}`) — read-only detail view
- **Edit** (`GET/POST /persons/{id}/edit`) — edit an existing person
- **Delete** (`POST /persons/{id}/delete`) — delete a person

## CRUD Operations — Address
- **List** (`GET /persons/{personId}/addresses`) — show all addresses of a person
- **Create** (`GET/POST /persons/{personId}/addresses/new`) — create a new address for a person
- **Detail** (`GET /persons/{personId}/addresses/{id}`) — read-only detail view
- **Edit** (`GET/POST /persons/{personId}/addresses/{id}/edit`) — edit an address
- **Delete** (`POST /persons/{personId}/addresses/{id}/delete`) — delete an address

## Validation & Error Messages
- All required fields are validated server-side with Bean Validation.
- Duplicate e-mail addresses are checked explicitly in the Controller (before saving).
- Success and error messages appear as flash attributes on the list page.
- When editing, a person may keep their own e-mail address (only duplicates with *other* persons are rejected).

## Seed Data
`DataInitializer` populates the empty database on startup with sample persons and addresses.
