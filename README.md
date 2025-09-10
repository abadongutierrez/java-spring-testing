# java-spring-testing

## Database Setup

This project uses PostgreSQL as the database. You can start a PostgreSQL instance using Docker Compose.

### Start Database

```bash
# Start PostgreSQL database
docker compose -f docker/db.yml up -d
```

### Stop Database

```bash
# Stop database
docker compose -f docker/db.yml down

# Stop and remove volume (deletes all data)
docker compose -f docker/db.yml down -v
```

### Database Configuration

- **Database Name**: `java_spring_testing`
- **Username**: `postgres`
- **Password**: `postgres`
- **Host**: `localhost`
- **Port**: `5432`
