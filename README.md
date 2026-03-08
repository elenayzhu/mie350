# PC Part Picker API

Spring Boot REST API for selecting PC components and generating budget-based recommendations.

## Tech Stack

- Java 11
- Spring Boot 2.7
- Spring Web
- Spring Data JPA
- H2 in-memory database
- Maven

## Project Structure

```text
pc-part-picker/
	src/main/java/com/team15/partpicker/
		controller/        # REST endpoints
		model/entity/      # JPA entities
		model/repository/  # Spring Data repositories
		model/service/     # Business logic
	src/main/resources/
		application.properties
		data.sql           # Seed data
```

## Quick Start

1. Open a terminal in `pc-part-picker/`
2. Run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Default server URL:

- `http://localhost:8086`

H2 Console:

- `http://localhost:8086/h2-console`
- JDBC URL: `jdbc:h2:mem:partpickerdb;DB_CLOSE_ON_EXIT=FALSE`
- Username: `partpicker`
- Password: `partpicker`

## Current Domain Coverage

The API currently supports 8 part entities:

- CPU
- GPU
- Motherboard
- RAM
- PSU
- Storage
- Case
- Cooler

User preference and recommendation endpoints are also available.

## API Endpoints

### CPU

- `GET /cpus`
- `GET /cpus/{cpuId}`
- `POST /cpus`

Query params for `GET /cpus`:

- `brand`
- `socket`
- `minCores`
- `minPrice`
- `maxPrice`

### GPU

- `GET /gpus`
- `GET /gpus/{gpuId}`
- `POST /gpus`

Query params for `GET /gpus`:

- `brand`
- `minVramGb`
- `minPrice`
- `maxPrice`

### Motherboard

- `GET /motherboards`
- `GET /motherboards/{motherboardId}`
- `POST /motherboards`

Query params for `GET /motherboards`:

- `brand`
- `socket`
- `formFactor`
- `minPrice`
- `maxPrice`

### RAM

- `GET /rams`
- `GET /rams/{ramId}`
- `POST /rams`

Query params for `GET /rams`:

- `brand`
- `ddrType`
- `minSpeed`
- `minCapacity`
- `minPrice`
- `maxPrice`

### PSU

- `GET /psus`
- `GET /psus/{psuId}`
- `POST /psus`

Query params for `GET /psus`:

- `brand`
- `minWattage`
- `efficiencyRating`
- `modularType`
- `minPrice`
- `maxPrice`

### Storage

- `GET /storages`
- `GET /storages/{storageId}`
- `POST /storages`

Query params for `GET /storages`:

- `brand`
- `type`
- `minCapacity`
- `minPrice`
- `maxPrice`

### Case

- `GET /cases`
- `GET /cases/{caseId}`
- `POST /cases`

Query params for `GET /cases`:

- `brand`
- `formFactor`
- `minMaxGpuLengthMm`
- `minPrice`
- `maxPrice`

### Cooler

- `GET /coolers`
- `GET /coolers/{coolerId}`
- `POST /coolers`

Query params for `GET /coolers`:

- `brand`
- `socket`
- `type`
- `minMaxTdp`
- `minPrice`
- `maxPrice`

### Preferences and Recommendations

- `POST /preferences`
- `GET /preferences/{preferenceId}`
- `GET /recommendations/{preferenceId}`

`UserPreference` supports:

- Preferred brand per part type
- `maxBudget`
- `buildCategory` (`GAMING`, `AI_ML`, `WORKSTATION`)

## Example Requests

Filter CPUs by brand and max price:

```http
GET /cpus?brand=AMD&maxPrice=300
```

Filter RAM by DDR type and speed:

```http
GET /rams?ddrType=DDR5&minSpeed=6000
```

Create a user preference:

```json
POST /preferences
{
	"preferredCpuBrand": "AMD",
	"preferredGpuBrand": "NVIDIA",
	"preferredRamBrand": "Corsair",
	"maxBudget": 1800,
	"buildCategory": "GAMING"
}
```

Get recommendation for a saved preference:

```http
GET /recommendations/1
```

## Running Tests

From `pc-part-picker/`:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Roadmap Status

- Completed: Expand domain model with all part entities
- Completed: Filtering query param support across all part list endpoints
- In progress: Recommendation engine improvements and deeper compatibility logic

## Notes

- Database is in-memory H2 and resets on restart.
- Seed data is loaded from `pc-part-picker/src/main/resources/data.sql`.
